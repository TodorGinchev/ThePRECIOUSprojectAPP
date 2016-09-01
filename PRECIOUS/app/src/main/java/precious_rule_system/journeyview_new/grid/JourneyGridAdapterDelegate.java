package precious_rule_system.journeyview_new.grid;

import java.util.ArrayList;

import precious_rule_system.journeyview_new.assets.JourneyAssets;
import precious_rule_system.rewardsystem.entities.RewardEvent;
import rules.helpers.Tuple;

/**
 * Created by christopher on 01.09.16.
 */

public interface JourneyGridAdapterDelegate {
    public int getNumberOfItems();
    public ArrayList<Tuple<RewardEvent, Float>> getEvents(int item);
    public ArrayList<Tuple<JourneyAssets.Size, double[]>> getAssets(int item);
    public float getPlayer(int item);
}
