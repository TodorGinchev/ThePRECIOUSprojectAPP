package activity_tracker.precious.comnet.aalto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Autostart extends BroadcastReceiver
{
    public void onReceive(Context arg0, Intent arg1)
    {
        Log.i("autostart recognition", "yes");
        Intent i = new Intent(arg0,activity_tracker.precious.comnet.aalto.DetectionRequester.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        arg0.startActivity(i);
    }
}