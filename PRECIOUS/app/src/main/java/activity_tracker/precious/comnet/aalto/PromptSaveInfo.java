package activity_tracker.precious.comnet.aalto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import aalto.comnet.thepreciousproject.R;


public class PromptSaveInfo extends Activity {
    /**
     *
     * @param savedInstanceState
     */
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prompt_save_info_layout);
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
        intent.putExtra("saveInfo",true);
        setResult(RESULT_OK, intent);
        finish();
    }
    /**
     *
     * @param v
     */
    public void onNoSelected (View v){
        Intent intent = new Intent();
        intent.putExtra("saveInfo",false);
        setResult(RESULT_OK, intent);
        finish();
    }
    /**
     *
     * @param v
     */
    public void onCancelSelected (View v){
        finish();
    }
}