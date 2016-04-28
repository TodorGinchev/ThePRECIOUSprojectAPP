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
//        Intent i = new Intent(arg0,activity_tracker.precious.comnet.aalto.DetectionRequester.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        arg0.startActivity(i);

        AlarmManager alarmMgr_at;
        PendingIntent alarmIntent_at;
        alarmMgr_at = (AlarmManager)arg0.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(arg0, activity_tracker.precious.comnet.aalto.DetectionRequester.class );
        alarmIntent_at = PendingIntent.getService(arg0, 0, i, 0);
        alarmMgr_at.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
                30 * 60 * 1000, alarmIntent_at);//30 min interval
    }
}