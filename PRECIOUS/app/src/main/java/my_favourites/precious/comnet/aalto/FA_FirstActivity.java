package my_favourites.precious.comnet.aalto;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import aalto.comnet.thepreciousproject.R;


public class FA_FirstActivity extends Fragment  {
    public static final String TAG = "FA_FirstActivity";
    public static final String FA_PREFS_NAME = "TM_Preferences";
    public static SharedPreferences preferences;
    private View v;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fa_layout1, null);
        preferences = this.getActivity().getSharedPreferences(FA_PREFS_NAME, 0);

        updateView();

        return v;
    }

    public void updateView(){
        String[] values = getResources().getStringArray(R.array.pa_names);
        List<String> values_array = Arrays.asList(values);
        Collections.sort(values_array, String.CASE_INSENSITIVE_ORDER);
        values = values_array.toArray(new String[values_array.size()]);

//        ChooseActivityAdapter adapter = new ChooseActivityAdapter(this, values);
//        setListAdapter(adapter);
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
