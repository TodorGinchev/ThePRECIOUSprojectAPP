package importance_ruler.precious.comnet.aalto;

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
import android.widget.Button;
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;
import outcomegoal.precious.comnet.aalto.outcomegoal_activity;


public class IRForthActivity extends Fragment {
    public static final String OG_PREFS_NAME = "OGsubappPreferences";
    public static final String IR_PREFS_NAME = "IRsubappPreferences";
    private View v;
    MyReceiver r; //For more info, check OGThirdActivity.java file
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.ir_layout4, null);
        updateView();
        return v;
    }
    /**
     *
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            IRForthActivity.this.updateView();
        }
    }
    /**
     *
     */
    public void updateView(){
        TextView tv = (TextView) v.findViewById(R.id.ir_4th_screen_text);
        SharedPreferences ir_preferences = ImportanceRulerActivity.appConext.getSharedPreferences(IR_PREFS_NAME, 0);
        SharedPreferences og_preferences = ImportanceRulerActivity.appConext.getSharedPreferences(OG_PREFS_NAME, 0);
        int progress = ir_preferences.getInt("IRseekbarProgress", -2)+1;
//        if(progress>4)
//            String text = getString(R.string.imporance_ruler_2nd_screen_title1);
        String title;
        Button button = (Button) v.findViewById(R.id.button);
        if(progress==-1) {
            title = getString(R.string.imporance_ruler_1st_screen_no_selection, progress, progress - 3);
            button.setVisibility(View.GONE);
        }
        else if (og_preferences.getInt("preferredBoxIR1",-1)==-1) {
            title = getString(R.string.imporance_ruler_1st_screen_no_selection, progress, progress - 3);
            button.setVisibility(View.GONE);
        }
        else {
            title = String.format(getString(R.string.imporance_ruler_4th_screen_feedback), IRThirdActivity.getPrefferedBoxString(og_preferences.getInt("preferredBoxIR1", -1)));
            button.setVisibility(View.VISIBLE);
        }
        tv.setText(title);
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
        LocalBroadcastManager.getInstance(ImportanceRulerActivity.appConext).registerReceiver(r,
                new IntentFilter("IR4_REFRESH"));
    }
}

