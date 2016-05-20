package activity_tracker.precious.comnet.aalto;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class Autostart extends BroadcastReceiver
{
    public void onReceive(Context arg0, Intent arg1)
    {
        Log.i("autostart recognition", "yes");

        AlarmManager alarmMgr_at;
        PendingIntent alarmIntent_at;
        alarmMgr_at = (AlarmManager)arg0.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(arg0, activity_tracker.precious.comnet.aalto.DetectionRequesterService.class );
        alarmIntent_at = PendingIntent.getService(arg0, 0, i, 0);
        alarmMgr_at.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
                6 * 60 * 1000, alarmIntent_at);//6 min interval

        AlarmManager alarmMgr_at2;
        PendingIntent alarmIntent_at2;
        alarmMgr_at2 = (AlarmManager)arg0.getSystemService(Context.ALARM_SERVICE);
        Intent i2 = new Intent(arg0, uploader.precious.comnet.aalto.SendLog.class );
        alarmIntent_at2 = PendingIntent.getService(arg0, 0, i2, 0);
        alarmMgr_at2.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
                3600 * 1000, alarmIntent_at2);//1 h interval
    }
}