package precious_rule_system.journeyview.data;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.util.Log;

import java.util.ArrayList;

import precious_rule_system.journeyview.JourneyActivity;
import precious_rule_system.journeyview.assets.Assets;
import precious_rule_system.journeyview.constants.Constants;
import precious_rule_system.journeyview.helpers.Position;
import precious_rule_system.journeyview.helpers.SVGHelper;
import precious_rule_system.journeyview.recycler.RecyclerView;
import precious_rule_system.journeyview.view.JourneyView;
import precious_rule_system.rewardsystem.entities.RewardEvent;
import rules.helpers.Tuple;

/**
 * Created by christopher on 04.09.16.
 */

public class State {

    // list of the pages, corresponds to cards in the recyclerview
    public ArrayList<StatePage> pages = new ArrayList<>();

    // references to instances the state uses
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private JourneyView journeyView;
    private JourneyActivity.JourneyStore store;

    // width and height, which are constantly being tracked
    private int width = 0;
    private int height = 0;

    // total number of points in this state (including all rewardevents)
    private int points = 0;

    // total number of points in this state (including rewardevents plus playerposition increases when animating)
    private int playerPoints = 0;

    // flag whether we should scroll to the player upon next state update
    private boolean shouldScrollToPlayerPosition = false;

    // animation related things
    private StateAnimation animation;
    private ArrayList<RewardEvent> tobeProcessed = new ArrayList<>();

    // the page we are currently at in terms of scrolling
    private int currentPage = 0;


    public State(JourneyActivity.JourneyStore store) {
        this.store = store;
        animation = new StateAnimation(this);
    }

    /**
     * Convenience method for storing used instance
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Convenience method for storing used instance
     * @param recyclerView
     */
    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    /**
     * Convenience method for storing used instance
     * @param view
     */
    public void setJourneyView(JourneyView view) {
        this.journeyView = view;
    }

    /**
     * Adds a new page to the state, and automatically sets the
     * width and height on that page which in turn calculates its background assets
     * @param p
     */
    private void addPage(StatePage p) {
        p.updateDimensions(width, height);
        pages.add(p);
    }

    /**
     * Clear all variables, i.e. reset the state
     */
    public synchronized  void clear() {
        this.animation.stopAnimating();
        this.points = 0;
        this.playerPoints = 0;
        this.pages.clear();
    }

    /**
     * Sets a flag that upon next update of the state, the state
     * should trigger a scrolling to the current player position
     */
    public synchronized void scrollOnNextRetreival() {
        shouldScrollToPlayerPosition = true;
    }

    /**
     * Scrolls the recyclerview to the current player position
     */
    public synchronized void scrollToPlayer() {
        int n = this.pages.size();
        int totalHeight = n * height;
        float position = (float) playerPoints / (float) Constants.pointsPerPage;
        int container = (int) Math.floor(position);
        float positionInContainer = position - container;
        android.graphics.Path path = store.assets.getPath().getPath(width, height);
        int s = store.sizes.getPlayerIndicatorSizes(width)[0]/2;
        recyclerView.scrollToCustom(container, Math.round(SVGHelper.getPositionAlongPath(path, positionInContainer)[1]) - height/2);
    }

    /**
     * Adds a new reward event to the state, wrapper
     * @param event
     * @param isNew
     */
    public synchronized void addRewardEvent(RewardEvent event, boolean isNew) {
        ArrayList<RewardEvent> events = new ArrayList<>();
        events.add(event);
        addRewardEvents(events, isNew, false);
    }

    /**
     * Adds a new reward event to the state, wrapper
     * @param event
     */
    public synchronized void addRewardEvent(RewardEvent event) {
        ArrayList<RewardEvent> events = new ArrayList<>();
        events.add(event);
        addRewardEvents(events, false, false);
    }

    /**
     * Adds new reward events to the state, wrapper
     * @param events
     */
    public synchronized void addRewardEvents(ArrayList<RewardEvent> events) {
        this.addRewardEvents(events, false, false);
    }

    /**
     * This adds a list of reward events to the current state
     * @param events - list of events
     * @param isNew - whether the passed events are new
     * @param fromAnimation - whether this comes from an animation
     */
    public synchronized void addRewardEvents(ArrayList<RewardEvent> events, boolean isNew, boolean fromAnimation) {

        // add a single page if necessary
        if(pages.size() == 0) {
            StatePage p = new StatePage(store, Constants.getLandscape(0));
            addPage(p);
        }

        // page count
        int count = pages.size();

        // go through all passed events
        for(int i=0; i<events.size(); i++) {

            // is this the last event?
            boolean isLast = i == events.size()-1;

            RewardEvent e = events.get(i);

            // increase point coint
            points += e.getPoints();

            // an animation automatically sets the player points via its own updates
            // so we only need to update the player points when this doesnt originate
            // from an animation
            if (!fromAnimation) {
                playerPoints += e.getPoints();
            }

            StatePage last = pages.get(pages.size()-1);
            boolean dontAddNewPage = false;

            // if pages are over the vertical middle position,
            // we add a new empty page to the state
            // so increasing player positions can still be displayed
            // at the middle of the screen when scrolling
            // this however makes this check necessary
            if (pages.size() > 1 && last.events.size() == 0) {
                StatePage lastBefore = pages.get(pages.size()-2);
                if (lastBefore.getRemainingPoints() > 0) {
                    dontAddNewPage = true;
                    last = lastBefore;
                }
            }

            int deductPoints = 0;

            // event does not fit anymore
            if(!last.doesFitEvent(e)) {
                // store how many points we need to deduct
                deductPoints = last.getRemainingPoints();

                if (!dontAddNewPage) {

                    // create a new page with increased count
                    last = new StatePage(store, Constants.getLandscape(count++));
                    // add the page to the array
                    addPage(last);

                } else {
                    last = pages.get(pages.size()-1);
                }

            }

            // add the event
            last.addRewardEvent(e, deductPoints, isNew, fromAnimation);

            // add an empty page in case we are over the middle
            if (isLast && last.isLastPositionOverMiddle() && !dontAddNewPage)  {
                addPage(new StatePage(store, Constants.getLandscape(count++)));
            }

        }
    }

