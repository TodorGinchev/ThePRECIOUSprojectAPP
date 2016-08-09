package activity_tracker.precious.comnet.aalto;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.util.Calendar;

import diet_challenges.precious.comnet.aalto.fi.dc_Reminder;
import food_diary.precious.comnet.aalto.fd_FoodInputReminder;
import ui.precious.comnet.aalto.precious.uiUtils;
import uploader.precious.comnet.aalto.SendLog;

public class Autostart extends BroadcastReceiver
{
    public void onReceive(Context arg0, Intent arg1)
    {
        Log.i("autostart all services", "yes");

        //Alarm manager for activity recognition
        AlarmManager alarmMgr_at;
        PendingIntent alarmIntent_at;
        alarmMgr_at = (AlarmManager)arg0.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(arg0, DetectionRequesterService.class );
        alarmIntent_at = PendingIntent.getService(arg0, 0, i, 0);
        alarmMgr_at.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
                6 * 60 * 1000, alarmIntent_at);//6 min interval

        //Alarm manager for PRECIOUS wearable
        AlarmManager alarmMgr_at1;
        PendingIntent alarmIntent_at1;
        alarmMgr_at1 = (AlarmManager)arg0.getSystemService(Context.ALARM_SERVICE);
        Intent i1 = new Intent(arg0, wearable.precious.comnet.aalto.BackgroundService.class );
        alarmIntent_at1 = PendingIntent.getService(arg0, 0, i1, 0);
        alarmMgr_at1.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                325 * 1000, alarmIntent_at1);//5min and 25s interval

        //Alarm manager for server upload
        AlarmManager alarmMgr_at2;
        PendingIntent alarmIntent_at2;
        alarmMgr_at2 = (AlarmManager)arg0.getSystemService(Context.ALARM_SERVICE);
        Intent i2 = new Intent(arg0, SendLog.class );
        alarmIntent_at2 = PendingIntent.getService(arg0, 0, i2, 0);
        alarmMgr_at2.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
                3600 * 1000, alarmIntent_at2);//1 h interval

        //Alarm manager for food intake reminder
        //Breakfast
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, uiUtils.BREAKFAST_HOUR_REMINDER); // For 11am
        AlarmManager alarmMgr_at3;
        PendingIntent alarmIntent_at3;
        alarmMgr_at3 = (AlarmManager)arg0.getSystemService(Context.ALARM_SERVICE);
        Intent i3 = new Intent(arg0, fd_FoodInputReminder.class );
        alarmIntent_at3 = PendingIntent.getService(arg0, 0, i3, 0);
        alarmMgr_at3.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent_at3);//Every day
        //Lunch
        calendar.set(Calendar.HOUR_OF_DAY, uiUtils.LUNCH_HOUR_REMINDER); // For 15:00 0'clock
        AlarmManager alarmMgr_at4;
        PendingIntent alarmIntent_at4;
        alarmMgr_at4 = (AlarmManager)arg0.getSystemService(Context.ALARM_SERVICE);
        Intent i4 = new Intent(arg0, fd_FoodInputReminder.class );
        alarmIntent_at4 = PendingIntent.getService(arg0, 0, i4, 0);
        alarmMgr_at4.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent_at4);//Every day
        //Dinner
        calendar.set(Calendar.HOUR_OF_DAY, uiUtils.DINNER_HOUR_REMINDER); // For 21:00 0'clock
        AlarmManager alarmMgr_at5;
        PendingIntent alarmIntent_at5;
        alarmMgr_at5 = (AlarmManager)arg0.getSystemService(Context.ALARM_SERVICE);
        Intent i5 = new Intent(arg0, fd_FoodInputReminder.class );
        alarmIntent_at5 = PendingIntent.getService(arg0, 0, i5, 0);
        alarmMgr_at5.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent_at5);//Every day



        //For goal setting reminder
        calendar.set(Calendar.HOUR_OF_DAY, uiUtils.PA_GOAL_SETTING_REMINDER); // For 21:00 0'clock
        AlarmManager alarmMgr_at6;
        PendingIntent alarmIntent_at6;
        alarmMgr_at6 = (AlarmManager)arg0.getSystemService(Context.ALARM_SERVICE);

        Intent i6 = new Intent(arg0, activity_tracker.precious.comnet.aalto.atGoalSettingReminder.class );
        alarmIntent_at6 = PendingIntent.getService(arg0, 0, i6, 0);
        alarmMgr_at6.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent_at6);//Every day


        //For diet challenge reminder
        calendar.set(Calendar.HOUR_OF_DAY, uiUtils.FRUIT_REMINDER); // For 13:00 0'clock
        AlarmManager alarmMgr_at7;
        PendingIntent alarmIntent_at7;
        alarmMgr_at7 = (AlarmManager)arg0.getSystemService(Context.ALARM_SERVICE);
        Intent i7 = new Intent(arg0, dc_Reminder.class );
        alarmIntent_at7 = PendingIntent.getService(arg0, 0, i7, 0);
        alarmMgr_at7.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent_at7);//Every day


    }
}