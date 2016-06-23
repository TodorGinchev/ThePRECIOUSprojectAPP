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
                    case 1  :
                        SharedPreferences og_preferences = pa_soc_activity.appConext.getSharedPreferences(OG_PREFS_NAME, 0);
                        int preferred_OG_Box_aux = og_preferences.getInt("preferredBox1", -1);
                        int preferred_OG_BOX;
                        switch (preferred_OG_Box_aux){
                            case 1  :   preferred_OG_BOX = og_preferences.getInt("selectedBox1", -1); break;
                            case 2  :   preferred_OG_BOX = og_preferences.getInt("selectedBox2", -1); break;
                            case 3  :   preferred_OG_BOX = og_preferences.getInt("selectedBox3", -1); break;
                            case 4  :   preferred_OG_BOX = og_preferences.getInt("selectedBox4", -1); break;
                            default: preferred_OG_BOX=-1;
                        }
                        if(preferred_OG_BOX==-1)
                            tvFeedback.setText(getResources().getString(R.string.pa_soc_3rd_screen_feedback1_2b));
                        else{
                            String stringIDname = ("outcomegoal_goal").concat(Integer.toString(preferred_OG_BOX));
                            int StringID = getResources().getIdentifier(stringIDname, "string", pa_soc_activity.appConext.getPackageName());
                            String feedbackString = getResources().getString(R.string.pa_soc_3rd_screen_feedback1_2b);
                            String outcome_goal=getResources().getString(StringID);
                            tvFeedback.setText(String.format(feedbackString, outcome_goal));
                        }
                        break;
                    case 2  :   tvFeedback.setText(getResources().getString(R.string.pa_soc_3rd_screen_feedback1_2c)); break;
                    case 3  :
                        SharedPreferences og_preferences2 = pa_soc_activity.appConext.getSharedPreferences(OG_PREFS_NAME, 0);
                        int preferred_OG_Box_aux2 = og_preferences2.getInt("preferredBox1", -1);
                        int preferred_OG_BOX2;
                        switch (preferred_OG_Box_aux2){
                            case 1  :   preferred_OG_BOX2 = og_preferences2.getInt("selectedBox1", -1); break;
                            case 2  :   preferred_OG_BOX2 = og_preferences2.getInt("selectedBox2", -1); break;
                            case 3  :   preferred_OG_BOX2 = og_preferences2.getInt("selectedBox3", -1); break;
                            case 4  :   preferred_OG_BOX2 = og_preferences2.getInt("selectedBox4", -1); break;
                            default: preferred_OG_BOX2=-1;
                        }
                        if(preferred_OG_BOX2==-1)
                            tvFeedback.setText(getResources().getString(R.string.pa_soc_3rd_screen_feedback1_2d));
                        else{
                            String stringIDname = ("outcomegoal_goal").concat(Integer.toString(preferred_OG_BOX2));
                            int StringID = getResources().getIdentifier(stringIDname, "string", pa_soc_activity.appConext.getPackageName());
                            String feedbackString = getResources().getString(R.string.pa_soc_3rd_screen_feedback1_2d);
                            String outcome_goal=getResources().getString(StringID);
                            tvFeedback.setText(String.format(feedbackString, outcome_goal));
                        }
                        break;
                    default :   break;
                }
                break;
            case 2  :
                switch (selection2){
                    case 0  :   tvFeedback.setText(getResources().getString(R.string.pa_soc_3rd_screen_feedback1_2a));break;
                    case 1  :
                        SharedPreferences og_preferences = pa_soc_activity.appConext.getSharedPreferences(OG_PREFS_NAME, 0);
                        int preferred_OG_Box_aux = og_preferences.getInt("preferredBox1", -1);
                        int preferred_OG_BOX;
                        switch (preferred_OG_Box_aux){
                            case 1  :   preferred_OG_BOX = og_preferences.getInt("selectedBox1", -1); break;
                            case 2  :   preferred_OG_BOX = og_preferences.getInt("selectedBox2", -1); break;
                            case 3  :   preferred_OG_BOX = og_preferences.getInt("selectedBox3", -1); break;
                            case 4  :   preferred_OG_BOX = og_preferences.getInt("selectedBox4", -1); break;
                            default: preferred_OG_BOX=-1;
                        }
                        if(preferred_OG_BOX==-1)
                            tvFeedback.setText(getResources().getString(R.string.pa_soc_3rd_screen_feedback1_2b));
                        else{
                            String stringIDname = ("outcomegoal_goal").concat(Integer.toString(preferred_OG_BOX));
                            int StringID = getResources().getIdentifier(stringIDname, "string", pa_soc_activity.appConext.getPackageName());
                            String feedbackString = getResources().getString(R.string.pa_soc_3rd_screen_feedback1_2b);
                            String outcome_goal=getResources().getString(StringID);
                            tvFeedback.setText(String.format(feedbackString, outcome_goal));
                        }
                        break;
                    case 2  :   tvFeedback.setText(getResources().getString(R.string.pa_soc_3rd_screen_feedback1_2c)); break;
                    case 3  :
                        SharedPreferences og_preferences2 = pa_soc_activity.appConext.getSharedPreferences(OG_PREFS_NAME, 0);
                        int preferred_OG_Box_aux2 = og_preferences2.getInt("preferredBox1", -1);
                        int preferred_OG_BOX2;
                        switch (preferred_OG_Box_aux2){
                            case 1  :   preferred_OG_BOX2 = og_preferences2.getInt("selectedBox1", -1); break;
                            case 2  :   preferred_OG_BOX2 = og_preferences2.getInt("selectedBox2", -1); break;
                            case 3  :   preferred_OG_BOX2 = og_preferences2.getInt("selectedBox3", -1); break;
                            case 4  :   preferred_OG_BOX2 = og_preferences2.getInt("selectedBox4", -1); break;
                            default: preferred_OG_BOX2=-1;
                        }
                        if(preferred_OG_BOX2==-1)
                            tvFeedback.setText(getResources().getString(R.string.pa_soc_3rd_screen_feedback1_2d));
                        else{
                            String stringIDname = ("outcomegoal_goal").concat(Integer.toString(preferred_OG_BOX2));
                            int StringID = getResources().getIdentifier(stringIDname, "string", pa_soc_activity.appConext.getPackageName());
                            String feedbackString = getResources().getString(R.string.pa_soc_3rd_screen_feedback1_2d);
                            String outcome_goal=getResources().getString(StringID);
                            tvFeedback.setText(String.format(feedbackString, outcome_goal));
                        }
                        break;
                    default :   break;
                }
                break;
            case 3  :
                SharedPreferences og_preferences = pa_soc_activity.appConext.getSharedPreferences(OG_PREFS_NAME, 0);
                int preferred_OG_Box_aux = og_preferences.getInt("preferredBox1", -1);
                int preferred_OG_BOX;
                switch (preferred_OG_Box_aux){
                    case 1  :   preferred_OG_BOX = og_preferences.getInt("selectedBox1", -1); break;
                    case 2  :   preferred_OG_BOX = og_preferences.getInt("selectedBox2", -1); break;
                    case 3  :   preferred_OG_BOX = og_preferences.getInt("selectedBox3", -1); break;
                    case 4  :   preferred_OG_BOX = og_preferences.getInt("selectedBox4", -1); break;
                    default: preferred_OG_BOX=-1;
                }
                if(preferred_OG_BOX==-1)
                    tvFeedback.setText(getResources().getString(R.string.pa_soc_2nd_screen_feedback3));
                else{
                    String stringIDname = ("outcomegoal_goal").concat(Integer.toString(preferred_OG_BOX));
                    int StringID = getResources().getIdentifier(stringIDname, "string", pa_soc_activity.appConext.getPackageName());
                    String feedbackString = getResources().getString(R.string.pa_soc_2nd_screen_feedback3);
                    String outcome_goal=getResources().getString(StringID);
                    tvFeedback.setText(String.format(feedbackString, outcome_goal));
                }
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
