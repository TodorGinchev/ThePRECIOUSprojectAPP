package my_favourites.precious.comnet.aalto;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;


public class FA_FirstActivity extends Fragment  {
    public static final String TAG = "FA_FirstActivity";
    public static final String OG_PREFS_NAME = "OGsubappPreferences";
    public static SharedPreferences og_preferences;
    public TextView tv;
    private View v;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fa_layout1, null);
        og_preferences = this.getActivity().getSharedPreferences(OG_PREFS_NAME, 0);
        tv = (TextView) v.findViewById(R.id.textView1);
        updateView();

        return v;
    }

    @SuppressLint("StringFormatMatches")
    public void updateView(){
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
            tv.setText(getResources().getString(R.string.imporance_ruler_2nd_screen_no_selection));
        else{
            String stringIDname = ("outcomegoal_goal").concat(Integer.toString(preferred_OG_BOX));
            int StringID = getResources().getIdentifier(stringIDname, "string", my_favourites_activity.appConext.getPackageName());
            String feedbackString = getResources().getString(R.string.fa_1st_screen_intro1);
            String outcome_goal=getResources().getString(StringID);
            tv.setText(String.format(feedbackString, outcome_goal));
        }
    }
}
