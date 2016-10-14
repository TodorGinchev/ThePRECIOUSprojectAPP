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
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import aalto.comnet.thepreciousproject.R;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;
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
            mContext = PRECIOUS_APP.getAppContext();
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
                        long currentTimestamp = System.currentTimeMillis();
                        final boolean ENABLE_LOG = true;
                        SharedPreferences preferences = BackgroundService.this.getSharedPreferences(WR_PREFS_NAME, 0);
                        //First, get wearable steps
                        int wearable_steps = (int) data;


                        //Get previous wearable steps and the timestamp of the last wearable update
                        ArrayList<Long> wearableInfo = sql_db.precious.comnet.aalto.DBHelper.getInstance().getWearableInformation();
                        //Second, check if there is actually previous data
                        if(wearableInfo==null){
                            //There is NO previous wearable data
                            //Store data for the current day in DB
                            long dayTimestamp = getCurrentDayTimestamp();
                            sql_db.precious.comnet.aalto.DBHelper.getInstance().insertWearableDailySteps(dayTimestamp, wearable_steps);
                            sql_db.precious.comnet.aalto.DBHelper.getInstance().updateWearableDailySteps(dayTimestamp, wearable_steps);

                            if(ENABLE_LOG)
                                writeStingInExternalFile(convertDate(String.valueOf(System.currentTimeMillis()),"dd/MM/yyyy hh:mm:ss")+" "+ "wearableInfo==null and wearable_steps = "+wearable_steps, "wearable_log.txt");
                        }
                        else{
                            //There IS previous wearable data
                            int prev_wearable_steps;
                            long prev_wearable_timestamp;
                            prev_wearable_steps = wearableInfo.get(1).intValue();
                            prev_wearable_timestamp = wearableInfo.get(2);
                            if(ENABLE_LOG)
                                writeStingInExternalFile( convertDate(String.valueOf(System.currentTimeMillis()),"dd/MM/yyyy hh:mm:ss")+" "+"prev_wearable_steps = "+prev_wearable_steps+" and prev_wearable_timestamp = "+prev_wearable_timestamp, "wearable_log.txt");
                            //Third, check how many days have passed since prev_wearable_timestamp
                            int daysSincePrevWearableTimestamp = checkHowManyDaysHavePassedSinceToday(prev_wearable_timestamp);
                            if(ENABLE_LOG)
                                writeStingInExternalFile( convertDate(String.valueOf(System.currentTimeMillis()),"dd/MM/yyyy hh:mm:ss")+" "+" "+daysSincePrevWearableTimestamp+" days have passed since prev_wearable_timestamp", "wearable_log.txt");

                            if(daysSincePrevWearableTimestamp==0) {
                                //if the day has NOT chaged
                                if(ENABLE_LOG)
                                    writeStingInExternalFile( convertDate(String.valueOf(System.currentTimeMillis()),"dd/MM/yyyy hh:mm:ss")+" "+"Day has not changed", "wearable_log.txt");
                                //Get steps_offset
                                int wearable_steps_offset= preferences.getInt("wearable_steps_offset",0); //default value is 0
                                //Check if prev_wearable_steps is greater than wearable_steps
                                if (prev_wearable_steps>wearable_steps+3){
                                    //Change the steps offset
                                    wearable_steps_offset = wearable_steps_offset + prev_wearable_steps;
                                    //Check that offset+wearable_steps is not negative
                                    if( (wearable_steps_offset+wearable_steps)<0){
                                        wearable_steps_offset=0;
                                        if(ENABLE_LOG)
                                            writeStingInExternalFile( convertDate(String.valueOf(System.currentTimeMillis()),"dd/MM/yyyy hh:mm:ss")+" "+"Something really bad happened, offset+steps<0 and prev_wearable_steps = "+prev_wearable_steps+" and wearable_steps = "+wearable_steps+"and wearable_steps_offset  ="+wearable_steps_offset, "wearable_log.txt");
                                    }
                                    //Save offset in shared prefs
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putInt("wearable_steps_offset",wearable_steps_offset);
                                    editor.commit();
                                }
                                //Store data in DB
                                int day_steps = wearable_steps+wearable_steps_offset;
                                long dayTimestamp = getCurrentDayTimestamp();
                                sql_db.precious.comnet.aalto.DBHelper.getInstance().insertWearableDailySteps(dayTimestamp, day_steps);
                                sql_db.precious.comnet.aalto.DBHelper.getInstance().updateWearableDailySteps(dayTimestamp, day_steps);
                                if(ENABLE_LOG)
                                    writeStingInExternalFile( convertDate(String.valueOf(System.currentTimeMillis()),"dd/MM/yyyy hh:mm:ss")+" "+"Day has not changed and data is updated, daily steps are "+day_steps,"wearable_log.txt");
                            }
                            else if(daysSincePrevWearableTimestamp==1){
                                //if the day has changed to the NEXT day
                                if(ENABLE_LOG)
                                    writeStingInExternalFile( convertDate(String.valueOf(System.currentTimeMillis()),"dd/MM/yyyy hh:mm:ss")+" "+"the day has changed to the NEXT day", "wearable_log.txt");
                                //Check if time is before 9am
                                Calendar c = Calendar.getInstance();
                                c.setTimeInMillis(System.currentTimeMillis());
                                if(c.get(Calendar.HOUR_OF_DAY)<24){ //TODO always true, leave it like that for now
                                    if(ENABLE_LOG)
                                        writeStingInExternalFile( convertDate(String.valueOf(System.currentTimeMillis()),"dd/MM/yyyy hh:mm:ss")+" "+"Day has changed and time is before 12:00", "wearable_log.txt");
                                    //Get steps_offset
                                    int wearable_steps_offset= preferences.getInt("wearable_steps_offset",0); //default value is 0
                                    //Check if prev_wearable_steps is greater than wearable_steps
                                    if (prev_wearable_steps>wearable_steps+3){
                                        //Change the steps offset
                                        wearable_steps_offset = wearable_steps_offset + prev_wearable_steps;
                                        //Check that offset+wearable_steps is not negative
                                        if( (wearable_steps_offset+wearable_steps)<0){
                                            wearable_steps_offset=0;
                                            if(ENABLE_LOG)
                                                writeStingInExternalFile( convertDate(String.valueOf(System.currentTimeMillis()),"dd/MM/yyyy hh:mm:ss")+" "+"Something really bad happened, offset+steps<0 and prev_wearable_steps = "+prev_wearable_steps+" and wearable_steps = "+wearable_steps+"and wearable_steps_offset  ="+wearable_steps_offset, "wearable_log.txt");
                                        }
                                        //Save offset in shared prefs
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putInt("wearable_steps_offset",wearable_steps_offset);
                                        editor.commit();
                                    }
                                    //Store data in DB
                                    int day_steps = wearable_steps+wearable_steps_offset;
                                    long dayTimestamp = getCurrentDayTimestamp()-24*3600*1000;
                                    sql_db.precious.comnet.aalto.DBHelper.getInstance().insertWearableDailySteps(dayTimestamp, day_steps);
                                    sql_db.precious.comnet.aalto.DBHelper.getInstance().updateWearableDailySteps(dayTimestamp, day_steps);
                                    //Save offset in shared prefs
                                    wearable_steps_offset = -wearable_steps;
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putInt("wearable_steps_offset",wearable_steps_offset);
                                    editor.commit();
                                    sql_db.precious.comnet.aalto.DBHelper.getInstance().insertWearableDailySteps(getCurrentDayTimestamp(), 3);
                                    sql_db.precious.comnet.aalto.DBHelper.getInstance().updateWearableDailySteps(getCurrentDayTimestamp(), 3);
                                }
                            }
                            else if (daysSincePrevWearableTimestamp<0){
                                if(ENABLE_LOG)
                                writeStingInExternalFile( convertDate(String.valueOf(System.currentTimeMillis()),"dd/MM/yyyy hh:mm:ss")+" "+"Back to the future ->"+convertDate(String.valueOf(prev_wearable_timestamp),"dd/MM/yyyy hh:mm:ss"), "wearable_log.txt");
                                //TODO handle this
                            }
                            else{
                                //There have been more 2 DAYS or more since the prev_wearable_timestamp
                                //This should NOT happen
                                if(ENABLE_LOG)
                                    writeStingInExternalFile( convertDate(String.valueOf(System.currentTimeMillis()),"dd/MM/yyyy hh:mm:ss")+" "+"There have been more 2 DAYS or more since the prev_wearable_timestamp", "wearable_log.txt");
                                //Better set offset to overwrite current steps data
                                int wearable_steps_offset = -wearable_steps;
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putInt("wearable_steps_offset",wearable_steps_offset);
                                editor.commit();
                                //Store data in DB
                                int day_steps = wearable_steps+wearable_steps_offset;
                                long dayTimestamp = getCurrentDayTimestamp();
                                sql_db.precious.comnet.aalto.DBHelper.getInstance().insertWearableDailySteps(dayTimestamp, day_steps);
                                sql_db.precious.comnet.aalto.DBHelper.getInstance().updateWearableDailySteps(dayTimestamp, day_steps);
                            }
                        }
