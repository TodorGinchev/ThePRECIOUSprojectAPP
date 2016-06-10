package diet_challenges.precious.comnet.aalto.fi;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;

public class dc_AddChallenge extends FragmentActivity {

    public static final String TAG = "dc_AddChallenge";
    private static final String DC_PREF_NAME ="dc_prefs";
    private static SharedPreferences preferences;
    private static Context mContext;

    private static TextView tvFruits;
    private static ImageButton ibFruits;
    private static ImageButton ibAddRemoveFruit;
    private static TextView tvWater;
    private static ImageButton ibWater;
    private static ImageButton ibAddRemoveWater;
    private static TextView tvFries;
    private static ImageButton ibFries;
    private static ImageButton ibAddRemoveFries;
    private static TextView tvCoke;
    private static ImageButton ibCoke;
    private static ImageButton ibAddRemoveCoke;
    private static TextView tvBeer;
    private static ImageButton ibBeer;
    private static ImageButton ibAddRemoveBeer;
    private static TextView tvFries2;
    private static ImageButton ibFries2;
    private static ImageButton ibAddRemoveFries2;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dc_add_challenge);

        mContext=this;
        preferences = this.getSharedPreferences(DC_PREF_NAME, 0);

        tvFruits = (TextView) findViewById(R.id.textViewFruitsDescription);
        ibFruits = (ImageButton) findViewById(R.id.imageButtonArrowFruits);
        ibAddRemoveFruit = (ImageButton) findViewById(R.id.imageButtonArrowFruits);

        tvWater = (TextView) findViewById(R.id.textViewWaterDescription);
        ibWater = (ImageButton) findViewById(R.id.imageButtonArrowWater);
        ibAddRemoveWater = (ImageButton) findViewById(R.id.ibAddRemoveWater);

        tvFries = (TextView) findViewById(R.id.textViewFriesDescription);
        ibFries = (ImageButton) findViewById(R.id.imageButtonArrowFries);
        ibAddRemoveFries = (ImageButton) findViewById(R.id.ibAddRemoveFries);

        tvCoke = (TextView) findViewById(R.id.textViewCokeDescription);
        ibCoke = (ImageButton) findViewById(R.id.imageButtonArrowCoke);
        ibAddRemoveCoke = (ImageButton) findViewById(R.id.ibAddRemoveCoke);

        tvBeer = (TextView) findViewById(R.id.textViewBeerDescription);
        ibBeer = (ImageButton) findViewById(R.id.imageButtonArrowBeer);
        ibAddRemoveBeer = (ImageButton) findViewById(R.id.ibAddRemoveBeer);

        tvFries2 = (TextView) findViewById(R.id.textViewFries2Description);
        ibFries2 = (ImageButton) findViewById(R.id.imageButtonArrowFries2);
        ibAddRemoveFries2 = (ImageButton) findViewById(R.id.ibAddRemoveFries2);

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

    public static void showHideWaterDescription (View v){
        if(tvWater.getVisibility()==View.GONE){
            tvWater.setVisibility(View.VISIBLE);
            ibWater.setImageResource(R.drawable.arrow_down_24);
        }
        else{
            tvWater.setVisibility(View.GONE);
            ibWater.setImageResource(R.drawable.arrow_right_24);
        }
    }

    public static void showHideFriesDescription (View v){
        if(tvFries.getVisibility()==View.GONE){
            tvFries.setVisibility(View.VISIBLE);
            ibFries.setImageResource(R.drawable.arrow_down_24);
        }
        else{
            tvFries.setVisibility(View.GONE);
            ibFries.setImageResource(R.drawable.arrow_right_24);
        }
    }

    public static void showHideCokeDescription (View v){
        if(tvCoke.getVisibility()==View.GONE){
            tvCoke.setVisibility(View.VISIBLE);
            ibCoke.setImageResource(R.drawable.arrow_down_24);
        }
        else{
            tvCoke.setVisibility(View.GONE);
            ibCoke.setImageResource(R.drawable.arrow_right_24);
        }
    }

    public static void showHideBeerDescription (View v){
        if(tvBeer.getVisibility()==View.GONE){
            tvBeer.setVisibility(View.VISIBLE);
            ibBeer.setImageResource(R.drawable.arrow_down_24);
        }
        else{
            tvBeer.setVisibility(View.GONE);
            ibBeer.setImageResource(R.drawable.arrow_right_24);
        }
    }

    public static void showHideFries2Description (View v){
        if(tvFries2.getVisibility()==View.GONE){
            tvFries2.setVisibility(View.VISIBLE);
            ibFries2.setImageResource(R.drawable.arrow_down_24);
        }
        else{
            tvFries2.setVisibility(View.GONE);
            ibFries2.setImageResource(R.drawable.arrow_right_24);
        }
    }


    public static void toggleFries2Challenge (View v){
//        android:src="@android:drawable/ic_delete"
        SharedPreferences.Editor editor = preferences.edit();
        Log.i("TAG","toggleFries2Challenge");
        if(preferences.getBoolean("Fries2Enabled",false)){
            ibAddRemoveFries2.setBackground(mContext.getResources().getDrawable(R.drawable.ic_delete));
            editor.putBoolean("Fries2Enabled", true);
        }
        else{
            ibAddRemoveFries2.setBackground(mContext.getResources().getDrawable(R.drawable.ic_input_add));
            editor.putBoolean("Fries2Enabled",false);
        }
        editor.commit();

    }

}
