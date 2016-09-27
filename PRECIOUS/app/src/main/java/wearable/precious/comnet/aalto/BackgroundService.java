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
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
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
    private boolean BLEwasOFF=false;
    private BluetoothAdapter mBluetoothAdapter;

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
                //Check if Bluetooth is OFF and activate it
                final BluetoothManager bluetoothManager =
                        (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = bluetoothManager.getAdapter();//BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    // Device does not support Bluetooth
                    Log.i(TAG, "Bluetooth not supported");
                    stopService(new Intent(this, BackgroundService.class));
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Log.i(TAG, "Bluetooth is OFF, turning ON");
                        // Bluetooth is OFF
                        BLEwasOFF = true;
                        //Turn Bluetooth ON
                        BluetoothAdapter.getDefaultAdapter().enable();
                        final Handler handler = new Handler();
                        //Wait 5s to ensure BLE is on and get steps
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 5s
                                SharedPreferences preferences = BackgroundService.this.getSharedPreferences(WR_PREFS_NAME, 0);
                                if (!preferences.getString("wearable_address", "-1").equals("-1")) {
                                    Log.i(TAG, "device Object will be created, device is paired");
//                        findWearable();
//                    final BluetoothManager bluetoothManager =
//                            (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//                    BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
                                    BLEdevice = mBluetoothAdapter.getRemoteDevice(preferences.getString("wearable_address", "00:00:00:00:00:00"));
                                    establishConnectionWithWearable(BLEdevice);
                                } else {
                                    Log.i(TAG, "No wearable device was paired");
                                    stopService(new Intent(mContext, BackgroundService.class));
                                }
                            }
                        }, 5000);

                        //Turn OFF BLE after 30s if it did not turn on
                        final Handler handler2 = new Handler();
                        handler2.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 30s
                                if(mBluetoothAdapter.isEnabled())
                                    BluetoothAdapter.getDefaultAdapter().disable();
                            }
                        }, 30000);
                    } else {
                        Log.i(TAG, "Bluetooth is ON");
                        SharedPreferences preferences = BackgroundService.this.getSharedPreferences(WR_PREFS_NAME, 0);
                        if (!preferences.getString("wearable_address", "-1").equals("-1")) {
                            Log.i(TAG, "device Object will be created, device is paired");
//                        findWearable();
//                    final BluetoothManager bluetoothManager =
//                            (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//                    BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
                            BLEdevice = mBluetoothAdapter.getRemoteDevice(preferences.getString("wearable_address", "00:00:00:00:00:00"));
                            establishConnectionWithWearable(BLEdevice);
                        } else {
                            Log.i(TAG, "No wearable device was paired");
                            stopService(new Intent(this, BackgroundService.class));
                        }
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, " ", e);
                stopService(new Intent(mContext, BackgroundService.class));
            }
        }
        return START_NOT_STICKY;
    }


    public void establishConnectionWithWearable(BluetoothDevice device){
        Log.i(TAG,"Connecting...");
        miband.connect(device, new ActionCallback() {

            @Override
            public void onSuccess(Object data) {
                Log.i(TAG, "Sending steps counter request...");
                miband.getSteps(new ActionCallback() {

                    @Override
                    public void onSuccess(Object data) {
                        SharedPreferences preferences = BackgroundService.this.getSharedPreferences(WR_PREFS_NAME, 0);
                        int steps = (int) data;
                        Log.d(TAG, "Steps: " + steps);
                        ArrayList<Long> wearableInfo = sql_db.precious.comnet.aalto.DBHelper.getInstance(mContext).getWearableInformation();

                        long prev_steps;
                        long lastUpdated;
                        if(wearableInfo==null){
                            prev_steps = 0;
                            lastUpdated = System.currentTimeMillis();
                        }
                        else{
                            prev_steps = wearableInfo.get(1);
                            lastUpdated = wearableInfo.get(2);
                        }
                        //Check if new day
                        if (!checkIfTimestampIsFromToday(lastUpdated)) {
                            Log.i(TAG, "resetting steps because new day begins");
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("steps_offset",(int)(-prev_steps));
                            editor.commit();
                        } else {

                            //Check if step counter has not erroneosly been reset (sometimes it happens with no reason)
                            if ((int) (prev_steps) > steps) {
                                writeStingInExternalFile(prev_steps + ";" + steps + ";" + System.currentTimeMillis() + ";", "wearable_steps_anomalies.txt");
                                int steps_offset = preferences.getInt("steps_offset",0);
                                steps_offset += prev_steps;
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putInt("steps_offset",steps_offset);
                                editor.commit();
                            }
                        }

                        int steps_offset = preferences.getInt("steps_offset",0);
                        if( (steps_offset+steps)<0)
                            steps_offset=0;
                        Log.i(TAG,"steps_offset = "+steps_offset);
                        Log.i(TAG,"final_offset = "+(steps_offset+steps));

                        if ( (steps_offset+steps) > 0 /*&& (System.currentTimeMillis() - lastUpdated) < (5 * 24 * 3600 * 1000)*/) {
                            Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(lastUpdated);
                            c.set(Calendar.HOUR_OF_DAY, 0);
                            c.set(Calendar.MINUTE, 0);
                            c.set(Calendar.SECOND, 0);
                            c.set(Calendar.MILLISECOND, 0);
                            long dayTimestamp = c.getTimeInMillis();
                            sql_db.precious.comnet.aalto.DBHelper.getInstance(mContext).insertWearableDailySteps(dayTimestamp, steps_offset+steps);
                            sql_db.precious.comnet.aalto.DBHelper.getInstance(mContext).updateWearableDailySteps(dayTimestamp, steps_offset+steps);
                        }
                        //Store data in DB
                        sql_db.precious.comnet.aalto.DBHelper.getInstance(mContext).insertWearableCurrentSteps(System.currentTimeMillis(), steps_offset+steps);
                        writeStingInExternalFile( (steps_offset+steps) + ";" + System.currentTimeMillis() + ";"+steps+";", "wearable_steps.txt");
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
        //If Bluetooth was OFF, leave it OFF
        if(BLEwasOFF){
            if(mBluetoothAdapter != null)
                BluetoothAdapter.getDefaultAdapter().disable();
            Log.i(TAG,"Bluetooth was OFF, turning OFF");
        }
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

    /**
     * Checks if a timestamp belong to the current day
     * @param timestamp the timestamp to be checked
     * @return true if timestamp belong to the current day, false if not
     */
    public static boolean checkIfTimestampIsFromToday(long timestamp){
        //Create calendar instance for current time
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        //Create calendar instance from timestamp
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(timestamp);
        //Return is timestamp is from current day
        return ( c.get(Calendar.YEAR)==c2.get(Calendar.YEAR) && c.get(Calendar.MONTH)==c2.get(Calendar.MONTH) && c.get(Calendar.DAY_OF_MONTH)==c2.get(Calendar.DAY_OF_MONTH));
    }
}