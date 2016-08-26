package precious_rule_system.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

import rules.types.RuleTypes;

/**
 * Created by christopher on 11.08.16.
 */

public class AlarmReceiver extends BroadcastReceiver {

    public static final String ACTION_SCHEDULE = "precious_rule_system.scheduler.action.SCHEDULE";
    String TAG = "Rules.AlarmReceiver";

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(("android.intent.action.BOOT_COMPLETED"))) {
            reset(context);
        } else if (intent.getAction().equals("android.intent.action.MY_PACKAGE_REPLACED")) {
            this.startService(context, RuleTypes.TriggerKeys.APP_WAS_UPDATED);
        } else if (intent.getAction().equals((ACTION_SCHEDULE))) {
            Log.i(TAG, "Alarm Received");

            // Send TIME_ALL Trigger
            this.startService(context, RuleTypes.TriggerKeys.TIME_ALL);


            // Find out the current hour of the day and send a trigger for it
            Calendar now = new GregorianCalendar();
            int currentHour = now.get(Calendar.HOUR_OF_DAY);
            String currentHourStr = String.valueOf(currentHour);


            if (currentHour < 10 && currentHourStr.length() == 1) {
                currentHourStr = "0" + currentHourStr;
            }


            RuleTypes.TriggerKeys keyTime = RuleTypes.TriggerKeys.fromString("$time_" + currentHourStr);

            if (keyTime == null) {
                Log.i(TAG,"Invalid Trigger Key??");
                return;
            }
            // Trigger a key for current hour
            this.startService(context, keyTime);




            //Find out the current test time
            int testHour = TestTime.getHour();
            String testHourStr = String.valueOf(testHour);

            if (testHour < 10 && testHourStr.length() == 1) {
                testHourStr = "0" + testHourStr;
            }

            //Find the right trigger key
            RuleTypes.TriggerKeys keyTestTime = RuleTypes.TriggerKeys.fromString("$test_time_"+ testHourStr);


            if (keyTestTime == null) {
                Log.i(TAG,"invalid TestTrigger Key ");
                return;
            }

           // send the trigger
            this.startService(context, keyTestTime);

        }
    }

    private void startService(Context context, RuleTypes.TriggerKeys key) {
        Intent schedulerIntent = new Intent(context, SchedulerIntentService.class);
            schedulerIntent.setAction(key.toString());
        context.startService(schedulerIntent);
    }

    public void resetAlarm (Context context) {
        reset(context);
    }

    private void reset(Context context) {

        Log.i(TAG,"AlarmReceiver Resetting Alarm");
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(ACTION_SCHEDULE);
        final int SECOND = 1000;

        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // Setup first firing of periodic alarm instantaneously
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
              60*SECOND  , pIntent);
    }
}