package aalto.comnet.thepreciousfoodintake;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import theprecioussandbox.comnet.aalto.precious.R;


//TODO CHANGE NON-FINAL VARIABLES INITIALIZATION, initialize them somewhere else
public class MainActivity extends Activity {

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
}

