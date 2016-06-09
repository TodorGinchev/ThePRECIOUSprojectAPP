package activity_tracker.precious.comnet.aalto;


import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.io.File;
import java.io.FileOutputStream;


/**
 *
 */
public class ActivityRecognitionIntentService extends IntentService {

    public final static String TAG = "RecogIntentService";
    public static final String AT_PREFS = "ActivityTrackerPreferences";

    public ActivityRecognitionIntentService() {
        super("ActivityRecognitionIntentService");
    }
    public ActivityRecognitionIntentService(String name) {
        super(name);
    }

    /**
     * Called when a new activity detection update is available.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // If the intent contains an update
        if (ActivityRecognitionResult.hasResult(intent)) {
            //Record current update timestamp
            SharedPreferences preferences = this.getSharedPreferences(AT_PREFS, 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("lastTimestamp", System.currentTimeMillis());
            editor.apply();

            // Get the update
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            // Log the update
            logActivityRecognitionResult(result);
        }
    }

    /**
     * Write the activity recognition update to the log file

     * @param result The result extracted from the incoming Intent
     */
    private void logActivityRecognitionResult(ActivityRecognitionResult result) {
        // Get all the probably activities from the updated result
        for (DetectedActivity detectedActivity : result.getProbableActivities()) {

            // Get the activity type, confidence level, and human-readable name
            int activityType = detectedActivity.getType();
            int confidence = detectedActivity.getConfidence();
            String activityName = getNameFromType(activityType);

            long timeNow = System.currentTimeMillis();
            String status = timeNow+";"+activityName+";"+confidence;

            Log.i("STATUS",status);
            //Saving or not data, based on threshold on the confidence detection of each activity
            if( !activityName.equals("unknown")  &&
                (
                    (confidence >= 70 && activityType==DetectedActivity.RUNNING)
                    || (confidence >= 60 && activityType==DetectedActivity.ON_BICYCLE)
                    || (confidence >= 100 && activityType==DetectedActivity.TILTING)
                    || (confidence >= 45 && activityType==DetectedActivity.WALKING)
                    || (confidence >= 70 && activityType==DetectedActivity.IN_VEHICLE)
                    || (confidence >= 80 && activityType==DetectedActivity.STILL)
                )
                ){
                writeStingInExternalFile(status,"/Log2File.txt") ;
                writeStingInExternalFile(status,"/ViewerLogFile.txt") ;
                writeStingInExternalFile(status,"/ServerActivity.txt") ;
            }
            else
                writeStingInExternalFile(status,"/ServerActivity.txt") ;
        }
    }

    /**
     * Map detected activity types to strings
     *
     * @param activityType The detected activity type
     * @return A user-readable name for the type
     */
    private String getNameFromType(int activityType) {
        switch(activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.WALKING:
                return "walking";
            case DetectedActivity.RUNNING:
                return "running";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.TILTING:
                return "tilting";
        }
        return "unknown";
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
                else Log.e("ActivityRecIntent","folder.mkdir()=false");
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



//    private boolean isMyServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }
}