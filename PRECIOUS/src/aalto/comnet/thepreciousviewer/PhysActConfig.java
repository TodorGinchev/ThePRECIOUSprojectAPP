package aalto.comnet.thepreciousviewer;


import aalto.comnet.thepreciousproject.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class PhysActConfig extends Activity{
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);      
        setContentView(R.layout.phys_act_config);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences(
        		"com.example._precious.SHARED_PREFERENCES", Context.MODE_PRIVATE);
        
        //
        //Link seek bars progresses to text views
        //
        final TextView tv1 = (TextView) findViewById(R.id.textView7); 
        tv1.setText(pref.getFloat("walkHours", 0)+" h"); 
        SeekBar sk1=(SeekBar) findViewById(R.id.seekBar1);  
        sk1.setProgress((int)(pref.getFloat("walkHours", 0)*10));
        
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
	        	float walkHours = (float)progress/10;
	        	SharedPreferences.Editor editor = pref.edit();
	        	editor.putFloat("walkHours", walkHours);
	        	editor.commit();
	        	tv1.setText("   "+walkHours+"  h");	
	        }       
        });  
        
        final TextView tv2 = (TextView) findViewById(R.id.textView8); 
        tv2.setText(pref.getFloat("runHours", 0)+" h"); 
        SeekBar sk2=(SeekBar) findViewById(R.id.seekBar2);  
        sk2.setProgress((int)(pref.getFloat("runHours", 0)*2));
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
	        	float runHours = (float)progress/2;
	        	SharedPreferences.Editor editor = pref.edit();
	        	editor.putFloat("runHours", runHours);
	        	editor.commit();
	            tv2.setText(" "+runHours+" h");	
	        }       
        }); 
        
        final TextView tv3 = (TextView) findViewById(R.id.textView9); 
        tv3.setText(pref.getFloat("bicycleHours", 0)+" h");
        SeekBar sk3=(SeekBar) findViewById(R.id.seekBar3);  
        sk3.setProgress((int)(pref.getFloat("bicycleHours", 0)*2));
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
	        	float bicycleHours = (float)progress/2;
	        	SharedPreferences.Editor editor = pref.edit();
	        	editor.putFloat("bicycleHours", bicycleHours);
	        	editor.commit();
	        	tv3.setText(" "+bicycleHours+" h");	
	        }       
        }); 
 
    }
}
