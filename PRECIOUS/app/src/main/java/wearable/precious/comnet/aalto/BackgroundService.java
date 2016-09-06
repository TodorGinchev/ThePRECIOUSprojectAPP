package wearable.precious.comnet.aalto;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import aalto.comnet.thepreciousproject.R;
import wearable.precious.comnet.aalto.listeners.NotifyListener;

public class BackgroundService extends Service {

    private static final String TAG = "BackgroundService";
    public static final String WR_PREFS_NAME = "WRsubappPreferences";
    public static final int WEARABLE_REMINDER_NOTIF_ID = 151;
    private MiBand miband;
    public static Context mContext;
    private ScanCallback scanCallback;
    public BluetoothDevice BLEdevice;
    HashMap<String, BluetoothDevice> devices = new HashMap<String, BluetoothDevice>();

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= 21) {
            mContext = this;
            miband = new MiBand(this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int idArranque) {
        Log.i(TAG, "onStartCommand");
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                SharedPreferences preferences = BackgroundService.this.getSharedPreferences(WR_PREFS_NAME, 0);
                if (!preferences.getString("wearable_address", "-1").equals("-1")) {
                    Log.i(TAG, "device Object will be created, device is paired");
//                        findWearable();
                    final BluetoothManager bluetoothManager =
                            (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                    BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
                    BLEdevice = mBluetoothAdapter.getRemoteDevice(preferences.getString("wearable_address", "00:00:00:00:00:00"));
                    establishConnectionWithWearable(BLEdevice);
                } else {
                    Log.i(TAG, "No wearable device was paired");
                    stopService(new Intent(this, BackgroundService.class));
                }
            } catch (Exception e) {
                Log.e(TAG, " ", e);
            }
        }
        return START_NOT_STICKY;
    }

    public void establishConnectionWithWearable(BluetoothDevice device){
        Log.i(TAG,"Connecting...");
        miband.connect(device, new ActionCallback() {

            @Override
            public void onSuccess(Object data) {
//                pd.dismiss();
                Log.d(TAG, "Connected!!!");


//                miband.startVibration(VibrationMode.VIBRATION_WITH_LED);


//                miband.pair(new ActionCallback() {
//
//                    @Override
//                    public void onSuccess(Object data) {
//                        Log.d(TAG, "pair succ");
//                    }
//
//                    @Override
//                    public void onFail(int errorCode, String msg) {
//                        Log.d(TAG, "pair fail");
//                    }
//                });


                Log.i(TAG,"Sending steps counter request...");
                miband.getSteps(new ActionCallback() {

                    @Override
                    public void onSuccess(Object data) {
                        int steps = (int) data;
//                        int steps=-3;
                        Log.d(TAG, "Steps: "+steps);
//                        sendConnectionNotification(true,steps);
                        writeStingInExternalFile(steps+";"+System.currentTimeMillis()+";","wearable_steps.txt");
//                        MiBand.stopScan(scanCallback);
                        stopService(new Intent(mContext, BackgroundService.class));

                    }

                    @Override
                    public void onFail(int errorCode, String msg) {
                        Log.d(TAG, "getBatteryInfo fail");
                    }
                });

                miband.setDisconnectedListener(new NotifyListener() {
                    @Override
                    public void onNotify(byte[] data) {
                        Log.d(TAG, "Disconnected!!!");
//                        stopService(new Intent(mContext, BackgroundService.class));
                    }
                });

//                miband.setRealtimeStepsNotifyListener(new RealtimeStepsNotifyListener() {
//
//                    @Override
//                    public void onNotify(int steps) {
//                        Log.d(TAG, "RealtimeStepsNotifyListener:" + steps);
//                        sendNotification(20,steps);
//                        try {
//                            writeStingInExternalFile(System.currentTimeMillis()+";"+steps,"Wearable_steps");
//                        } catch (Exception e) {
//                            Log.e(TAG, "Unable to store steps into file", e);
//                        }
//                    }
//                });

            }

            @Override
            public void onFail(int errorCode, String msg) {
//                pd.dismiss();
                Log.d(TAG, "connect fail, code:" + errorCode + ",mgs:" + msg);
            }
        });
    }

    public void onDestroy()
    {
        Log.i(TAG,"Background service stopped");
    }

    @Override
    public IBinder onBind(Intent intencion) {
        return null;
    }

    /**
     *  Writes a string line in a file in external memory
     * filename specifies both location and name of the file
     * example: filename="/folder1/myfile.txt"
     */
    public void writeStingInExternalFile(String data, String fileName){
        try {
            if(isExternalStorageWritable()){
                File ext_storage = Environment.getExternalStorageDirectory();
                Log.i(TAG,"Ext storage path="+Environment.getExternalStorageDirectory().toString());
                String extPath = ext_storage.getPath();
                File folder = new File(extPath+"/precious");
                boolean success = false;
                if(!folder.exists())
                    success = folder.mkdir();
                if(folder.exists() || success){
                    File file = new File (folder, fileName);
                    if(!file.exists())
                        file.createNewFile();
                    FileOutputStream f = new FileOutputStream(file, true);
                    String text = data + "\n";
                    f.write(text.getBytes());
                    f.close();
//                    Log.i("File "+fileName, "Stored "+data);
                }
                else Log.e("WearableBGService","folder.mkdir()=false");
            }
        } catch (Exception e) {
            Log.e("Error opening file", e.getMessage(), e);
        }
    }

    /**
     *  Checks if external storage is available for read and write
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    public static void sendNotification(int battery, int steps){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.precious_icon)
                        .setContentTitle("Steps: "+steps)
                        .setContentText("Good job!");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mContext, OLD_API_WearableMainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack for the Intent (but not the Intent itself)


//        stackBuilder.addParentStack(fd_MainActivity.class);


        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
// FOOD_REMINDER_NOTIF_ID allows you to update the notification later on.
            mNotificationManager.notify(WEARABLE_REMINDER_NOTIF_ID, mBuilder.build());
    }


    public static void sendConnectionNotification(boolean Connected, int steps){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.precious_icon)
                        .setContentTitle("Connected!")
                        .setContentText("Steps: "+steps);
        if(!Connected){
            mBuilder =
                    new NotificationCompat.Builder(mContext)
                            .setSmallIcon(R.drawable.precious_icon)
                            .setContentTitle("Disconnected")
                            .setContentText("Wait for 5min please");
        }
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mContext, OLD_API_WearableMainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack for the Intent (but not the Intent itself)


//        stackBuilder.addParentStack(fd_MainActivity.class);


        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
// FOOD_REMINDER_NOTIF_ID allows you to update the notification later on.
        mNotificationManager.notify(WEARABLE_REMINDER_NOTIF_ID, mBuilder.build());
    }
}
