package aalto.comnet.thepreciousfoodintake;

import aalto.comnet.thepreciousproject.R;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;



//TODO CHANGE NON-FINAL VARIABLES INITIALIZATION, initialize them somewhere else
public class MainActivity extends Activity {
	
	public String TAG = "thepreciousfoodintake.MainActivity";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_foodintake);   
       
    }
    
    /**
     * 
     */
    public void scanBarcode(View view) {   
    	Intent i = new Intent(this, BarcodeScanner.class);
    	startActivity(i);
     }
    /**
     *         
     */
    public void startManualInput(View view) {    	
    	Intent i = new Intent(this, ManualInput.class);
    	startActivity(i);
	}
    /**
     *         
     */
    public void startCameraDetection(View view) {    	
    	Intent i = new Intent(this, CameraFoodRecognition.class);
    	startActivity(i);
	}
    /**
     *         
     */
    public void startBTManager(View view) {  
    	if(isMyServiceRunning(BluetoothManager.class)){
    		Intent i = new Intent(this, BluetoothManager.class);
    		Log.i(TAG,"Stopping BT service");
    		stopService(i);
    	}
    	else{
	    	Intent i = new Intent(this, BluetoothManager.class);
	    	Log.i(TAG,"Starting BT service");
	    	startService(i);
    	}
	}
    
    /**
     * Checks if a service is running
     * Parameters: class of the service
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

