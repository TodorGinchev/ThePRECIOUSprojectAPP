package wearable.precious.comnet.aalto;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import aalto.comnet.thepreciousproject.R;

public class ScanActivity extends Activity {
    private static final String TAG = "==[mibandtest]==";
    final private int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 124;
    public static final String WR_PREFS_NAME = "WRsubappPreferences";
    private ArrayAdapter adapter;
    private ScanCallback scanCallback;

//    private MiBand miband;


    HashMap<String, BluetoothDevice> devices = new HashMap<String, BluetoothDevice>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wearable_scan);
        askForPermissions();

        TextView tv = (TextView) findViewById(R.id.textView35);
        SharedPreferences preferences = getSharedPreferences(WR_PREFS_NAME, 0);
        if(!preferences.getString("wearable_adress","-1").equals("-1")) {
            tv.setText("Device paired! Start walking and your steps will be shown in the notification panel as you walk. Clicking on STARSCAN will unpair your device and search for new devices to be paired.");
            Intent backgroundService = new Intent(ScanActivity.this,BackgroundService.class);
            startService(backgroundService);
        }
        else{
            tv.setText("No paired device.");
        }


        adapter = new ArrayAdapter<String>(this, R.layout.item, new ArrayList<String>());

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
                    String deviceName = device.getName();
                    try {
                        if (deviceName.equals("MI1A")) {
                            devices.put(item, device);
                            adapter.add(item);
                        }
                    }catch (Exception e){
                        Log.e(TAG," ",e);
                    }
                }
            }
        };


        ((Button) findViewById(R.id.starScanButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) findViewById(R.id.textView35);
                tv.setText("No paired device.");
                SharedPreferences preferences = ScanActivity.this.getSharedPreferences(WR_PREFS_NAME, 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("wearable_adress","-1");
                editor.commit();


                Log.d(TAG, "Start scanning Bluetooth Le devices nearby ...");
                MiBand.startScan(scanCallback);
            }
        });

        ((Button) findViewById(R.id.stopScanButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Stop scan...");
                MiBand.stopScan(scanCallback);
            }
        });


        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView) view).getText().toString();
                if (devices.containsKey(item)) {

                    Log.d(TAG, "Stop scan...");
                    MiBand.stopScan(scanCallback);

                    BluetoothDevice device = devices.get(item);
//                    Intent intent = new Intent();
//                    intent.putExtra("device", device);
//                    intent.setClass(ScanActivity.this, WearableMainActivity.class);
//                    ScanActivity.this.startActivity(intent);
//                    ScanActivity.this.finish();
                    SharedPreferences preferences = ScanActivity.this.getSharedPreferences(WR_PREFS_NAME, 0);
                    SharedPreferences.Editor editor = preferences.edit();
//                    editor.putString(device.getAddress().toString());
                    Log.i(TAG,"Address="+device.getAddress());
                    editor.putString("wearable_adress",device.getAddress());
                    editor.commit();


                    TextView tv = (TextView) findViewById(R.id.textView35);
                    tv.setText("Device paired! Start walking and your steps will be shown in the notification panel as you walk. Clicking on STARSCAN will unpair your device and search for new devices to be paired.");


                    Intent backgroundService = new Intent(ScanActivity.this,BackgroundService.class);
                    backgroundService.putExtra("device", device);
                    startService(backgroundService);
//                    Intent backgroundService = new Intent(ScanActivity.this,BackgroundService.class);
////                    backgroundService.putExtra("device", device);
//                    startService(backgroundService);
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
