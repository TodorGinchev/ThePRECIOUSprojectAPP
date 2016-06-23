package pa_state_of_change.precious.comnet.aalto;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import aalto.comnet.thepreciousproject.R;


public class PA_SOC_FirstActivity extends Fragment  {
    public static final String PA_SOC_PREFS_NAME = "PA_SOC_Preferences";
    public static SharedPreferences preferences;
    public static RadioGroup rg;
    private View v;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.pa_soc_layout1, null);
        preferences = this.getActivity().getSharedPreferences(PA_SOC_PREFS_NAME, 0);
        rg = (RadioGroup) v.findViewById(R.id.radio_group);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonID = group.getCheckedRadioButtonId();
                View radioButton = group.findViewById(radioButtonID);
                int selection = group.indexOfChild(radioButton);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("pa_soc_1st_act_selection",selection);
//                editor.putInt("pa_soc_2nd_act_selection",-1);
                editor.apply();
            }
        });
        return v;
    }

    private void initRadioGroup (){
        int selection = preferences.getInt("pa_soc_1st_act_selection", -1);
        switch (selection){
            case 0  :   rg.check(R.id.radioButton); break;
            case 1  :   rg.check(R.id.radioButton2); break;
            case 2  :   rg.check(R.id.radioButton3); break;
            case 3  :   rg.check(R.id.radioButton4); break;
            default:    break;
        }
    }



    /**
     *
     */
    public void onPause() {
        super.onPause();
    }
    /**
     *
     */
    public void onResume() {
        initRadioGroup();
        super.onResume();
    }

}
