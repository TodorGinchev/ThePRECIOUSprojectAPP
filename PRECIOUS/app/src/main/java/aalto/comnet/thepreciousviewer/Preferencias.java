package aalto.comnet.thepreciousviewer;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import theprecioussandbox.comnet.aalto.precious.R;


public class Preferencias extends PreferenceActivity {

	   @SuppressWarnings("deprecation")
	@Override protected void onCreate(Bundle savedInstanceState) {

	      super.onCreate(savedInstanceState);

	      addPreferencesFromResource(R.xml.preferencias);

	   }

	     }