package wearable.precious.comnet.aalto;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import aalto.comnet.thepreciousproject.R;
import wearable.precious.comnet.aalto.listeners.NotifyListener;
import wearable.precious.comnet.aalto.listeners.RealtimeStepsNotifyListener;

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

        mContext=this;
        miband = new MiBand(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int idArranque) {
        Log.i(TAG, "onStartCommand");

//        try {
//            BLEdevice = intent.getParcelableExtra("device");
//            Log.i(TAG,"Address of non-existing BLE device"+BLEdevice.getAddress());
//        }catch (Exception e){
//            Log.e(TAG," ",e);
//        }

        try {
            BLEdevice = intent.getParcelableExtra("device");
            if (BLEdevice != null) {
                Log.i(TAG, "device Object already exists");
                establishConnectionWithWearable(BLEdevice);
            }
            else {
                SharedPreferences preferences = BackgroundService.this.getSharedPreferences(WR_PREFS_NAME, 0);
                if (!preferences.getString("wearable_adress", "-1").equals("-1")) {
                    Log.i(TAG, "device Object will be created, device is paired");
                    findWearable();
                } else
                    Log.e(TAG, "No device was paired");
            }
        }
        catch (Exception e){
            Log.e(TAG," ",e);
            SharedPreferences preferences = BackgroundService.this.getSharedPreferences(WR_PREFS_NAME, 0);
            if (!preferences.getString("wearable_adress", "-1").equals("-1")) {
                findWearable();
            } else
                Log.e(TAG, "No device was paired");
        }

        return START_STICKY;
    }

    public void findWearable(){
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice device = result.getDevice();
                Log.d(TAG,
                        "Find nearby Bluetooth devices: name:" + device.getName() + ",uuid:"
                                + device.getUuids() + ",add:"
                                + device.getAddress() + ",type:"
                                + device.getType() + ",bondState:"
                                + device.getBondState() + ",rssi:" + result.getRssi());

                String item = device.getName() + " | " + device.getAddress();
                if (!devices.containsKey(item)) {
                    try {
                        SharedPreferences preferences = BackgroundService.this.getSharedPreferences(WR_PREFS_NAME, 0);
                        if (device.getAddress().equals(preferences.getString("wearable_adress","C8:0F:10:08:79:E7"))) {
                            devices.put(item, device);
                            establishConnectionWithWearable(device);
                        }
                    }catch (Exception e){
                        Log.e(TAG," ",e);
                    }
                }
            }
        };
        MiBand.startScan(scanCallback);
    }

    public void establishConnectionWithWearable(BluetoothDevice device){
        Log.i(TAG,"Connecting...");
        miband.connect(device, new ActionCallback() {

            @Override
            public void onSuccess(Object data) {
//                pd.dismiss();
                Log.d(TAG, "Connected!!!");
                sendConnectionNotification(true);
//                miband.startVibration(VibrationMode.VIBRATION_WITH_LED);
                miband.setRealtimeStepsNotifyListener(new RealtimeStepsNotifyListener() {

                    @Override
                    public void onNotify(int steps) {
                        Log.d(TAG, "RealtimeStepsNotifyListener:" + steps);
                        sendNotification(20,steps);
                        try {
                            writeStingInExternalFile(System.currentTimeMillis()+";"+steps,"Wearable_steps");
                        } catch (Exception e) {
                            Log.e(TAG, "Unable to store steps into file", e);
                        }
                    }
                });

                miband.setDisconnectedListener(new NotifyListener() {
                    @Override
                    public void onNotify(byte[] data) {
                        Log.d(TAG, "Disconnected!!!");
                        stopService(new Intent(mContext, BackgroundService.class));
                    }
                });
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
        Log.i(TAG,"Background service stopped, restarting...");

        Intent backgroundService = new Intent(BackgroundService.this,BackgroundService.class);
        backgroundService.putExtra("device", BLEdevice);
        startService(backgroundService);
        sendConnectionNotification(false);

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
        Intent resultIntent = new Intent(mContext, WearableMainActivity.class);

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


    public static void sendConnectionNotification(boolean Connected){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.precious_icon)
                        .setContentTitle("Connected!")
                        .setContentText("Start walking.");
        if(!Connected){
            mBuilder =
                    new NotificationCompat.Builder(mContext)
                            .setSmallIcon(R.drawable.precious_icon)
                            .setContentTitle("Disconnected")
                            .setContentText("Wait for 5min please");
        }
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mContext, WearableMainActivity.class);

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
