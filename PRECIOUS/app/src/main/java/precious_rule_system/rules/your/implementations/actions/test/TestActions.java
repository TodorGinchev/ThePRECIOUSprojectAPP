package precious_rule_system.rules.your.implementations.actions.test;

import precious_rule_system.rules.your.implementations.data.test_data.TestData;

import rules.types.RuleTypes;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;

/**
 * Created by khatt on 8/22/2016.
 */

public class TestActions {

    private static String TAG = " Test Actions";

    public static void setTestIntervention(int value) {
        RuleTypes.TriggerKeys key = null;
        TestData.setData(RuleTypes.DataKeys.TEST_INTERVENTION_STATE, value);

        switch (value) {
            case 1:
                key = RuleTypes.TriggerKeys.TEST_INTERVENTION_1;
                break;
            case 2:
                key = RuleTypes.TriggerKeys.TEST_INTERVENTION_2;
                break;
            case 3:
                key = RuleTypes.TriggerKeys.TEST_INTERVENTION_3;
                break;
            case 4:
                key = RuleTypes.TriggerKeys.TEST_INTERVENTION_4;
                break;
            }
        PRECIOUS_APP.postEvent(key, null);
        }
}
