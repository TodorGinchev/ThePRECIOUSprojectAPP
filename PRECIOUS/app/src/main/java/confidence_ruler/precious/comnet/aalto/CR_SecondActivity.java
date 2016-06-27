package confidence_ruler.precious.comnet.aalto;

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


public class CR_SecondActivity extends Fragment {
    public static final String PREFS_NAME = "CRsubappPreferences";
    private View v;
    MyReceiver r; //For more info, check OGThirdActivity.java file
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.cr_layout2, null);
        updateView();
        return v;
    }
    /**
     *
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            CR_SecondActivity.this.updateView();
        }
    }
    /**
     *
     */
    public void updateView(){
        TextView tv = (TextView) v.findViewById(R.id.cr_2n_screen_title);
        SharedPreferences preferences = confidence_ruler_activity.appConext.getSharedPreferences(PREFS_NAME, 0);
        int progress = preferences.getInt("CRseekbarProgress", -2)+1;
//        if(progress>4)
//            String text = getString(R.string.imporance_ruler_2nd_screen_title1);
        String title;
        if(progress==-1)
            title = getString(R.string.imporance_ruler_1st_screen_no_selection,progress,progress-3);
        else if(progress>8)
            title = getString(R.string.cr_2nd_screen_title4);
        else if (progress>6)
            title = getString(R.string.cr_2nd_screen_title3);
        else if (progress>1)
            title = getString(R.string.cr_2nd_screen_title2);
        else
            title = getString(R.string.cr_2nd_screen_title1);
        tv.setText(title);
    }
    /**
     *
     */
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(confidence_ruler_activity.appConext).unregisterReceiver(r);
    }
    /**
     *
     */
    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(confidence_ruler_activity.appConext).registerReceiver(r,
                new IntentFilter("CR2_REFRESH"));
    }
}
