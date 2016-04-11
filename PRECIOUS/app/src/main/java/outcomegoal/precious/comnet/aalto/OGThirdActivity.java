package outcomegoal.precious.comnet.aalto;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;


public class OGThirdActivity extends Fragment  {
    public static final String PREFS_NAME = "OGsubappPreferences";
    private static final int NUMBOXES = 4; //number of checkboxes
    MyReceiver r; //YES! I am using a broadcast receiver to update the view... so what???????
    private CheckBox[] cb = new CheckBox[NUMBOXES];//array with the checkbox objects
    public int[] selectedBoxesPage2 = new int[NUMBOXES]; //this array contains the outcome goal selected by the user IN OGsecondActivity!
    public int selectedBox; // this is the id of the preffered goal
    public boolean boxSelected; //this boolean defines if the preferred goal is selected
    private View v;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.og_layout3, null);
        getSelectedBoxes();
        initCheckboxes();
        return v;
    }
    /**
     *
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            OGThirdActivity.this.updateView();
        }
    }
    /**
     *
     */
    public void updateView(){
        getSelectedBoxes();
        initCheckboxes();
    }
    /**
     *
     */
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(outcomegoal_activity.appConext).unregisterReceiver(r);
    }
    /**
     *
     */
    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(outcomegoal_activity.appConext).registerReceiver(r,
                new IntentFilter("OG3_REFRESH"));
    }
    /**
     * This method gets the selected outcome goals from the previous page (OGSecondActivity)
     */
    public void getSelectedBoxes(){
        SharedPreferences preferences = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        selectedBoxesPage2[0] = preferences.getInt("selectedBox1", -1);
        selectedBoxesPage2[1] = preferences.getInt("selectedBox2", -1);
        selectedBoxesPage2[2] = preferences.getInt("selectedBox3", -1);
        selectedBoxesPage2[3] = preferences.getInt("selectedBox4", -1);
    }
    /**
     * Initialise the checkbox array
     */
    void initCheckboxes() {
        cb[0] = (CheckBox) v.findViewById(R.id.checkBox1);
        cb[1] = (CheckBox) v.findViewById(R.id.checkBox2);
        cb[2] = (CheckBox) v.findViewById(R.id.checkBox3);
        cb[3] = (CheckBox) v.findViewById(R.id.checkBox4);

        int numSelectedboxes = 0;

        for (int i = 0; i < NUMBOXES; i++) {
            if (selectedBoxesPage2[i] != -1) {
                numSelectedboxes++;
                cb[i].setVisibility(View.VISIBLE);
                String stringIDname = ("outcomegoal_goal").concat(Integer.toString(selectedBoxesPage2[i]));
                int StringID = getResources().getIdentifier(stringIDname, "string", outcomegoal_activity.appConext.getPackageName());
                String text = getString(StringID);
                if (text.startsWith("type your own reason")) {
                    SharedPreferences preferences = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
                    cb[i].setText(preferences.getString("manualOutcomeGoal", ""));
                } else
                    cb[i].setText(text);
            } else
                cb[i].setVisibility(View.GONE);
        }
        // Set tags
        for (int i = 0; i < cb.length; i++)
            cb[i].setTag(i + 1);
        //Load array with selected boxes
        loadPreferences();
        //Check number of selected boxes
        CheckSelectedBoxes();

        //Setup onClick listeners for all the checkboxes
        for (int i = 0; i < cb.length; i++) {
            cb[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    if (buttonView.isChecked()) {
                        selectedBox = (int) buttonView.getTag();
                        buttonView.setBackgroundColor(getResources().getColor(R.color.checkbox_selected_background));
                        disableCheckboxes();
                    } else {
                        buttonView.setBackgroundColor(getResources().getColor(R.color.checkbox_background));
                        enableCheckboxes();
                    }
                    saveSelectedBoxes();
                }
            });
        }

        TextView tv = (TextView) v.findViewById(R.id.textView2);
        if (numSelectedboxes == 0) {
            tv.setText(getResources().getString(R.string.outcomegoal_2nd_screen_no_selection));
        } else {
            tv.setText(getResources().getString(R.string.outcomegoal_3rd_screen));
            if (numSelectedboxes == 1) {
                for (int i = 0; i < NUMBOXES; i++) {
                    if (selectedBoxesPage2[i] != -1)
                        cb[i].setChecked(true);
                }
            }
        }
    }
    /**
     * Disable checkboxes to be selected (when maximum number of selected boxes is reached)
     */
    void disableCheckboxes(){
        boxSelected=true;
        for(int i=0; i<cb.length; i++){
            if(selectedBox!=(int)cb[i].getTag())
                cb[i].setEnabled(false);
        }
    }
    /**
     *
     */
    void uncheckBoxes(){
        for(int i=0; i<cb.length; i++) {
            cb[i].setChecked(false);
        }
    }
    /**
     * Enable checkboxes to be selected
     */
    void enableCheckboxes(){
        boxSelected=false;
        for(int i=0; i<cb.length; i++) {
            cb[i].setEnabled(true);
        }
    }
    /**
     *
     */
    public void saveSelectedBoxes(){
        SharedPreferences preferences = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("preferredBox1",selectedBox);
        editor.apply();
    }
    /**
     *
     */
    public void loadPreferences(){
        SharedPreferences preferences = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        selectedBox=preferences.getInt("preferredBox1",-1);
    }
    /**
     *
     */
    public void CheckSelectedBoxes(){
        boolean enableSelection=false;
            if(selectedBox==-1) {
                enableSelection = true;
                uncheckBoxes();
            }
            else {
                CheckBox cb_aux = (CheckBox) v.findViewWithTag(selectedBox);
                cb_aux.setChecked(true);
            }
        if(enableSelection)
            enableCheckboxes();
        else
            disableCheckboxes();
    }


}
