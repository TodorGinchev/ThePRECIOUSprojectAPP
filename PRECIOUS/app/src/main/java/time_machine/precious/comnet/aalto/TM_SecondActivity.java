package time_machine.precious.comnet.aalto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;


public class TM_SecondActivity extends Fragment {

    public static final String TAG = "PA_SOC_SecondActivity";
    public static final String TM_PREFS_NAME = "TM_Preferences";
    public SharedPreferences preferences;
    TextView tv;
    private View v;
    MyReceiver r;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.tm_layout2, null);
        preferences = this.getActivity().getSharedPreferences(TM_PREFS_NAME, 0);
        tv = (TextView) v.findViewById(R.id.textView2);
        return v;
    }

    /**
     *
     */
    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(time_machine_activity.appConext).registerReceiver(r,
                new IntentFilter("TM_2_REFRESH"));
    }
    public void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(time_machine_activity.appConext).unregisterReceiver(r);
    }

    public void updateView (){
        if(preferences.getBoolean("tm_remember_last_time",false) &&
                !preferences.getString("tm_last_time_year"," ").equals(" ")){
            tv.setText(getString(R.string.tm_2nd_screen_yes));
            tv.setText(String.format(getString(R.string.tm_2nd_screen_yes), preferences.getString("tm_last_time_year"," ")));
        }
        else{
            tv.setText(R.string.tm_2nd_screen_no);
        }
    }

    /**
     *
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            TM_SecondActivity.this.updateView();
        }
    }
}
