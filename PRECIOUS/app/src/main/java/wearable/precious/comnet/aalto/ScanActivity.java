package wearable.precious.comnet.aalto;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import aalto.comnet.thepreciousproject.R;
import wearable.precious.comnet.aalto.listeners.NotifyListener;
import wearable.precious.comnet.aalto.model.VibrationMode;

public class ScanActivity extends Activity {
    public static Context mContext;
    private static final String TAG = "==[mibandtest]==";
    public static final String WR_PREFS_NAME = "WRsubappPreferences";
    private ArrayAdapter adapter;
    public static ScanCallback scanCallback;
    private static MiBand miband;

//    private MiBand miband;


    HashMap<String, BluetoothDevice> devices = new HashMap<String, BluetoothDevice>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wr_scan);

        mContext = this;

        //Check if Android version > API 21, if not, wearable cannot be used.
        if (Build.VERSION.SDK_INT < 21) {
            Toast.makeText(this,"Your Android version is 4.4 or lower and it is not compatible",Toast.LENGTH_LONG).show();
            finish();
        }
        else {

//            askForPermissions();
            //Generate callback
            scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    if (Build.VERSION.SDK_INT > 21) {
                        BluetoothDevice device = result.getDevice();
                        Log.d(TAG,
                                "Find nearby Bluetooth devices: name:" + device.getName() + ",uuid:"
                                        + device.getUuids() + ",add:"
                                        + device.getAddress() + ",type:"
                                        + device.getType() + ",bondState:"
                                        + device.getBondState() + ",rssi:" + result.getRssi());

                        String item = device.getName() + " | " + device.getAddress();
                        if (!devices.containsKey(item)) {
                            String deviceName = device.getName();
                            try {
                                if (deviceName.equals("MI1A")||deviceName.equals("MI")||deviceName.equals("MI1S")) {
                                    establishConnectionWithWearable(device);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, " ", e);
                            }
                        }
                    }
                }
            };
            //Scan for device
            MiBand.startScan(scanCallback);
        }
    }


    public static void pairDevice(BluetoothDevice device){

        Log.d(TAG, "pairing device");

        miband.connect(device, new ActionCallback() {

            @Override
            public void onSuccess(Object data) {
                Log.d(TAG, "Connected!!!");
                //Pair
                miband.pair(new ActionCallback() {

                    @Override
                    public void onSuccess(Object data) {
                        Log.d(TAG, "pair succ");

                    }

                    @Override
                    public void onFail(int errorCode, String msg) {
                        Log.d(TAG, "pair fail");
                    }
                });
            }
            @Override
            public void onFail(int errorCode, String msg) {
                Log.d(TAG, "connect fail, code:" + errorCode + ",mgs:" + msg);
            }
                });


        MiBand.stopScan(scanCallback);
        SharedPreferences preferences = mContext.getSharedPreferences(WR_PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        Log.i(TAG, "Address=" + device.getAddress());
        editor.putString("wearable_address", device.getAddress());
        editor.commit();
        CloseActivity();

//        Intent backgroundService = new Intent(mContext, BackgroundService.class);
//        mContext.startService(backgroundService);

    }

    public static BluetoothDevice mDevice;
    public void establishConnectionWithWearable(BluetoothDevice device){
        Log.i(TAG,"Connecting...");
        mDevice = device;
        miband = new MiBand(this);
        miband.connect(device, new ActionCallback() {

            @Override
            public void onSuccess(Object data) {
                Log.d(TAG, "Connected!!!");
                //Stop scan
                MiBand.stopScan(scanCallback);
                //Vibrate
                miband.startVibration(VibrationMode.VIBRATION_WITH_LED);
                askForPair(mDevice);

//                miband.setDisconnectedListener(new NotifyListener() {
//                    @Override
//                    public void onNotify(byte[] data) {
//                        Log.d(TAG, "Disconnected!!!");
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








    private static ScanActivity parent;
    public static  void askForPair(final BluetoothDevice device){

        ((Activity)mContext).runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        mContext);

                alertDialogBuilder.setTitle("Did your wearable vibrate?");

                alertDialogBuilder
                        .setMessage("If it did not, click on No and the program will scan again.")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                                pairDevice(device);
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Intent intent = ((Activity)mContext).getIntent();
                                ((Activity)mContext).finish();
                                mContext.startActivity(intent);
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });


    }

    public void CloseActivity (View v){
        MiBand.stopScan(scanCallback);
        ((Activity)mContext).finish();
    }
    public static void CloseActivity (){
        MiBand.stopScan(scanCallback);
        ((Activity)mContext).finish();
    }
}
