package outcomegoal.precious.comnet.aalto;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import ui.precious.comnet.aalto.precious.R;

public class OGThirdActivity extends Fragment  {
    public static final String PREFS_NAME = "OGsubappPreferences";
    private static final int NUMBOXES = 4; //number of checkboxes
    private static final int MAX_SEL_ITEMS=1; //Maximum number of checkboxes that can be selected at the same time;
    private CheckBox[] cb = new CheckBox[NUMBOXES];//array with the checkbox objects
    public int[] selectedBoxesPage2 = new int[NUMBOXES]; //this array contains the outcome goal selected by the user IN OGsecondActivity!
    public int[] selectedBoxes = new int[MAX_SEL_ITEMS]; //this array contains the outcome goal selected by the user in this view
    private int current_sel_items;
    private View v;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.og_layout3, null);


        getSelectedBoxes();
        initCheckboxes();

        return v;
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

        for(int i=0; i<NUMBOXES;i++){
            if(selectedBoxesPage2[i]!=-1){
                cb[i].setVisibility(View.VISIBLE);
                String stringIDname = ("outcomegoal_goal").concat(Integer.toString(selectedBoxesPage2[i]));
                Log.i("STRING ID", stringIDname);
//                int StringID = getResources().getIdentifier("outcomegoal_goal1", "id", "precious.comnet.aalto");
//                String goal_name = getResources().getString(StringID);
//                OGSecondActivity og2 = new OGSecondActivity();
//                CheckBox cb_aux = (CheckBox) og2.v.findViewWithTag(selectedBoxesPage2[i]);
//                cb[i].setText(cb_aux.getText());
                int StringID = getResources().getIdentifier(stringIDname, "string", outcomegoal_activity.appConext.getPackageName());
                String text = getString(StringID);
                cb[i].setText(text);
            }
        }
        // Set tags
        for (int i=0; i<cb.length;i++)
            cb[i].setTag(i+1);
        //init counter
        current_sel_items=0;
        //Load array with selected boxes
        loadPreferences();
        //Check number of selected boxes
        CheckSelectedBoxes();

        //Setup onClick listeners for all the checkboxes
        for(int i=0; i<cb.length; i++){
            cb[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    if (buttonView.isChecked()) {
                        for (int i=0; i<MAX_SEL_ITEMS;i++){
                            if(selectedBoxes [i] == -1) {
                                Log.i("TAG BOX",buttonView.getTag().toString()+"");
                                selectedBoxes[i] = (int)buttonView.getTag();
                                break;
                            }
                        }
                        //TODO
                        current_sel_items++;
                        if(current_sel_items >= MAX_SEL_ITEMS)
                            disableCheckboxes();
                    }
                    else {
                        for (int i=0; i<MAX_SEL_ITEMS;i++){
                            if(selectedBoxes [i]== (int)buttonView.getTag() ) {
                                selectedBoxes[i] = -1;
                                break;
                            }
                        }
                        current_sel_items--;
                        if(current_sel_items < 4)
                            enableCheckboxes();
                        //selectedBoxes[current_sel_items-1]=-1;

                    }
                    saveSelectedBoxes();
                }
            });
        }

    }
    /**
     * Disable checkboxes to be selected (when maximum number of selected boxes is reached)
     */
    void disableCheckboxes(){
        for(int i=0; i<cb.length; i++){
            Log.i("CB TAG_2", cb[i].getTag() + "");
            if(selectedBoxes[0]!=(int)cb[i].getTag())
                cb[i].setEnabled(false);
        }
    }

    /**
     * Enable checkboxes to be selected
     */
    void enableCheckboxes(){
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
        selectedBoxes.toString();
        editor.putInt("preferedBox1",selectedBoxes[0]);
        editor.commit();
    }

    /**
     *
     */
    public void loadPreferences(){
        SharedPreferences preferences = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        selectedBoxes[0]=preferences.getInt("prefferedBox1",-1);
    }

    /**
     *
     */
    public void CheckSelectedBoxes(){
        boolean enableSelection=false;
        for(int i=0; i<MAX_SEL_ITEMS;i++)
            if(selectedBoxes[i]==-1)
                enableSelection=true;
            else{
                CheckBox cb_aux = (CheckBox) v.findViewWithTag(selectedBoxes[i]);
                cb_aux.setChecked(true);
                current_sel_items++;
            }
        if(enableSelection)
            enableCheckboxes();
        else
            disableCheckboxes();
    }
}
