package aalto.comnet.thepreciousviewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import aalto.comnet.thepreciousproject.R;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	private static final String AppVersion="109";
	private static final double PONDERACION = 1.5;
	private static final int LPF_size = 3; //Size if the low-pass filter. Must be at least 2!
	//Location manager and listener, needed to get current location via Wifi network
	private LocationManager locationManager;
	private LocationListener locationListener;
	//Vector for data storage
	private static Vector <String> LogVectorDateTimeline = new Vector<String>();
	private static Vector <String> LogVectorDayResult = new Vector<String>();
	private static Vector <String> LogVectorStill = new Vector<String>();
	private static Vector <String> LogVectorWalk = new Vector<String>();
	private static Vector <String> LogVectorBicycle = new Vector<String>();
	private static Vector <String> LogVectorVehicle = new Vector<String>();
	private static Vector <String> LogVectorRun = new Vector<String>();
	private static Vector <String> LogVectorTilting = new Vector<String>();
	//Text view to show pedometer data
//	private static TextView tvStepTemp;
//	private static TextView tvStepTotal;
	private static TextView tvStepDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);      
        setContentView(R.layout.activity_main);
        
        //Check if this is the first time the app is started and if so, run user profile configuration
        SharedPreferences prefs =this.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String previousVersion = prefs.getString("previous_version", "0");
        if(!previousVersion.equals(AppVersion))
        	firstStartConfig();
    }
    
	@Override
	public void onResume(){		
		super.onResume();	
				
        startLocationUpdates();
        //Get Info      
        getLog();
        chooseDay();
		//Update step count info
        //showTempTotalSteps();
        //for(int cont=0;cont<LogVectorDateTimeline.size();cont++)
        	//Log.i("TIMELINE", LogVectorDateTimeline.get(cont));
	}
	
    @Override
    protected void onPause() {
    	stopLocationUpdates();
        super.onPause();
    }
    
    
    /**      
     *  Choose the day for drawing the physical activity
     */
    private void chooseDay(){
        ArrayList<String> spinnerArray = new ArrayList<String>();
        for (int i=0; i<LogVectorDayResult.size();i++)
        {
        	spinnerArray.add(LogVectorDayResult.get(i));
        }
        Spinner spinner = (Spinner)findViewById(R.id.spinnerMain);        
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(spinner.getAdapter().getCount()-1);
        
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            	showDayInfo(pos);  
            	showDaySteps();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    
    /**
     *  Draw images, bars and textViews depending on the duration of each physicla activity
     */
    
    public void showDayInfo(int location){
    	/*
    	 * Get day of the week and calculate week activity durations
    	 */
    	String dayOfWeek = LogVectorDayResult.get(location);
    	dayOfWeek = dayOfWeek.substring(0, dayOfWeek.indexOf(","));
    	int numDaysWeek = (dayOfWeek.equals("Monday"))?0: (dayOfWeek.equals("Tuesday"))?1: (dayOfWeek.equals("Wednesday"))?2:
    		(dayOfWeek.equals("Thursday"))?3: (dayOfWeek.equals("Friday"))?4: (dayOfWeek.equals("Saturday"))?5: 6;
    	
    	int walkWeekDuration = 0; 
    	int runWeekDuration = 0;
    	int bikeWeekDuration = 0;
    	for (int i=0;i<=numDaysWeek;i++)
    		if(location-i>=0){
    			walkWeekDuration += Integer.parseInt(LogVectorWalk.get(location-i));
    			runWeekDuration += Integer.parseInt(LogVectorRun.get(location-i));
    			bikeWeekDuration += Integer.parseInt(LogVectorBicycle.get(location-i));
    		}
    	for (int i=1;i<7-numDaysWeek;i++)
    		if((location+i)<LogVectorWalk.size()){
    			walkWeekDuration += Integer.parseInt(LogVectorWalk.get(location+i));
    			runWeekDuration += Integer.parseInt(LogVectorRun.get(location+i));
    			bikeWeekDuration += Integer.parseInt(LogVectorBicycle.get(location+i));
    		}
    	/*
    	 * Set text info
    	 */
    	TextView tvWalk = (TextView) findViewById(R.id.textViewWalk);
    	tvWalk.setText(getStringTime(Integer.parseInt(LogVectorWalk.get(location))));    	
    	TextView tvWalkWeek = (TextView) findViewById(R.id.textViewWalkWeek);
    	tvWalkWeek.setText(getString(R.string.weekly)+getStringTime(walkWeekDuration));
    	
    	TextView tvRun = (TextView) findViewById(R.id.textViewRun);
    	int runTime=Integer.parseInt(LogVectorRun.get(location));
    	if(runTime<300)
    		runTime=0;
    	tvRun.setText(getStringTime(runTime));
    	TextView tvWalkRun = (TextView) findViewById(R.id.textViewRunWeek);
    	tvWalkRun.setText(getString(R.string.weekly)+getStringTime(runWeekDuration));
    	
    	TextView tvBicycle = (TextView) findViewById(R.id.textViewBicycle);
    	int bikeTime=Integer.parseInt(LogVectorBicycle.get(location));
    	if(bikeTime<300)
    		bikeTime=0;
    	tvBicycle.setText(getStringTime(bikeTime));
    	TextView tvWalkBicycle = (TextView) findViewById(R.id.textViewBicycleWeek);
    	tvWalkBicycle.setText(getString(R.string.weekly)+getStringTime(bikeWeekDuration));
    	
    	TextView tvVehicle = (TextView) findViewById(R.id.textViewVehicle);
    	int vehicleTime=Integer.parseInt(LogVectorVehicle.get(location));
    	if(vehicleTime<300)
    		vehicleTime=0;
    	tvVehicle.setText(getStringTime(vehicleTime));
    	
    	TextView tvPhone = (TextView) findViewById(R.id.textViewPhone);
    	tvPhone.setText(getStringTime(Integer.parseInt(LogVectorTilting.get(location))));
    	
    	TextView tvSleep= (TextView) findViewById(R.id.textViewSleep);
    	tvSleep.setText(getStringTime(Integer.parseInt(LogVectorStill.get(location)))); 
    	/*
    	 *  Set progress bars
    	 */
    	SharedPreferences pref = getApplicationContext().getSharedPreferences(
        		"com.example._precious.SHARED_PREFERENCES", Context.MODE_PRIVATE);	
    	//Walk bar
        ProgressBar progressBarWalk = (ProgressBar) findViewById(R.id.progressBarWalk);
        float walkingTime = Float.valueOf(LogVectorWalk.get(location))/3600;
        float desiredWalkingTime = 24;
        try{
        	desiredWalkingTime = pref.getFloat("walkHours", 0);
        }catch (Exception e){
        	Log.e("Preferencias", "Problema conversión a entero",e);
        }
        int progressWalk = (int) (100*walkingTime/desiredWalkingTime) ;
        if (progressWalk < 50)
        	progressBarWalk.getProgressDrawable().setColorFilter(Color.RED,  Mode.MULTIPLY);
        else if (progressWalk < 80)
        	progressBarWalk.getProgressDrawable().setColorFilter(Color.YELLOW,  Mode.MULTIPLY);
        else
        	progressBarWalk.getProgressDrawable().setColorFilter(Color.GREEN,  Mode.MULTIPLY);
        progressBarWalk.setProgress(progressWalk);
        
        //Run bar
        ProgressBar progressBarRun = (ProgressBar) findViewById(R.id.progressBarRun);
        float runningTime = Float.valueOf(runWeekDuration)/3600;
        float desireRunningTime = 24;
        try{
        	desireRunningTime = pref.getFloat("runHours", 0);
        }catch (Exception e){
        	Log.e("Preferencias", "Problema conversión a entero",e);
        }
        int progressRun = (int) (100*runningTime/desireRunningTime) ;
        if (progressRun < 50)
        	progressBarRun.getProgressDrawable().setColorFilter(Color.RED,  Mode.MULTIPLY);
        else if (progressRun < 80)
        	progressBarRun.getProgressDrawable().setColorFilter(Color.YELLOW,  Mode.MULTIPLY);
        else
        	progressBarRun.getProgressDrawable().setColorFilter(Color.GREEN,  Mode.MULTIPLY);
        progressBarRun.setProgress(progressRun);
        
        //Bicycle bar
        ProgressBar progressBarBicycle = (ProgressBar) findViewById(R.id.progressBarBicycle);
        float bicycleTime = Float.valueOf(bikeWeekDuration)/3600;
        float desireBicycleTime = 24;
        try{
        	desireBicycleTime = pref.getFloat("bicycleHours", 0);
        }catch (Exception e){
        	Log.e("Preferencias", "Problema conversión a entero",e);
        }
        int progressBicycle = (int) (100*bicycleTime/desireBicycleTime) ;
        if (progressBicycle < 50)
        	progressBarBicycle.getProgressDrawable().setColorFilter(Color.RED,  Mode.MULTIPLY);
        else if (progressBicycle < 80)
        	progressBarBicycle.getProgressDrawable().setColorFilter(Color.YELLOW,  Mode.MULTIPLY);
        else
        	progressBarBicycle.getProgressDrawable().setColorFilter(Color.GREEN,  Mode.MULTIPLY);
        progressBarBicycle.setProgress(progressBicycle);        
    	/*
    	 *  Set images
    	 */
        ImageButton walkingImage = (ImageButton) findViewById(R.id.walkingButton);
        walkingImage.setOnClickListener( new OnClickListener() {
            public void onClick(View v) {
            	onCLickWalking ();
             }
              });
        if(progressWalk>80)
        	walkingImage.setBackgroundResource(R.drawable.walking_verry_happy);
        else if (progressWalk>50)
        	walkingImage.setBackgroundResource(R.drawable.walking_happy);
        else //if (progressWalk>25)
        	walkingImage.setBackgroundResource(R.drawable.walking);
        
        ImageButton runningImage = (ImageButton) findViewById(R.id.runButton);
        runningImage.setOnClickListener( new OnClickListener() {
            public void onClick(View v) {
            	onCLickRunning ();
             }
              });
        if(progressRun>100)
        	runningImage.setBackgroundResource(R.drawable.running_verry_happy);
        else if (progressRun>60)
        	runningImage.setBackgroundResource(R.drawable.running_happy);
        else //if (progressRun>25)
        	runningImage.setBackgroundResource(R.drawable.running);
        
        ImageButton bicycleImage = (ImageButton) findViewById(R.id.bikeButton);
        bicycleImage.setOnClickListener( new OnClickListener() {
            public void onClick(View v) {
            	onCLickBicycle();
             }
              });
        if(progressBicycle>100)
        	bicycleImage.setBackgroundResource(R.drawable.bicycle_verry_happy);
        else if (progressBicycle>60)
        	bicycleImage.setBackgroundResource(R.drawable.bicycle_happy);
        else //if (progressBicycle>25)
        	bicycleImage.setBackgroundResource(R.drawable.bicycle);            	    	
    }
    
    /**
     * 
     */
//    private void showTempTotalSteps(){
//    	tvStepTemp = (TextView) findViewById(R.id.stepCountTempTextView);
//    	tvStepTotal = (TextView) findViewById(R.id.stepCountTotalTextView); 
//    	
//    	//Update temporary steps info
//    	SharedPreferences prefsPedometer = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//    	int tempSteps  = prefsPedometer.getInt("aalto.comnet.thepreciouspedometer.TEMP_STEPS",0);
//    	tvStepTemp.setText(getString(R.string.session_steps)+" "+(int)(tempSteps*PONDERACION));
//    	//Update total steps info
//    	File ext_storage = Environment.getExternalStorageDirectory();
//		String extPath = ext_storage.getPath();
//		File folder = new File(extPath+"/precious");    	
//		File file = new File(folder, "totalSteps.txt");		
//	    //tvStepTotal.setText(getString(R.string.total_steps)+" "+(int)((file.length()/2)*PONDERACION));
//    }
    private void showDaySteps(){    
    	tvStepDay = (TextView) findViewById(R.id.stepCountDayTextView);
    	
    	/*
    	 * Process data from txt file
    	 */    	
    	String dateToday = getStringDate(System.currentTimeMillis());
    	int todayStepCount=0;
    	//Process step raw data and organize it
    	File ext_storage = Environment.getExternalStorageDirectory();
		String extPath = ext_storage.getPath();
    	File folder = new File(extPath+"/precious");    
    	try{
    		 
    		 boolean success = false;
    		 boolean newDay = false;
    		 if(!folder.exists())
    			 success = folder.mkdir();
    		 if(folder.exists() || success){
	      	     File file = new File (folder, "viewerSteps.txt");
	      	     if(!file.exists())
     	     		file.createNewFile();
	      	     FileInputStream f = new FileInputStream(file);
	      	     BufferedReader entrada = new BufferedReader(  new InputStreamReader(f));
	      	     String line="";      	     
	      	     
		  		String previousDate="-1";
		  		int stepCount=0;	  		
		  		while ((line = entrada.readLine()) != null) {			  
					  Long time = Long.parseLong(line);
					  String date = getStringDate(time);		
//					  Log.i("DATE STEPS",date);
//					  Log.i("DATE PREVIOUS STEPS",previousDate);
//					  Log.i("DATE TODAY STEPS",dateToday);
					  if (previousDate.equals("-1"))
						  previousDate=date;
					  if(date.equals(previousDate)){						  
						  if(date.equals(dateToday))
							  todayStepCount++;
						  else{
							  stepCount++;
							  newDay = true;
						  }
					  }				 
					  else{
						  //ensure that we are not recording data for today's day
						  if(!previousDate.equals(dateToday))
							  writeStingInExternalFile(previousDate+";"+stepCount, "/dateSteps.txt");
						  stepCount=1;					  
					  }
					  
					  previousDate=date;
		  		}
		  		f.close();  
		  		
		  		if(newDay){
			  		file = new File (folder, "viewerSteps.txt");
		      	    if(file.exists())
	     	     		file.delete();
			  		if(previousDate.equals(dateToday)){		  			
			      	    file.createNewFile();
			      	    for (int i=0;i<todayStepCount;i++)//TODO hacer que no esté abriendo y cerrando el fichero mil veces
			      	    	writeStingInExternalFile(System.currentTimeMillis()+"", "/viewerSteps.txt");		  			
			  		}
		  		}
    		 }
	  		
  		}catch (Exception e) {
  			Log.e("ShowDayStep","Error escribiendo/leyendo fichero",e);
		}
    	
    	/*
    	 * Show number of selected date's steps
    	 */
    	int steps=0;
    	try{
    		Spinner spinner = (Spinner)findViewById(R.id.spinnerMain);     		
    		if (spinner.getCount() < 1){
    			tvStepDay.setText(getString(R.string.today_steps)+" "+(int)(todayStepCount*PONDERACION));
    			//Log.i("SPINNER","spinner.getCount() < 1");
    		}
    		else{
	        	String text = spinner.getSelectedItem().toString();
	        	String date = text.substring(text.indexOf(",")+2);
	        	//Log.i("SPINNER",date);
	        	steps = findStepsInFile(date);
	        	if(steps==-1){
	        		if (date.equals(dateToday))
	        			tvStepDay.setText(getString(R.string.today_steps)+" "+(int)(todayStepCount*PONDERACION));
	        		else 
	        			tvStepDay.setText(getString(R.string.no_steps));
	        	}
	        	else
	        		tvStepDay.setText(getString(R.string.day_steps)+" "+(int)(steps*PONDERACION));
	        	
    		}
    	}catch (Exception e){     		
    		Log.e("MainActivity","Spinner problem",e);  			
    	}
    }
    
    /**
     * Read file, filter, reorganize information, make calculations over user's physical activity
     */    
    private void getLog (){    	
    	  //Read File
    	  Vector<String> LogVector = new Vector<String>();    	  
    	  try {
	    		 File ext_storage = Environment.getExternalStorageDirectory();
	     		 String extPath = ext_storage.getPath();
	     		 File folder = new File(extPath+"/precious");
	     		 boolean success = false;
	     		 if(!folder.exists())
	     			 success = folder.mkdir();
	     		 if(folder.exists() || success){
		      	     File file = new File (folder, "ViewerLogFile.txt");
		      	     if(!file.exists())
	      	     		file.createNewFile();
		      	     FileInputStream f = new FileInputStream(file);
		      	     BufferedReader entrada = new BufferedReader(  new InputStreamReader(f));
		      	     String line="";
		    		 while ((line = entrada.readLine()) != null) {
			    		 LogVector.add(line);
		    		 }
		    		 f.close();
	     		 }
     		 } catch (Exception e){
    	    	Log.e("getLog","Fichero ViewerLogFile.txt no existe",e);
    	  }  	  
    	  
    	  int [] detectedActivityBuffer = new int [LPF_size];//store last LPF_size detected activities
					//This will be used as a Low Pass Filter. Position 0 is the oldest activity
    	  			//and position LPF_size-1 is the newest
    	  
    	  for(int i=0;i<LPF_size;i++){
    		  detectedActivityBuffer[i]=0;
    	  }
    	  long startTime=0; //when the last detected activity started
    	  long stopTime=0; //when the last detected activity stopped
    	  long activityDuration=0; //duration of the last detected activity
    	  long activityDurationNewDay=0; //duration of the last detected activity when day has changed
    	  long activityDurationAcumul=0; //used in the low pass filter
    	  //duration of the activities during the current day
    	  long durationStill=0;
    	  long durationWalk=0;
    	  long durationBicycle=0;
    	  long durationVehicle=0;
    	  long durationRun=0;
    	  long durationTilting=0;    	  
    	  String currentDay=""; //stores the current day's date
    	  long timePast = 0; //Store last timestamp. If current timestamp's timing is before the last one, do not store the current one.
    	  					 //Explanation: Receive data with date 1/Jan/2010 13:15 and then receive data with date 1/Jan/2010 13:14
    	  					 //If this happens, no not store the data with date 1/Jan/2010 13:14
    	  //Store last day and month
    	  int dayLast = 0;
    	  int monthLast = 0;
    	  int yearLast = 0;
    	  
    	  //Clear vector data
    	  LogVectorDateTimeline.clear();
    	  LogVectorDayResult.clear();
    	  LogVectorStill.clear();
    	  LogVectorWalk.clear();  	  
    	  LogVectorBicycle.clear();
    	  LogVectorVehicle.clear();
    	  LogVectorRun.clear();
    	  LogVectorTilting.clear();  
    	  int deleteIndex=0; //Ones data is processed, delete it from txt file leaving only today's data
    	  
    	  //Process every line on the file and organize physical activity data
    	  for (int i=0 ; i<LogVector.size() ; i++){
				String Line = LogVector.elementAt(i);
				long time;
				try{
					time = Long.parseLong ( Line.substring(0, 13) );
				}catch (Exception e){
					continue;
				}
				Line = Line.substring(Line.indexOf(";")+1);
				String detectedActivity = Line.substring(0,Line.indexOf(";"));
				
				if (detectedActivity.equals("still")){
					detectedActivityBuffer[LPF_size-1]=1;
					}
				else if (detectedActivity.equals("walking")){
					detectedActivityBuffer[LPF_size-1]=2;
					}
				else if (detectedActivity.equals("on_bicycle")){
					detectedActivityBuffer[LPF_size-1]=3;
				  	}
				else if (detectedActivity.equals("in_vehicle")){
					detectedActivityBuffer[LPF_size-1]=4;
				  	}
				else if (detectedActivity.equals("running")){
					detectedActivityBuffer[LPF_size-1]=5;
					}
				else if (detectedActivity.equals("tilting")){
					detectedActivityBuffer[LPF_size-1]=6;
				  	}
				else detectedActivityBuffer[LPF_size-1]=0;

    		  if(timePast==0)
    			  timePast = time;
    		  //Check if there is a problem with the timeline
    		  else if (timePast>time){
    			  //Inform about the problem and do not store current data
    			  Log.i("DRAWCHART","Skipping Line "+time);
    			  Log.i("DRAWCHART","Last good timestamp was "+timePast);
    			  continue;
    		  	}
    		  	else timePast=time;   
    		  
    		  Calendar c = Calendar.getInstance(); //get calendar instance    		  
    		  c.setTimeInMillis(time); //set current time to the calendar instance
    		  
		  	  int dayCurrent = c.get(Calendar.DAY_OF_MONTH);	
    		  int monthCurrent = c.get(Calendar.MONTH)+1;
    		  int yearCurrent = c.get(Calendar.YEAR); 
    		  int mHourOfDay = c.get(Calendar.HOUR_OF_DAY);
              int mMinute = c.get(Calendar.MINUTE);
              int mSecond = c.get(Calendar.SECOND);
    		  
    		  //Check if day or month has changed and mark it with a the flag newDay
    		  boolean newDay = false;
    		  if(dayLast==0 && monthLast==0 && yearLast==0){
    			  dayLast = dayCurrent;
    			  monthLast = monthCurrent;
    			  yearLast = yearCurrent;
    		  }
    		  else if (dayLast!=dayCurrent || monthLast!=monthCurrent || yearLast!=yearCurrent){
    		  			newDay = true;
    		  			dayLast=dayCurrent;		
    		  			monthLast=monthCurrent;
    		  			yearLast=yearCurrent;
    		  }
    		  
			  stopTime=time; //Store timestamp			  

			  //Check if day or month or year has changed or if this is the last line of the  txt file
			  //If so, get previous date information
			  if (newDay || i==LogVector.size()-1){
                	if(!newDay && i==LogVector.size()-1)
                		c.setTimeInMillis(time);
                	else
                		c.setTimeInMillis(time-24*60*60*1000);
                	
                	int iYear = c.get(Calendar.YEAR); 
   				    int iMonth = c.get(Calendar.MONTH)+1;
   				    int iDay = c.get(Calendar.DAY_OF_MONTH);  
   				    int iDayWeek = c.get(Calendar.DAY_OF_WEEK);
   				    String sYear = ""+iYear;
   				    String sMonth = (iMonth>9)? ""+iMonth : "0"+iMonth;
   				    String sDay = (iDay>9)? ""+iDay : "0"+iDay;
   				    String sDayWeek="";
   				    switch (iDayWeek){
	   				    case 2	:	sDayWeek=getString(R.string.monday);break;
		   				case 3	:	sDayWeek=getString(R.string.tuesday);break;
		   				case 4	:	sDayWeek=getString(R.string.wednesday);break;
		   				case 5	:	sDayWeek=getString(R.string.thursday);break;
		   				case 6	:	sDayWeek=getString(R.string.friday);break;
		   				case 7	:	sDayWeek=getString(R.string.saturday);break;
		   				case 1	:	sDayWeek=getString(R.string.sunday);break;
		   				default	:	sDayWeek=null;break;
   				    }    	   				    
   				    currentDay = sDayWeek+", "+sDay+" "+ sMonth+" "+sYear;
   				    
              }
				  
			  //When new day arrives, store the date in the vector and calculate activity duration of previous day
			  if(newDay){
				  activityDurationNewDay = 1000* (mHourOfDay*3600 + mMinute*60 + mSecond);
				  activityDuration = ((stopTime-startTime) - activityDurationNewDay);
			  }
			  else{
				  activityDurationNewDay = 0;
				  activityDuration = (stopTime-startTime);
			  }  			  
			  		  
			  String timeLine=startTime+"";//Convert startTime into String
		  	  startTime=time;//Update startTime 
              c.setTimeInMillis(time);//Unsure to set current time in the calendar
              
              //If the last update was more than 5 min ago, store data as unknown activity (phone was off or it was not responding)
			  if (activityDurationNewDay>5*60*1000 || activityDuration>5*60*1000){
				  Log.i("BUFFER","SKIPPING DURATION: "+activityDuration+ " "+activityDurationNewDay);
				  activityDuration = 1;
				  activityDurationNewDay = 1;
			  }
			  
              //LPF is not applicable for walking,running or vehicle. For example, if user is walking and at the same time texting a SMS
              //the activity data will have the following format: [WALKING][TILTING][WALKING].That case is considered to be walking activity.
              //On the other hand, when driving a car there will be stops/traffic lights which will lead to the format [VEHICLE][STILL][VEHICLE]
              //which will be considered as staying in the vehicle.              
              //LPF is applicable to still, tilting, bicycle and unknown states. Still and tilting are ignored if user is driving and stopping
              //for a while or texting and walking at the same time. Furthermore, some user movements are confused with bicycle state, so filter
              //is needed there too. Finally, unknown state is ignored. 
//              Log.i("BUFFER","*******************************");
//              Log.i("BUFFER","activityDuration= "+activityDuration);
//              Log.i("BUFFER","activityDurationAcumul= "+activityDurationAcumul);
//              Log.i("BUFFER","bufer= "+detectedActivityBuffer[0]+" "+detectedActivityBuffer[1]+" "+detectedActivityBuffer[2]);//+" "+detectedActivityBuffer[3]+" "+detectedActivityBuffer[4]);
			  switch(detectedActivityBuffer[LPF_size-2]){
			  case 1	:	//Still: apply low pass filter
				  			boolean lowPass1 = true;//flag used to create a low-pass filter
				  			for (int j=0;j<(LPF_size-1);j++){
				  				if(detectedActivityBuffer[j]!=1)
				  					lowPass1=false;
				  			}
				  			if(lowPass1){
				  				durationStill = durationStill+activityDuration+activityDurationAcumul;
				  				activityDurationAcumul = 0;
				  				LogVectorDateTimeline.add(timeLine+";"+activityDuration+";"+detectedActivityBuffer[LPF_size-2]+";");
				  			}
				  			else activityDurationAcumul += activityDuration;			  				 
			  				break;
			  case 2	:	//Walking:If previous detected activity is walking, do not apply filter and store data 
				  			durationWalk = durationWalk + activityDuration+ activityDurationAcumul;
				  			activityDurationAcumul = 0;
			  				LogVectorDateTimeline.add(timeLine+";"+(activityDuration+activityDurationAcumul)+";"+detectedActivityBuffer[LPF_size-2]+";");
			  				break;
			  case 3	:	//Bicycle: apply low pass filter
				  			boolean lowPass2 = true;//flag used to create a low-pass filter
				  			for (int j=0;j<(LPF_size-1);j++){
				  				if(detectedActivityBuffer[j]!=3)
				  					lowPass2=false;
				  			}
				  			if(lowPass2){
				  				durationBicycle = durationBicycle+activityDuration+activityDurationAcumul;
				  				activityDurationAcumul = 0;
				  				LogVectorDateTimeline.add(timeLine+";"+activityDuration+";"+detectedActivityBuffer[LPF_size-2]+";");
				  			}
				  			else activityDurationAcumul += activityDuration;			  				 
			  				break;
			  case 4	:	//Vehicle previous detected activity is walking, do not apply filter and store data 
				  			durationVehicle = durationVehicle + activityDuration+ activityDurationAcumul;
				  			activityDurationAcumul = 0;
			  				LogVectorDateTimeline.add(timeLine+";"+(activityDuration+activityDurationAcumul)+";"+detectedActivityBuffer[LPF_size-2]+";");
			  				break;
			  case 5	:	//Running previous detected activity is walking, do not apply filter and store data 
				  			durationRun = durationRun + activityDuration+ activityDurationAcumul;
				  			activityDurationAcumul = 0;
			  				LogVectorDateTimeline.add(timeLine+";"+(activityDuration+activityDurationAcumul)+";"+detectedActivityBuffer[LPF_size-2]+";");
			  				break;
			  case 6	:	//Tilting: apply low pass filter
				  			boolean lowPass3 = true;//flag used to create a low-pass filter
				  			for (int j=0;j<(LPF_size-1);j++){
				  				if(detectedActivityBuffer[j]!=6)
				  					lowPass3=false;
				  			}
				  			if(lowPass3){
				  				durationTilting = durationTilting+activityDuration+activityDurationAcumul;
				  				activityDurationAcumul = 0;
				  				LogVectorDateTimeline.add(timeLine+";"+activityDuration+";"+detectedActivityBuffer[LPF_size-2]+";");
				  			}
				  			else activityDurationAcumul += activityDuration;			  				 
			  				break;
			  default	:	//Uknown state
				  			break;
			  }
			 	//Store day information into array in preferences
		  		if(newDay){		
		  			LogVectorDateTimeline.clear(); //Timeline only represent todays information
		  			
		  			writeStingInExternalFile(currentDay+";"+(int)(durationStill/1000)+";"+(int)(durationWalk/1000)+";"
		  					+(int)(durationBicycle/1000)+";"+(int)(durationVehicle/1000)+";"+(int)(durationRun/1000)+";"
		  					+(int)(durationTilting/1000),"dateActivity.txt");		
					    
					//Here low-pass filter is not applied (this happens once a day so it is negligible)
				    switch(detectedActivityBuffer[LPF_size-2]){
				    case 1	:	durationStill= activityDurationNewDay;durationWalk=0;durationBicycle=0;durationVehicle=0;durationRun=0; durationTilting=0;break;
				    case 2	:	durationStill=0;durationWalk= activityDurationNewDay;durationBicycle=0;durationVehicle=0;durationRun=0;durationTilting=0;break;
				    case 3	:	durationStill=0;durationWalk=0;durationBicycle= activityDurationNewDay;durationVehicle=0;durationRun=0;durationTilting=0;break;
				    case 4	:	durationStill=0;durationWalk=0;durationBicycle=0;durationVehicle= activityDurationNewDay;durationRun=0;durationTilting=0;break;
				    case 5	:	durationStill=0;durationWalk=0;durationBicycle=0;durationVehicle=0;durationRun= activityDurationNewDay;durationTilting=0;break;
				    case 6	:	durationStill=0;durationWalk=0;durationBicycle=0;durationVehicle=0;durationRun=0;durationTilting= activityDurationNewDay;break;
				    default	:	break;
				    }					    
				    deleteIndex = i; //update index to delete previous day from the txt file
				}
		  		else if(i==LogVector.size()-1){
		  			//Load information from application's preferences
		  			loadVectors();		  		    
		  		    //Update information in vectors (not in preferences)
		  		    //Log.i("CURRENT DAY", currentDay + " "+ durationStill/1000 + " " + durationWalk/1000 );
		  			LogVectorDayResult.add(currentDay);
		  			LogVectorStill.add(""+(int)(durationStill/1000));
		  			LogVectorWalk.add(""+(int)(durationWalk/1000));
		  			LogVectorBicycle.add(""+(int)(durationBicycle/1000));
		  			LogVectorVehicle.add(""+(int)(durationVehicle/1000));
		  			LogVectorRun.add(""+(int)(durationRun/1000));
		  			LogVectorTilting.add(""+(int)(durationTilting/1000));		  			
		  		}//End if(newDay){}else{
		  		
		  		
		  		if(i==LogVector.size()-1 && deleteIndex!=0){
		  			//Delete all lines of the txt file that do not belong to current day
					try {
						 File ext_storage = Environment.getExternalStorageDirectory();
						 String extPath = ext_storage.getPath();
						 File folder = new File(extPath+"/precious");
						 boolean success = false;
						 if(!folder.exists())
							 success = folder.mkdir();
						 if(folder.exists() || success){
					  	     File file = new File (folder, "ViewerLogFile.txt");
					  	     if(file.exists()){
					  	    	file.delete();
					  	    	Log.i("BUFFER","deleted");
					  	     }
					         file.createNewFile();
					         FileOutputStream f = new FileOutputStream(file, true);
					         for(int j =deleteIndex; j<LogVector.size();j++){
								 String texto = LogVector.get(j) + "\n";
								 f.write(texto.getBytes());
					         }
							 f.close();
					 		 }
					 } catch (Exception e){
						Log.e("getLog","Fichero local.txt no existe");
					 } 
		  		}
		  		
		  	  //Shift buffer
		  	  for(int k=0;k<(LPF_size-1);k++){
		  		  detectedActivityBuffer[k]=detectedActivityBuffer[k+1];
		  	  }
    	  }//End for LogVector    	  
      }//End get log
    
    /**
     *  Get activity duration from time in seconds
     */    
	public String getStringTime (long date){
		  int durationHours = (int)(date/3600);
		  int durationMin = (int)(date%3600/60);
		  int durationSec = (int)(date%60);
		  return durationHours + "h"+durationMin+"m"+durationSec+"s";
	}
	
    /**
     * Get date from date in millis since 1970
     */    
	public String getStringDate (long date){
		  Calendar c = Calendar.getInstance();
		  c.setTimeInMillis(date);
		  int day = c.get(Calendar.DAY_OF_MONTH);
		  int month = 1+c.get(Calendar.MONTH);
		  int year = c.get(Calendar.YEAR);
		  String sMonth = (month>9)? ""+month : "0"+month;
		  String sDay = (day>9)? ""+day : "0"+day;
		  return sDay+" "+sMonth+" "+year;
	}
	
	 /**
     * START LOCATION SERVICES
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
	                	 TextView tv = (TextView) findViewById(R.id.textViewAdressMain);
		    	    		tv.setText(addresses.get(0).getAddressLine(0)/*+", "+addresses.get(0).getLocality()*/);
	                }
	             });
	    	}
    	}catch (Exception e){
    		stopLocationUpdates();    				    		
    		Log.e("DrawChart getLocation","Error encontrando ubicación actual",e);				    		
    	}			
    }
  }
