package activity_tracker.precious.comnet.aalto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import ui.precious.comnet.aalto.precious.R;


public class AddActivity extends Activity {
    /**
     *
     * @param savedInstanceState
     */
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activity_layout);

        //Get screen size and calculate object sizes
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screen_width = size.x;
        int screen_height = size.y;

        //Set Main layout size
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl_add_activity);
        rl.getLayoutParams().height = (int)(0.9*screen_height);  // change height of the layout
        rl.getLayoutParams().width = (int)(0.9*screen_width);  // change height of the layout


        ImageButton tvActivity = (ImageButton) findViewById(R.id.selected_pa_iv);
        tvActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setActivityType();
            }
        });
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
    public void closeView (View v){
        Intent i = new Intent(this, PromptSaveInfo.class);
        startActivityForResult(i, 1001);
    }

    /**
     *
     */
    public void setActivityType(){
        Intent i = new Intent(this, ChooseActivity.class);
        startActivityForResult(i, 1001);
        ImageButton tvActivity = (ImageButton) findViewById(R.id.selected_pa_iv);
    }
    /**
     *
     */
    @Override protected void onActivityResult (int requestCode,
                                               int resultCode, Intent data){
        if (requestCode==1001 && resultCode==RESULT_OK) {
            ImageButton tvActivity = (ImageButton) findViewById(R.id.selected_pa_iv);
            String str = data.getExtras().getString("activity");
            if (str.startsWith("Walking"))
                tvActivity.setImageResource(R.drawable.walking);
            else if (str.startsWith("Running"))
                tvActivity.setImageResource(R.drawable.running);
            else if (str.startsWith("Riding a bicycle"))
                tvActivity.setImageResource(R.drawable.bicycle);
        }
    }
}
