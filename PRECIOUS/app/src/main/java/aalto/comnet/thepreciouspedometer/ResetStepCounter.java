package aalto.comnet.thepreciouspedometer;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;

import theprecioussandbox.comnet.aalto.precious.R;


public class ResetStepCounter extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_step_counter);
        
    }
    
    @Override public void onResume(){
    	super.onResume();
    	Context context = getApplicationContext();
    	Intent i = new Intent(context, aalto.comnet.theprecioussandbox.MainActivity.class);
    	startActivity(i);
    	
        //Ask if user whats to reset counter
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(getString(R.string.reset_counter_prompt));
        builder1.setCancelable(true);
        builder1.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	resetCounter();
            	closeActivity();
            	Context context = getApplicationContext();
            	Intent i = new Intent(context, aalto.comnet.theprecioussandbox.MainActivity.class);
                startActivity(i);
                dialog.cancel();
            }
        });
        builder1.setNegativeButton(getString(R.string.no),
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	closeActivity();
            	Context context = getApplicationContext();
            	Intent i = new Intent(context, aalto.comnet.theprecioussandbox.MainActivity.class);
                startActivity(i);
                dialog.cancel();                
            }
        });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    
    public void resetCounter(){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	Editor editor1 = prefs.edit();
        editor1.putInt("aalto.comnet.thepreciouspedometer.TEMP_STEPS", 0);
        editor1.commit();
    }
    
    public void closeActivity(){
    	finish();
    }
    
}
