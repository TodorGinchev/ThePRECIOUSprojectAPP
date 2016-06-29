package time_machine.precious.comnet.aalto;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import aalto.comnet.thepreciousproject.R;


public class TM_FirstActivity extends Fragment {

    public SharedPreferences preferences;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tm_layout1, null);
        return v;
    }
}
