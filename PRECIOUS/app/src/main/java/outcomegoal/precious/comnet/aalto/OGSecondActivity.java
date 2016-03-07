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



public class OGSecondActivity extends Fragment {
    public static final String PREFS_NAME = "OGsubappPreferences";
    private static final int NUMBOXES = 20; //number of checkboxes
    private static final int MAX_SEL_ITEMS=4; //Maximum number of checkboxes that can be selected at the same time;
    private CheckBox[] cb = new CheckBox[NUMBOXES];//array with the checkbox objects
    public int[] selectedBoxes = new int[MAX_SEL_ITEMS]; //this array contains the outcome goal selected by the user
    private int current_sel_items;
    public View v; //needs to the accessible from OGThirdActivity <=== Nope, actually there is no need to be public
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.og_layout2, null);

        initCheckboxes();


        return v;
    }
    /**
     * Initialise the checkbox array
     */
    void initCheckboxes(){
        cb[0]=(CheckBox) v.findViewById(R.id.checkBox1);
        cb[1]=(CheckBox) v.findViewById(R.id.checkBox2);
        cb[2]=(CheckBox) v.findViewById(R.id.checkBox3);
        cb[3]=(CheckBox) v.findViewById(R.id.checkBox4);
        cb[4]=(CheckBox) v.findViewById(R.id.checkBox5);
        cb[5]=(CheckBox) v.findViewById(R.id.checkBox6);
        cb[6]=(CheckBox) v.findViewById(R.id.checkBox7);
        cb[7]=(CheckBox) v.findViewById(R.id.checkBox8);
        cb[8]=(CheckBox) v.findViewById(R.id.checkBox9);
        cb[9]=(CheckBox) v.findViewById(R.id.checkBox10);
        cb[10]=(CheckBox) v.findViewById(R.id.checkBox11);
        cb[11]=(CheckBox) v.findViewById(R.id.checkBox12);
        cb[12]=(CheckBox) v.findViewById(R.id.checkBox13);
        cb[13]=(CheckBox) v.findViewById(R.id.checkBox14);
        cb[14]=(CheckBox) v.findViewById(R.id.checkBox15);
        cb[15]=(CheckBox) v.findViewById(R.id.checkBox16);
        cb[16]=(CheckBox) v.findViewById(R.id.checkBox17);
        cb[17]=(CheckBox) v.findViewById(R.id.checkBox18);
        cb[18]=(CheckBox) v.findViewById(R.id.checkBox19);
        cb[19]=(CheckBox) v.findViewById(R.id.checkBox20);

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
            if(selectedBoxes[0]!=(int)cb[i].getTag() && selectedBoxes[1]!=(int)cb[i].getTag() && selectedBoxes[2]!=(int)cb[i].getTag() && selectedBoxes[3]!=(int)cb[i].getTag())
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
        editor.putInt("selectedBox1",selectedBoxes[0]);
        editor.putInt("selectedBox2",selectedBoxes[1]);
        editor.putInt("selectedBox3",selectedBoxes[2]);
        editor.putInt("selectedBox4",selectedBoxes[3]);
        //One of the boxes has been unchecked => uncheck the prefered goal too
        editor.putInt("preferedBox1",-1);
        editor.commit();
    }

    /**
     *
     */
    public void loadPreferences(){
        SharedPreferences preferences = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        selectedBoxes[0]=preferences.getInt("selectedBox1",-1);
        selectedBoxes[1]=preferences.getInt("selectedBox2",-1);
        selectedBoxes[2]=preferences.getInt("selectedBox3",-1);
        selectedBoxes[3]=preferences.getInt("selectedBox4",-1);
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
