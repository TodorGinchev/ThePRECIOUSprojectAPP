package aalto.comnet.thepreciousviewer;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import theprecioussandbox.comnet.aalto.precious.R;

public class AlcoholConfig extends Activity{
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);      
        setContentView(R.layout.alco_config);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences(
        		"com.example._precious.SHARED_PREFERENCES", Context.MODE_PRIVATE);
        
        //
        //Link seek bars progresses to text views
        //
        final TextView tv1 = (TextView) findViewById(R.id.textView7); 
        tv1.setText(pref.getInt("beerConsum", 0)+" units"); 
        SeekBar sk1=(SeekBar) findViewById(R.id.seekBar1);
        sk1.setProgress((int)(pref.getInt("beerConsum", 0)));
        sk1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {   
	        @Override       
	        public void onStopTrackingTouch(SeekBar seekBar) {      
	            // Do nothing   
	        }   
	        @Override       
	        public void onStartTrackingTouch(SeekBar seekBar) {     
	            // Do nothing    
	        }       	
	        @Override       
	        public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) { 
	        	//Transfer changed progress to text view
	        	tv1.setText(" "+progress+" units");	
	        	SharedPreferences.Editor editor = pref.edit();
	        	editor.putInt("beerConsum", progress);
	        	editor.commit();
	        }       
        });  
        
        final TextView tv2 = (TextView) findViewById(R.id.textView8); 
        tv2.setText(pref.getInt("wineConsum", 0)+" units"); 
        SeekBar sk2=(SeekBar) findViewById(R.id.seekBar2);  
        sk2.setProgress((int)(pref.getInt("wineConsum", 0)));
        sk2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {   
	        @Override       
	        public void onStopTrackingTouch(SeekBar seekBar) {      
	            // Do nothing   
	        }   
	        @Override       
	        public void onStartTrackingTouch(SeekBar seekBar) {     
	            // Do nothing    
	        }       	
	        @Override       
	        public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) { 
	        	//Transfer changed progress to text view
	            tv2.setText(" "+progress+" units");	
	            SharedPreferences.Editor editor = pref.edit();
	        	editor.putInt("wineConsum", progress);
	        	editor.commit();
	        }       
        }); 
        
        final TextView tv3 = (TextView) findViewById(R.id.textView9); 
        tv3.setText(pref.getInt("cocktailConsum", 0)+" units"); 
        SeekBar sk3=(SeekBar) findViewById(R.id.seekBar3);     
        sk3.setProgress((int)(pref.getInt("cocktailConsum", 0)));
        sk3.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {   
	        @Override       
	        public void onStopTrackingTouch(SeekBar seekBar) {      
	            // Do nothing   
	        }   
	        @Override       
	        public void onStartTrackingTouch(SeekBar seekBar) {     
	            // Do nothing    
	        }       	
	        @Override       
	        public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) { 
	        	//Transfer changed progress to text view
	        	tv3.setText(" "+progress+" units");	
	        	SharedPreferences.Editor editor = pref.edit();
	        	editor.putInt("cocktailConsum", progress);
	        	editor.commit();
	        }       
        }); 
        
        final TextView tv4 = (TextView) findViewById(R.id.textView10); 
        tv4.setText(pref.getInt("spiritsConsum", 0)+" units"); 
        SeekBar sk4=(SeekBar) findViewById(R.id.seekBar4);     
        sk4.setProgress((int)(pref.getInt("spiritsConsum", 0)));
        sk4.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {   
	        @Override       
	        public void onStopTrackingTouch(SeekBar seekBar) {      
	            // Do nothing   
	        }   
	        @Override       
	        public void onStartTrackingTouch(SeekBar seekBar) {     
	            // Do nothing    
	        }       	
	        @Override       
	        public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) { 
	        	//Transfer changed progress to text view
	        	tv4.setText(" "+progress+" units");
	        	SharedPreferences.Editor editor = pref.edit();
	        	editor.putInt("spiritsConsum", progress);
	        	editor.commit();
	        }       
        }); 
    }
}