    /**
     * Wrapper for the arraylist function
     * @param e
     */
    public synchronized void updateRewardEventWithAnimation(RewardEvent e) {
        ArrayList<RewardEvent> events = new ArrayList<>();
        events.add(e);
        this.updateRewardEventsWithAnimation(events);
    }

    /**
     * Adds a list of reward events to the animation object for animation
     * if the animator is already animating, we store the events
     * in the toBeProcessed array
     * @param events
     */
    public synchronized void updateRewardEventsWithAnimation(ArrayList<RewardEvent> events) {

        if (!animation.isAnimating()) {
            Log.i("JourneyState", "Starting animation");
            animation.addRewardEvents(events);
            animation.startAnimating();
        } else {
            Log.i("JourneyState", "Processing unanimated elements");
            for(RewardEvent e : events) {
                tobeProcessed.add(e);
            }
        }
    }

    /**
     * Called from the animator whenever an animation finished
     * we here check whether there are still events to be processed which
     * were added when the animation was ongoing, and retrigger the animation
     */
    public synchronized void animationFinished() {
        if (tobeProcessed.size() > 0) {
            this.updateRewardEventsWithAnimation(tobeProcessed);
            tobeProcessed = new ArrayList<>();
        } else {
            this.recyclerView.enableTouch();
        }
    }

    /**
     * Called whenever an animation is started
     */
    public synchronized void animationStarted() {
        this.recyclerView.disableTouch();
    }

    /**
     * Updates the state's dimensions, and then all the page's dimensions
     * This is a potentially expense overation, this we only make these
     * updates whenever width or height are unchanged
     * If the shouldScrollToPlayerPosition flag is set, we also scroll to the player's
     * current position plus we update the journey view's points
     * @param width
     * @param height
     */
    public synchronized void updateDimensions(int width, int height) {

        if (this.width != width || this.height != height) {

            this.postIsUpdating(true);

            this.width = width;
            this.height = height;

            for(StatePage page : pages) {
                page.updateDimensions(width, height);
            }

            this.postIsUpdating(false);

        }

        if (shouldScrollToPlayerPosition) {
            shouldScrollToPlayerPosition = false;
            scrollToPlayer();
        }

        this.journeyView.updatePoints(playerPoints);
    }

    /**
     * Unused at the moment, maybe used later to popup a view in the JourneyView
     * class indicating the we are loading atm
     * @param isUpdating
     */
    public void postIsUpdating(boolean isUpdating) {
        // TODO
    }

    /**
     * Notify the RecyclerView's adapter that changes were made
     */
    public synchronized void postInvalidate() {
        this.adapter.notifyDataSetChanged();
    }

    /**
     * Reset the player's points, only used by dummy functions atm.
     */
    public void resetPlayerPoints() {
        this.playerPoints = 0;
    }

    /**
     * Adds a number of points to the player
     * Creates a new page if necessary and updates the JourneyView
     * @param points
     */
    public void addPlayerPoints(int points) {
        this.playerPoints += points;

        float position = (float) playerPoints / (float) Constants.pointsPerPage;
        int container = (int) Math.floor(position);
        Float positionInContainer = position - (float) container;

        if (positionInContainer > 0.5 && pages.size()-1 == container) {
            addPage(new StatePage(store, Constants.getLandscape(pages.size())));
        }

        this.journeyView.updatePoints(this.playerPoints);
    }

    /**
     * Returns the number of points the player currently has
     * @return
     */
    public int getPlayerPoints() {
        return this.playerPoints;
    }

