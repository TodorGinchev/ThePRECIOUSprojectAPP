package precious_rule_system.journeyview.data;


import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

import precious_rule_system.journeyview.JourneyActivity;
import precious_rule_system.journeyview.assets.Assets;
import precious_rule_system.journeyview.constants.Constants;
import precious_rule_system.journeyview.helpers.AssetBackgroundGenerator;
import precious_rule_system.journeyview.helpers.Position;
import precious_rule_system.journeyview.recycler.RecyclerView;
import precious_rule_system.rewardsystem.entities.RewardEvent;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;

/**
 * Created by christopher on 04.09.16.
 */

public class DataManager {

    private State state;
    int dummyCount = 0;

    public DataManager(JourneyActivity.JourneyStore store) {

        state = new State(store);

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
                if (dummyCount < 1) handler.postDelayed(this, 10000);
            }
        };

        handler.postDelayed(r, 5000);
    }

    public void updateDimensions(int width, int height) {
        state.updateDimensions(width, height);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        state.setAdapter(adapter);
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        state.setRecyclerView(recyclerView);
    }

    public int getPageCount() {
        return state.pages.size();
    }

    public AssetBackgroundGenerator.AssetBackground getBackground(int item) {
        return state.pages.get(item).background;
    }

    public ArrayList<StatePage.StatePageRewardEvent> getRewardEvents(int item) {
        return state.getRewardEvents(item);
    }

    public Float getPlayer(int item) {
        return state.getPlayerPosition(item);
    }

    public ArrayList<StatePage.StatePageRewardEvent> getOverlappingRewardEvents(int item) {
        return state.getOverlappingRewardEvents(item);
    }

    public Position getOverlappingPlayerPosition(int item) {
        return state.getOverlappingPlayerPosition(item);
    }

    public State getState() {
        return state;
    }


}
