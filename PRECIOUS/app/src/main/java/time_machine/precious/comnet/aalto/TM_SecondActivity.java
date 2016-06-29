package time_machine.precious.comnet.aalto;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import aalto.comnet.thepreciousproject.R;
import uploader.precious.comnet.aalto.upUtils;


public class TM_SecondActivity extends Fragment  {
    public static final String TAG = "TM_SecondActivity";
    public static final String TM_PREFS_NAME = "TM_Preferences";
    public static SharedPreferences preferences;
    public Spinner spinnerYear;
    public Button bYes;
    public Button bNo;
    public TextView tvYear;
    private View v;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.tm_layout2, null);
        preferences = this.getActivity().getSharedPreferences(TM_PREFS_NAME, 0);
        bYes = (Button) v.findViewById(R.id.buttonYes);
        bYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("tm_remember_last_time",true);
                editor.commit();
                updateView();
            }
        });
        bNo = (Button) v.findViewById(R.id.buttonNo);
        bNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("tm_remember_last_time",false);
                editor.commit();
                updateView();
            }
        });
        tvYear = (TextView) v.findViewById(R.id.textView5);
        spinnerYear = (Spinner) v.findViewById(R.id.spinner);
        initSpinner();
        updateView();

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(preferences.getBoolean("tm_remember_last_time",false)) {
                    SharedPreferences.Editor editor = preferences.edit();
                    if(!spinnerYear.getSelectedItem().toString().equals(" ")) {
                        editor.putString("tm_last_time_year", spinnerYear.getSelectedItem().toString());
                        Log.i(TAG, "tm_last_time_year=" + spinnerYear.getSelectedItem().toString());
                        editor.apply();
                    }

                    String text = getResources().getString(R.string.tm_1st_screen_promt_yes_and_year);
                    tvYear.setText(String.format(text, spinnerYear.getSelectedItem().toString()));
                    updateView();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return v;
    }

    public void initSpinner(){
        String lastYear = preferences.getString("tm_last_time_year"," ");
        Log.i(TAG, "tm_last_time_year2=" + preferences.getString("tm_last_time_year"," "));
        List<String> list = new ArrayList<String>();
        SharedPreferences up_preferences = this.getActivity().getSharedPreferences(upUtils.PREFS_NAME, 0);
        String birthdate = up_preferences.getString("birthdate", "-1");
        Log.i(TAG, "birthdate=" + birthdate);
        int yearOfBirth = -1;
        try {
            Log.i(TAG, "YEAR OF BIRTH=" + birthdate.substring(0, 4));
            yearOfBirth = Integer.parseInt(birthdate.substring(0, 4));
        } catch (Exception e) {
            Log.e(TAG, " ", e);
            return;
        }
        if (yearOfBirth == -1)
            return;
        Calendar c_aux = Calendar.getInstance();
        c_aux.setTimeInMillis(System.currentTimeMillis());
        int currentYear = c_aux.get(Calendar.YEAR);
        list.add(" ");
        int j=-1;
        for (int i = currentYear; i >= yearOfBirth; i--) {
            list.add(String.valueOf(i));
            if(String.valueOf(i).equals(lastYear))
                j=currentYear-i+1;
        }
        Log.i(TAG, "j=" + j);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(dataAdapter);
        if(j!=-1)
            spinnerYear.setSelection(j);
    }

    public void updateView(){
        TextView tv4 = (TextView) v.findViewById(R.id.textView4);
        TextView tv5 = (TextView) v.findViewById(R.id.textView5);
        if(preferences.getBoolean("tm_remember_last_time",false)) {
            tv4.setVisibility(View.VISIBLE);
            try {
                if (preferences.getString("tm_last_time_year"," ").toString().equals(" "))
                    tv5.setVisibility(View.INVISIBLE);
                else
                    tv5.setVisibility(View.VISIBLE);
            }catch (Exception e){
                Log.e(TAG," ",e);
                tv5.setVisibility(View.INVISIBLE);
            }
            spinnerYear.setVisibility(View.VISIBLE);
            bYes.setBackgroundColor(getResources().getColor(R.color.mountainNotAchieved_start));
            bNo.setBackgroundColor(getResources().getColor(R.color.checkbox_selected_background));
        }
        else{
            tv4.setVisibility(View.INVISIBLE);
            tv5.setVisibility(View.INVISIBLE);
            spinnerYear.setVisibility(View.INVISIBLE);
            bNo.setBackgroundColor(getResources().getColor(R.color.mountainNotAchieved_start));
            bYes.setBackgroundColor(getResources().getColor(R.color.checkbox_selected_background));
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
        super.onResume();
    }
}