//                        //Store data in DB
//                        sql_db.precious.comnet.aalto.DBHelper.getInstance().insertWearableDailySteps(dayTimestamp, current_steps);
//                        sql_db.precious.comnet.aalto.DBHelper.getInstance().updateWearableDailySteps(dayTimestamp, current_steps);

                        //                        MiBand.stopScan(scanCallback);
                        //Store data in DB
                        Log.i(TAG,convertDate(String.valueOf(currentTimestamp),"dd/MM/yyyy hh:mm:ss")+" wearable_steps="+(wearable_steps)+ " and offset is "+preferences.getInt("wearable_steps_offset",0));
                        if(wearable_steps<3)
                            sql_db.precious.comnet.aalto.DBHelper.getInstance().insertWearableCurrentSteps(currentTimestamp, wearable_steps+3);//just leave the +3, no questions asked
                        else
                            sql_db.precious.comnet.aalto.DBHelper.getInstance().insertWearableCurrentSteps(currentTimestamp, wearable_steps);
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
     * Checks if a timestamp belong to the current day and if not, return how many days hava passed
     * @param timestamp the timestamp to be checked
     * @return 0 if timestamp belong to the current day, otherwise return the number of days that have passed
     */
    public static int checkHowManyDaysHavePassedSinceToday(long timestamp){
        //Create calendar instance for current time
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        //Create calendar instance from timestamp
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(timestamp);
        //Return 0  is timestamp is from current day
        if ( c.get(Calendar.YEAR)==c2.get(Calendar.YEAR) && c.get(Calendar.MONTH)==c2.get(Calendar.MONTH) && c.get(Calendar.DAY_OF_MONTH)==c2.get(Calendar.DAY_OF_MONTH) )
            return 0;
        else {
            long timeDifference = c.getTimeInMillis()-timestamp;
            double daysPassed = (double)timeDifference/1000/3600/24;
            Log.i(TAG,"timeDifference="+timeDifference+"; daysPassed="+daysPassed);
            if(daysPassed<2)
                return 1;
            else
                return  (int) daysPassed;
        }
    }

    /**
     * Return a timestamp of the current day at 0h0min0s0ms
     * @return
     */
    public static long getCurrentDayTimestamp(){
        //Create calendar instance for current time
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return c.getTimeInMillis();
    }

    public static String convertDate(String dateInMilliseconds,String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }
}