package outcomegoal.precious.comnet.aalto;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;

public class OGFifthActivity extends Fragment {
    public static final String TAG = "OGFifthActivity";
    public static final String PREFS_NAME = "OGsubappPreferences";
    private View v;
    MyReceiver r;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.og_layout5, null);
        return v;


    }

    /**
     *
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            OGFifthActivity.this.updateView();
        }
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
        LocalBroadcastManager.getInstance(outcomegoal_activity.appConext).registerReceiver(r,
                new IntentFilter("OG5_REFRESH"));
    }


    public void updateView() {
        TextView tv = (TextView) v.findViewById(R.id.og_5th_screen_text);
        Button button = (Button) v.findViewById(R.id.button);
        SharedPreferences preferences = this.getActivity().getSharedPreferences(PREFS_NAME, 0);

        Log.i(TAG, "UPDATE VIEW CALLED:_"+preferences.getInt("preferredBox1", -1));
        if (preferences.getInt("preferredBox1", -1) == -1
                || preferences.getInt("PrefferedBehaviour",-1)== -1
                || OGForthActivity.getPrefferedBehaviour().equals("-1")) {
            tv.setText(R.string.outcomegoal_5th_screen_no_selection);
            button.setVisibility(View.INVISIBLE);
        } else {
//            tv.setText(R.string.outcomegoal_5th_screen_feedback);
            String feedbackString = getResources().getString(R.string.outcomegoal_5th_screen_feedback);
            tv.setText(String.format(feedbackString,OGForthActivity.getPrefferedBehaviour(),OGThirdActivity.getPrefferedBoxString(preferences.getInt("preferredBox1", -1))));
            if(outcomegoal_activity.showcaseView!=null)
                outcomegoal_activity.showcaseView.show();
            button.setVisibility(View.VISIBLE);
//        }
        }
    }


}