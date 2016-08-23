package precious_rule_system.rules.your.implementations.data.UHtrial_data;

import java.util.Calendar;
import java.util.GregorianCalendar;

import rules.data.Data;
import rules.helpers.Helpers;
import rules.types.RuleTypes;

/**
 * Created by khatt on 8/21/2016.
 */
public class UHTrialData {
    private static String TAG = "Trial Data";

    public static Data[] getData(RuleTypes.DataKeys dataType) {
        switch (dataType) {
            case UH_PARTICIPANT_ID:
                return Helpers.wrapData(1);
            case UH_INTERVENTION_STATE:
                return Helpers.wrapData(1);
            case UH_TRIAL_START_DATE:
                Calendar c = new GregorianCalendar();
                return Helpers.wrapData(c.getTime());
            case UH_TRIAL_END_DATE:
            case TIME_SINCE_REGISTRATION_HOURS:
            case TIME_SINCE_REGISTRATION_DAYS:
            default:
                break;
        }
        return null;
    }
}
