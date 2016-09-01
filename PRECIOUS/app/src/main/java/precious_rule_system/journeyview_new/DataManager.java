package precious_rule_system.journeyview_new;

import java.util.ArrayList;

import precious_rule_system.journeyview_new.assets.JourneyAssets;
import precious_rule_system.journeyview_new.grid.JourneyGridAdapterDelegate;
import precious_rule_system.journeyview_new.utilities.Layout;
import precious_rule_system.rewardsystem.entities.RewardEvent;
import rules.helpers.Tuple;

/**
 * Created by christopher on 01.09.16.
 */

public class DataManager implements JourneyGridAdapterDelegate {

    JourneyAssets assets;
    Layout layout;

    public DataManager(JourneyAssets assets, Layout layout) {
        this.assets = assets;
        this.layout = layout;
    }

    public int getNumberOfItems() {
        return 9;
    }

    public ArrayList<Tuple<RewardEvent, Float>> getEvents(int item) {
        ArrayList<Tuple<RewardEvent, Float>> events = new ArrayList<>();
        for(float p=0.0f;p<=1.09f;p+=0.1f) {
            if (Math.random() > 0.5) {
                events.add(new Tuple<RewardEvent, Float>(RewardEvent.getMilestone("a","b",10,0), p));
            } else {
                events.add(new Tuple<RewardEvent, Float>(RewardEvent.getEvent("c",10,0), p));
            }
        }
        return events;
    }

    public ArrayList<Tuple<JourneyAssets.Size, double[]>> getAssets(int item) {
        return layout.calculateRandomSizes(assets.assetSizes,assets.path.offset,assets);
    }

    public float getPlayer(int item) {
        return 0.65f;
    }

    public int getPlayerItem() {
        return 0;
        // last is the oldest
    }

    public String getLabelForItem(int item) {
        return "The Mountains";
    }

    public int getNumberOfPoints() {
        return 1000;
    }


}
