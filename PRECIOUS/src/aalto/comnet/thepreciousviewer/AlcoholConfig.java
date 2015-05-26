package aalto.comnet.thepreciousviewer;


import aalto.comnet.thepreciousproject.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class AlcoholConfig extends Activity{
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);      
        setContentView(R.layout.alco_config);
        
        
        //
        //Link seek bars progresses to text views
        //
        final TextView tv1 = (TextView) findViewById(R.id.textView7); 
        tv1.setText("0 units"); 
        SeekBar sk1=(SeekBar) findViewById(R.id.seekBar1);     
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
	        }       
        });  
        
        final TextView tv2 = (TextView) findViewById(R.id.textView8); 
        tv2.setText("0 units"); 
        SeekBar sk2=(SeekBar) findViewById(R.id.seekBar2);     
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
	        }       
        }); 
        
        final TextView tv3 = (TextView) findViewById(R.id.textView9); 
        tv3.setText("0 units"); 
        SeekBar sk3=(SeekBar) findViewById(R.id.seekBar3);     
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
	        }       
        }); 
        
        final TextView tv4 = (TextView) findViewById(R.id.textView10); 
        tv4.setText("0 units"); 
        SeekBar sk4=(SeekBar) findViewById(R.id.seekBar4);     
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
	        }       
        }); 
    }
}
