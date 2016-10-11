package onboarding.precious.comnet.aalto;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import aalto.comnet.thepreciousproject.R;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;

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
        EditText etEmail = (EditText) this.findViewById(R.id.etEmail);
        String sEmail = etEmail.getText().toString();
        EditText etPassword = (EditText) this.findViewById(R.id.etPassword);
        String sPassword = etPassword.getText().toString();
        EditText etGroupID = (EditText) this.findViewById(R.id.etGroupID);
        String sGroupID = etGroupID.getText().toString();
        if ( sEmail.equals("") || sPassword.equals("") || sGroupID.equals("") ) {
            Toast.makeText(this, getResources().getString(R.string.empty_param), Toast.LENGTH_SHORT).show();
        }
        else if(!sGroupID.equals("130") && !sGroupID.equals("517") && !sGroupID.equals("678") && !sGroupID.equals("392") && !sGroupID.equals("387") && !sGroupID.equals("599") && !sGroupID.equals("827") && !sGroupID.equals("135") && !sGroupID.equals("333") && Integer.parseInt(sGroupID)/1000!=9) {
            Toast.makeText(this,getResources().getString(R.string.wrong_group_id),Toast.LENGTH_SHORT).show();
        }
        else {
            SharedPreferences preferences = this.getSharedPreferences(UP_PREFS_NAME, 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("email", etEmail.getText().toString());
            editor.putString("password", etPassword.getText().toString());
            editor.putInt("group_ID", Integer.parseInt(sGroupID));
            editor.apply();
//        Toast.makeText(this, "Signing in as: " + etEmail.getText().toString() + " with pass: " + etPassword.getText().toString(), Toast.LENGTH_SHORT).show();
            uploader.precious.comnet.aalto.upUtils.login(this);
        }
    }

    @Override
    protected void onPause() {
        //Store app usage
        try {
            sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).insertAppUsage(System.currentTimeMillis(), TAG, "onPause");
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
            sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).insertAppUsage(System.currentTimeMillis(), TAG, "onResume");
        }catch (Exception e){Log.e(TAG," ",e);}
    }


    public void closeActivity(View v){
        finish();
    }
}
