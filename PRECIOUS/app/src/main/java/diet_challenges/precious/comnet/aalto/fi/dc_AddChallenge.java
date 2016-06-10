package diet_challenges.precious.comnet.aalto.fi;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;

public class dc_AddChallenge extends FragmentActivity {

    private static TextView tvFruits;
    private static ImageButton ibFruits;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dc_add_challenge);

        tvFruits = (TextView) findViewById(R.id.textViewFruitsDescription);
        ibFruits = (ImageButton) findViewById(R.id.imageButtonArrowFruits);
    }


    public static void showHideFruitsDescription (View v){
        if(tvFruits.getVisibility()==View.GONE){
            tvFruits.setVisibility(View.VISIBLE);
            ibFruits.setImageResource(R.drawable.arrow_down_24);
        }
        else{
            tvFruits.setVisibility(View.GONE);
            ibFruits.setImageResource(R.drawable.arrow_right_24);
        }

    }
}