/*
 * END LOCATION SERVICES
 */
    
    /**
     * Declare onClick events
     */

    public void drawLog (View v){
    	Intent i = new Intent(this, Timeline.class);
    	startActivity(i);
    }       
    public void showAbout (View v){
    	      Intent i = new Intent(this, About.class);
    	            startActivity(i);
    }       
    public void exitApplication (View v){
    	       finish();
    }   
    public void openConfiguration (View v){
    	Intent i = new Intent(this, Preferencias.class);
        startActivity(i); 
    }
    public void openConfiguration (){
    	Intent i = new Intent(this, Preferencias.class);
        startActivity(i); 
    }
    public void resetTempSteps (View v){
    	Intent i = new Intent(this, aalto.comnet.thepreciouspedometer.ResetStepCounter.class);
        startActivity(i); 
    }
    public void sendDataToServer (View v){
    	Intent i = new Intent(this, SendLog.class);
        startService(i); 
    }    
    public void onCLickWalking (){
    	Toast.makeText(this, getString(R.string.time_walking), Toast.LENGTH_SHORT).show();
    }
    public void onCLickRunning (){
    	Toast.makeText(this, getString(R.string.time_running), Toast.LENGTH_SHORT).show();
    }
    public void onCLickBicycle (){
    	Toast.makeText(this, getString(R.string.time_bicycle), Toast.LENGTH_SHORT).show();
    }
    public void onCLickVehicle (View v){
    	Toast.makeText(this, getString(R.string.time_vehicle), Toast.LENGTH_SHORT).show();
    }
    public void onCLickTilting (View v){
    	Toast.makeText(this, getString(R.string.time_tilt), Toast.LENGTH_SHORT).show();
    }
    public void onCLickStill (View v){
    	Toast.makeText(this, getString(R.string.time_stand), Toast.LENGTH_SHORT).show();
    }    
    public void runFoodIntake (View v){    	
    	Intent i = new Intent(this,aalto.comnet.thepreciousfoodintake.MainActivity.class);    	
    	this.startActivity(i);
    }    
    public void runFaceDetection (View v){    	
    	Intent i = new Intent(this,aalto.comnet.thepreciousfacerecognition.MainActivity.class);    	
    	this.startActivity(i);
    }  
    public void runGame3 (View v){
    	Intent i = new Intent(this,aalto.comnet.thepreciousgame3.MainActivity.class);
    	this.startActivity(i);
    }    
    public void runGame1 (View v){
    	Intent i = new Intent(this,aalto.comnet.thepreciousgame1.MainActivity.class);
    	this.startActivity(i);
    }    
    /**
     * END onClick event
     */    
    
    /**
     * LogVectorDateTimeline getter
     */
    public static Vector<String> getTimeline(){    	
    	return LogVectorDateTimeline;
    }    
    
    /**
     * Startup configuration: get user personal data, create local files, get user ID
     */
    public void firstStartConfig(){
    	//Start pedometer
    	Intent i = new Intent(this,aalto.comnet.thepreciouspedometer.MainActivity.class);
    	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	this.startActivity(i);
    	//Start activity recognition
    	i = new Intent(this,aalto.comnet.thepreciousrecognition.MainActivity.class);
    	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	this.startActivity(i);      	
    	/**
    	 * 
    	 * 
    	 * 
    	 * 
    	 */
    	AlarmManager alarmMgr2;
    	PendingIntent alarmIntent2;
    	alarmMgr2 = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
    	i = new Intent(this,aalto.comnet.thepreciouslocationservice.LocationService.class);
    	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	//this.startActivity(i);
    	alarmIntent2 = PendingIntent.getBroadcast(this, 0, i, 0);
    	alarmMgr2.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
    			1000*60, alarmIntent2);
                //AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
    	
    	// Send data every 1 hour
    	AlarmManager alarmMgr;
    	PendingIntent alarmIntent;
    	alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
    	Intent intent = new Intent(this, SendLog.class);
    	alarmIntent = PendingIntent.getService(this, 0, intent, 0);
    	alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
                AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);
    			 //21*1000, alarmIntent); //TODO
    	
		//SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	SharedPreferences prefs =this.getSharedPreferences("preferences", Context.MODE_PRIVATE);
    	SharedPreferences.Editor edit = prefs.edit();
        edit.putString("previous_version", AppVersion);
        edit.commit(); 
        try{
        	File file = new File(getFilesDir(), "server.txt");
        	file.createNewFile();	        	
        }catch (Exception e){
        	Log.e("MainActivity","Error creating new files",e);
        }        
        //Create user ID        
	     String model = Build.MODEL;
	     TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	     String imei = telephonyManager.getDeviceId();
	     imei = imei.substring(imei.length()-5);
	     String userID =model.concat(" ").concat(imei);
	     edit.putString("user_id", userID);
	     edit.commit(); 
	   //Ask to open user configuration
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(getString(R.string.first_start));
        builder1.setCancelable(true);
        builder1.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	openConfiguration();
                dialog.cancel();
            }
        });
        builder1.setNegativeButton(getString(R.string.later),
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert1 = builder1.create();
        alert1.show();
    }
    
    /**
     *  Writes a string line in a file in external memory
	 * filename  name of the file
	 */
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
	
	/**
	 * 
	 */
	public Integer findStepsInFile ( String date){ 
		String steps="-1";
		try{
			File ext_storage = Environment.getExternalStorageDirectory();
			String extPath = ext_storage.getPath();
			 File folder = new File(extPath+"/precious");
			 boolean success = false;
			 if(!folder.exists())
				 success = folder.mkdir();
			 if(folder.exists() || success){
		 	     File file = new File (folder, "dateSteps.txt");
		 	     if(!file.exists())
		     		file.createNewFile();
		 	     FileInputStream f = new FileInputStream(file);
		 	     BufferedReader entrada = new BufferedReader(  new InputStreamReader(f));
		 	     String line="";   
		 	     String dateLine="";
		 		while ((line = entrada.readLine()) != null) {
		 			dateLine = line.substring(0, line.indexOf(";"));
		 			if (date.equals(dateLine) ){
		 				steps=line.substring(line.indexOf(";")+1, line.length());
		 			}
		 		}
		 		f.close();
			 }
		 }
		 catch (Exception e) {
			Log.e("findStepsInFile"," ",e);
		 }
		int iSteps = Integer.parseInt(steps);
	 return iSteps;
	}
	
	/**
	 * 
	 */
	public void loadVectors(){		
		try{
			File ext_storage = Environment.getExternalStorageDirectory();
			String extPath = ext_storage.getPath();
			 File folder = new File(extPath+"/precious");
			 boolean success = false;
			 if(!folder.exists())
				 success = folder.mkdir();
			 if(folder.exists() || success){
		 	     File file = new File (folder, "dateActivity.txt");
		 	     if(!file.exists())
		     		file.createNewFile();
		 	     FileInputStream f = new FileInputStream(file);
		 	     BufferedReader entrada = new BufferedReader(  new InputStreamReader(f));
		 	     String line="";   
		 		while ((line = entrada.readLine()) != null) {
		 			line = line.substring(0,line.length());
		 			LogVectorDayResult.add(line.substring(0, line.indexOf(";")));
		 			line = line.substring(line.indexOf(";")+1,line.length());
		 			LogVectorStill.add(line.substring(0, line.indexOf(";")));
		 			line = line.substring(line.indexOf(";")+1,line.length());
		 			LogVectorWalk.add(line.substring(0, line.indexOf(";")));
		 			line = line.substring(line.indexOf(";")+1,line.length());
		 			LogVectorBicycle.add(line.substring(0, line.indexOf(";")));
		 			line = line.substring(line.indexOf(";")+1,line.length());
		 			LogVectorVehicle.add(line.substring(0, line.indexOf(";")));
		 			line = line.substring(line.indexOf(";")+1,line.length());
		 			LogVectorRun.add(line.substring(0, line.indexOf(";")));
		 			line = line.substring(line.indexOf(";")+1,line.length());
		 			LogVectorTilting.add(line);		 			
		 		}
		 		f.close();
			 }
		 }
		 catch (Exception e) {
			Log.e("loadVectors"," ",e);
		 }
	}	
}