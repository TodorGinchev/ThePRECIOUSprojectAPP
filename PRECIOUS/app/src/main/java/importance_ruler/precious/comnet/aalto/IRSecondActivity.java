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
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;
import outcomegoal.precious.comnet.aalto.outcomegoal_activity;



public class IRSecondActivity extends Fragment {
    public static final String PREFS_NAME = "IRsubappPreferences";
    private View v;
    MyReceiver r; //For more info, check OGThirdActivity.java file
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.ir_layout2, null);
        updateView();
        return v;
    }
    /**
     *
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            IRSecondActivity.this.updateView();
        }
    }
    /**
     *
     */
    public void updateView(){
        TextView tv = (TextView) v.findViewById(R.id.ir_2n_screen_title);
        SharedPreferences preferences = ImportanceRulerActivity.appConext.getSharedPreferences(PREFS_NAME, 0);
        int progress = preferences.getInt("IRseekbarProgress", -1)+1;
//        if(progress>4)
//            String text = getString(R.string.imporance_ruler_2nd_screen_title1);
        String title;
        if(progress>4)
            title = getString(R.string.imporance_ruler_2nd_screen_title1,progress,progress-3);
        else if (progress==1)
            title = getString(R.string.imporance_ruler_2nd_screen_title2);
        else
            title = getString(R.string.imporance_ruler_2nd_screen_title1,progress,1);
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
                new IntentFilter("IR2_REFRESH"));
    }
}
