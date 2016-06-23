package pa_state_of_change.precious.comnet.aalto;

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
import android.widget.ImageButton;
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;


public class PA_SOC_ThirdActivity extends Fragment {


    MyReceiver r;
    public static final String PA_SOC_PREFS_NAME = "PA_SOC_Preferences";
    public static final String OG_PREFS_NAME = "OGsubappPreferences";
    public static SharedPreferences preferences;
    public TextView tvFeedback;
    public ImageButton okButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.pa_soc_layout3, null);

        preferences = this.getActivity().getSharedPreferences(PA_SOC_PREFS_NAME, 0);
        tvFeedback = (TextView) v.findViewById(R.id.pa_soc_3rd_screen_text);
        okButton = (ImageButton) v.findViewById(R.id.button);

        return v;
    }
    /**
     *
     */
    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(pa_soc_activity.appConext).registerReceiver(r,
                new IntentFilter("PA_SOC_3_REFRESH"));
    }
    public void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(pa_soc_activity.appConext).unregisterReceiver(r);
    }

    public void updateView () {
        int selection = preferences.getInt("pa_soc_1st_act_selection", -1);
        int selection2 = preferences.getInt("pa_soc_2nd_act_selection", -1);

        switch (selection){
            case 0  :
                switch (selection2){
                    case 0  : tvFeedback.setText(getResources().getString(R.string.pa_soc_3rd_screen_feedback0a)); break;
                    case 1  : tvFeedback.setText(getResources().getString(R.string.pa_soc_3rd_screen_feedback0b)); break;
                    default :   break;
                }
                break;
            case 1  :
                switch (selection2){
                    case 0  :   tvFeedback.setText(getResources().getString(R.string.pa_soc_3rd_screen_feedback1_2a));break;
                    case 1  :   tvFeedback.setText(getResources().getString(R.string.pa_soc_3rd_screen_feedback1_2b)); break;
                    case 2  :   tvFeedback.setText(getResources().getString(R.string.pa_soc_3rd_screen_feedback1_2c)); break;
                    case 3  :   tvFeedback.setText(getResources().getString(R.string.pa_soc_3rd_screen_feedback1_2d)); break;
                    default :   break;
                }
                break;
            case 2  :
                switch (selection2){
                    case 0  :   tvFeedback.setText(getResources().getString(R.string.pa_soc_3rd_screen_feedback1_2a));break;
                    case 1  :   tvFeedback.setText(getResources().getString(R.string.pa_soc_3rd_screen_feedback1_2b)); break;
                    case 2  :   tvFeedback.setText(getResources().getString(R.string.pa_soc_3rd_screen_feedback1_2c)); break;
                    case 3  :   tvFeedback.setText(getResources().getString(R.string.pa_soc_3rd_screen_feedback1_2d)); break;
                    default :   break;
                }
                break;
            case 3  :
                tvFeedback.setText(getResources().getString(R.string.pa_soc_2nd_screen_feedback3));
                break;
            default:    break;
        }

    }

    /**
     *
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            PA_SOC_ThirdActivity.this.updateView();
        }
    }
}
