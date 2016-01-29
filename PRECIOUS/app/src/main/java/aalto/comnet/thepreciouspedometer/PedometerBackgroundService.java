package aalto.comnet.thepreciouspedometer;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import theprecioussandbox.comnet.aalto.precious.R;


public class PedometerBackgroundService extends Service implements SensorEventListener
{
    private final static String TAG = "StepDetector";
    private float   mLimit = (float)6.66; //Sensibility
    //private final static double PONDERACION=1.5;
    
    private float   mLastValues[] = new float[3*2];
    private float escala;
    private float   mYOffset;

    private float   mLastDirections[] = new float[3*2];
    private float   mLastExtremes[][] = { new float[3*2], new float[3*2] };
    private float   mLastDiff[] = new float[3*2];
    private int     mLastMatch = -1;
    
    private Sensor mSensor;
    private SensorManager mSensorManager;
    
    private long lastTime;
	
	private NotificationCompat.Builder mBuilder;
	private NotificationManager mNotifyMgr;
	private int mNotificationId = 2113;
	
	private Intent resultIntent;
	private PendingIntent resultPendingIntent;
	
//	private double prevX=0,prevY=0,prevZ=0;
	
	private SharedPreferences prefs;
    
    //private ArrayList<StepListener> mStepListeners = new ArrayList<StepListener>();
	@Override
    public void onCreate() {
		resultIntent = new Intent(this, aalto.comnet.theprecioussandbox.MainActivity.class);
		resultPendingIntent =
				    PendingIntent.getActivity(
				    this,
				    0,
				    resultIntent,
				    PendingIntent.FLAG_UPDATE_CURRENT
				);

		 mBuilder = new NotificationCompat.Builder(this);
		 mBuilder.setContentIntent(resultPendingIntent)
         .setSmallIcon(R.drawable.walking_white)
         .setTicker(""+getString(R.string.step_count_notif))
         .setWhen(System.currentTimeMillis())
         .setAutoCancel(true)
         .setContentTitle(""+getString(R.string.counting_steps))
         .setContentText(getString(R.string.step_count_notif_content));
		 // Gets an instance of the NotificationManager service
		 mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		 startForeground(mNotificationId, mBuilder.build());	
		 
		 prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	}
	
    @SuppressLint("Wakelock") public int onStartCommand(Intent intenc, int flags, int idArranque) {
		//mStepDetector = new StepCountService();
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		lastTime = System.currentTimeMillis();
		registerDetector();
		
		
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		PowerManager.WakeLock lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SensorRead");
		lock.acquire(); //Never release!
		return START_STICKY;
	}
	
	 public void onDestroy() {
		  mNotifyMgr.cancel(mNotificationId);
	 }
	 
	 @Override
     public IBinder onBind(Intent intencion) {
           return null;
     }
	 
    public PedometerBackgroundService() {
          mYOffset = 480 * 0.5f;
          escala = - (480 * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }

    
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor; 
//        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//        if (!powerManager.isScreenOn())
        	synchronized (this) {
        		if( sensor.getType() == Sensor.TYPE_ACCELEROMETER){
        			
        			
                    float vSum = 0;
                    for (int i=0 ; i<3 ; i++) {
                    	final float v = mYOffset + event.values[i] * escala;
                        vSum += v;
                    }
                    int k = 0;
                    float v = vSum / 3;
                    
                    float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
                    if (direction == - mLastDirections[k]) {
                        // Direction changed
                        int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
                        mLastExtremes[extType][k] = mLastValues[k];
                        float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

                        if (diff > mLimit) {
                            
                            boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k]*2/3);
                            boolean isPreviousLargeEnough = mLastDiff[k] > (diff/3);
                            boolean isNotContra = (mLastMatch != 1 - extType);
                            
                            if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                            	long timeNow = System.currentTimeMillis();
                                long timeDifference = timeNow-lastTime;
                                lastTime = timeNow;
                                Log.i(TAG,""+"Step duration "+timeDifference);
                                //Toast.makeText(this, ""+timeDifference, Toast.LENGTH_SHORT).show();
                                if(timeDifference>300 && timeDifference<3000){
                                	//add step to total steps counter file
                                	//int totalSteps =
                                	writeStingInExternalFile("1", "/totalSteps.txt"); 
                                	writeStingInExternalFile(timeNow+"", "/serverSteps.txt"); 
                                	writeStingInExternalFile(timeNow+"", "/viewerSteps.txt"); 
                                	//increment number of session steps
                                	int tempSteps  = 1+prefs.getInt("aalto.comnet.thepreciouspedometer.TEMP_STEPS",0);
                                	Editor editor = prefs.edit();
                                    editor.putInt("aalto.comnet.thepreciouspedometer.TEMP_STEPS", tempSteps);
                                    editor.commit();
//                                    //notify
//                        			mBuilder.setContentIntent(resultPendingIntent)
//	                       	         .setSmallIcon(R.drawable.walking_white)
//	                       	         .setWhen(System.currentTimeMillis())
//	                       	         .setAutoCancel(true)
//	                       			 .setContentTitle("Counting steps")
//	                       			 .setContentText((int)(tempSteps*PONDERACION)+" session steps");
//		                       		 mNotifyMgr.notify(mNotificationId, mBuilder.build());                       		
                                }
                            
                                mLastMatch = extType;
                            }
                            else {
                                mLastMatch = -1;
                            }
                        }
                        mLastDiff[k] = diff;
                    }
                    mLastDirections[k] = direction;
                    mLastValues[k] = v;
                }
            }
        }
    private void registerDetector() {
    	mSensor = mSensorManager.getDefaultSensor(
    		Sensor.TYPE_ACCELEROMETER);
    	mSensorManager.registerListener(this,
    		mSensor,
    		SensorManager.SENSOR_DELAY_FASTEST);
    }
    
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /* Writes a string line in a file in external memory
	 * filename specifies both location and name of the file
	 * example: filename="/folder1/myfile.txt"  */
    public int writeStingInExternalFile(String data, String fileName){
    	int fileSize=0;
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
		             fileSize=(int)file.length();
        		 }
        		 else Log.e("ActivityRecognitionIntent","unable to create folder or file");
        	}
        } catch (Exception e) {
               Log.e("Error opening file", e.getMessage(), e);
        }
        return fileSize;
    }
    
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
}
