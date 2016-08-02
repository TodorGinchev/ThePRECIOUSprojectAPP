package wearable.precious.comnet.aalto;


import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class CheckIfWearableServiceRunning extends Service {

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int idArranque) {
//        if(!isMyServiceRunning(wearable.precious.comnet.aalto.BackgroundService.class)){
//            Intent backgroundService = new Intent(this,BackgroundService.class);
//            startService(backgroundService);
//        }

        onDestroy();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @Override
    public IBinder onBind(Intent intencion) {
        return null;
    }

}
