package onboarding.precious.comnet.aalto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import aalto.comnet.thepreciousproject.R;


public class obTermsAndConditions extends Activity {
    /**
     *
     * @param savedInstanceState
     */
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ob_terms_and_conditions_layout);
    }

    /**
     *
     */
    @Override
    protected void onPause() {
        super.onPause();
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