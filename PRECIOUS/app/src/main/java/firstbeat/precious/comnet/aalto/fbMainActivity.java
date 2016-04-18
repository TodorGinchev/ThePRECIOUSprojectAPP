package firstbeat.precious.comnet.aalto;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import aalto.comnet.thepreciousproject.R;

public class fbMainActivity extends Activity{

    public static final String PREFS_NAME = "UploaderPreferences";
    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bg_main_activity);
        mContext=this;
    }

    public void getFBdata(View v){
        SharedPreferences preferences = this.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString("email", sEmail);
        editor.apply();
        uploader.precious.comnet.aalto.upUtils.setContext(mContext);
//        uploader.precious.comnet.aalto.upUtils.getJson("/data?key=USER_STEPS&from=0");
        uploader.precious.comnet.aalto.upUtils.getJson("/data/?key=BG2_REPORT&query=3");
//        uploader.precious.comnet.aalto.upUtils.getJson("/data?key=BG2_REPORT_IMAGE&query=3");


    }
}
