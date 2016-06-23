package time_machine.precious.comnet.aalto;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import aalto.comnet.thepreciousproject.R;
import uploader.precious.comnet.aalto.upUtils;


public class TM_FirstActivity extends Fragment  {
    public static final String TAG = "TM_FirstActivity";
    public static final String TM_PREFS_NAME = "TM_Preferences";
    public static SharedPreferences preferences;
    public Spinner spinnerYear;
    private View v;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.tm_layout1, null);
        preferences = this.getActivity().getSharedPreferences(TM_PREFS_NAME, 0);
        spinnerYear = (Spinner) v.findViewById(R.id.spinner);
        updateView();
        return v;
    }

    public void updateView(){
        List<String> list = new ArrayList<String>();
        SharedPreferences up_preferences = this.getActivity().getSharedPreferences(upUtils.PREFS_NAME, 0);
        String birthdate = up_preferences.getString("birthdate", "-1");
        Log.i(TAG, "birthdate=" + birthdate);
        int yearOfBirth=-1;
        try{
            Log.i(TAG,"YEAR OF BIRTH="+birthdate.substring(0,4));
            yearOfBirth=Integer.parseInt(birthdate.substring(0,4));
        }catch (Exception e){
            Log.e(TAG," ",e);
            return;
        }
        if(yearOfBirth==-1)
            return;
        Calendar c_aux = Calendar.getInstance();
        c_aux.setTimeInMillis(System.currentTimeMillis());
        int currentYear = c_aux.get(Calendar.YEAR);
        for(int i=currentYear;i>=yearOfBirth;i--){
            list.add(String.valueOf(i));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(dataAdapter);
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
        super.onResume();
    }
}
