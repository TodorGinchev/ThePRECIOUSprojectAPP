package food_diary.precious.comnet.aalto;

import android.app.Activity;
import android.os.Bundle;

import aalto.comnet.thepreciousproject.R;


public class fdDatePicker extends Activity {
    /**
     *
     * @param savedInstanceState
     */
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fd_date_picker);
    }

    /**
     *
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

}