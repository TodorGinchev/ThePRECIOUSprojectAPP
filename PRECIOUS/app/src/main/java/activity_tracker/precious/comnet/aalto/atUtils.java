package activity_tracker.precious.comnet.aalto;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import aalto.comnet.thepreciousproject.R;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;
import ui.precious.comnet.aalto.precious.ui_MainActivity;


public  class atUtils {
    public static final String TAG = "atUtils";
    //    private static final double PONDERACION = 1.5;
    private static final int LPF_size = 3; //Size if the low-pass filter. Must be at least 2!
    //    //Location manager and listener, needed to get current location via Wifi network
//    private static LocationManager locationManager;
//    private static LocationListener locationListener;
    //Vector for data storage
    private static Vector<String> LogVectorDateTimeline = new Vector<>();
    private static Vector <Long> LogVectorDayResult = new Vector<>();
    private static Vector <Integer> LogVectorStill = new Vector<>();
    private static Vector <Integer> LogVectorWalk = new Vector<>();
    private static Vector <Integer> LogVectorBicycle = new Vector<>();
    private static Vector <Integer> LogVectorVehicle = new Vector<>();
    private static Vector <Integer> LogVectorRun = new Vector<>();
    private static Vector <Integer> LogVectorTilting = new Vector<>();
    private static Vector <Integer> LogVectorGoals = new Vector<>();

