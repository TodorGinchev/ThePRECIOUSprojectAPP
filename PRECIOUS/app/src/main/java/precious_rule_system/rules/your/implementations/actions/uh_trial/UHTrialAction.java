package precious_rule_system.rules.your.implementations.actions.uh_trial;

import precious_rule_system.rules.your.implementations.data.UHtrial_data.UHTrialData;
import precious_rule_system.rules.your.implementations.data.test_data.TestData;
import rules.entities.actions.ActionParameter;
import rules.types.RuleTypes;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;

/**
 * Created by christopher on 12.08.16.
 */



public class UHTrialAction {

    private final String TAG = " Test Actions";

    public static void handleSetInterventionState(int interventionState) {

        RuleTypes.TriggerKeys key = null;
        UHTrialData.setData(RuleTypes.DataKeys.UH_INTERVENTION_STATE, interventionState);

        switch (interventionState) {
            case 1:
                key = RuleTypes.TriggerKeys.UH_INTERVENTION_1;
                break;
            case 2:
                key = RuleTypes.TriggerKeys.UH_INTERVENTION_2;
                break;
            case 3:
                key = RuleTypes.TriggerKeys.UH_INTERVENTION_3;
                break;
            case 4:
                key = RuleTypes.TriggerKeys.UH_INTERVENTION_4;
                break;
            case 5:
                key = RuleTypes.TriggerKeys.UH_INTERVENTION_5;
                break;
            case 6:
                key = RuleTypes.TriggerKeys.UH_INTERVENTION_6;
                break;
            case 7:
                key = RuleTypes.TriggerKeys.UH_INTERVENTION_7;
                break;
            case 8:
                key = RuleTypes.TriggerKeys.UH_INTERVENTION_8;
                break;
            case 9:
                key = RuleTypes.TriggerKeys.UH_INTERVENTION_9;
                break;
            case 10:
                key = RuleTypes.TriggerKeys.UH_INTERVENTION_10;
                break;
            case 0:
            default:
                key = RuleTypes.TriggerKeys.UH_INTERVENTION_0;
                break;
        }
        PRECIOUS_APP.postEvent(key, null);
    }
}
