package onboarding.precious.comnet.aalto;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import aalto.comnet.thepreciousproject.R;

public class obSignIn extends AppCompatActivity {

    public static final String TAG = "obSignIn";
    public static final String UP_PREFS_NAME = "UploaderPreferences";
    public static Context mContext;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ob_sign_in);
        mContext=this;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.onboarding));
        }
    }

    public void signIn(View v){
        //My credentials
//        test@abc.com
//        password
        SharedPreferences preferences = this.getSharedPreferences(UP_PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        EditText etEmail = (EditText) this.findViewById(R.id.etEmail);
        EditText etPassword = (EditText) this.findViewById(R.id.etPassword);
        editor.putString("email", etEmail.getText().toString());
        editor.putString("password",etPassword.getText().toString());
        editor.apply();
//        Toast.makeText(this, "Signing in as: " + etEmail.getText().toString() + " with pass: " + etPassword.getText().toString(), Toast.LENGTH_SHORT).show();
        uploader.precious.comnet.aalto.upUtils.setContext(mContext);
        uploader.precious.comnet.aalto.upUtils.login();
    }

    @Override
    protected void onPause() {
        //Store app usage
        try {
            sql_db.precious.comnet.aalto.DBHelper.getInstance(this).insertAppUsage(System.currentTimeMillis(), TAG, "onPause");
        } catch (Exception e) {
            Log.e(TAG, " ", e);

        }
        super.onPause();
    }


    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences preferences = this.getSharedPreferences(UP_PREFS_NAME, 0);
        if((preferences.getBoolean("isUserLoggedIn",false)) )
            finish();
        //Store app usage
        try{
            sql_db.precious.comnet.aalto.DBHelper.getInstance(this).insertAppUsage(System.currentTimeMillis(), TAG, "onResume");
        }catch (Exception e){Log.e(TAG," ",e);}
    }


    public void closeActivity(View v){
        finish();
    }
}