    /**
     * Returns a list of overlapping RewardEvents for a specific position
     * NOTE THAT the absolute position is used within StatePageRewardEvent
     * @param item
     * @return
     */
    public ArrayList<StatePageRewardEvent> getOverlappingRewardEvents(int item) {

        android.graphics.Path path = store.assets.getPath().getPath(width, height);
        ArrayList<StatePageRewardEvent> events = new ArrayList<>();

        // check for one position downward
        if (item >= 1) {

            // get one page down
            StatePage previous = this.pages.get(item-1);
            StatePageRewardEvent e = previous.getLast();

            if (e != null) {
                float s = store.sizes.getRewardSize(width, height, e.event);
                Tuple<StatePage.Direction, Position> critical = previous.getOverlappingPosition(path, Math.round(s),Math.round(s), e.position);
                if (critical != null) {
                    e.setOverlapPosition(critical.y);
                    events.add(e);
                }
            }

        }

        // also check for one position downward
        if (item < this.pages.size()-1) {

            // get one page up
            StatePage next = this.pages.get(item+1);
            StatePageRewardEvent e = next.getFirst();

            if (e != null) {
                float s = store.sizes.getRewardSize(width, height, e.event);
                Tuple<StatePage.Direction, Position> critical = next.getOverlappingPosition(path, Math.round(s), Math.round(s), e.position);
                if (critical != null) {
                    e.setOverlapPosition(critical.y);
                    events.add(e);
                }
            }

        }

        return events;

    }

    /**
     * Get all rewardevents for a specific position (no overlapping elements included)
     * @param item
     * @return
     */
    public ArrayList<StatePageRewardEvent> getRewardEvents(int item) {
        StatePage p = this.pages.get(item);
        return p.events;
    }

    /**
     * Return the Position of the player which could be overlapping from another page
     * Returns null if no player needs to be added at item
     * @param item
     * @return
     */
    public Position getOverlappingPlayerPosition(int item) {

        if (item >= pages.size()) return null;

        float position = (float) playerPoints / (float) Constants.pointsPerPage;
        int container = (int) Math.floor(position);
        if (container >= pages.size()) return null;
        Float positionInContainer = position - (float) container;

        if (item == container) return null;
        if (item < container -1 || item > container +1 ) return null;
        if (positionInContainer > 0.3 && positionInContainer < 0.7) return null;

        android.graphics.Path path = store.assets.getPath().getPath(width, height);

        StatePage containerPage = this.pages.get(container);
        int[] s = store.sizes.getPlayerIndicatorSizes(this.width);
        Tuple<StatePage.Direction, Position> critical = containerPage.getOverlappingPosition(path, Math.round(s[0]+s[1]), Math.round(s[0]) ,positionInContainer);
        if (critical == null) return null;

        if (item < container && critical.x == StatePage.Direction.DOWN) {
            return critical.y;
        } else if (item > container && critical.x == StatePage.Direction.UP) {
            return critical.y;
        }

        return critical.y;

    }

    /**
     * Get the current Player position at a item position
     * @param item
     * @return null of player not existing at item, float 0...1 value otherwise
     */
    public Float getPlayerPosition(int item) {

        if (item >= this.pages.size()) return null;

        float position = (float) playerPoints / (float) Constants.pointsPerPage;
        int container = (int) Math.floor(position);

        Float positionInContainer = position - (float) container;
        if (item == container) return positionInContainer;
        return null;

    }

    public synchronized float getRemainingScrollOffsetToPlayer(float verticalOffset, float height, int dy) {

        float position = verticalOffset / height;

        // store the page
        int item = (pages.size()-1) - (int) Math.round(position);

        // check whether we are above the player position which is not allowed
        float playerPosition = (float) playerPoints / (float) Constants.pointsPerPage;
        int playerContainer = (int) Math.floor(playerPosition);

        // can the player scroll faster than this?
        if (item < playerContainer-2) return dy;

        float playerPositionInContainer = playerPosition - playerContainer;
        android.graphics.Path path = store.assets.getPath().getPath(width, this.height);

        float offset = Math.round(SVGHelper.getPositionAlongPath(path, playerPositionInContainer)[1]) - height/2;
        float playerVerticalOffset = (this.pages.size()-1-playerContainer)*this.height + offset;
        float bufferedVerticalOffset = verticalOffset + 25;

        // no more space left
        if (bufferedVerticalOffset < playerVerticalOffset) {
            return 1;
        } else if (bufferedVerticalOffset + dy >= playerVerticalOffset) {
            return dy;
        } else {
            return playerVerticalOffset - bufferedVerticalOffset;
        }

    }

    /**
     * Delegate method whenever a scroll change is made
     * Updates background color and labels, and stores the page that's currently visible
     */
    public synchronized void scrollChange(float verticalOffset, float height, int dy) {

        float position = (verticalOffset + dy) / height;
        position = Math.max(0, position);

        // store the page
        float currentPositionInContainer = 1.0f - (position - (float) Math.ceil(position));
        int item = (pages.size()-1) - (int) Math.round(position);

        item = Math.max(Math.min(item, pages.size()-1),0);

        this.setCurrentPage(item);

        // update background color
        Constants.Landscape l = this.pages.get(item).landscape;
        this.journeyView.setBackgroundColorAnimated(l.getColor());
        this.journeyView.updateLabel(l.getNiceText());

    }

    /**
     * Store the page that's currently visible
     * @param item
     */
    public synchronized void setCurrentPage(int item) {
        this.currentPage = item;
    }


    public RecyclerView getRecyclerView() {
        return this.recyclerView;
    }

}
