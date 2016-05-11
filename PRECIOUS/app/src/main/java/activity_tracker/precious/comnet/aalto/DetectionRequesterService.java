package activity_tracker.precious.comnet.aalto;


import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

public class DetectionRequesterService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public final String TAG = "DetectionRequesterServi";
    public GoogleApiClient mApiClient;
    public static final String AT_PREFS = "ActivityTrackerPreferences";

    // Must create a default constructor
    public DetectionRequesterService() {
        // Used to name the worker thread, important only for debugging.
        super("test-service");
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        SharedPreferences preferences = this.getSharedPreferences(AT_PREFS, 0);
        if(System.currentTimeMillis()-preferences.getLong("lastTimestamp",-1)>5*60*1000){
            Log.i(TAG, "Connecting");
            mApiClient.connect();
        }
        else
            Log.i(TAG, "Already connected");
//        Toast.makeText(this,"Running",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // This describes what will happen when service is triggered
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected called");
        Intent intent = new Intent( this, ActivityRecognitionIntentService.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 20000, pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG,"onConnectionFailed");
        Toast.makeText(this, "Google Play services out of date",Toast.LENGTH_LONG).show();
        Toast.makeText(this,"Please contact the PRECIOUS team immediately",Toast.LENGTH_LONG).show();
    }
}
