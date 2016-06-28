package pa_state_of_change.precious.comnet.aalto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;


public class PA_SOC_SecondActivity extends Fragment {

    public static final String TAG = "PA_SOC_SecondActivity";
    public static final String PA_SOC_PREFS_NAME = "PA_SOC_Preferences";
    public static final String OG_PREFS_NAME = "OGsubappPreferences";
    public static SharedPreferences preferences;
    public TextView tvFeedback;
    public Button okButton;
    public RadioGroup rg;
    public RadioButton rb0;
    public RadioButton rb1;
    public RadioButton rb2;
    public RadioButton rb3;
    private View v;
    MyReceiver r;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.pa_soc_layout2, null);
        preferences = this.getActivity().getSharedPreferences(PA_SOC_PREFS_NAME, 0);
        tvFeedback = (TextView) v.findViewById(R.id.pa_soc_2nd_screen_text);
        okButton = (Button) v.findViewById(R.id.button);
        rg = (RadioGroup) v.findViewById(R.id.radio_group);
        rb0 = (RadioButton) v.findViewById(R.id.radioButton);
        rb1 = (RadioButton) v.findViewById(R.id.radioButton2);
        rb2 = (RadioButton) v.findViewById(R.id.radioButton3);
        rb3 = (RadioButton) v.findViewById(R.id.radioButton4);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonID = group.getCheckedRadioButtonId();
                View radioButton = group.findViewById(radioButtonID);
                int selection = group.indexOfChild(radioButton);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("pa_soc_2nd_act_selection",selection);
                editor.apply();
            }
        });
        updateView();
        return v;
    }

    /**
     *
     */
    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(pa_soc_activity.appConext).registerReceiver(r,
                new IntentFilter("PA_SOC_2_REFRESH"));
    }
    public void onPause(){
        super.onPause();
        LocalBroadcastManager.getInstance(pa_soc_activity.appConext).unregisterReceiver(r);
    }

    public void updateView (){
        int selection = preferences.getInt("pa_soc_1st_act_selection", -1);
        int selection2 = preferences.getInt("pa_soc_2nd_act_selection", -1);
        switch (selection){
            case 0  :
                tvFeedback.setText(getResources().getString(R.string.pa_soc_2nd_screen_feedback0));
                okButton.setVisibility(View.GONE);

                rb0.setVisibility(View.VISIBLE);
                rb0.setText(getResources().getString(R.string.pa_soc_2nd_screen_response0a));
                rb1.setVisibility(View.VISIBLE);
                rb1.setText(getResources().getString(R.string.pa_soc_2nd_screen_response0b));
                rb2.setVisibility(View.GONE);
                rb3.setVisibility(View.GONE);

                switch (selection2){
                    case 0  :   rg.check(R.id.radioButton); break;
                    case 1  :   rg.check(R.id.radioButton2); break;
                    default :   rg.clearCheck(); break;
                }
                break;
            case 1  :
                tvFeedback.setText(getResources().getString(R.string.pa_soc_2nd_screen_feedback1_2));
                okButton.setVisibility(View.GONE);

                rb0.setVisibility(View.VISIBLE);
                rb0.setText(getResources().getString(R.string.pa_soc_2nd_screen_response1_2a));
                rb1.setVisibility(View.VISIBLE);
                rb1.setText(getResources().getString(R.string.pa_soc_2nd_screen_response1_2b));
                rb2.setVisibility(View.VISIBLE);
                rb2.setText(getResources().getString(R.string.pa_soc_2nd_screen_response1_2c));
                rb3.setVisibility(View.VISIBLE);
                rb3.setText(getResources().getString(R.string.pa_soc_2nd_screen_response1_2d));

                switch (selection2){
                    case 0  :   rg.check(R.id.radioButton); break;
                    case 1  :   rg.check(R.id.radioButton2); break;
                    case 2  :   rg.check(R.id.radioButton3); break;
                    case 3  :   rg.check(R.id.radioButton4); break;
                    default :   rg.clearCheck(); break;
                }
                break;
            case 2  :
                tvFeedback.setText(getResources().getString(R.string.pa_soc_2nd_screen_feedback1_2));
                okButton.setVisibility(View.GONE);

                rb0.setVisibility(View.VISIBLE);
                rb0.setText(getResources().getString(R.string.pa_soc_2nd_screen_response1_2a));
                rb1.setVisibility(View.VISIBLE);
                rb1.setText(getResources().getString(R.string.pa_soc_2nd_screen_response1_2b));
                rb2.setVisibility(View.VISIBLE);
                rb2.setText(getResources().getString(R.string.pa_soc_2nd_screen_response1_2c));
                rb3.setVisibility(View.VISIBLE);
                rb3.setText(getResources().getString(R.string.pa_soc_2nd_screen_response1_2d));

                switch (selection2){
                    case 0  :   rg.check(R.id.radioButton); break;
                    case 1  :   rg.check(R.id.radioButton2); break;
                    case 2  :   rg.check(R.id.radioButton3); break;
                    case 3  :   rg.check(R.id.radioButton4); break;
                    default :   rg.clearCheck(); break;
                }
                break;
            case 3  :
                SharedPreferences og_preferences = pa_soc_activity.appConext.getSharedPreferences(OG_PREFS_NAME, 0);
                int preferred_OG_Box_aux = og_preferences.getInt("preferredBox1", -1);
                int preferred_OG_BOX;
                Log.i(TAG,"preferredBox1="+preferred_OG_Box_aux);
                switch (preferred_OG_Box_aux){
                    case 1  :   preferred_OG_BOX = og_preferences.getInt("selectedBox1", -1); break;
                    case 2  :   preferred_OG_BOX = og_preferences.getInt("selectedBox2", -1); break;
                    case 3  :   preferred_OG_BOX = og_preferences.getInt("selectedBox3", -1); break;
                    case 4  :   preferred_OG_BOX = og_preferences.getInt("selectedBox4", -1); break;
                    default: preferred_OG_BOX=-1;
                }
                Log.i(TAG,"preferred_OG_BOX="+preferred_OG_BOX);
                if(preferred_OG_BOX==-1)
                    tvFeedback.setText(getResources().getString(R.string.pa_soc_2nd_screen_feedback3));
                else{
                    String stringIDname = ("outcomegoal_goal").concat(Integer.toString(preferred_OG_BOX));
                    int StringID = getResources().getIdentifier(stringIDname, "string", pa_soc_activity.appConext.getPackageName());
                    String feedbackString = getResources().getString(R.string.pa_soc_2nd_screen_feedback3);
                    String outcome_goal=getResources().getString(StringID);
                    tvFeedback.setText(String.format(feedbackString, outcome_goal));
                }


                okButton.setVisibility(View.VISIBLE);

                rb0.setVisibility(View.GONE);
                rb1.setVisibility(View.GONE);
                rb2.setVisibility(View.GONE);
                rb3.setVisibility(View.GONE);

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
            PA_SOC_SecondActivity.this.updateView();
        }
    }
}
