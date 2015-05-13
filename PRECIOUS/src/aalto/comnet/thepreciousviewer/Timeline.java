package aalto.comnet.thepreciousviewer;


import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import aalto.comnet.thepreciousproject.R;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
 
@SuppressLint("SimpleDateFormat")
public class Timeline extends ListActivity{//Activity {
	
	
	public LocationManager locationManager;
	public LocationListener locationListener;


	private static Vector <String> LogVectorOverview = new Vector<String>();
	private static Vector <Integer> LogVectorActivityOverview = new Vector<Integer>();
	
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline_layout);
        
        SharedPreferences prefs =this.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String userID = prefs.getString("user_id","???");
        TextView textV = (TextView) findViewById(R.id.textViewUserID);
    	textV.setText(getString(R.string.user_id)  + userID ); 
            }
    
    @Override
    protected void onResume(){  
    	//Draw physical activity
        drawActivity();
        startLocationUpdates(); 
        super.onResume();
    }
    
    @Override
    protected void onPause() {
    	stopLocationUpdates();
        super.onPause();
    }
  
	/**
	 * 
	 */
    public void drawActivity (){
    	//Clear data
    	LogVectorOverview.clear();
	  	LogVectorActivityOverview.clear();
    	String line="";
    	long startTime=0;
    	long time=0;
    	long activityDuration=0;
    	long duration=0;
    	int activityType = 0;
    	int previousActivityType = -1;
    	Vector<String> LogTimeline = MainActivity.getTimeline();
    	for (int i=0;i<LogTimeline.size();i++){
    		line = LogTimeline.get(i);
    		time= Long.parseLong(line.substring(0,line.indexOf(";")));
	   		line = line.substring(line.indexOf(";")+1);
	   		duration = Long.parseLong(line.substring(0,line.indexOf(";")))/1000;
	   		line = line.substring(line.indexOf(";")+1);
	   		activityType = Integer.parseInt(line.substring(0,line.indexOf(";")));
	   		//Log.i("TIMELINE",time+" "+duration+" "+activityType+"");
	   		if(previousActivityType!=activityType || i==LogTimeline.size()-1){	
	   			if(previousActivityType!=-1){
	   				Calendar c = Calendar.getInstance();
	   				c.setTimeInMillis(startTime);
	   				//***if( (activityDuration>300 && previousActivityType!=1) || //TODO change made on 2 April 2015
	   						//***(activityDuration>3*60*60 && previousActivityType==1) ){//TODO change made on 2 April 2015
	   				if( (activityDuration>300 && previousActivityType!=1  && previousActivityType!=6) ||		
	   						(activityDuration>3*60*60 && previousActivityType==1  && previousActivityType!=6) ){
	   						//Log.i("TIMELINE",activityDuration+" "+previousActivityType+"");
	   					LogVectorOverview.add(getString(R.string.start_at)+" " + c.get(Calendar.HOUR_OF_DAY) + "h"+
	   						c.get(Calendar.MINUTE) + getString(R.string.min_duration)+" "+ getStringTime(activityDuration));
	   				LogVectorActivityOverview.add(previousActivityType);
	   				}
	   			}
	   			startTime=time;
   				activityDuration=duration;
	   		}
	   		else{
	   			activityDuration += duration;
	   		}
	   		previousActivityType=activityType;
    	}
    	//Invert vectors
    	Collections.reverse(LogVectorOverview);
    	Collections.reverse(LogVectorActivityOverview);
    	setListAdapter(new aalto.comnet.thepreciousviewer.TimelineAdaptor(this,
        		LogVectorOverview,LogVectorActivityOverview));
    }
    
    /**
     * 
     * 
     * 
     */
//    @Override protected void onListItemClick(ListView listView, 
//            View view, int position, long id) {
//    	
//		super.onListItemClick(listView, view, position, id);
//		//Object o = getListAdapter().getItem(position);
//		if(invertedLog==false){
//			Collections.reverse(LogVectorActivityTime);
//			invertedLog=true;
//		}
//		long time = LogVectorActivityTime.get(position);
//		Intent i = new Intent (this, ShowActivityOnMap.class);
//		i.putExtra("time", time);
//		startActivity(i);
//	}
//    
//    public void saveLog(String data, String fileName){
//        try {
//      	     File file = new File(getFilesDir(), fileName);
//      	     if(!file.exists())
//      	     	file.createNewFile();
//               FileOutputStream f = openFileOutput(fileName,Context.MODE_APPEND);
//               String texto = data + "\n";
//               f.write(texto.getBytes());
//               f.close();
//               Log.i("File "+fileName, "Stored "+data);
//        } catch (Exception e) {
//               Log.e("Error opening file", e.getMessage(), e);
//        }
//  }
    
    
    /**
     * 
     * START LOCATION SERVICES
     * 
     */
    
    /* 
     * Get the location from the NETWORK (uses less battery than GPS) 
     */
    public void getLocation(){
    	
    	// Acquire a reference to the system Location Manager
    	
    	locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);        	
    	
    	// Define a listener that responds to location updates
    	locationListener = new LocationListener() {
    	    public void onLocationChanged(Location location) {
    	      // Called when a new location is found by the network location provider.
    	      //makeUseOfNewLocation(location);
    	    	MiThread thread = new MiThread(location);
                thread.start();
    	    }

    	    public void onStatusChanged(String provider, int status, Bundle extras) {}
    	    public void onProviderEnabled(String provider) {}
    	    public void onProviderDisabled(String provider) {}
    	  };
    }
    
    public void startLocationUpdates(){
    	try{
	    	getLocation();
	    	// Register the listener with the Location Manager to receive location updates
	    	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    	}catch (Exception e){
    		Log.e("MainAcitivy startLocationUpdates", "No WIFI?");
    	}
    }
    
    public void stopLocationUpdates(){
    	try{
    		locationManager.removeUpdates(locationListener);
    	} catch (Exception e){
    		Log.e("stopLocationUpdates","No WIFI?");
    	}
    }
    
    class MiThread extends Thread {

        private Location location;
        public MiThread(Location location) {
               this.location = location;
        }
    @Override public void run() {
    	final Context context = getApplicationContext();

    	Geocoder gCoder = new Geocoder(context);
    	try{
	    	final List<Address> addresses = gCoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
	    	if (addresses != null && addresses.size() > 0) {
	    		
	    		runOnUiThread(new Runnable() {

	                 @Override public void run() {

	                	 TextView tv = (TextView) findViewById(R.id.textViewAdress);
		    	    		tv.setText(addresses.get(0).getAddressLine(0)/*+", "+addresses.get(0).getLocality()*/);

	                }
	             });	    			    		
	    	    //Toast.makeText(context, "city: " + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
	    		//Toast.makeText(context, "Adress" + addresses.get(0).getAddressLine(0), Toast.LENGTH_LONG).show();
	    	}
    	}catch (Exception e){
    		stopLocationUpdates();    				    		
    		Log.e("DrawChart getLocation","Error encontrando ubicación actual",e);				    		
    	}			
    }

  }
    /**
     * 
     * END LOCATION SERVICES
     * 
     */
    
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
    /**
     *  Get activity duration from time in seconds
     */    
	public String getStringTime (long date){
		  int durationHours = (int)(date/3600);
		  int durationMin = (int)(date%3600/60);
		  int durationSec = (int)(date%60);
		  return durationHours + "h"+durationMin+"m"+durationSec+"s";
	}
}