    /**
     * Read file, filter, reorganize information, make calculations over user's physical activity
     */
    public static void getLog (){
        Context context = PRECIOUS_APP.getAppContext();
        //Read File
        Vector<String> LogVector = new Vector<String>();
        try {
            boolean hasPermission = (ContextCompat.checkSelfPermission(context.getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            if(!hasPermission)
                return;
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
                file.delete();
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
        long currentDayTimestamp; //stores the current day's date
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
        LogVectorGoals.clear();
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
                Log.i("DRAWCHART","Skipping Line "+time +" at line number "+i);
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
            currentDayTimestamp=-1;
            if (newDay || i==LogVector.size()-1){
                if(!newDay && i==LogVector.size()-1) {
                    c.setTimeInMillis(time);
                    currentDayTimestamp = c.getTimeInMillis()-(c.get(Calendar.HOUR_OF_DAY)*3600*1000+c.get(Calendar.MINUTE)*60*1000+c.get(Calendar.SECOND)*1000+c.get(Calendar.MILLISECOND));
                }
                else {
                    c.setTimeInMillis(time - 24 * 60 * 60 * 1000);
                    currentDayTimestamp = c.getTimeInMillis()-(c.get(Calendar.HOUR_OF_DAY)*3600*1000+c.get(Calendar.MINUTE)*60*1000+c.get(Calendar.SECOND)*1000+c.get(Calendar.MILLISECOND));
                }
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
//                Log.i("BUFFER","SKIPPING DURATION: "+time+" "+activityDuration+ " "+activityDurationNewDay);
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
//                        durationStill = durationStill+activityDuration+activityDurationAcumul;
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

//                writeStingInExternalFile(currentDayTimestamp + ";" + (int) (durationStill / 1000) + ";" + (int) (durationWalk / 1000) + ";"
//                        + (int) (durationBicycle / 1000) + ";" + (int) (durationVehicle / 1000) + ";" + (int) (durationRun / 1000) + ";"
//                        + (int) (durationTilting / 1000), "dateActivity.txt");
                Log.i(TAG,"Inserting Auto PA");
                sql_db.precious.comnet.aalto.DBHelper.getInstance(context).insertPA(currentDayTimestamp, (int) (durationStill / 1000),
                        (int) (durationWalk / 1000), (int) (durationBicycle / 1000),
                        (int) (durationVehicle / 1000), (int) (durationRun / 1000), (int) (durationTilting / 1000), -1);
                sql_db.precious.comnet.aalto.DBHelper.getInstance(context).updatePA(currentDayTimestamp, (int) (durationStill / 1000),
                        (int) (durationWalk / 1000), (int) (durationBicycle / 1000),
                        (int) (durationVehicle / 1000), (int) (durationRun / 1000), (int) (durationTilting / 1000));

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
//                LogVectorDayResult.add(currentDay);
                if(LogVectorDayResult.size()>0){
                    if(LogVectorDayResult.get(LogVectorDayResult.size()-1)==currentDayTimestamp) {
                        //                    LogVectorDayResult.add(currentDayTimestamp);
                        LogVectorStill.set(LogVectorDayResult.size() - 1,(int) (durationStill / 1000));
                        LogVectorWalk.set(LogVectorDayResult.size() - 1,(int) (durationWalk / 1000));
                        LogVectorBicycle.set(LogVectorDayResult.size() - 1,(int) (durationBicycle / 1000));
                        LogVectorVehicle.set(LogVectorDayResult.size() - 1,(int) (durationVehicle / 1000));
                        LogVectorRun.set(LogVectorDayResult.size() - 1,(int) (durationRun / 1000));
                        LogVectorTilting.set(LogVectorDayResult.size() - 1,(int) (durationTilting / 1000));
                    }
                    else {
                        LogVectorDayResult.add(currentDayTimestamp);
                        LogVectorStill.add((int) (durationStill / 1000));
                        LogVectorWalk.add((int) (durationWalk / 1000));
                        LogVectorBicycle.add((int) (durationBicycle / 1000));
                        LogVectorVehicle.add((int) (durationVehicle / 1000));
                        LogVectorRun.add((int) (durationRun / 1000));
                        LogVectorTilting.add((int) (durationTilting / 1000));
                    }
                }
                else {
                    LogVectorDayResult.add(currentDayTimestamp);
                    LogVectorStill.add((int) (durationStill / 1000));
                    LogVectorWalk.add((int) (durationWalk / 1000));
                    LogVectorBicycle.add((int) (durationBicycle / 1000));
                    LogVectorVehicle.add((int) (durationVehicle / 1000));
                    LogVectorRun.add((int) (durationRun / 1000));
                    LogVectorTilting.add((int) (durationTilting / 1000));
                }
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

        if(LogVectorDayResult.size()==0){
            loadVectors();
            LogVectorStill.add(0);
            LogVectorWalk.add(0);
            LogVectorBicycle.add(0);
            LogVectorVehicle.add(0);
            LogVectorRun.add(0);
            LogVectorTilting.add(0);
            LogVectorGoals.add(-1);
        }
        else{
            Calendar c1 = Calendar.getInstance();
            Calendar c2 = Calendar.getInstance();
            c1.setTimeInMillis(LogVectorDayResult.get(LogVectorDayResult.size() - 1));
            c2.setTimeInMillis(System.currentTimeMillis());
            if(c1.get(Calendar.DAY_OF_MONTH)!=c2.get(Calendar.DAY_OF_MONTH)
                    &&c1.get(Calendar.MONTH)!=c2.get(Calendar.MONTH) ){
                LogVectorDayResult.add(c2.getTimeInMillis() - (c2.get(Calendar.HOUR_OF_DAY) * 3600 * 1000 + c2.get(Calendar.MINUTE) * 60 * 1000 + c2.get(Calendar.SECOND) * 1000 + c2.get(Calendar.MILLISECOND)));
                LogVectorStill.add(0);
                LogVectorWalk.add(0);
                LogVectorBicycle.add(0);
                LogVectorVehicle.add(0);
                LogVectorRun.add(0);
                LogVectorTilting.add(0);
                LogVectorGoals.add(-1);
            }
        }
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
    //TODO
//    public static void getLocation(final Context context){
//        // Acquire a reference to the system Location Manager
//        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//
//        // Define a listener that responds to location updates
//        locationListener = new LocationListener() {
//            public void onLocationChanged(Location location) {
//                // Called when a new location is found by the network location provider.
//                //makeUseOfNewLocation(location);
//                MiThread thread = new MiThread(location, context);
//                thread.start();
//            }
//            public void onStatusChanged(String provider, int status, Bundle extras) {}
//            public void onProviderEnabled(String provider) {}
//            public void onProviderDisabled(String provider) {}
//        };
//    }

    //TODO
//    public static void startLocationUpdates(Context context){
//        try{
//            getLocation(context);
//            // Register the listener with the Location Manager to receive location updates
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//        }catch (SecurityException e){
//            Log.e("startLocationUpdates", "No WIFI?");
//        }
//    }

//    public void stopLocationUpdates(){
//        try{
//            locationManager.removeUpdates(locationListener);
//        } catch (SecurityException e){
//            Log.e("stopLocationUpdates","No WIFI?");
//        }
//    }


    //TODO
//    class MiThread extends Thread {
//        private Location location;
//        private Context context;
//        public MiThread(Location location, Context context) {
//            this.location = location;
//            this.context = context;
//        }
//        @Override public void run() {
////            final Context context = getApplicationContext();
//            Geocoder gCoder = new Geocoder(context);
//            try{
//                final List<Address> addresses = gCoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
//                if (addresses != null && addresses.size() > 0) {
//                    runOnUiThread(new Runnable() {
//                        @Override public void run() {
//                            TextView tv = (TextView) findViewById(R.id.textViewAdressMain);
//                            tv.setText(addresses.get(0).getAddressLine(0)/*+", "+addresses.get(0).getLocality()*/);
//                        }
//                    });
//                }
//            }catch (Exception e){
//                stopLocationUpdates();
//                Log.e("DrawChart getLocation","Error encontrando ubicacion actual",e);
//            }
//        }
//    }
/*
 * END LOCATION SERVICES
 */


    /**
     *  Writes a string line in a file in external memory
     * filename  name of the file
     */
    public static  int writeStringInExternalFile(String data, String fileName){
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
                else Log.e("ActRecognitionIntent","unable to create folder or file");
            }
        } catch (Exception e) {
            Log.e("Error opening file", e.getMessage(), e);
        }
        return fileSize;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

//    /**
//     *
//     */
//    public Integer findStepsInFile ( String date){
//        String steps="-1";
//        try{
//            File ext_storage = Environment.getExternalStorageDirectory();
//            String extPath = ext_storage.getPath();
//            File folder = new File(extPath+"/precious");
//            boolean success = false;
//            if(!folder.exists())
//                success = folder.mkdir();
//            if(folder.exists() || success){
//                File file = new File (folder, "dateSteps.txt");
//                if(!file.exists())
//                    file.createNewFile();
//                FileInputStream f = new FileInputStream(file);
//                BufferedReader entrada = new BufferedReader(  new InputStreamReader(f));
//                String line="";
//                String dateLine="";
//                while ((line = entrada.readLine()) != null) {
//                    dateLine = line.substring(0, line.indexOf(";"));
//                    if (date.equals(dateLine) ){
//                        steps=line.substring(line.indexOf(";")+1, line.length());
//                    }
//                }
//                f.close();
//            }
//        }
//        catch (Exception e) {
//            Log.e("findStepsInFile"," ",e);
//        }
//        int iSteps = Integer.parseInt(steps);
//        return iSteps;
//    }

    /**
     *
     */
    public static void loadVectors(){
        Log.i(TAG,"loadVectors()");
        Log.i(TAG,"1LogVectorDayResult size:"+LogVectorDayResult.size()+" LogVectorGoals size:"+LogVectorGoals.size());
        try{
            ArrayList<ArrayList<Long>> paData = sql_db.precious.comnet.aalto.DBHelper.getInstance(ui_MainActivity.mContext).getAllPA();
            long prev_timestamp=0;
            long current_timestamp;
            for (int i=0; i<paData.size();i++) {
//                Log.i(TAG, ("Walk data:"+paData.get(i).get(1)) + "");
                current_timestamp = paData.get(i).get(0);
                if(i>1){
                    while(current_timestamp-prev_timestamp>(47.9*3600*1000)){
                        prev_timestamp = prev_timestamp+(24*3600*1000);
                        LogVectorDayResult.add(prev_timestamp);
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(prev_timestamp);
                        Log.i(TAG,"LogVectorDayResult "+i+"ADDING MISSING DAY"+"Day"+getDayMonth(c));
                        LogVectorStill.add(0);
                        LogVectorWalk.add(0);
                        LogVectorBicycle.add(0);
                        LogVectorVehicle.add(0);
                        LogVectorRun.add(0);
                        LogVectorTilting.add(0);
                        LogVectorGoals.add(0);
                        Log.i(TAG,"2LogVectorDayResult size:"+LogVectorDayResult.size()+" LogVectorGoals size:"+LogVectorGoals.size());
                    }
                }
                prev_timestamp = current_timestamp;

                LogVectorDayResult.add((paData.get(i).get(0)));
                Log.i(TAG,"LogVectorDayResult "+i+" = "+paData.get(i).get(0));
                LogVectorStill.add((paData.get(i).get(1)).intValue());
                LogVectorWalk.add((paData.get(i).get(2)).intValue());
                LogVectorBicycle.add((paData.get(i).get(3)).intValue());
                LogVectorVehicle.add((paData.get(i).get(4)).intValue());
                LogVectorRun.add((paData.get(i).get(5)).intValue());
                LogVectorTilting.add((paData.get(i).get(6)).intValue());
                LogVectorGoals.add((paData.get(i).get(7)).intValue());
            }
            Log.i(TAG,"3LogVectorDayResult size:"+LogVectorDayResult.size()+" LogVectorGoals size:"+LogVectorGoals.size());

            if(prev_timestamp!=0) {
                while (System.currentTimeMillis()-prev_timestamp>(47.9*3600*1000)){
                    prev_timestamp = prev_timestamp+(24*3600*1000);
                    LogVectorDayResult.add(prev_timestamp);
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(prev_timestamp);
                    Log.i(TAG,"ADDING MISSING DAY UNTIL TODAY, "+"Day"+getDayMonth(c));
                    LogVectorStill.add(0);
                    LogVectorWalk.add(0);
                    LogVectorBicycle.add(0);
                    LogVectorVehicle.add(0);
                    LogVectorRun.add(0);
                    LogVectorTilting.add(0);
                    LogVectorGoals.add(0);
                }
            }

            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY,0);
            c.set(Calendar.MINUTE,0);
            c.set(Calendar.SECOND,0);
            c.set(Calendar.MILLISECOND,0);
            long timestamp_today = c.getTimeInMillis();
            if(LogVectorDayResult==null){
                Log.i(TAG,"ADDING MISSING DAY: TODAY, because LogVectorDayResult is null");
                LogVectorDayResult.add(timestamp_today);
                LogVectorStill.add(0);
                LogVectorWalk.add(0);
                LogVectorBicycle.add(0);
                LogVectorVehicle.add(0);
                LogVectorRun.add(0);
                LogVectorTilting.add(0);
                LogVectorGoals.add(0);
            }
            else if(LogVectorDayResult.get(LogVectorDayResult.size()-1)!=timestamp_today){
                Log.i(TAG,"ADDING MISSING DAY: TODAY");
                LogVectorDayResult.add(timestamp_today);
                LogVectorStill.add(0);
                LogVectorWalk.add(0);
                LogVectorBicycle.add(0);
                LogVectorVehicle.add(0);
                LogVectorRun.add(0);
                LogVectorTilting.add(0);
                LogVectorGoals.add(0);
            }

            Log.i(TAG,"4LogVectorDayResult size:"+LogVectorDayResult.size()+" LogVectorGoals size:"+LogVectorGoals.size());
        }
        catch (Exception e) {
            Log.e("loadVectors"," ",e);
        }
    }


    public static String getMonth(Context context, String Smonth){
        int month = Integer.parseInt(Smonth);
        switch (month){
            case 1  :   return context.getResources().getString(R.string.jan);
            case 2  :   return context.getResources().getString(R.string.feb);
            case 3  :   return context.getResources().getString(R.string.mar);
            case 4  :   return context.getResources().getString(R.string.apr);
            case 5  :   return context.getResources().getString(R.string.may);
            case 6  :   return context.getResources().getString(R.string.jun);
            case 7  :   return context.getResources().getString(R.string.jul);
            case 8  :   return context.getResources().getString(R.string.aug);
            case 9  :   return context.getResources().getString(R.string.sep);
            case 10 :   return context.getResources().getString(R.string.oct);
            case 11 :   return context.getResources().getString(R.string.nov);
            case 12 :   return context.getResources().getString(R.string.dec);
            default :   return " ";
        }
    }




//    int iYear = c.get(Calendar.YEAR);
//    int iMonth = c.get(Calendar.MONTH)+1;
//    int iDay = c.get(Calendar.DAY_OF_MONTH);
//    int iDayWeek = c.get(Calendar.DAY_OF_WEEK);
//    String sYear = ""+iYear;
//    String sMonth = (iMonth>9)? ""+iMonth : "0"+iMonth;
//    String sDay = (iDay>9)? ""+iDay : "0"+iDay;
//    String sDayWeek="";
//    switch (iDayWeek){
//        case 2	:	sDayWeek=context.getString(R.string.monday);break;
//        case 3	:	sDayWeek=context.getString(R.string.tuesday);break;
//        case 4	:	sDayWeek=context.getString(R.string.wednesday);break;
//        case 5	:	sDayWeek=context.getString(R.string.thursday);break;
//        case 6	:	sDayWeek=context.getString(R.string.friday);break;
//        case 7	:	sDayWeek=context.getString(R.string.saturday);break;
//        case 1	:	sDayWeek=context.getString(R.string.sunday);break;
//        default	:	sDayWeek=null;break;
//    }
//    currentDay[0] = sDayWeek;
//    currentDay[1] = sDay;
//    currentDay[2] = sMonth;
//    currentDay[3] = sYear;

    /**
     *
     */
    public static String getDayWeek(Context context, Calendar c){
        int iDayWeek = c.get(Calendar.DAY_OF_WEEK);
        switch (iDayWeek){
            case 2	:	return context.getString(R.string.monday);
            case 3	:	return context.getString(R.string.tuesday);
            case 4	:	return context.getString(R.string.wednesday);
            case 5	:	return context.getString(R.string.thursday);
            case 6	:	return context.getString(R.string.friday);
            case 7	:	return context.getString(R.string.saturday);
            case 1	:	return context.getString(R.string.sunday);
            default	:	return null;
        }
    }

    /**
     *
     */
    public static String getDayMonth(Calendar c) {
        int iMonth = c.get(Calendar.DAY_OF_MONTH);
        return (iMonth>9)? ""+iMonth : "0"+iMonth;
    }
    /**
     *
     */
    public static String getMonth(Calendar c) {
        int iMonth = c.get(Calendar.MONTH)+1;
        return (iMonth>9)? ""+iMonth : "0"+iMonth;
    }
    /**
     *
     */
    public static String getYear(Calendar c) {
        int iYear = c.get(Calendar.YEAR);
        return ""+iYear;
    }

    /**
     *
     */
    public void removeLine(final File file, final int lineIndex) throws IOException {
        final List<String> lines = new LinkedList<>();
        final Scanner reader = new Scanner(new FileInputStream(file), "UTF-8");
        while(reader.hasNextLine())
            lines.add(reader.nextLine());
        reader.close();
        if ( !(lineIndex >= 0 && lineIndex <= lines.size() - 1)) {
            Log.e(TAG,"In removeLine function, Error deleting line, line number not found");
            return;
        }
        lines.remove(lineIndex);
        final BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
        for(final String line : lines)
            writer.write(line);
        writer.flush();
        writer.close();
    }


    public static Vector<Integer> getLogVectorSteps() {
        Vector <Integer> LogVectorSteps = new Vector<>();
        int stepsAcumul = 0;
        try {
            for (int i=0;i<LogVectorDayResult.size();i++) {
                ArrayList<ArrayList<Long>> paManualData = sql_db.precious.comnet.aalto.DBHelper.getInstance(ui_MainActivity.mContext).getManPA(
                        LogVectorDayResult.get(i), (long) (LogVectorDayResult.get(i) + 24 * 3600 * 1000)
                );
                for (int j = 0; j < paManualData.size(); j++) {
                    stepsAcumul += (paManualData.get(j).get(4));
                }


                //For WALK
                int walk_duration = LogVectorWalk.get(i);
                if(walk_duration>0) {
                    ArrayList<Long> aux = new ArrayList<>();
                    aux.add(LogVectorDayResult.get(i));//timestamp
                    aux.add((long)26);//type
                    aux.add((long)(1));//intensity
                    aux.add((long)(walk_duration));//duration
                    aux.add((long)(walk_duration*84/60));//steps
                    paManualData.add(0, aux);
                    stepsAcumul += walk_duration*84/60;
                }
                //For RUN
                int run_duration = LogVectorRun.get(i);
                if(run_duration>120) {
                    ArrayList<Long> aux = new ArrayList<>();
                    aux.add(LogVectorDayResult.get(i));//timestamp
                    aux.add((long)37);//type
                    aux.add((long)(1));//intensity
                    aux.add((long)(run_duration));//duration
                    aux.add((long)(run_duration*222/60));//steps
                    paManualData.add(0, aux);
                    stepsAcumul += run_duration*222/60;
                }
                int bicycle_duration = LogVectorBicycle.get(i);
                if(bicycle_duration>120) {
                    ArrayList<Long> aux = new ArrayList<>();
                    aux.add(LogVectorDayResult.get(i));//timestamp
                    aux.add((long)35);//type
                    aux.add((long)(1));//intensity
                    aux.add((long)(bicycle_duration));//duration
                    aux.add((long)(bicycle_duration*170/60));//steps
                    paManualData.add(0, aux);
                    stepsAcumul += bicycle_duration*170/60;
                }

                LogVectorSteps.add(stepsAcumul);
                stepsAcumul=0;
            }
        } catch (Exception e) {
            Log.e("loadVectors", " ", e);
        }
        return LogVectorSteps;
    }


    public static Vector<String> getLogVectorDateTimeline(){
        return LogVectorDateTimeline;
    }
    public static Vector<Long> getLogVectorDayResult(){
        return LogVectorDayResult;
    }
    public static Vector<Integer> getLogVectorStill(){
        return LogVectorStill;
    }
    public static Vector<Integer> getLogVectorWalk(){return LogVectorWalk;}
    public static Vector<Integer> getLogVectorBicycle(){
        return LogVectorBicycle;
    }
    public static Vector<Integer> getLogVectorVehicle(){
        return LogVectorVehicle;
    }
    public static Vector<Integer> getLogVectorRun(){
        return LogVectorRun;
    }
    public static Vector<Integer> getLogVectorTilting(){
        return LogVectorTilting;
    }
    public static Vector<Integer> getLogVectorGoals(){
        return LogVectorGoals;
    }




}