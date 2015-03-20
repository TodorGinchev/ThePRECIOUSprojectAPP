package aalto.comnet.thepreciouspedometer;



import aalto.comnet.thepreciousproject.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


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
