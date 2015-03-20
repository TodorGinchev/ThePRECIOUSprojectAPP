package aalto.comnet.thepreciousrecognition;



import aalto.comnet.thepreciousproject.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends Activity {
	
	public final static String TAG = "com.example._precious";
	
	// The activity recognition update request object
    private DetectionRequester mDetectionRequester;
   
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recognition_activity);
		
		// Get detection requester object
        mDetectionRequester = new DetectionRequester(this);
        
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		PowerManager.WakeLock lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SensorRead");
		//if ((lock != null) &&           // we have a WakeLock
		//	    (lock.isHeld() == false)) {  // but we don't hold it 
			  lock.acquire(); //Never release!
		//	}	
	}
	
	
    /*
     * Handle results returned to this Activity by other Activities started with
     * startActivityForResult(). In particular, the method onConnectionFailed() in
     * DetectionRemover and DetectionRequester may call startResolutionForResult() to
     * start an Activity that handles Google Play services problems. The result of this
     * call returns here, to onActivityResult.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        // Choose what to do based on the request code
        switch (requestCode) {

            // If the request code matches the code sent in onConnectionFailed
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:
                    		// Restart the process of requesting activity recognition updates
                            mDetectionRequester.requestUpdates();                     
                    break;

                    // If any other result was returned by Google Play services
                    default:

                        // Report that Google Play services was unable to resolve the problem.
                        Log.d(TAG, getString(R.string.no_resolution));
                }

            // If any other request code was received
            default:
               // Report that this Activity received an unknown requestCode
               Log.d(TAG,
                       getString(R.string.unknown_activity_request_code, requestCode));

               break;
        }
    }
    
    
    @Override
    protected void onResume() {
        super.onResume();
         startUpdates();
        killActivity();
        //...
    }
    
    @Override
    protected void onPause() {
    	//...
        super.onPause();
    }
    
    /*
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {

            // In debug mode, log the status
            Log.d(TAG, getString(R.string.play_services_available));

            // Continue
            return true;

        // Google Play services was not available for some reason
        } else {

            // Display an error dialog
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0).show();
            return false;
        }
    }
    /**
     * Start requesting activity updates
     */
    public void startUpdates() {

        // Check for Google Play services
        if (!servicesConnected()) {
            return;
        }

        // Pass the update request to the requester object
        mDetectionRequester.requestUpdates();
    }
    
    void onKillActivity(View v)
    { 
    	killActivity();
    }

    void killActivity()
    { 
        finish();
    }
}
