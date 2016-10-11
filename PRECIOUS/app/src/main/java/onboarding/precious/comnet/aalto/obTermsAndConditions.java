package onboarding.precious.comnet.aalto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import aalto.comnet.thepreciousproject.R;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;


public class obTermsAndConditions extends Activity {
    public static final String TAG = "obTermsAndConditions";
    /**
     *
     * @param savedInstanceState
     */
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ob_terms_and_conditions_layout);
    }

    public void onBackPressed(){
    }

    @Override
     protected void onPause() {
        //Store app usage
        try {
            sql_db.precious.comnet.aalto.DBHelper.getInstance().insertAppUsage(System.currentTimeMillis(), TAG, "onPause");
        } catch (Exception e) {
            Log.e(TAG, " ", e);

        }
        super.onPause();
    }
    /**
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        //Store app usage
        try{
            sql_db.precious.comnet.aalto.DBHelper.getInstance().insertAppUsage(System.currentTimeMillis(), TAG, "onResume");
        }catch (Exception e) {
            Log.e(TAG, " ", e);
        }
    }

    /**
     *
     * @param v
     */
    public void onYesSelected (View v){
        Intent intent = new Intent();
        intent.putExtra("terms_accepted",true);
        setResult(RESULT_OK, intent);
        finish();

    }
    /**
     *
     * @param v
     */
    public void onNoSelected (View v){
        Intent intent = new Intent();
        intent.putExtra("terms_accepted",false);
        setResult(RESULT_OK, intent);
        finish();
    }
}