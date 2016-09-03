package precious_rule_system.journeyview_new.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Date;

import precious_rule_system.journeyview_new.assets.JourneyAssets;
import precious_rule_system.journeyview_new.grid.JourneyGridAdapterDelegate;
import precious_rule_system.journeyview_new.utilities.Layout;
import precious_rule_system.journeyview_new.utilities.SVGExtended;
import precious_rule_system.rewardsystem.entities.RewardEvent;
import rules.helpers.Tuple;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;

/**
 * Created by christopher on 02.09.16.
 */

public class DataUIManager implements JourneyGridAdapterDelegate {

    JourneyAssets assets;
    Layout layout;
    Context context;

    /**
     * Keys for storing data in user preferences
     */
    private final String lastStoredDateKey = "journeyView.lastStoredDate";

    /**
     * Sizes, colors, etc. for rendering
     */
    private static final float rewardEventSize = 0.05f;
    private static final float rewardMilestoneSize = 0.08f;
    private static final float rewardPointIncreaseSize = 0.03f;
    private static final float playerHeight = 0.086f;

    public static final float pathThinkness = 0.03f;
    public static final int gridBackgroundColor = Color.parseColor("#eeeae9");

    /**
     * Constants w.r.t how points are rendered
     */
    public static final float pointsPerPage = 100f;

    /**
     * Store width and height
     */
    int width;
    int height;

    /**
     * Our list of pages
     */
    ArrayList<DataPage> pages;
    int playerPosition;
    Tuple<RewardEvent, Float> lastVisibleEvent;
    int totalPoints = 0;

    public DataUIManager(Context context, JourneyAssets assets, Layout layout, int width, int height) {
        this.assets = assets;
        this.layout = layout;
        this.context = context;
        this.width = width;
        this.height = height;
        this.initialize();
    }

    /**
     * Updates all items
     */
    public void initialize() {

        // get the date which was last processed
        long lastReceived = this.getLastRetreivedDate();
        lastReceived = -1;
        long now = new Date().getTime();

        // get all values up to that point, split it up into events we have viewed and which we haven't
        ArrayList<RewardEvent> oldEvents;
        ArrayList<RewardEvent> newEvents;
        boolean shouldAnimate = false;

        if (lastReceived > 0) {
            oldEvents = PRECIOUS_APP.getRewardSystem().dummy_getAllEventsSortedByDate(0, lastReceived);
            newEvents = PRECIOUS_APP.getRewardSystem().dummy_getAllEventsSortedByDate(lastReceived+1, now);
            if (newEvents.size() > 0) {
                shouldAnimate = true;
            }
        } else {
            oldEvents = PRECIOUS_APP.getRewardSystem().dummy_getAllEventsSortedByDate();
            newEvents = new ArrayList<>();
        }

        // store this date
        //this.setLastRetreivedDate(now);

        // create an array storing the pages
        ArrayList<DataPage> pages = new ArrayList<DataPage>();

        // for now, add old and new
        this.totalPoints = 0;
        this.totalPoints += this.addEvents(pages, oldEvents, true);

        // store where the positions are
        int oldPagePosition;
        int oldEventsPosition;

        if (pages.size() > 0) {
            oldPagePosition = pages.size()-1;
            oldEventsPosition = pages.get(oldPagePosition).events.size()-1;

            if (oldEventsPosition < 0 && pages.size() > 1) {
                oldPagePosition = pages.size()-2;
                oldEventsPosition = pages.get(oldPagePosition).events.size()-1;
            }

        } else {
            oldPagePosition = -1;
            oldEventsPosition = -1;
        }

        // store how much points we'll increase through new events
        int pointIncrease = this.addEvents(pages, newEvents, false);
        this.totalPoints += pointIncrease;

        // store the pages
        this.pages = pages;

        // set the player position
        this.playerPosition = pages.size() - oldPagePosition - 1;

        // store the player offset
        if(oldPagePosition >= 0 && oldEventsPosition >= 0) {
            DataPage page = pages.get(oldPagePosition);
            lastVisibleEvent = pages.get(oldPagePosition).events.get(oldEventsPosition);
            page.addPlayer();

            Tuple<Integer, Float> critical = page.getCriticalPlayerPosition(this.width, this.height);
            if (critical != null) {

                int direction = critical.x;
                float position = critical.y;

                // position is too small, go one view back and add this event there as well at the top
                if (direction < 0) {
                    // only do it for page sizes >= 2
                    if (pages.size() >= 2) {
                        DataPage previous = pages.get(pages.size()-2);
                        previous.addPlayerAtPosition(position);
                    }
                }
                // position is too high, go one view forward and add the event there
                else {
                    // create the next page,
                    DataPage next = new DataPage(getLandscape(pages.size()), this);
                    // add the player
                    next.addPlayerAtPosition(position);
                    // store it
                    pages.add(next);
                }
            }
        }

    }



    public int getPlayerItem() {
        return this.playerPosition;
    }

    public Tuple<RewardEvent, Float> getLastVisibleEvent() {
        return this.lastVisibleEvent;
    }

    private Float getPlayerPosition() {
        int idx = pages.size() - getPlayerItem() - 1;
        Float position = pages.get(idx).getLastPosition();
        if (position == null) {
            idx = pages.size() - getPlayerItem() - 2;
            return pages.get(idx).getLastPosition();
        } else {
            return position;
        }
    }

