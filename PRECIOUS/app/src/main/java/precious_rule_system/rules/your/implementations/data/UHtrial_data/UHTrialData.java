package precious_rule_system.rules.your.implementations.data.UHtrial_data;

import android.content.SharedPreferences;
import android.util.Log;

import junit.framework.Test;

import precious_rule_system.precoiusinterface.PreciousApplicationData;
import precious_rule_system.scheduler.TestTime;
import rules.data.Data;
import rules.helpers.Helpers;
import rules.types.RuleTypes;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;

/**
 * Created by khatt on 8/21/2016.
 */
public class UHTrialData {
    private final static String TAG = "UH Trial Data";
    private final static int idOffset = 9000;
    private final static String UH_Trial_Prefs = "UHTrialPreferences";


    public static Data[] getData(RuleTypes.DataKeys dataType) {
        switch (dataType) {
            case UH_PARTICIPANT_ID:
                return Helpers.wrapData(PreciousApplicationData.getGroupID() - idOffset);
            case UH_INTERVENTION_STATE:
                SharedPreferences preferences = PRECIOUS_APP.getAppContext().getSharedPreferences(UH_Trial_Prefs, 0);
                return Helpers.wrapData(preferences.getInt("current_Intervention_state",-1));
          //Todo: Set to actual application call after testing
            case UH_TRIAL_CURRENT_DAY:
//                return Helpers.wrapData(PreciousApplicationData.getDaysSinceRegistation() - trialStartOffset);
                return Helpers.wrapData(TestTime.getInstance().getDay());
            default:
                break;
        }
        return null;
    }

    public static void setData(RuleTypes.DataKeys key, int value) {
        SharedPreferences preferences = PRECIOUS_APP.getAppContext().getSharedPreferences(UH_Trial_Prefs, 0);
        SharedPreferences.Editor editor = preferences.edit();

        switch (key) {
            case UH_INTERVENTION_STATE:
                editor.putInt("current_Intervention_state",value);
                editor.apply();
                Log.i(TAG, "current_Intervention_state set to -> " +value);
                break;
        }
    }
}
