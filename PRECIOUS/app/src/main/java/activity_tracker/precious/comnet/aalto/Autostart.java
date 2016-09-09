package activity_tracker.precious.comnet.aalto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ui.precious.comnet.aalto.precious.uiUtils;

public class Autostart extends BroadcastReceiver
{
    public void onReceive(Context arg0, Intent arg1)
    {
        Log.i("autostart all services", "yes");
        uiUtils.firstStartConfig();
    }
}