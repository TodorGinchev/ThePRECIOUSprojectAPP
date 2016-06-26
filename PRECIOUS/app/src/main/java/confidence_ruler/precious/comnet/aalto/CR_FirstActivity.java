package confidence_ruler.precious.comnet.aalto;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;


public class CR_FirstActivity extends Fragment  {
    public static final String TAG = "FA_FirstActivity";
    public static final String FA_PREFS_NAME = "CRsubappPreferences";
    private View v;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.cr_layout1, null);
        SeekBar sb = (SeekBar) v.findViewById(R.id.seekBar);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences preferences = confidence_ruler_activity.appConext.getSharedPreferences(FA_PREFS_NAME, 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("CRseekbarProgress", progress);
                editor.apply();
                TextView tv1 = (TextView) v.findViewById(R.id.textView11);
                tv1.setText(Integer.toString(progress + 1));
            }
        });
        SharedPreferences preferences = confidence_ruler_activity.appConext.getSharedPreferences(FA_PREFS_NAME, 0);
        sb.setProgress(preferences.getInt("CRseekbarProgress",1));
        TextView tv = (TextView) v.findViewById(R.id.textView11);
        tv.setText(Integer.toString(preferences.getInt("CRseekbarProgress", 5) + 1));
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
