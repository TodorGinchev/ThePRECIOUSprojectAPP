package precious_rule_system.rules.your.implementations.actions.rewards;

import android.util.Log;

import com.precious.christopher.precious_rule_system.RulesApplication;

import rules.entities.actions.ActionParameter;

/**
 * Created by christopher on 14.08.16.
 */

public class RewardActions {

    private static final String TAG = "Rules.RewardActions";

    public static void handleRewardPoints(ActionParameter[] parameters) {

        int points = 0;

        for (ActionParameter p : parameters) {
            if (p.getKey().equals("points")) {
                points = (Integer) p.getValue();
            }
        }

        if (points > 0) {
            Log.d(TAG, "Posting " + points + " points to the reward system");
            RulesApplication.getRewardSystem().postPointIncrease(points);
        }
    }
}
