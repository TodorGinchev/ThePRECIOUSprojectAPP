package aalto.comnet.thepreciousviewer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class autostart extends BroadcastReceiver 
{
    public void onReceive(Context arg0, Intent arg1) 
    {
    	Log.i("autostart thepreciousviewer SendLog","yes");
    	// Send data every 30 minutes
    	AlarmManager alarmMgr;
    	PendingIntent alarmIntent;
    	alarmMgr = (AlarmManager)arg0.getSystemService(Context.ALARM_SERVICE);
    	Intent intent = new Intent(arg0, SendLog.class);
    	alarmIntent = PendingIntent.getBroadcast(arg0, 0, intent, 0);
    	alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
                AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
    			//30*1000, alarmIntent);
    }
}