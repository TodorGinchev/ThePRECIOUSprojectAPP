package aalto.comnet.thepreciouslocationservice;

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
    	Log.i("autostart LocationService","yes");
    	// Check GPS every 30 minutes
    	AlarmManager alarmMgr;
    	PendingIntent alarmIntent;
    	alarmMgr = (AlarmManager)arg0.getSystemService(Context.ALARM_SERVICE);
    	Intent i = new Intent(arg0,aalto.comnet.thepreciouslocationservice.LocationService.class);
    	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	alarmIntent = PendingIntent.getBroadcast(arg0, 0, i, 0);
    	alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
    			1000*60, alarmIntent);
                //AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
    }
}
