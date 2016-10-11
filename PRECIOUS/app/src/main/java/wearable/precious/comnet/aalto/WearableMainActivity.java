package wearable.precious.comnet.aalto;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import aalto.comnet.thepreciousproject.R;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;
import wearable.precious.comnet.aalto.model.BatteryInfo;

public class WearableMainActivity extends Activity {

    public static Context mContext;
    public static final String WR_PREFS_NAME = "WRsubappPreferences";
    public static final String TAG = "WearableMainActivity";
    final private int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 124;
    public static TextView tvBatteryData;
    public static TextView tvStepsData;
    public static TextView tvLastUpdated;
    private boolean isInFront;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wr_main);

        mContext=this;

        //Check if Android version > API 21, if not, wearable cannot be used.
        if (Build.VERSION.SDK_INT < 21) {
            Toast.makeText(this,"Your Android version is 4.4 or lower and it is not compatible",Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            askForPermissions();
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.wearable));
        }
        //Set toolbar title and icons
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.wearable_title));
        toolbar.setTitleTextColor(getResources().getColor(R.color.wearable));
        toolbar.setNavigationIcon(R.drawable.wr_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvBatteryData = (TextView) findViewById(R.id.tvBatteryInfo);
        tvStepsData = (TextView) findViewById(R.id.tvStepsInfo);
        tvLastUpdated = (TextView) findViewById(R.id.tvLastUpdated);


//        int batteryLevel = sql_db.precious.comnet.aalto.DBHelper.getInstance(mContext).getWearableBatteryLevelLast();
//        if(batteryLevel>0)
//            tvBatteryData.setText("Battery level: " + batteryLevel + "%");
//        int currentSteps = sql_db.precious.comnet.aalto.DBHelper.getInstance(mContext).getWearableStepsLast();
//        if(batteryLevel>0)
//            tvStepsData.setText("Current steps: " + batteryLevel);

        //Update wearable information
//        Intent backgroundService = new Intent(mContext, BackgroundService.class);
//        mContext.startService(backgroundService);


        SharedPreferences preferences = getSharedPreferences(WR_PREFS_NAME, 0);
        if (!(preferences.getString("wearable_address", "-1").equals("-1")) ){
            Log.i(TAG, "getWearableInfo()");
            getWearableInfo();
        }

    }

    @Override
    protected void onPause () {
        isInFront=false;
        super.onPause();
    }

    @Override
    protected void onResume (){
        super.onResume();


        isInFront=true;
        //Init buttons
        Button bPair = (Button) findViewById(R.id.buttonPair);
        Button bUnpair = (Button) findViewById(R.id.buttonUnpair);
        SharedPreferences preferences = getSharedPreferences(WR_PREFS_NAME, 0);
        if (preferences.getString("wearable_address", "-1").equals("-1")) {
            bPair.setClickable(true);
            bUnpair.setClickable(false);
            bPair.setBackgroundColor(getResources().getColor(R.color.wearable));
            bPair.setTextColor(getResources().getColor(R.color.black));
            bUnpair.setBackgroundColor(getResources().getColor(R.color.non_clickable_button_background));
            bUnpair.setTextColor(getResources().getColor(R.color.non_clickable_button_text));
        }
        else{
            bUnpair.setClickable(true);
            bPair.setClickable(false);
            bUnpair.setBackgroundColor(getResources().getColor(R.color.wearable));
            bUnpair.setTextColor(getResources().getColor(R.color.black));
            bPair.setBackgroundColor(getResources().getColor(R.color.non_clickable_button_background));
            bPair.setTextColor(getResources().getColor(R.color.non_clickable_button_text));
            //Update view every 5s
            Thread t = new Thread() {

                @Override
                public void run() {
                    try {
                        while (isInFront) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    Log.i(TAG,"runOnUiThread");
                                    ArrayList<Long> wearableInfo = sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).getWearableInformation();
                                    if(wearableInfo==null)
                                        return;
                                    if(wearableInfo.get(0)!=-1) {
                                        tvBatteryData.setText("Battery level: " + wearableInfo.get(0) + "%");
                                        tvStepsData.setText("Current steps: " + wearableInfo.get(1));
                                        //Check if updated just now
                                        long timeDiff = System.currentTimeMillis()-wearableInfo.get(2);
                                        if(timeDiff<60*1000)
                                            tvLastUpdated.setText("Updated "+(int)(timeDiff/1000)+" seconds ago");
                                        else if (timeDiff<60*60*1000)
                                            tvLastUpdated.setText("Updated "+(int)(timeDiff/60/1000)+" minutes ago");
                                        else if (timeDiff<24*60*60*1000)
                                            tvLastUpdated.setText("Updated "+(int)(timeDiff/60/60/1000)+" hours ago");
                                        else if (timeDiff<48*60*60*1000)
                                            tvLastUpdated.setText("Updated yesterday");
                                        else {
                                            Calendar cal = Calendar.getInstance();
                                            cal.setTimeInMillis(wearableInfo.get(2));
                                            String date = DateFormat.format("yyyy-MM-dd HH:mm", cal).toString();
                                            tvLastUpdated.setText("Updated on " + date);
                                        }
                                    }
                                }
                            });
                            Thread.sleep(5000);
                        }
                    } catch (InterruptedException e) {
                        Log.e(TAG," ",e);
                    }
                }
            };
            t.start();
        }
    }

    public static void PairWearable (View v){
        Intent i = new Intent(mContext, wearable.precious.comnet.aalto.ScanActivity.class);
        mContext.startActivity(i);
    }

    public static void UnpairWearable (View v){
        ((Activity)mContext).runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        mContext);
                alertDialogBuilder.setTitle("Unpair wearable?");
                alertDialogBuilder
                        .setMessage("You should NOT unpair your wearable.\nThe PRECIOUS team recommends you to select No.")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                                SharedPreferences preferences = mContext.getSharedPreferences(WR_PREFS_NAME, 0);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("wearable_address", "-1");
                                editor.commit();
                                Intent intent = ((Activity)mContext).getIntent();
                                ((Activity)mContext).finish();
                                mContext.startActivity(intent);
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });
    }


    private static MiBand miband;
    public static BluetoothDevice BLEdevice;

    /**
     *
     */
    public static void getWearableInfo(){
        //Check if Android version > API 21, if not, wearable cannot be used.
        if (Build.VERSION.SDK_INT < 21) {
            Toast.makeText(mContext,"Your Android version is 4.4 or lower and it is not compatible",Toast.LENGTH_LONG).show();
            ((Activity)mContext).finish();
        }
        else{
            //Create miband and BLE device object and init them
            miband = new MiBand(mContext);
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
            SharedPreferences preferences = mContext.getSharedPreferences(WR_PREFS_NAME, 0);
            BLEdevice = mBluetoothAdapter.getRemoteDevice(preferences.getString("wearable_address", "00:00:00:00:00:00"));
            //Connect to wearable
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
                Log.i(TAG, "Bluetooth not supported");
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    Log.i(TAG, "Bluetooth is OFF, starting background service");
                    Intent backgroundService = new Intent(mContext, BackgroundService.class);
                    mContext.startService(backgroundService);
                } else {
                    miband.connect(BLEdevice, new ActionCallback() {
                        @Override
                        public void onSuccess(Object data) {
                            Log.d(TAG, "Connected!!!");
                            //Get Battery Info
                            miband.getBatteryInfo(new ActionCallback() {

                                @Override
                                public void onSuccess(Object data) {
                                    BatteryInfo info = (BatteryInfo) data;
                                    Log.i(TAG, "Battery level: " + info.toString());
                                    //                            SharedPreferences preferences = mContext.getSharedPreferences(WR_PREFS_NAME, 0);
                                    //                            SharedPreferences.Editor editor= preferences.edit();
                                    //                            editor.putString("BatteryLevel",""+info.toString());
                                    //                            editor.commit();
                                    sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).insertWearableBatteryLevel(System.currentTimeMillis(), info.getLevel());
                                    updateBatteryInfo();

                                    Intent backgroundService = new Intent(mContext, BackgroundService.class);
                                    mContext.startService(backgroundService);
                                }

                                @Override
                                public void onFail(int errorCode, String msg) {
                                    Log.d(TAG, "getBatteryInfo fail");
                                    Intent backgroundService = new Intent(mContext, BackgroundService.class);
                                    mContext.startService(backgroundService);
                                }
                            });
                        }

                        @Override
                        public void onFail(int errorCode, String msg) {
                            Log.d(TAG, "connect fail, code:" + errorCode + ",mgs:" + msg);
                        }
                    });
                }
            }
        }
    }

    /**
     *
     */
    public static void updateBatteryInfo(){
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                SharedPreferences preferences = mContext.getSharedPreferences(WR_PREFS_NAME, 0);
//                tvBatteryData.setText("Battery level: "+preferences.getString("BatteryLevel","?")+"%");
//                    int batteryLevel = sql_db.precious.comnet.aalto.DBHelper.getInstance(mContext).getWearableBatteryLevelLast();
//                    if(batteryLevel!=-1)
//                        tvBatteryData.setText("Battery level: " + batteryLevel + "%");
                ArrayList<Long> wearableInfo = sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).getWearableInformation();
                try {
                    if(wearableInfo!=null) {
                        if (wearableInfo.get(0) != -1) {
                            tvBatteryData.setText("Battery level: " + wearableInfo.get(0) + "%");
                            tvStepsData.setText("Current steps: " + wearableInfo.get(1));
                        }
                    }
                }catch (Exception e){
                    Log.e(TAG," ",e);
                }
            }
        });
    }

    public void askForPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions((Activity)this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_COARSE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, getString(R.string.storage_permission_warning), Toast.LENGTH_LONG).show();
                    askForPermissions();


                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

