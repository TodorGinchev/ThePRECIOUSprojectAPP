package activity_tracker.precious.comnet.aalto;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.util.Calendar;

import food_diary.precious.comnet.aalto.fd_FoodInputReminder;
import ui.precious.comnet.aalto.precious.uiUtils;
import uploader.precious.comnet.aalto.SendLog;

public class Autostart extends BroadcastReceiver
{
    public void onReceive(Context arg0, Intent arg1)
    {
        Log.i("autostart recognition", "yes");

        //Alarm manager for activity recognition
        AlarmManager alarmMgr_at;
        PendingIntent alarmIntent_at;
        alarmMgr_at = (AlarmManager)arg0.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(arg0, DetectionRequesterService.class );
        alarmIntent_at = PendingIntent.getService(arg0, 0, i, 0);
        alarmMgr_at.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
                6 * 60 * 1000, alarmIntent_at);//6 min interval

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



    }
}