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
    private final static int trial_duration = 50;
    private final static String UH_Trial_Prefs = "UHTrialPreferences";
    private static SharedPreferences uhTrialPreferences = PRECIOUS_APP.getAppContext().getSharedPreferences(UH_Trial_Prefs, 0);

    public static Data[] getData(RuleTypes.DataKeys dataType) {
        switch (dataType) {
            case UH_PARTICIPANT_ID:
                return Helpers.wrapData(PreciousApplicationData.getGroupID() - idOffset);
            case UH_INTERVENTION_STATE:
                int state = uhTrialPreferences.getInt("current_intervention_state",-1);
                return Helpers.wrapData(state);
          //Todo: Set to actual application call after testing
            case UH_TRIAL_CURRENT_DAY:
                //int today = TestTime.getInstance().getDay();
                int today = PreciousApplicationData.getDaysSinceRegistation();
                Log.i(TAG, "today is trial day => : " + today);
                if (today > trial_duration)
                    return Helpers.wrapData(trial_duration);
                return Helpers.wrapData(today);
            case UH_TRIAL_YESTERDAY:
                //int yesterday = TestTime.getInstance().getDay()-1;
                int yesterday = PreciousApplicationData.getDaysSinceRegistation()-1;
                Log.i(TAG, "yesterday was trial day => : " + yesterday);
                if ((yesterday < 0) || (yesterday > trial_duration))
                    return Helpers.wrapData(0);
                else
                return Helpers.wrapData(yesterday);
            case UH_TRIAL_TOMORROW:
                int tomorrow = PreciousApplicationData.getDaysSinceRegistation()+1;
                Log.i(TAG, "tomorrow trial day => : " + tomorrow);
                if (tomorrow > trial_duration)
                    return Helpers.wrapData(0);
                else
                return Helpers.wrapData(tomorrow);
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
                editor.putInt("current_intervention_state",value);
                editor.apply();
                Log.i(TAG, "current_intervention_state set to -> " +value);
                break;
        }
    }
}
