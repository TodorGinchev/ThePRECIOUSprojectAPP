package aalto.comnet.thepreciouspedometer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class autostart extends BroadcastReceiver 
{
    public void onReceive(Context arg0, Intent arg1) 
    {
    	Log.i("autostart thepreciouspedometer.MainActivity","yes");
    	Intent i = new Intent(arg0,aalto.comnet.thepreciouspedometer.MainActivity.class);
    	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	arg0.startActivity(i);

    }
}