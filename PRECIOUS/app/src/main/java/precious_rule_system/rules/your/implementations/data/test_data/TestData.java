package precious_rule_system.rules.your.implementations.data.test_data;

import rules.data.Data;
import rules.helpers.Helpers;
import rules.types.RuleTypes;

/**
 * Created by khatt on 8/22/2016.
 */
public class TestData {
    private  static String TAG = "Test Data";
    public static final String PREFS_NAME = "Test_Preferences";

    private static int testDays = 0;
    private static int testHours = 0;
    private static int interventionState = 0;
    private static int count = 0;

    public static Data[] getData(RuleTypes.DataKeys key) {
        switch (key)
        {
            case TEST_INTERVENTION_STATE:
                 return Helpers.wrapData(interventionState);
            case TEST_PARTICIPANT_ID:
                 return  Helpers.wrapData(2);
            case TEST_HOURS:
                update_time();
                 return Helpers.wrapData(testHours);
            case TEST_DAYS:
                 return Helpers.wrapData(testDays);
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

    public static void update_time() {
        TestData.testHours = count % 24;
        TestData.testDays = count / 24;
        TestData.testHours = TestData.testHours % 24;
        count++;
    }
}
