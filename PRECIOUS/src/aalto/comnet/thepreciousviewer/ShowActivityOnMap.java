package aalto.comnet.thepreciousviewer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import aalto.comnet.thepreciousproject.R;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowActivityOnMap extends FragmentActivity  implements OnMapClickListener {
	
	private GoogleMap mapa;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_activity_on_map_layout);
		
		Bundle extras = getIntent().getExtras();
	    long time = extras.getLong("time");
	    
	    try {
	    	Boolean dataValid=false;
	  		FileInputStream f = openFileInput("LocationLog.txt");
	  		BufferedReader entrada = new BufferedReader(  new InputStreamReader(f));
	  		String line="";
	  		while ((line = entrada.readLine()) != null) {
  			  Log.i("DATA",line);
  			  long timeLog = Long.parseLong(line.substring(0,line.indexOf(";")));
  			Log.i("Time",""+time);
  			Log.i("TimeLog",timeLog+"");
  			  if(timeLog==time){
  				Log.i("Time","YEEEEES");
  				setMarkers(line.substring(line.indexOf(";")+1));
  				dataValid=true;
  				 break;
  			  }  		    
  		  }
	  		if(!dataValid){
	  			Toast.makeText(this, "No GPS information", Toast.LENGTH_LONG).show(); 
	  			finish();
	  		}
  		  f.close();
  		  
  	    } catch (Exception e){
  	    	Log.e("getLog","",e);
  	    }  	
	}
	
	
	public void setMarkers (String Line){
		
		double inicioLat = Double.parseDouble(Line.substring(0,Line.indexOf(";")));
		Line = Line.substring(Line.indexOf(";")+1);
		Log.i("InicioLat",inicioLat+"");
		double inicioLng = Double.parseDouble(Line.substring(0,Line.indexOf(";")));
		Line = Line.substring(Line.indexOf(";")+1);
		Log.i("inicioLng",""+inicioLng);
		double finalLat = Double.parseDouble(Line.substring(0,Line.indexOf(";")));
		Line = Line.substring(Line.indexOf(";")+1);
		Log.i("finalLat",""+finalLat);
		double finalLng = Double.parseDouble(Line.substring(0,Line.indexOf(";")));
		Log.i("finalLng",""+""+finalLng);
		
		if( (inicioLat==0.0 && inicioLng==0.0) || (finalLat==0.0 && finalLng==0.0)){
			Toast.makeText(this, "No GPS information", Toast.LENGTH_LONG).show(); 	
			finish();
		}
		
		LatLng start = new LatLng(inicioLat, inicioLng);
		LatLng stop = new LatLng(finalLat, finalLng);
		
		mapa = ((SupportMapFragment) getSupportFragmentManager()

	            .findFragmentById(R.id.map)).getMap();

	      mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);

	      mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 15));

	      mapa.setMyLocationEnabled(true);

	      mapa.getUiSettings().setZoomControlsEnabled(false);

	      mapa.getUiSettings().setCompassEnabled(true);
		
	     mapa.addMarker(new MarkerOptions()
         .position(start)
         .title("Start")
         .snippet("")
         .icon(BitmapDescriptorFactory
         .fromResource(R.drawable.walking_small))
         .anchor(0.5f, 0.5f));
	     
	     mapa.addMarker(new MarkerOptions()
         .position(stop)
         .title("Stop")
         .snippet("")
         .icon(BitmapDescriptorFactory
         .fromResource(R.drawable.finish))
         .anchor(0.5f, 0.5f));
	     		
		 mapa = ((SupportMapFragment) getSupportFragmentManager()
		            .findFragmentById(R.id.map)).getMap();
		 mapa.setMyLocationEnabled(true);
	     mapa.getUiSettings().setZoomControlsEnabled(false);
	     mapa.getUiSettings().setCompassEnabled(true);
	     
	     
	     
	     //zoom in and show the start and finish locations
	     LatLngBounds bounds = new LatLngBounds.Builder()
         .include(stop)
         .include(start).build();

	     Point displaySize = new Point();
	     getWindowManager().getDefaultDisplay().getSize(displaySize);

	     mapa.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, displaySize.x, 250, 30));
		
	}
	
    @Override

   public void onMapClick(LatLng puntoPulsado) {

      mapa.addMarker(new MarkerOptions().position(puntoPulsado).

         icon(BitmapDescriptorFactory

            .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

   }

//public class Resumen extends FragmentActivity  {
//	/** Called when the activity is first created. */
//	private TextView vel;
//	private TextView time;
//	private TextView cal;
//	private float velocidad_media;
//	private float recorrido;
//	private long tiempo;
//	private String currentTime;	
//	private GoogleMap mapa;
//	private LatLng LocalizacionInicio;
//	private LatLng LocalizacionFinal;
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		//stopService(new Intent(this, ServicioCompeticion.class));
//		Bundle extras = getIntent().getExtras();
//		tiempo = 0;
//		recorrido = 0;
//		LocalizacionInicio = new LatLng(0, 0);
//		LocalizacionFinal=new LatLng(0, 0);
//		try{
//		tiempo = extras.getLong("tiempo");
//		}catch (Exception e) {}
//		long hours;
//		long minutes;
//		long seconds;
//		int calorias;
//			
//		
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.resumen);
//		
////		vel = (TextView) findViewById(R.id.velocidad);
////		time = (TextView) findViewById(R.id.tiempo);
////		cal = (TextView) findViewById(R.id.calorias);
//		if(tiempo>0)
//			velocidad_media = (recorrido/1000)/(tiempo/1000/3600);
//		else velocidad_media=0;
//		String s1 = String.format("%.2f", velocidad_media);
//		vel.setText("Velocidad media: "+s1+" km/h");		
//	    calorias = (int) (57* velocidad_media + tiempo/1000);	
//	    hours = tiempo/1000/3600;
//	    tiempo = tiempo - hours*1000*3600;
//		minutes = ((tiempo) / 1000) / 60;
//		seconds = ((tiempo) / 1000) % 60;
//		currentTime = hours+"h"+minutes + "m" + seconds+"s";	    
//	    time.setText("Tiempo: "+currentTime);
//	    cal.setText("Calorías: "+(calorias/1000)+10+" Kcal");
//	    
//	    
//	    //Guardo los datos en el historial
//	    Calendar c = Calendar.getInstance();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String formattedDate = df.format(c.getTime());
//        String tRecorrido = String.format("%.3f", recorrido/1000);
//	    String cadena = formattedDate+"\n"+tRecorrido+"km "+currentTime;
//	    
//	    
//	    //Muestro mapa
//	    mapa = ((SupportMapFragment) getSupportFragmentManager()
//	            .findFragmentById(R.id.map)).getMap();
//	    //mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//	    mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(LocalizacionFinal, 15));
//	    mapa.setMyLocationEnabled(true);
//	    mapa.getUiSettings().setCompassEnabled(true);
//	    mapa.addMarker(new MarkerOptions()
//        .position(LocalizacionInicio)
//        .title("Yo")
//        .snippet("Inicio carrera")
//        .icon(BitmapDescriptorFactory
//               .fromResource(R.drawable.running))
//        .anchor(0.5f, 0.5f));
//	    mapa.addMarker(new MarkerOptions()
//        .position(LocalizacionFinal)
//        .title("Yo")
//        .snippet("Final carrera")
//        .icon(BitmapDescriptorFactory
//               .fromResource(R.drawable.bicycle_happy))
//        .anchor(0.5f, 0.5f));
//	}
//	
//	
//	public void lanzarMain(View view ){
//		Intent i = new Intent(this, MainActivity.class);		
//		startActivity(i);
//	}
//	
//	public void lanzarCompartir(View view){
//	
//	String message = "Este es el resumen de la carrera que acabo de hacer con Run4URwife:";
//	String s1 = String.format("%.3f", recorrido/1000);
//	message = message+" recorrido: "+s1 +"km";
//	message = message+ " tiempo: "+currentTime ;
//	String s2 = String.format("%.2f", velocidad_media);
//	message = message+ " velocidad media: "+s2+"km/h" ;
//	Intent share = new Intent(Intent.ACTION_SEND);
//	share.setType("text/plain");
//	share.putExtra(Intent.EXTRA_TEXT, message);
//
//	startActivity(Intent.createChooser(share, "Comparte tu carrera con los demás"));
//
//		
//	}
//	
//	
////	saveLog(time+";"+inicioLat+";"+inicioLng+";"+finalLat+";"+finalLng+
////			 ";"+recorrido+";"+tiempo, "LocationLog.txt");
//	
	
	
	
}
