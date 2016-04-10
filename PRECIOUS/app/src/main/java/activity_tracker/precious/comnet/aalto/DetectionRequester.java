package activity_tracker.precious.comnet.aalto;

//Documentation http://code.tutsplus.com/tutorials/how-to-recognize-user-activity-with-activity-recognition--cms-25851

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import aalto.comnet.thepreciousproject.R;


public class DetectionRequester extends Activity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public GoogleApiClient mApiClient;
    public final String TAG = "DetectionRequester";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_requestsender_activity);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
//        PowerManager.WakeLock lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SensorRead");
//        //if ((lock != null) &&           // we have a WakeLock
//        //	    (lock.isHeld() == false)) {  // but we don't hold it
//        lock.acquire(); //Never release!
//        //	}

        Log.i(TAG, "Calling connect()");
        mApiClient.connect();
        finish();
        //...
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG,"onConnected called");
        Intent intent = new Intent( this, ActivityRecognitionIntentService.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( mApiClient, R.integer.activityRecongitionPeriod, pendingIntent );
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG,"onConnectionFailed");
    }
}