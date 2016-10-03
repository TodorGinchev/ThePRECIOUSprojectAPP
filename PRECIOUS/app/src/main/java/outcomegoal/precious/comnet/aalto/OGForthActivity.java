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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import aalto.comnet.thepreciousproject.R;

public class OGForthActivity extends Fragment {
    public static final String TAG = "OGForthActivity";
    public static final String PREFS_NAME = "OGsubappPreferences";
    public static final String UP_PREFS_NAME = "UploaderPreferences";
    public static SharedPreferences preferences;
    public static int groupID;
    private static Context mContext;
    private static View v;
    MyReceiver r;
    private static RadioGroup rg;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.og_layout4, null);
        preferences = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        mContext=this.getContext();
        rg = (RadioGroup) v.findViewById(R.id.radioGroupBehaviour);
        rg.clearCheck();

        SharedPreferences preferences_up = this.getActivity().getSharedPreferences(UP_PREFS_NAME, 0);
        groupID = preferences_up.getInt("group_ID", -1);


        if(preferences.getInt("PrefferedBehaviour",-1)!=-1)
            rg.check(preferences.getInt("PrefferedBehaviour",-1));


        if(groupID/1000==9){
            rg.check(R.id.radioOption1);
            updateView();
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(groupID/1000==9){
                    Toast.makeText(mContext,mContext.getResources().getString(R.string.og_4th_screen_not_allowed),Toast.LENGTH_SHORT).show();
                    group.check(R.id.radioOption1);

                }
                else {
                    Log.i("OGForthActivity", "CHECKED_" + checkedId);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("PrefferedBehaviour", checkedId);
                    editor.apply();
                }

            }
        });

        return v;
    }
    /**
     *
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            OGForthActivity.this.updateView();
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
                new IntentFilter("OG4_REFRESH"));
    }
    /*
     *
     */
    public void updateView() {
        TextView tv = (TextView) v.findViewById(R.id.og_4th_screen_title);

        Log.i(TAG, "UPDATE VIEW CALLED:_" + preferences.getInt("preferredBox1", -1));
        if (preferences.getInt("preferredBox1", -1) == -1) {
            tv.setText(R.string.outcomegoal_5th_screen_no_selection);
            rg.setVisibility(View.INVISIBLE);
        } else {
//            tv.setText(R.string.outcomegoal_5th_screen_feedback);
            String feedbackString = getResources().getString(R.string.outcomegoal_4th_screen_title);
            tv.setText(String.format(feedbackString,OGThirdActivity.getPrefferedBoxString(preferences.getInt("preferredBox1", -1))));
            rg.setVisibility(View.VISIBLE);
//        }
        }
    }
    public static String getPrefferedBehaviour(){
        RadioButton rb = (RadioButton) v.findViewById(rg.getCheckedRadioButtonId());
        if (rb!=null)
            return  rb.getText().toString();
        else
            return "-1";
    }
}