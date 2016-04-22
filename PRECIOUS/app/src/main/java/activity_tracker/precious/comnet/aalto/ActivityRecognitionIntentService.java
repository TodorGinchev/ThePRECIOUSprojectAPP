package activity_tracker.precious.comnet.aalto;


import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import aalto.comnet.thepreciousproject.R;


/**
 *
 */
public class ActivityRecognitionIntentService extends IntentService {

    public final static String TAG = "RecogIntentService";
    // Formats the timestamp in the log
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss.SSSZ";
    // Delimits the timestamp from the log info
    private static final String LOG_DELIMITER = ";;";
    // Sleep pattern
    private static final int WAKEUP_TIME_EARLY=5; //Consider that user will wake up after 5:59am
    private static final int WAKEUP_TIME_LATE=12; //Consider that user will wake up before 12:00am
    private static final int MIN_TIME_SLEEP=4*3600*1000;//User must sleep at least 4 hours in order to detect pattern
    // A date formatter
    private SimpleDateFormat mDateFormat;
    // Store the app's shared preferences repository
    private SharedPreferences mPrefs;
    public ActivityRecognitionIntentService() {
        // Set the label for the service's background thread
        super("ActivityRecognitionIntentService");
    }

    /**
     * Called when a new activity detection update is available.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG,"onHandleIntent");
        // Get a handle to the repository
        mPrefs = getApplicationContext().getSharedPreferences(
                "com.example._precious.SHARED_PREFERENCES", Context.MODE_PRIVATE);

        // Get a date formatter, and catch errors in the returned timestamp
        try {
            mDateFormat = (SimpleDateFormat) DateFormat.getDateTimeInstance();
        } catch (Exception e) {
            Log.e(TAG, getString(R.string.date_format_error));
        }

        // Format the timestamp according to the pattern, then localize the pattern
        mDateFormat.applyPattern(DATE_FORMAT_PATTERN);
        mDateFormat.applyLocalizedPattern(mDateFormat.toLocalizedPattern());

        // If the intent contains an update
        if (ActivityRecognitionResult.hasResult(intent)) {

            // Get the update
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            // Log the update
            logActivityRecognitionResult(result);

            // Get the most probable activity from the list of activities in the update
            DetectedActivity mostProbableActivity = result.getMostProbableActivity();

            // Get the type of activity
            int activityType = mostProbableActivity.getType();

            // Check to see if the repository contains a previous activity
            if (!mPrefs.contains("com.example._precious.KEY_PREVIOUS_ACTIVITY_TYPE")) {

                // This is the first type an activity has been detected. Store the type
                Editor editor = mPrefs.edit();
                editor.putInt("com.example._precious.KEY_PREVIOUS_ACTIVITY_TYPE", activityType);
                editor.commit();
            }
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

            // Make a timestamp
            String timeStamp = mDateFormat.format(new Date());

            //ShowToastInIntentService(data);

            long timeSinceLastUpdate = mPrefs.getLong("com.example._precious.TIME_PREVIOUS_UPDATE",//TODO
                    DetectedActivity.UNKNOWN);
            long timeNow = System.currentTimeMillis();
            long difference = timeNow-timeSinceLastUpdate;
            Editor editor = mPrefs.edit();
            editor.putLong("com.example._precious.TIME_PREVIOUS_UPDATE", timeNow); //TODO
            editor.commit();
//            writeStingInExternalFile(""+difference,"/TimeDifference.txt");
            String status = timeNow+";"+activityName+";"+confidence;

            Log.i("STATUS",status);
            //Saving or not data, based on threshold on the confidence detection of each activity
            if( !activityName.equals("unknown") &&  activityName!=null  &&
                    (
                            (confidence >= 70 && activityName.equals("running"))
                                    || (confidence >= 60 && activityName.equals("on_bicycle")) //Bicycle threshold to be studied better!
                                    || (confidence >= 100 && activityName.equals("tilting"))
                                    || (confidence >= 45 && activityName.equals("walking"))
                                    || (confidence >= 70 && activityName.equals("in_vehicle"))
                                    || (confidence >= 80 && activityName.equals("still"))
                    )
                    ){
                writeStingInExternalFile(status,"/Log2File.txt") ;
                writeStingInExternalFile(status,"/ViewerLogFile.txt") ;
                writeStingInExternalFile(status,"/ServerActivity.txt") ;
//                detectSleepingPattern(editor,timeNow,activityType);
            }
//            writeStingInExternalFile(status, "/Log2File.txt");
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

//    public void ShowToastInIntentService(final String sText){
//        final Context MyContext = this;
//        new Handler(Looper.getMainLooper()).post(new Runnable()
//        {  @Override public void run()
//            {  Toast toast1 = Toast.makeText(MyContext, sText, Toast.LENGTH_SHORT);
//                toast1.show();
//            }
//        });
//    }

    /**
     *  Writes a string line in a file in external memory
     * filename specifies both location and name of the file
     * example: filename="/folder1/myfile.txt"
     */
    public void writeStingInExternalFile(String data, String fileName){
        try {
            if(isExternalStorageWritable()){
                File ext_storage = Environment.getExternalStorageDirectory();
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
                    Log.i("File "+fileName, "Stored "+data);
                }
                else Log.e("ActivityRecIntent","unable create folder/file");
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

    /**
     *
     */
    public void detectSleepingPattern(Editor editor, long timeNow, int activityType){
        //Update information over last activity and timestamp, used to recognize sleeping patterns

        //If there is no information, break
        if( mPrefs.getInt("com.example._precious.KEY_PREVIOUS_ACTIVITY_TYPE",-1)==-1 ||
                mPrefs.getLong("com.example._precious.KEY_PREVIOUS_ACTIVITY_TIMESTAMP",-1)==-1){
            editor.putLong("com.example._precious.KEY_PREVIOUS_ACTIVITY_TIMESTAMP", timeNow);
            editor.putInt("com.example._precious.KEY_PREVIOUS_ACTIVITY_TYPE", activityType);
            editor.commit();
            return;
        }

        //If activity has changed, calculate time duration of previous activity
        if (activityType!=mPrefs.getInt("com.example._precious.KEY_PREVIOUS_ACTIVITY_TYPE",-1)){
            Calendar c=Calendar.getInstance();
            c.setTimeInMillis(timeNow);
            int hour_24=c.get(Calendar.HOUR_OF_DAY);
            Log.e("PRECIOUS","NOTIFICATION NOT SENT YET"+
                    (timeNow-mPrefs.getLong("com.example._precious.KEY_PREVIOUS_ACTIVITY_TIMESTAMP",-1))+
                    hour_24+mPrefs.getBoolean("com.example._precious.GOODMORNING_NOTIF_SENT", false));
            if( (hour_24>=WAKEUP_TIME_LATE || hour_24<WAKEUP_TIME_EARLY) && mPrefs.getBoolean("com.example._precious.GOODMORNING_NOTIF_SENT", true)){
//                sendGoodmorningNotif(true);TODO
                editor.putBoolean("com.example._precious.GOODMORNING_NOTIF_SENT", false);
                editor.commit();
            }
            else if(timeNow-mPrefs.getLong("com.example._precious.KEY_PREVIOUS_ACTIVITY_TIMESTAMP",-1)>=MIN_TIME_SLEEP
                    && mPrefs.getInt("com.example._precious.KEY_PREVIOUS_ACTIVITY_TYPE",-1)==DetectedActivity.STILL
                    && hour_24>WAKEUP_TIME_EARLY && hour_24<WAKEUP_TIME_LATE
                    && !mPrefs.getBoolean("com.example._precious.GOODMORNING_NOTIF_SENT", false)) {
                //Good morning notification
//                sendGoodmorningNotif(false);TODO
                Log.e("PRECIOUS","NOTIFICATION SENT!"+
                        (timeNow-mPrefs.getLong("com.example._precious.KEY_PREVIOUS_ACTIVITY_TIMESTAMP",-1))+
                        hour_24);
                editor.putBoolean("com.example._precious.GOODMORNING_NOTIF_SENT", true);
                editor.commit();
            }
            //update activity info
            editor.putLong("com.example._precious.KEY_PREVIOUS_ACTIVITY_TIMESTAMP", timeNow);
            editor.putInt("com.example._precious.KEY_PREVIOUS_ACTIVITY_TYPE", activityType);
            editor.commit();
        }
    }
    /**
     *
     */
//    TODO
//    private void sendGoodmorningNotif(boolean cancel){
//        Intent i = new Intent(this,aalto.comnet.thepreciousrecognition.GoodmorningService.class);
//        if(cancel && isMyServiceRunning(aalto.comnet.thepreciousrecognition.GoodmorningService.class)){
//            Log.i("SERVICE GM","STOPPED");
//            stopService(i);
//        }
//        else if(!cancel) {
//            Log.i("SERVICE GM","STARTED");
//            startService(i);
//        }
//    }
    /**
     *
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