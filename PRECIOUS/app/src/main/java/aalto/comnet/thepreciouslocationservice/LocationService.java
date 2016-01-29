package aalto.comnet.thepreciouslocationservice;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.security.acl.Permission;

//Check location every 30min.
//If location info is accurate enough, search nearby places.
//Based on the nearby places information, implement an algorithm to detect useful places.
//If a useful place is detected, send notification to the user.

//public class LocationService{
//	
//}

public class LocationService extends Service implements LocationListener{
	
	private static final long TIME_BETWEEN_REQUESTS = 10 * 1000; // check location every TIME_BETWEEN_REQUESTS milliseconds
	private static final long MIN_DISTANCE = 5; // do not update if location has changed in less than MIN_DISTANCE meters
	private static final int MIN_ACCURACY = 80; //minimum accuracy set to 80m, do not update if accuracy is above this parameter
	private static final int MAX_UPDATES = 100; //If a certain number of location updates in done without meeting the accuracy requirements, stop the service.
	
	private int numUpdates;
	private boolean enableGPS;
	private LocationManager lm;
	private String networkProvider;
	private Location lastLocation;
	
	
		
	
	
	@Override
    public void onCreate() {	
		numUpdates=0;
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		enableGPS = prefs.getBoolean("useGPS", false);	
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		//Configure GPS 
		Criteria criteria = new Criteria();
		criteria.setCostAllowed(false);
		criteria.setAltitudeRequired(false);
		if(enableGPS)
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
		else 
			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		networkProvider = lm.getBestProvider(criteria, true);
		try {
			lm.requestLocationUpdates(networkProvider, TIME_BETWEEN_REQUESTS, MIN_DISTANCE, this);
			lastLocation = lm.getLastKnownLocation(networkProvider);
		}catch (SecurityException e)
		{

		}
		
	}
	
	@Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {	
		Log.i("LocationService","LocationService onStartCommand");
        return START_STICKY;
    }
	
    @Override
    public void onDestroy() {

		try {
			lm.removeUpdates(this);
		}catch (SecurityException e){

		}
    }    	 
    
	// M�todos de la interfaz LocationListener//
	public void onLocationChanged(Location localizacion) {		
		float accuracy = lastLocation.getAccuracy();
		Log.i("Exactitud GPS",""+accuracy);
		if(accuracy<MIN_ACCURACY || numUpdates>=MAX_UPDATES)
			onDestroy();
		numUpdates++;
//		//Inicio cron�metro s�lo si dispongo de una localizaci�n fiable
//		if (accuracy<EXACTITUD_MIN){
//		
//			//vel = (int) localizacion.getSpeed() * (3600 / 1000);
//			recorrido = recorrido+localizacion.distanceTo(localizacionAnterior);
//			localizacionAnterior = localizacion;			
//			tiempo_inicio = SystemClock.elapsedRealtime();	
//			if(noInicioLocalizacion){
//				inicioLat = localizacion.getLatitude();
//				inicioLng = localizacion.getLongitude();
//				noInicioLocalizacion=false;
//			}
//			finalLat = localizacion.getLatitude();
//			finalLng = localizacion.getLongitude();
//			
//			
//		}
//			
//		if( (SystemClock.elapsedRealtime()-tiempo_inicio)!=0 && recorrido!=0 )//Evitamos divisi�n entre 0
//		{
//			tiempo = (SystemClock.elapsedRealtime() - tiempo_inicio)/1000;
//		}
	}
	public void onProviderDisabled(String proveedor) {
	}

	public void onProviderEnabled(String proveedor) {
	}

	public void onStatusChanged(String proveedor, int estado, Bundle extras) {
	}
	
	 @Override
     public IBinder onBind(Intent intencion) {
           return null;
     }
}

