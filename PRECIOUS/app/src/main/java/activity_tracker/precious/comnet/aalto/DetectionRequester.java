package activity_tracker.precious.comnet.aalto;

//Documentation http://code.tutsplus.com/tutorials/how-to-recognize-user-activity-with-activity-recognition--cms-25851

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import aalto.comnet.thepreciousproject.R;


public class DetectionRequester extends Activity {

    public static final String TAG = "DetectionRequester";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_requestsender_activity);
        Log.e(TAG,"onCreate");
        finish();
    }

}