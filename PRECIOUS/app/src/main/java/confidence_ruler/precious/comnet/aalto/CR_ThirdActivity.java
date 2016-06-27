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
import android.widget.CheckBox;
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;


public class CR_ThirdActivity extends Fragment  {
    public static final String TAG = "FA_FirstActivity";
    public static final String CR_PREFS_NAME = "CRsubappPreferences";
    public static SharedPreferences preferences;
    private View v;
    public TextView tv;
    MyReceiver r;
    public CheckBox cb1,cb2,cb3,cb4;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.cr_layout3, null);
        preferences = this.getActivity().getSharedPreferences(CR_PREFS_NAME, 0);
        tv = (TextView) v.findViewById(R.id.cr_3rd_screen_title);
        cb1 = (CheckBox) v.findViewById(R.id.checkBox20);
        cb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckBox1Clicked();
            }
        });
        cb2 = (CheckBox) v.findViewById(R.id.checkBox21);
        cb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckBox2Clicked();
            }
        });
        cb3 = (CheckBox) v.findViewById(R.id.checkBox22);
        cb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckBox3Clicked();
            }
        });
        cb4 = (CheckBox) v.findViewById(R.id.checkBox23);
        cb4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckBox4Clicked();
            }
        });
        updateView();
        return v;
    }

    public void updateView(){
        int progress = preferences.getInt("CRseekbarProgress", -2)+1;
        if(progress<9)
            tv.setText(String.format(getString(R.string.cr_3rd_screen_title1),progress,progress+2));
        else
            tv.setText(R.string.cr_3rd_screen_title2);

        cb1.setChecked(preferences.getBoolean("cr_behaviour1_enabled",false));
        cb2.setChecked(preferences.getBoolean("cr_behaviour2_enabled",false));
        cb3.setChecked(preferences.getBoolean("cr_behaviour3_enabled",false));
        cb4.setChecked(preferences.getBoolean("cr_behaviour4_enabled",false));
    }
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
                new IntentFilter("CR_3_REFRESH"));
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            CR_ThirdActivity.this.updateView();
        }
    }

    public void onCheckBox1Clicked (){
        SharedPreferences.Editor editor = preferences.edit();
        if(preferences.getBoolean("cr_behaviour1_enabled",false))
            editor.putBoolean("cr_behaviour1_enabled",false);
        else
            editor.putBoolean("cr_behaviour1_enabled",true);
        editor.apply();
    }

    public void onCheckBox2Clicked (){
        SharedPreferences.Editor editor = preferences.edit();
        if(preferences.getBoolean("cr_behaviour2_enabled",false))
            editor.putBoolean("cr_behaviour2_enabled",false);
        else
            editor.putBoolean("cr_behaviour2_enabled",true);
        editor.apply();
    }

    public void onCheckBox3Clicked (){
        SharedPreferences.Editor editor = preferences.edit();
        if(preferences.getBoolean("cr_behaviour3_enabled",false))
            editor.putBoolean("cr_behaviour3_enabled",false);
        else
            editor.putBoolean("cr_behaviour3_enabled",true);
        editor.apply();
    }

    public void onCheckBox4Clicked (){
        SharedPreferences.Editor editor = preferences.edit();
        if(preferences.getBoolean("cr_behaviour4_enabled",false))
            editor.putBoolean("cr_behaviour4_enabled",false);
        else
            editor.putBoolean("cr_behaviour4_enabled",true);
        editor.apply();
    }
}
