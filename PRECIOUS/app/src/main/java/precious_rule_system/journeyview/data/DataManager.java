package precious_rule_system.journeyview.data;


import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

import precious_rule_system.journeyview.helpers.AssetBackgroundGenerator;
import precious_rule_system.journeyview.helpers.Position;
import precious_rule_system.journeyview.recycler.RecyclerView;
import precious_rule_system.rewardsystem.entities.RewardEvent;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;
import ui.precious.comnet.aalto.precious.ui_MainActivity;

/**
 * Created by christopher on 04.09.16.
 */

public class DataManager {

    private State state;

    public DataManager(ui_MainActivity.JourneyStore store) {
        state = new State(store);

        // temporary
        ArrayList<RewardEvent> dummyList = PRECIOUS_APP.getRewardSystem().dummy_createDummyEvents(71);
        state.addRewardEvents(dummyList);
        state.scrollOnNextRetreival();
    }

    public void onPause() {

    }

    public void onResume() {
        //startDummyActions();
    }

    /**
     * Dummy counter and method for testing
     */
    int dummyCount = 0;
    public void oldstartDummyActions() {

        ArrayList<RewardEvent> dummyList = PRECIOUS_APP.getRewardSystem().dummy_createDummyEvents(71);
        state.addRewardEvents(dummyList);
        state.scrollOnNextRetreival();

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {

                ArrayList<RewardEvent> dummyList = PRECIOUS_APP.getRewardSystem().dummy_createDummyEvents(7);
                Log.i("JourneyState", "Updating Rewards from DataManager");
                state.updateRewardEventsWithAnimation(dummyList);
                dummyCount++;
                //if (dummyCount < 1) handler.postDelayed(this, 10000);
            }
        };

        handler.postDelayed(r, 1000);
    }

    public void startDummyDoubleTapActions() {
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                int n = (int) Math.round(Math.random()*9)+1;
                ArrayList<RewardEvent> dummyList = PRECIOUS_APP.getRewardSystem().dummy_createDummyEvents(n);
                Log.i("JourneyState", "Updating Rewards from DataManager");
                state.updateRewardEventsWithAnimation(dummyList);
            }
        };
        handler.post(r);
    }

    /**
     * Update the dimensions of the state, should only happen on resize/screen orientation change
     * @param width
     * @param height
     */
    public void updateDimensions(int width, int height) {
        state.updateDimensions(width, height);
    }

    /**
     * Convenience method to store the adapter in the state
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        state.setAdapter(adapter);
    }

    /**
     * Convenience method to store the recycler view in the state
     * @param recyclerView
     */
    public void setRecyclerView(RecyclerView recyclerView) {
        state.setRecyclerView(recyclerView);
    }

    /**
     * Returns the total number of pages (i.e. landscapes) for the adapter
     * @return
     */
    public int getPageCount() {
        return state.pages.size();
    }

    /**
     * Returns the AssetBackground instance for a specific position within the recycler
     * view - called from the adapter
     * @param item
     * @return
     */
    public AssetBackgroundGenerator.AssetBackground getBackground(int item) {
        return state.pages.get(item).background;
    }

    /**
     * Returns an array of reward events for a specific position within the recycler view
     * called from the adapter
     * @param item
     * @return
     */
    public ArrayList<StatePageRewardEvent> getRewardEvents(int item) {
        return state.getRewardEvents(item);
    }

    /**
     * Returns the Float position of the player if the called position
     * is equal to the position the player is currently in, null otherwise
     * @param item
     * @return
     */
    public Float getPlayer(int item) {
        return state.getPlayerPosition(item);
    }

    /**
     * Returns an array of overlapping reward events, i.e. events that are positioned too high
     * or too low in a neighboring pages, and which need to be displayed on another page as
     * well, returns absolute positions within StatePage.StatePageRewardEvent
     * @param item
     * @return
     */
    public ArrayList<StatePageRewardEvent> getOverlappingRewardEvents(int item) {
        return state.getOverlappingRewardEvents(item);
    }

    /**
     * Same as getOverlappingRewardEvents, returns the absolute position of the player
     * in the passed in position, null if no player should be displayed in the page
     * @param item
     * @return
     */
    public Position getOverlappingPlayerPosition(int item) {
        return state.getOverlappingPlayerPosition(item);
    }

    /**
     * State accessor
     * @return
     */
    public State getState() {
        return state;
    }



}
