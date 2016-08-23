package precious_rule_system.scheduler;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.precious.christopher.precious_rule_system.RulesApplication;

import rules.types.RuleTypes;

/**
 * Created by christopher on 11.08.16.
 */

public class SchedulerIntentService extends IntentService {

    private String TAG = "Rules.IntentService";

    public SchedulerIntentService() {
        super("SchedulerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            Log.i(TAG,"Intentservice handler: Action Received: "+ action);
            RuleTypes.TriggerKeys key = RuleTypes.TriggerKeys.fromString(action);
            if (key == null) {
                Log.i(TAG,"Invalid Trigger Key??");
                return;
            }
            handleActionSchedule(key);
        }
    }

    /**
     * Handle action Schedule in the provided background thread
     */
    private void handleActionSchedule(RuleTypes.TriggerKeys key) {
        Log.i(TAG,"Posting Event "+key.toString()+" to Rule System");
        RulesApplication.postEvent(key, null);
    }
}