/*
 package aalto.thepreciousproject.precious;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class OnFootService extends Service implements LocationListener{
	private static final long TIEMPO_MIN = 1 * 1000; // 1 segundo
	private static final long DISTANCIA_MIN = 0; // 0 metros
	private static final int EXACTITUD_MIN = 80;
	private LocationManager manejador;
	private long tiempo_inicio;
	private String proveedor;
	private long tiempo=0;
	private float recorrido=0;
	private Location localizacionAnterior;
	
	private double inicioLat, inicioLng;
	private double finalLat, finalLng;
	
	private boolean noInicioLocalizacion = true;
	
	private long time;
	boolean useGPS;
	
	//For the pedometer
	private Sensor mSensor;
	private StepCountService mStepDetector;
	private SensorManager mSensorManager;
	
	@Override
    public void onCreate() {	
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        useGPS = prefs.getBoolean("useGPS", false);
    	if(useGPS) { 		
			manejador = (LocationManager) getSystemService(LOCATION_SERVICE);
			//Configuramos el GPS//
			Criteria criterio = new Criteria();
			criterio.setCostAllowed(false);
			criterio.setAltitudeRequired(false);
			criterio.setAccuracy(Criteria.ACCURACY_FINE);
			proveedor = manejador.getBestProvider(criterio, true);
			manejador.requestLocationUpdates(proveedor, TIEMPO_MIN, DISTANCIA_MIN,this);
			localizacionAnterior = manejador.getLastKnownLocation(proveedor);
    	}
    	
    	//Start pedometer
    	mStepDetector = new StepCountService();
    	mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
  //  	registerDetector(); TODO arreglar esto, que inicie o que no inicie, pero no dejarlo a medias
	}
	
	@Override
     public int onStartCommand(Intent intenc, int flags, int idArranque) {
		if(useGPS)
			time = intenc.getLongExtra("time",0); 
         return START_STICKY;
     }	

     @Override
     public void onDestroy() {
    	 if(useGPS){
	    	 saveLog(time+";"+inicioLat+";"+inicioLng+";"+finalLat+";"+finalLng+
	    			 ";"+recorrido+";"+tiempo, "LocationLog.txt");
	    	 manejador.removeUpdates(this);
    	 }    	 
     }
	
	
	// M�todos de la interfaz LocationListener//
	public void onLocationChanged(Location localizacion) {		
		float accuracy = localizacionAnterior.getAccuracy();
		Log.i("Exactitud GPS",""+accuracy);
		//Inicio cron�metro s�lo si dispongo de una localizaci�n fiable
		if (accuracy<EXACTITUD_MIN){
		
			//vel = (int) localizacion.getSpeed() * (3600 / 1000);
			recorrido = recorrido+localizacion.distanceTo(localizacionAnterior);
			localizacionAnterior = localizacion;			
			tiempo_inicio = SystemClock.elapsedRealtime();	
			if(noInicioLocalizacion){
				inicioLat = localizacion.getLatitude();
				inicioLng = localizacion.getLongitude();
				noInicioLocalizacion=false;
			}
			finalLat = localizacion.getLatitude();
			finalLng = localizacion.getLongitude();
			
			
		}
			
		if( (SystemClock.elapsedRealtime()-tiempo_inicio)!=0 && recorrido!=0 )//Evitamos divisi�n entre 0
		{
			tiempo = (SystemClock.elapsedRealtime() - tiempo_inicio)/1000;
		}
	}

	public void onProviderDisabled(String proveedor) {
	}

	public void onProviderEnabled(String proveedor) {
	}

	public void onStatusChanged(String proveedor, int estado, Bundle extras) {
	}
	
	 @Override
     public IBinder onBind(Intent intencion) {
           return null;
     }
	 
	    public void saveLog(String data, String fileName){
	        try {
	      	     File file = new File(getFilesDir(), fileName);
	      	     if(!file.exists())
	      	     	file.createNewFile();
	               FileOutputStream f = openFileOutput(fileName,Context.MODE_APPEND);
	               String texto = data + "\n";
	               f.write(texto.getBytes());
	               f.close();
	               Log.i("File "+fileName, "Stored "+data);
	        } catch (Exception e) {
	               Log.e("Error opening file", e.getMessage(), e);
	        }
	  }
	    
//	    private void registerDetector() {
//	    	mSensor = mSensorManager.getDefaultSensor(
//	    		Sensor.TYPE_ACCELEROMETER // | 
//	    		//Sensor.TYPE_MAGNETIC_FIELD | 
//	    		//Sensor.TYPE_ORIENTATION
 					);
//	    	mSensorManager.registerListener(mStepDetector,
//	    		mSensor,
//	    		SensorManager.SENSOR_DELAY_FASTEST);
//	    }
}

*/

