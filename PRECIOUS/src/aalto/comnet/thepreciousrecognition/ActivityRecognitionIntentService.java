package aalto.comnet.thepreciousrecognition;


import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import aalto.comnet.thepreciousproject.R;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Service that receives ActivityRecognition updates. It receives updates
 * in the background, even if the main Activity is not visible.
 */
public class ActivityRecognitionIntentService extends IntentService {

	public final static String TAG = "com.example._precious";
	
    // Formats the timestamp in the log
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss.SSSZ";

    // Delimits the timestamp from the log info
    private static final String LOG_DELIMITER = ";;";

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
            String data = timeStamp +
                    LOG_DELIMITER +
                    getString(R.string.log_message, activityType, activityName, confidence);
            //ShowToastInIntentService(data);  
            
            long timeSinceLastUpdate = mPrefs.getLong("com.example._precious.TIME_PREVIOUS_UPDATE",
                    DetectedActivity.UNKNOWN);
            long timeNow = System.currentTimeMillis();
            long difference = timeNow-timeSinceLastUpdate;
            Editor editor = mPrefs.edit();
            editor.putLong("com.example._precious.TIME_PREVIOUS_UPDATE", timeNow);
            editor.commit();
            writeStingInExternalFile(""+difference,"/TimeDifference.txt");
            writeStingInExternalFile(data,"/LogFile.txt");
            
            Log.i("STATUS",data);
          //Saving or not data, based on threshold on the confidence detection of each activity            
            if( activityName!="unknown" &&  activityName!=null  && 
            		( 
	        			   (confidence >= 70 && activityName=="running")
	        			|| (confidence >= 60 && activityName=="on_bicycle") //Bicycle threshold to be studied better!
	        			|| (confidence >= 100 && activityName=="tilting")
	        			|| (confidence >= 45 && activityName=="walking") 
	        			|| (confidence >= 70 && activityName=="in_vehicle") 
	        			|| (confidence >= 80 && activityName=="still")
            		)
    		){
            	writeStingInExternalFile(timeNow+";"+activityName+";","/Log2File.txt") ;
            	writeStingInExternalFile(timeNow+";"+activityName+";","/ViewerLogFile.txt") ;
            	writeStingInExternalFile(timeNow+";"+activityName+";","/ServerActivity.txt") ;
            }            
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
    
	public void ShowToastInIntentService(final String sText){
		final Context MyContext = this;
	     new Handler(Looper.getMainLooper()).post(new Runnable()
	     {  @Override public void run()
	        {  Toast toast1 = Toast.makeText(MyContext, sText, Toast.LENGTH_SHORT);
	           toast1.show(); 
	        }
	     });
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
					 String texto = data + "\n";
					 f.write(texto.getBytes());
					 f.close();
		             Log.i("File "+fileName, "Stored "+data);
        		 }
        		 else Log.e("ActivityRecognitionIntent","unable to create folder or file");
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
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
}