    // ITEM ZERO IS AT THE TOP!!!! = most up-to-date value
    private int addEvents(ArrayList<DataPage> pages, ArrayList<RewardEvent> events, boolean visible) {

        // store total points
        int points = 0;

        // add a single page if necessary
        if(pages.size() == 0) {
            DataPage p = new DataPage(getLandscape(0), this);
            pages.add(p);
        }

        // page count
        int count = pages.size();

        for(int i=0; i<events.size(); i++) {

            boolean isLast = i == events.size()-1;
            RewardEvent e = events.get(i);
            points += e.getPoints();

            DataPage last = pages.get(pages.size()-1);
            int deductPoints = 0;

            // event does not fit anymore
            if(!last.doesFitEvent(e)) {
                // store how many points we need to deduct
                deductPoints = last.getRemainingPoints();
                // create a new page with increased count
                last = new DataPage(getLandscape(++count), this);
                // add the page to the array
                pages.add(last);
            }

            // add the event
            last.addRewardEvent(e, deductPoints, visible);

            // check whether we need to add the event again to one of the pages due to borders
            Tuple<Integer, Float> critical = last.getCriticalPosition(this.width, this.height);

            // store whether we have created the next view already
            boolean hasCreatedNext = false;

            if (critical != null) {

                int direction = critical.x;
                float position = critical.y;

                // position is too small, go one view back and add this event there as well at the top
                if (direction < 0) {
                    // only do it for page sizes >= 2
                    if (pages.size() >= 2) {
                        DataPage previous = pages.get(pages.size()-2);
                        previous.addRewardEventAtPosition(e, position, visible);
                    }
                }
                // position is too high, go one view forward and add the event there
                else {
                    // create the next page,
                    DataPage next = new DataPage(getLandscape(++count), this);
                    // store that we've already added the next page
                    hasCreatedNext = true;
                    // add the reward
                    next.addRewardEventAtPosition(e, position, visible);
                    // store it
                    pages.add(next);
                }

            }

            // check whether this is the last event, if yes check whether we above the middle, if yes, then add an empty page
            if (isLast && last.isLastPositionOverMiddle() && !hasCreatedNext)  {
                pages.add(new DataPage(getLandscape(++count), this));
            }

        }

        return points;

    }

    public int getNumberOfItems() {
        return pages.size();
    }

    private int reverse(int item) {
        return (pages.size()-1)-item;
    }

    public ArrayList<Tuple<RewardEvent, Float>> getEvents(int item) {
        return pages.get(reverse(item)).events;
    }

    public JourneyAssets.Landscape getLandscapeForItem(int item) {
        return pages.get(reverse(item)).landscape;
    }

    public Float getPlayerPosition(int item) {
        return pages.get(reverse(item)).playerPosition;
    }

    public ArrayList<Tuple<JourneyAssets.Size, double[]>> getAssets(int item) {
        return pages.get(reverse(item)).assets;
    }

    /*public Float getPlayer(int item) {
        item = reverse(item);
        if (item == pages.size()-1) return pages.get(item).getLastPosition();
        else return null;
    }

    public int getPlayerItem() {
        return 0;
    }*/

    public String getLabelForItem(int item) {
        return pages.get(reverse(item)).landscape.getNiceText();
    }

    public int getNumberOfPoints() {
        return totalPoints;
    }

    /**
     * Returns the landscape for a given index
     * @param index
     * @return
     */
    private JourneyAssets.Landscape getLandscape(int index) {
        if (index == 0) {
            return JourneyAssets.Landscape.TREES;
        }
        return JourneyAssets.Landscape.MOUNTAIN;
    }

    /**
     * Converts a 0-1 position to a screen
     * @param position
     * @return
     */
    public float[] convertPosition(float position) {
        // we invert it here because the path is inverted ...
        position = 1.0f - position;
        return SVGExtended.getPositionAlongPath(assets.path.path,position).x;
    }


    /**
     * Convenience method to generate random assets
     * @return
     */
    public ArrayList<Tuple<JourneyAssets.Size, double[]>> generateAssets() {
        return layout.calculateRandomSizes(assets.assetSizes,assets.path.offset,assets);
    }

    /**
     * Sets the date which was last processed
     * @param date
     */
    private void setLastRetreivedDate(long date) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        prefs.edit().putLong(lastStoredDateKey, date).commit();
    }

    /**
     * Returns the date which was last processed
     * @return
     */
    private long getLastRetreivedDate() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        long date = prefs.getLong(lastStoredDateKey, -1);
        return date;
    }

    /**
     * Converts a number of points to a relative position on a page
     * @param points (not accumulated, but individually)
     * @return
     */
    private static float convertPointsToPagePosition(float points) {
        return points/pointsPerPage;
    }

    /**
     * Convenience methods return the size of reward items
     */
    public static final float getRewardSize(RewardEvent e, int width, int height) {
        if (e.isEvent()) return getRewardEventSize(width, height);
        if (e.isMilestone()) return getRewardMilestoneSize(width, height);
        if (e.isPointIncrease()) return getRewardPointIncreaseSize(width, height);
        return 0.0f;
    }

    public static final float getRewardEventSize(int width, int height) {
        return (float) width * rewardEventSize;
    }

    public static final float getRewardMilestoneSize(int width, int height) {
        return (float) width * rewardMilestoneSize;
    }

    public static final float getRewardPointIncreaseSize(int width, int height) {
        return (float) width * rewardPointIncreaseSize;
    }

    public static final float getPlayerHeight(int width, int height) {
        return (float) width * playerHeight;
    }

}
