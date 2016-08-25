package precious_rule_system.rules.your.implementations.data.test_data;

import rules.data.Data;
import rules.helpers.Helpers;
import precious_rule_system.scheduler.TestTime;
import rules.types.RuleTypes;

/**
 * Created by khatt on 8/22/2016.
 */
public class TestData {
    private  static String TAG = "Test Data";
    public static final String PREFS_NAME = "Test_Preferences";
    private static int interventionState = 0;

    public static Data[] getData(RuleTypes.DataKeys key) {
        switch (key)
        {
            case TEST_INTERVENTION_STATE:
                 return Helpers.wrapData(interventionState);
            case TEST_PARTICIPANT_ID:
                 return  Helpers.wrapData(2);
            case TEST_HOURS:
                 return Helpers.wrapData(TestTime.getHour());
            case TEST_DAYS:
                 return Helpers.wrapData(TestTime.getDay());
            default:
                 return Helpers.wrapData(null);
            }
        }

    public static void setData(RuleTypes.DataKeys key, int value) {
        switch (key)
        {
            case TEST_INTERVENTION_STATE:
                interventionState = value;
                break;
        }
    }
}
