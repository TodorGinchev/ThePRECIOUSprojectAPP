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

    public ArrayList<StatePage> pages = new ArrayList<>();

    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private JourneyView journeyView;

    private JourneyActivity.JourneyStore store;


    private int width = 0;
    private int height = 0;

    private int points = 0;

    private int playerPoints = 0;
    private boolean shouldScrollToPlayerPosition = false;

    // animation
    private StateAnimation animation;
    private int currentPage = 0;
    private ArrayList<RewardEvent> tobeProcessed = new ArrayList<>();

    public State(JourneyActivity.JourneyStore store) {
        this.store = store;
        animation = new StateAnimation(this);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setJourneyView(JourneyView view) {
        this.journeyView = view;
    }

    private void addPage(StatePage p) {
        p.updateDimensions(width, height);
        pages.add(p);
    }

    public synchronized  void clear() {
        this.points = 0;
        this.playerPoints = 0;
        this.pages.clear();
    }

    public synchronized void scrollOnNextRetreival() {
        shouldScrollToPlayerPosition = true;
    }

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

    public synchronized void addRewardEvent(RewardEvent event, boolean isNew) {
        ArrayList<RewardEvent> events = new ArrayList<>();
        events.add(event);
        addRewardEvents(events, isNew, false);
    }

    public synchronized void addRewardEvent(RewardEvent event) {
        ArrayList<RewardEvent> events = new ArrayList<>();
        events.add(event);
        addRewardEvents(events, false, false);
    }

    public synchronized void addRewardEvents(ArrayList<RewardEvent> events) {
        this.addRewardEvents(events, false, false);
    }

    public synchronized void addRewardEvents(ArrayList<RewardEvent> events, boolean isNew, boolean fromAnimation) {

        // add a single page if necessary
        if(pages.size() == 0) {
            StatePage p = new StatePage(store, Constants.getLandscape(0));
            addPage(p);
        }

        // page count
        int count = pages.size();

        for(int i=0; i<events.size(); i++) {

            boolean isLast = i == events.size()-1;
            RewardEvent e = events.get(i);
            points += e.getPoints();

            if (!fromAnimation) {
                playerPoints += e.getPoints();
            }

            StatePage last = pages.get(pages.size()-1);
            boolean dontAddNewPage = false;

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

    public synchronized void updateRewardEventWithAnimation(RewardEvent e) {
        ArrayList<RewardEvent> events = new ArrayList<>();
        events.add(e);
        this.updateRewardEventsWithAnimation(events);
    }

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

    public synchronized void animationFinished() {
        if (tobeProcessed.size() > 0) {
            this.updateRewardEventsWithAnimation(tobeProcessed);
            tobeProcessed = new ArrayList<>();
        }
    }

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

    public void postIsUpdating(boolean isUpdating) {
        // TODO
    }

    public synchronized void postInvalidate() {
        this.adapter.notifyDataSetChanged();
    }

    public void resetPlayerPoints() {
        this.playerPoints = 0;
    }

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

    public int getPlayerPoints() {
        return this.playerPoints;
    }

    public ArrayList<StatePage.StatePageRewardEvent> getOverlappingRewardEvents(int item) {

        android.graphics.Path path = store.assets.getPath().getPath(width, height);
        ArrayList<StatePage.StatePageRewardEvent> events = new ArrayList<>();

        if (item >= 1) {

            StatePage previous = this.pages.get(item-1);
            StatePage.StatePageRewardEvent e = previous.getLast();

            if (e != null) {
                float s = store.sizes.getRewardSize(width, height, e.event);
                Tuple<StatePage.Direction, Position> critical = previous.getOverlappingPosition(path, Math.round(s),Math.round(s), e.position);
                if (critical != null) {
                    StatePage.StatePageRewardEvent additional = previous.getPageRewardEvent(e.event, critical.y.left);
                    additional.absolutePosition = critical.y;
                    events.add(additional);
                }
            }

        }

        if (item < this.pages.size()-1) {

            StatePage next = this.pages.get(item+1);
            StatePage.StatePageRewardEvent e = next.getFirst();

            if (e != null) {
                float s = store.sizes.getRewardSize(width, height, e.event);
                Tuple<StatePage.Direction, Position> critical = next.getOverlappingPosition(path, Math.round(s), Math.round(s), e.position);
                if (critical != null) {
                    StatePage.StatePageRewardEvent additional = next.getPageRewardEvent(e.event, critical.y.left);
                    additional.absolutePosition = critical.y;
                    events.add(additional);
                }
            }


        }

        return events;

    }

    public ArrayList<StatePage.StatePageRewardEvent> getRewardEvents(int item) {
        StatePage p = this.pages.get(item);
        return p.events;
    }

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

    public Float getPlayerPosition(int item) {

        if (item >= this.pages.size()) return null;

        float position = (float) playerPoints / (float) Constants.pointsPerPage;
        int container = (int) Math.floor(position);

        Float positionInContainer = position - (float) container;
        if (item == container) return positionInContainer;
        return null;

    }

    public synchronized void scrollChange(float position) {
        // store the page
        int item = (pages.size()-1) - (int) Math.round(position);
        this.setCurrentPage(item);
        // update background color
        Constants.Landscape l = this.pages.get(item).landscape;
        this.journeyView.setBackgroundColorAnimated(l.getColor());
        this.journeyView.updateLabel(l.getNiceText());
    }

    public synchronized void setCurrentPage(int item) {
        this.currentPage = item;
    }



}
