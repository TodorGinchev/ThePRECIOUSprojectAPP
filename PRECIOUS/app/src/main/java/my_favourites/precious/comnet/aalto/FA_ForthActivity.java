package my_favourites.precious.comnet.aalto;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import aalto.comnet.thepreciousproject.R;


public class FA_ForthActivity extends Fragment  {
    public static final String TAG = "FA_FirstActivity";
    public static final String FA_PREFS_NAME = "TM_Preferences";
    public static SharedPreferences preferences;
    private View v;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fa_layout1, null);
        preferences = this.getActivity().getSharedPreferences(FA_PREFS_NAME, 0);

        return v;
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