package precious_rule_system.rules.your.implementations.actions.other;

import android.util.Log;

import com.precious.christopher.precious_rule_system.RulesApplication;

import rules.entities.actions.ActionParameter;
import rules.types.RuleTypes;

/**
 * Created by christopher on 12.08.16.
 */

public class OtherAction {

    private static final String TAG = "Rules.OtherAction";

    public static void handleCallTrigger(ActionParameter[] parameters) {

        if (parameters.length == 0) {
            Log.d(TAG, RuleTypes.ActionKeys.CALL_TRIGGER.toString() + "key called with no parameters");
            return;
        }

        String key = parameters[0].getKey();
        Object value = parameters[0].getValue();

        if (!key.equals("trigger")) {
            Log.d(TAG, RuleTypes.ActionKeys.CALL_TRIGGER.toString() + "key with no parameter including a key 'trigger'");
            return;
        }

        if (!(value instanceof String)) {
            Log.d(TAG, RuleTypes.ActionKeys.CALL_TRIGGER.toString() + "key trigger parameter does not contain a valid trigger key");
            return;
        }

        String strV = (String) value;

        RuleTypes.TriggerKeys tK = RuleTypes.TriggerKeys.fromString(strV);
        RuleTypes.DataKeys dK = RuleTypes.DataKeys.fromString(strV);

        if (tK == null && dK == null) {
            Log.d(TAG, RuleTypes.ActionKeys.CALL_TRIGGER.toString() + "key trigger parameter does not contain a valid trigger key");
            return;
        }

        RuleTypes.Key eventKey = null;
        if (tK != null) {
            eventKey = tK;
        } else {
            eventKey = dK;
        }

        Log.d(TAG, RuleTypes.ActionKeys.CALL_TRIGGER.toString() + "successfully called");
        RulesApplication.postEvent(eventKey, null);

    }

}
