package aalto.comnet.thepreciouslocationservice;




//Check location every 30min.
//If location info is accurate enough, search nearby places.
//Based on the nearby places information, implement an algorithm to detect useful places.
//If a useful place is detected, send notification to the user.

public class LocationService{
	
}
//
//public class LocationService extends Service implements LocationListener{
//	
//	private boolean enableGPS;
//	private LocationManager lm;
//	private String networkProvider;
//	private static final long TIME_BETWEEN_REQUESTS = 30 * 60 * 1000; // check location every TIME_BETWEEN_REQUESTS milliseconds
//	private static final long MIN_DISTANCE = 50; // do not update if location has changed in less than MIN_DISTANCE meters
//	
//	@Override
//    public void onCreate() {	
//		//SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//		enableGPS = prefs.getBoolean("useGPS", false);
//	
//			lm = (LocationManager) getSystemService(LOCATION_SERVICE);
//			//Configure GPS 
//			Criteria criteria = new Criteria();
//			criteria.setCostAllowed(false);
//			criteria.setAltitudeRequired(false);
//			if(enableGPS)
//				criteria.setAccuracy(Criteria.ACCURACY_FINE);
//			else 
//				criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//			networkProvider = lm.getBestProvider(criteria, true);
//			lm.requestLocationUpdates(networkProvider, TIME_BETWEEN_REQUESTS, MIN_DISTANCE,this);
//			lm.requ
//			
//			localizacionAnterior = manejador.getLastKnownLocation(proveedor);
//    	
//    	
//    	//Start pedometer
//    	mStepDetector = new StepCountService();
//    	mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//  //  	registerDetector(); TODO arreglar esto, que inicie o que no inicie, pero no dejarlo a medias
//	}
//	
//	
//	
//
//}


