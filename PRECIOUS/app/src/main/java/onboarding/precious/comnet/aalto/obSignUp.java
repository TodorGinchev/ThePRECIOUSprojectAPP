package onboarding.precious.comnet.aalto;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
        EditText etEmail = (EditText) this.findViewById(R.id.etEmail);
        String sEmail = etEmail.getText().toString();
        EditText etPassword = (EditText) this.findViewById(R.id.etPassword);
        String sPassword = etPassword.getText().toString();
        EditText etPassword2 = (EditText) this.findViewById(R.id.etPassword2);
        String sPassword2 = etPassword2.getText().toString();
        EditText etNickname = (EditText) this.findViewById(R.id.etNickname);
        String sNickname = etNickname.getText().toString();
        EditText etWeight = (EditText) this.findViewById(R.id.etWeight);
        String sWeight = etWeight.getText().toString();
        EditText etHeight = (EditText) this.findViewById(R.id.etHeight);
        String sHeight = etHeight.getText().toString();
//        EditText etActivityClass = (EditText) this.findViewById(R.id.etActivityClass);
//        String sActivityClass = etActivityClass.getText().toString();
        String sActivityClass="-1";
        EditText etBirthDate = (EditText) this.findViewById(R.id.etBirthDate);
        String sBirthDate = etBirthDate.getText().toString();
        EditText etGender = (EditText) this.findViewById(R.id.etGender);
        String sGender = etGender.getText().toString();


        if(sGender.equals("")){
            Toast.makeText(this,getResources().getString(R.string.gender_empty),Toast.LENGTH_SHORT).show();
        }
        else if(!sPassword.equals(sPassword2)){
            Toast.makeText(this,getResources().getString(R.string.pass_not_match),Toast.LENGTH_SHORT).show();
        }
        else if ( !(
                sGender.equals(getResources().getString(R.string.male))
                || sGender.equals(getResources().getString(R.string.female))
                 ) ){
            Toast.makeText(this,getResources().getString(R.string.gender_not_match),Toast.LENGTH_SHORT).show();
        }
        else if (
                sEmail.equals("") || sPassword.equals("") || sPassword2.equals("") || sNickname.equals("") ||
                sWeight.equals("") || sHeight.equals("") || sActivityClass.equals("") || sBirthDate.equals("") ||
                sGender.equals("")
                ){
            Toast.makeText(this,getResources().getString(R.string.empty_param),Toast.LENGTH_SHORT).show();
        }
        else {
            SharedPreferences preferences = this.getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("email", sEmail);
            editor.putString("password", sPassword);
            editor.putString("weight", sWeight);
            editor.putString("height", sHeight);
            editor.putString("activityClass", sActivityClass);
            editor.putString("nickname", sNickname);
            editor.putString("birthdate", sBirthDate);
            if(sGender.equals(getResources().getString(R.string.male)))
                editor.putString("gender", "male");
            else
                editor.putString("gender", "female");
            editor.apply();
//        Toast.makeText(this,"Signing in as: "+etEmail.getText().toString()+" with pass: "+etPassword.getText().toString(),Toast.LENGTH_SHORT).show();
            uploader.precious.comnet.aalto.upUtils.setContext(mContext);
            uploader.precious.comnet.aalto.upUtils.register();
        }
    }
}