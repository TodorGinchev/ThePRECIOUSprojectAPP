package confidence_ruler.precious.comnet.aalto;

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


public class CR_ForthActivity extends Fragment  {
    public static final String TAG = "FA_FirstActivity";
    public static final String CR_PREFS_NAME = "CRsubappPreferences";
    public static SharedPreferences preferences;
    private View v;
    MyReceiver r;
    public TextView tv;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.cr_layout4, null);
        preferences = this.getActivity().getSharedPreferences(CR_PREFS_NAME, 0);
        tv = (TextView) v.findViewById(R.id.cr_4th_screen_title);
        updateView();

        return v;
    }

    public void updateView(){
        int numSelectedBehaviours=0;
        if(preferences.getBoolean("cr_behaviour1_enabled",false))
            numSelectedBehaviours++;
        if(preferences.getBoolean("cr_behaviour2_enabled",false))
            numSelectedBehaviours++;
        if(preferences.getBoolean("cr_behaviour3_enabled",false))
            numSelectedBehaviours++;
        if(preferences.getBoolean("cr_behaviour4_enabled",false))
            numSelectedBehaviours++;
        if(numSelectedBehaviours==0)
            tv.setText(R.string.cr_4th_screen_feedback1);
        else if (numSelectedBehaviours==1)
            tv.setText(R.string.cr_4th_screen_feedback2);
        else
            tv.setText(R.string.cr_4th_screen_feedback3);
    }
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(confidence_ruler_activity.appConext).unregisterReceiver(r);
    }
    /**
     *
     */
    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(confidence_ruler_activity.appConext).registerReceiver(r,
                new IntentFilter("CR_4_REFRESH"));
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            CR_ForthActivity.this.updateView();
        }
    }
}
