package precious_rule_system.rules.your.implementations.actions;

import android.util.Log;

import precious_rule_system.rules.your.implementations.actions.applications.ApplicationAction;
import precious_rule_system.rules.your.implementations.actions.notifications.NotificationAction;
import precious_rule_system.rules.your.implementations.actions.other.OtherAction;
import precious_rule_system.rules.your.implementations.actions.rewards.RewardActions;
import precious_rule_system.rules.your.implementations.actions.test.TestActions;
import precious_rule_system.rules.your.implementations.actions.uh_trial.UHTrialAction;

import rules.entities.actions.Action;
import rules.entities.actions.ActionParameter;
import rules.managers.action.ActionManagerInterface;

/**
 * Created by christopher on 11.08.16.
 */

public class ActionManager implements ActionManagerInterface {

    private final String TAG = "Rules.ActionManager";

    public void handleAction(Action action) throws Exception {

        Log.d(TAG, "New Action committed: " + action.getKey().toString());
        for (ActionParameter p : action.getParameters()) {
            Log.d(TAG, "Parameter: " + p.getKey() + ":" + p.getValue());
        }

        switch (action.getKey()) {
            case SHOW_NOTIFICATION:
                NotificationAction.handleNotifications(action.getParameters());
                break;
            case ENABLE_APPLICATION:
                ApplicationAction.handleApplicationEnabling(action.getParameters());
                break;
            case DISABLE_APPLICATION:
                ApplicationAction.handleApplicationDisabling(action.getParameters());
                break;
            case OPEN_APPLICATION:
                ApplicationAction.handleApplicationOpen(action.getParameters());
                break;
            case CLOSE_APPLICATION:
                ApplicationAction.handleApplicationClose(action.getParameters());
                break;
            case SET_UH_INTERVENTION_STATE:
                UHTrialAction.handleSetInterventionState(action.getParameters());
                break;
            case CALL_TRIGGER:
                OtherAction.handleCallTrigger(action.getParameters());
                break;
            case SET_SUGGESTED_APP:
                ApplicationAction.handleSetSuggestedApp(action.getParameters());
                break;
            case OPEN_APP_STREAM:
                ApplicationAction.handleOpenAppStream(action.getParameters());
                break;
            case REWARD_POINTS:
                RewardActions.handleRewardPoints(action.getParameters());
                break;
            case SET_TEST_INTERVENTION:
                Log.i(TAG," Setting Test Intervention");
                TestActions.setTestIntervention((int) action.getParameters()[0].getValue());
                break;
        }
    }
}
