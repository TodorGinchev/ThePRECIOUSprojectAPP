package aalto.comnet.thepreciouslocationservice;
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

public class help {
	
}
//
//public class LocationService extends Service implements LocationListener{
//	private static final long TIEMPO_MIN = 1 * 1000; // 1 segundo
//	private static final long DISTANCIA_MIN = 0; // 0 metros
//	private static final int EXACTITUD_MIN = 80;
//	private LocationManager manejador;
//	private long tiempo_inicio;
//	private String proveedor;
//	private long tiempo=0;
//	private float recorrido=0;
//	private Location localizacionAnterior;
//	
//	private double inicioLat, inicioLng;
//	private double finalLat, finalLng;
//	
//	private boolean noInicioLocalizacion = true;
//	
//	private long time;
//	boolean useGPS;
//	
//	//For the pedometer
//	private Sensor mSensor;
//	private StepCountService mStepDetector;
//	private SensorManager mSensorManager;
//	
//	@Override
//    public void onCreate() {	
//		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        useGPS = prefs.getBoolean("useGPS", false);
//    	if(useGPS) { 		
//			manejador = (LocationManager) getSystemService(LOCATION_SERVICE);
//			//Configuramos el GPS//
//			Criteria criterio = new Criteria();
//			criterio.setCostAllowed(false);
//			criterio.setAltitudeRequired(false);
//			criterio.setAccuracy(Criteria.ACCURACY_FINE);
//			proveedor = manejador.getBestProvider(criterio, true);
//			manejador.requestLocationUpdates(proveedor, TIEMPO_MIN, DISTANCIA_MIN,this);
//			localizacionAnterior = manejador.getLastKnownLocation(proveedor);
//    	}
//    	
//    	//Start pedometer
//    	mStepDetector = new StepCountService();
//    	mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//  //  	registerDetector(); TODO arreglar esto, que inicie o que no inicie, pero no dejarlo a medias
//	}
//	
//	@Override
//     public int onStartCommand(Intent intenc, int flags, int idArranque) {
//		if(useGPS)
//			time = intenc.getLongExtra("time",0); 
//         return START_STICKY;
//     }	
//
//     @Override
//     public void onDestroy() {
//    	 if(useGPS){
//	    	 saveLog(time+";"+inicioLat+";"+inicioLng+";"+finalLat+";"+finalLng+
//	    			 ";"+recorrido+";"+tiempo, "LocationLog.txt");
//	    	 manejador.removeUpdates(this);
//    	 }    	 
//     }
//	
//	
//	// Métodos de la interfaz LocationListener//
//	public void onLocationChanged(Location localizacion) {		
//		float accuracy = localizacionAnterior.getAccuracy();
//		Log.i("Exactitud GPS",""+accuracy);
//		//Inicio cronómetro sólo si dispongo de una localización fiable
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
//		if( (SystemClock.elapsedRealtime()-tiempo_inicio)!=0 && recorrido!=0 )//Evitamos división entre 0
//		{
//			tiempo = (SystemClock.elapsedRealtime() - tiempo_inicio)/1000;
//		}
//	}
//
//	public void onProviderDisabled(String proveedor) {
//	}
//
//	public void onProviderEnabled(String proveedor) {
//	}
//
//	public void onStatusChanged(String proveedor, int estado, Bundle extras) {
//	}
//	
//	 @Override
//     public IBinder onBind(Intent intencion) {
//           return null;
//     }
//	 
//	    public void saveLog(String data, String fileName){
//	        try {
//	      	     File file = new File(getFilesDir(), fileName);
//	      	     if(!file.exists())
//	      	     	file.createNewFile();
//	               FileOutputStream f = openFileOutput(fileName,Context.MODE_APPEND);
//	               String texto = data + "\n";
//	               f.write(texto.getBytes());
//	               f.close();
//	               Log.i("File "+fileName, "Stored "+data);
//	        } catch (Exception e) {
//	               Log.e("Error opening file", e.getMessage(), e);
//	        }
//	  }
//	    
////	    private void registerDetector() {
////	    	mSensor = mSensorManager.getDefaultSensor(
////	    		Sensor.TYPE_ACCELEROMETER /*| 
////	    		Sensor.TYPE_MAGNETIC_FIELD | 
////	    		Sensor.TYPE_ORIENTATION*/);
////	    	mSensorManager.registerListener(mStepDetector,
////	    		mSensor,
////	    		SensorManager.SENSOR_DELAY_FASTEST);
////	    }
//}
