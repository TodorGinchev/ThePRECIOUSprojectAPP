package ui.precious.comnet.aalto.precious;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.util.Calendar;

import food_diary.precious.comnet.aalto.fd_FoodInputReminder;

public class uiUtils {

    /**
     * Startup configuration: get user personal data, create local files, get user ID
     */


    public static final int BREAKFAST_HOUR_REMINDER = 11;
    public static final int LUNCH_HOUR_REMINDER = 15;
    public static final int DINNER_HOUR_REMINDER = 21;
    public static final int PA_GOAL_SETTING_REMINDER = 13;


    public static void firstStartConfig(Context context){

        Log.i("firstStartConfig", "Starting DetectionRequesterService");
        Intent i = new Intent(context, activity_tracker.precious.comnet.aalto.DetectionRequesterService.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(i);

        //For activity recognition
        AlarmManager alarmMgr_at;
        PendingIntent alarmIntent_at;
        alarmMgr_at = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent_at = new Intent(context, activity_tracker.precious.comnet.aalto.DetectionRequesterService.class );
        alarmIntent_at = PendingIntent.getService(context, 0, intent_at, 0);
        alarmMgr_at.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                6 * 60 * 1000, alarmIntent_at);//6 min interval


        //For sending log
        AlarmManager alarmMgr_at2;
        PendingIntent alarmIntent_at2;
        alarmMgr_at2 = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent i2 = new Intent(context, uploader.precious.comnet.aalto.SendLog.class );
        alarmIntent_at2 = PendingIntent.getService(context, 0, i2, 0);
        alarmMgr_at2.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
                3600 * 1000, alarmIntent_at2);//1 h interval
        context.startService(i2);


        //Alarm manager for food intake reminder
        //Breakfast
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, BREAKFAST_HOUR_REMINDER); // For 11am
        AlarmManager alarmMgr_at3;
        PendingIntent alarmIntent_at3;
        alarmMgr_at3 = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i3 = new Intent(context, fd_FoodInputReminder.class );
        alarmIntent_at3 = PendingIntent.getService(context, 0, i3, 0);
        alarmMgr_at3.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent_at3);//Every day
        //Lunch
        calendar.set(Calendar.HOUR_OF_DAY, LUNCH_HOUR_REMINDER); // For 15:00 0'clock
        AlarmManager alarmMgr_at4;
        PendingIntent alarmIntent_at4;
        alarmMgr_at4 = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i4 = new Intent(context, fd_FoodInputReminder.class );
        alarmIntent_at4 = PendingIntent.getService(context, 0, i4, 0);
        alarmMgr_at4.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent_at4);//Every day
        //Dinner
        calendar.set(Calendar.HOUR_OF_DAY, DINNER_HOUR_REMINDER); // For 21:00 0'clock
        AlarmManager alarmMgr_at5;
        PendingIntent alarmIntent_at5;
        alarmMgr_at5 = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i5 = new Intent(context, fd_FoodInputReminder.class );
        alarmIntent_at5 = PendingIntent.getService(context, 0, i5, 0);
        alarmMgr_at5.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent_at5);//Every day



        //For goal setting reminder
        calendar.set(Calendar.HOUR_OF_DAY, PA_GOAL_SETTING_REMINDER); // For 13:00 0'clock
        AlarmManager alarmMgr_at6;
        PendingIntent alarmIntent_at6;
        alarmMgr_at6 = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i6 = new Intent(context, activity_tracker.precious.comnet.aalto.atGoalSettingReminder.class );
        alarmIntent_at6 = PendingIntent.getService(context, 0, i6, 0);
        alarmMgr_at6.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent_at6);//Every day






        // Send data every 1/2 hour
//        AlarmManager alarmMgr;
//        PendingIntent alarmIntent;
//        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, SendLog.class );
//        alarmIntent = PendingIntent.getService(context, 0, intent, 0);
//        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
//                AlarmManager.INTERVAL_HOUR, alarmIntent);
        //21*1000, alarmIntent); //TODO

        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        SharedPreferences prefs =context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
//        SharedPreferences.Editor edit = prefs.edit();
//        edit.putString("previous_version", ui_MainActivity.AppVersion);
//        edit.commit();
        //TODO
        //TODO
        //TODO
//        try{
//            File file = new File(getFilesDir(), "server.txt");
//            file.createNewFile();
//        }catch (Exception e){
//            Log.e("upUtils", "Error creating new files", e);
//        }
//        //Create user ID
//        String model = Build.MODEL;
//        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        String imei = telephonyManager.getDeviceId();
//        imei = imei.substring(imei.length()-5);
//        String userID =model.concat(" ").concat(imei);
//        edit.putString("user_id", userID);
//        edit.commit();
//        //Ask to open user configuration
//        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
//        builder1.setMessage(getString(R.string.first_start));
//        builder1.setCancelable(true);
//        builder1.setPositiveButton(getString(R.string.ok),
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        openConfiguration();
//                        dialog.cancel();
//                    }
//                });
//        builder1.setNegativeButton(getString(R.string.later),
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog alert1 = builder1.create();
//        alert1.show();
    }
}

