package onboarding.precious.comnet.aalto;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import aalto.comnet.thepreciousproject.R;

public class obSignUp extends FragmentActivity {

    public static final String TAG = "obSignUp";
    public static final String UP_PREFS_NAME = "UploaderPreferences";
    public static Context mContext;
    public static boolean isMaleSelected=true;
    private static Button maleButton;
    private static Button femaleButton;
    private static Integer groupID;

    static final int GET_TERMS_AND_CONDITIONS_ACCEPTANCE = 1;  // The request code
    static final int GET_GROUP_ID = 2;  // The request code

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ob_sign_up);
        mContext=this;
        groupID = -1;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.onboarding));
        }

        maleButton = (Button) findViewById(R.id.buttonMale);
        femaleButton = (Button) findViewById(R.id.buttonFemale);
    }

    public void signUp(View v){

        EditText etYearBirth = (EditText) this.findViewById(R.id.etYearBirth);
        String yearBirth = etYearBirth.getText().toString();
        int iYearBirth=-1;
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
        String sActivityClass="1";


        try{
            iYearBirth=Integer.parseInt(sHeight);
            if(Integer.parseInt(sWeight)<40 || Integer.parseInt(sWeight)>200
                    || Integer.parseInt(sHeight)<100 || Integer.parseInt(sHeight)>250
                    ) {
                Toast.makeText(this, getResources().getString(R.string.wrong_param), Toast.LENGTH_SHORT).show();
                return;
            }

            //Check for birth year
            if (Integer.parseInt(yearBirth)<1900 || Integer.parseInt(yearBirth)>2020){
                Toast.makeText(this, getResources().getString(R.string.wrong_param), Toast.LENGTH_LONG).show();
                return;
            }

        }catch (Exception e){
            Log.e(TAG,"",e);
            Toast.makeText(this, getResources().getString(R.string.wrong_param), Toast.LENGTH_LONG).show();
        }

        if(!sPassword.equals(sPassword2)){
            Toast.makeText(this,getResources().getString(R.string.pass_not_match),Toast.LENGTH_SHORT).show();
        }
        else if (
                sEmail.equals("") || sPassword.equals("") || sPassword2.equals("") || sNickname.equals("") ||
                sWeight.equals("") || sHeight.equals("") || sActivityClass.equals("") || etYearBirth.equals("")
                ){
            Toast.makeText(this,getResources().getString(R.string.empty_param),Toast.LENGTH_SHORT).show();
        }
        else {
            SharedPreferences preferences = this.getSharedPreferences(UP_PREFS_NAME, 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("email", sEmail);
            editor.putString("password", sPassword);
            editor.putString("weight", sWeight);
            editor.putString("height", sHeight);
            editor.putString("activityClass", sActivityClass);
            editor.putString("nickname", sNickname);
            Log.i(TAG,"Year of birth=_01/01/"+iYearBirth);
            editor.putString("birthdate", "01/01/"+iYearBirth);
            Log.i(TAG, "Storing groupID as:" + groupID);
            long reg_time =System.currentTimeMillis();
            editor.putLong("rd",(long)reg_time);
            Log.i(TAG,"REG_DATE: "+(reg_time/1000));
            if(isMaleSelected)
                editor.putString("gender", "male");
            else
                editor.putString("gender", "female");
            editor.commit();
//        Toast.makeText(this,"Signing in as: "+etEmail.getText().toString()+" with pass: "+etPassword.getText().toString(),Toast.LENGTH_SHORT).show();
            Log.i(TAG,sEmail+"_"+sPassword+"_"+sWeight+"_"+sHeight+"_"+sActivityClass+"_"+sNickname+"_"+"01/01/\"+iYearBirth"+"_"+isMaleSelected);


            String locale = this.getResources().getConfiguration().locale.getCountry();
            Log.i(TAG,"Country: "+locale);
            Intent i = new Intent(this,obTermsAndConditions.class);
            startActivityForResult(i,GET_TERMS_AND_CONDITIONS_ACCEPTANCE);

            if(locale.equals("GB") || locale.equals("US")) {
                Intent i2 = new Intent(this,obRequestGroupID.class);
                startActivityForResult(i2, GET_GROUP_ID);
            }
            else{
                    uploader.precious.comnet.aalto.upUtils.setContext(mContext);
                    uploader.precious.comnet.aalto.upUtils.register();
            }
        }
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


    /**
     *
     */
    @Override protected void onActivityResult (int requestCode,
                                               int resultCode, Intent data){
        if (requestCode==GET_TERMS_AND_CONDITIONS_ACCEPTANCE && resultCode==RESULT_OK) {
            if(data.getExtras().getBoolean("terms_accepted")) {
                uploader.precious.comnet.aalto.upUtils.setContext(mContext);
                uploader.precious.comnet.aalto.upUtils.register();
            }
        }
        else if (requestCode==GET_GROUP_ID && resultCode==RESULT_OK) {
            groupID=data.getExtras().getInt("group_ID");
            Log.i(TAG,"USER GROUP: "+data.getExtras().getInt("group_ID"));
            SharedPreferences preferences = this.getSharedPreferences(UP_PREFS_NAME, 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("group_ID", groupID);
            editor.commit();

            uploader.precious.comnet.aalto.upUtils.setContext(mContext);
            uploader.precious.comnet.aalto.upUtils.register();
            }
    }

    public static void onMaleSelected( View v){
        isMaleSelected = true;
        maleButton.setBackgroundColor(mContext.getResources().getColor(R.color.ob_selected_button_background));
        femaleButton.setBackgroundColor(mContext.getResources().getColor(R.color.ob_button_background));
    }

    public static void onFemaleSelected( View v){
        isMaleSelected = false;
        maleButton.setBackgroundColor(mContext.getResources().getColor(R.color.ob_button_background));
        femaleButton.setBackgroundColor(mContext.getResources().getColor(R.color.ob_selected_button_background));
    }
}