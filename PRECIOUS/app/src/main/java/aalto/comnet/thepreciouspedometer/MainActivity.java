package aalto.comnet.thepreciouspedometer;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import theprecioussandbox.comnet.aalto.precious.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);
		Intent i = new Intent(this,PedometerBackgroundService.class);
		startService(i);
		finish();
    }
   

}
