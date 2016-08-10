package outcomegoal.precious.comnet.aalto;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;


public class OGFirstActivity  extends Fragment {

    public static final String UI_PREFS_NAME = "UIPreferences";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.og_layout1, null);

        TextView tv = (TextView) v.findViewById(R.id.og_1st_screen_text2);
        SharedPreferences preferences = outcomegoal_activity.appConext.getSharedPreferences(UI_PREFS_NAME, 0);
        if( preferences.getBoolean("OGset",false)){
            tv.setText(R.string.outcomegoal_1st_screen_text2_completed);
        }
        else{
            tv.setText(R.string.outcomegoal_1st_screen_text2);
        }
        return v;
    }
}
