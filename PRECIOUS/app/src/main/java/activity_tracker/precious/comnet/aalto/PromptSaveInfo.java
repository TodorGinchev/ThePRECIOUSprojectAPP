package activity_tracker.precious.comnet.aalto;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import ui.precious.comnet.aalto.precious.R;

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

    public void saveInfo (View v){
    }
}