package ui.precious.comnet.aalto.precious;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

public class uiUtils {

    /**
     * Startup configuration: get user personal data, create local files, get user ID
     */

//    public static final int BREAKFAST_HOUR_REMINDER = 11;
//    public static final int LUNCH_HOUR_REMINDER = 15;
//    public static final int DINNER_HOUR_REMINDER = 21;
//    public static final int PA_GOAL_SETTING_REMINDER = 13;
//    public static final int FRUIT_REMINDER = 14;
    public static final String TAG = "uiUtils";

    public static void firstStartConfig(){

        Context context = PRECIOUS_APP.getAppContext();

        Log.i("firstStartConfig", "Starting DetectionRequesterService");
        Intent i = new Intent(context, activity_tracker.precious.comnet.aalto.DetectionRequesterService.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(i);

        //For activity recognition
        AlarmManager alarmMgr_at;
        PendingIntent alarmIntent_at;
        alarmMgr_at = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent_at = new Intent(context, activity_tracker.precious.comnet.aalto.DetectionRequesterService.class );
        alarmIntent_at = PendingIntent.getService(context, 3301, intent_at, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr_at.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                6 * 60 * 1000, alarmIntent_at);//6 min interval

        //For sending log
        AlarmManager alarmMgr_at2;
        PendingIntent alarmIntent_at2;
        alarmMgr_at2 = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i2 = new Intent(context, uploader.precious.comnet.aalto.SendLog.class );
        alarmIntent_at2 = PendingIntent.getService(context, 3302, i2, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr_at2.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
                3600 * 1000, alarmIntent_at2);//1 h interval
        context.startService(i2);

        //Alarm manager for PRECIOUS wearable
        if (Build.VERSION.SDK_INT >= 21) {
            AlarmManager alarmMgr_at3;
            PendingIntent alarmIntent_at3;
            alarmMgr_at3 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i3 = new Intent(context, wearable.precious.comnet.aalto.BackgroundService.class);
            alarmIntent_at3 = PendingIntent.getService(context, 3303, i3, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmMgr_at3.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    925 * 1000, alarmIntent_at3);//5min and 25s interval
        }

//        //Alarm manager for server upload
//        AlarmManager alarmMgr_at4;
//        PendingIntent alarmIntent_at4;
//        alarmMgr_at4 = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        Intent i4 = new Intent(context, SendLog.class );
//        alarmIntent_at4 = PendingIntent.getService(context, 0, i4, 0);
//        alarmMgr_at4.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
//                3600 * 1000, alarmIntent_at4);//1 h interval
    }

    /**
     *
     */
    public static void CheckIfAlarmAlive(){
        Context context = PRECIOUS_APP.getAppContext();

        //For activity recognition
        Intent i1 = new Intent(context, activity_tracker.precious.comnet.aalto.DetectionRequesterService.class );
        boolean alarmUp = (PendingIntent.getService(context, 3301,
                i1,
                PendingIntent.FLAG_NO_CREATE) != null);
        if (alarmUp){
            Log.i(TAG,"DetectionRequesterService ALARM is up");
        }
        else{
            Log.i(TAG,"DetectionRequesterService ALARM is NOT up, restarting...");
            AlarmManager alarmMgr_at;
            PendingIntent alarmIntent_at;
            alarmMgr_at = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarmIntent_at = PendingIntent.getService(context, 3301, i1, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmMgr_at.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    6 * 60 * 1000, alarmIntent_at);//6 min interval
        }
        //For sending log
        Intent i2 = new Intent(context, uploader.precious.comnet.aalto.SendLog.class );
        alarmUp = (PendingIntent.getService(context, 3302,
                i2,
                PendingIntent.FLAG_NO_CREATE) != null);
        if (alarmUp){
            Log.i(TAG,"SendLog ALARM is up");
        }
        else{
            Log.i(TAG,"SendLog ALARM is NOT up, restarting...");
            AlarmManager alarmMgr_at2;
            PendingIntent alarmIntent_at2;
            alarmMgr_at2 = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarmIntent_at2 = PendingIntent.getService(context, 3302, i2, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmMgr_at2.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
                    3600 * 1000, alarmIntent_at2);//1 h interval
            context.startService(i2);
        }
        //Alarm manager for PRECIOUS wearable
        Intent i3 = new Intent(context, wearable.precious.comnet.aalto.BackgroundService.class);
        alarmUp = (PendingIntent.getService(context, 3303,
                i3,
                PendingIntent.FLAG_NO_CREATE) != null);
        if (alarmUp){
            Log.i(TAG,"Wearable BackgroundService ALARM is up");
        }
        else{
            if (Build.VERSION.SDK_INT >= 21) {
                Log.i(TAG,"Wearable BackgroundService ALARM is NOT up, restarting...");
                AlarmManager alarmMgr_at3;
                PendingIntent alarmIntent_at3;
                alarmMgr_at3 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmIntent_at3 = PendingIntent.getService(context, 3303, i3, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmMgr_at3.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                        325 * 1000, alarmIntent_at3);//5min and 25s interval
            }
        }
    }
}

