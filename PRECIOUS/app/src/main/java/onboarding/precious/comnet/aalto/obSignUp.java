package onboarding.precious.comnet.aalto;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import aalto.comnet.thepreciousproject.R;

public class obSignUp extends FragmentActivity {

    public static final String TAG = "obSignUp";
    public static final String UP_PREFS_NAME = "UploaderPreferences";
    public static Context mContext;
    public static TextView etBirthDate;
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
        etBirthDate = (TextView) findViewById(R.id.etBirthDate);
        groupID = -1;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.onboarding));
        }

        maleButton = (Button) findViewById(R.id.buttonMale);
        femaleButton = (Button) findViewById(R.id.buttonFemale);
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
        String sActivityClass="1";
        String sBirthDate = etBirthDate.getText().toString();


        Log.i(TAG,sBirthDate.substring(sBirthDate.length()-4,sBirthDate.length())+"");

        try{
            if(Integer.parseInt(sWeight)<40 || Integer.parseInt(sWeight)>200
                    || Integer.parseInt(sHeight)<100 || Integer.parseInt(sHeight)>250
                    || Integer.parseInt(sBirthDate.substring(sBirthDate.length()-4,sBirthDate.length()))>2000
                    ) {
                Toast.makeText(this, getResources().getString(R.string.wrong_param), Toast.LENGTH_SHORT).show();
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
                sWeight.equals("") || sHeight.equals("") || sActivityClass.equals("") || sBirthDate.equals("")
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
            editor.putString("birthdate", sBirthDate);
            editor.putInt("groupID", groupID);
            if(isMaleSelected)
                editor.putString("gender", "male");
            else
                editor.putString("gender", "female");
            editor.apply();
//        Toast.makeText(this,"Signing in as: "+etEmail.getText().toString()+" with pass: "+etPassword.getText().toString(),Toast.LENGTH_SHORT).show();
            Log.i(TAG,sEmail+"_"+sPassword+"_"+sWeight+"_"+sHeight+"_"+sActivityClass+"_"+sNickname+"_"+sBirthDate+"_"+isMaleSelected);


            String locale = this.getResources().getConfiguration().locale.getCountry();
            Log.i(TAG,"Country: "+locale);
            if(locale.equals("ES")||locale.equals("CA")){
                Intent i = new Intent(this,obTermsAndConditions.class);
                startActivityForResult(i,GET_TERMS_AND_CONDITIONS_ACCEPTANCE);
            }
            else if(locale.equals("GB")) {
                Intent i = new Intent(this,obRequestGroupID.class);
                startActivityForResult(i, GET_GROUP_ID);
            }
            else{
                    uploader.precious.comnet.aalto.upUtils.setContext(mContext);
                    uploader.precious.comnet.aalto.upUtils.register();
            }

        }
    }

    /**
     *
     * Configure datePicker
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            setTvDate(year, month + 1, day);
        }
    }
    public void openDatePicker(View v){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    public static void setTvDate(int year, int month, int dayOfMonth){
        etBirthDate.setText(" " + month + "/" + dayOfMonth + "/" + year);
    }
    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences preferences = this.getSharedPreferences(UP_PREFS_NAME, 0);
        if((preferences.getBoolean("isUserLoggedIn",false)) )
            finish();
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
            Log.i(TAG,"USER GROUP: "+data.getExtras().getInt("group_ID"));
            groupID=data.getExtras().getInt("group_ID");
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