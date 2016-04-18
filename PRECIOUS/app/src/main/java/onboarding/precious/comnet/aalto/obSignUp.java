package onboarding.precious.comnet.aalto;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import aalto.comnet.thepreciousproject.R;

public class obSignUp extends AppCompatActivity {

    public static final String PREFS_NAME = "UploaderPreferences";
    public static Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ob_sign_up);
        mContext=this;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.onboarding));
        }
    }

    public void signUp(View v){
        SharedPreferences preferences = this.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", "aaa@bbb.com");
        editor.putString("password", "pass");
        editor.putString("weight", "100");
        editor.putString("height", "100");
        editor.putString("activityClass", "5");
        editor.putString("nickname", "1090");
        editor.putString("birthdate", "20July2019");
        editor.putString("gender", "male");
        editor.apply();
//        Toast.makeText(this,"Signing in as: "+etEmail.getText().toString()+" with pass: "+etPassword.getText().toString(),Toast.LENGTH_SHORT).show();
        uploader.precious.comnet.aalto.upUtils.setContext(mContext);
        uploader.precious.comnet.aalto.upUtils.register();
    }
}