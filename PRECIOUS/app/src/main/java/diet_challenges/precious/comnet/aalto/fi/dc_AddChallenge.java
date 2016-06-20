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
        ibAddRemoveFruit = (ImageButton) findViewById(R.id.ibAddRemoveFruit);

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

        initDrawables();

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

    /**
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        //Store app usage
        try{
            sql_db.precious.comnet.aalto.DBHelper.getInstance(this).insertAppUsage(System.currentTimeMillis(), TAG, "onResume");
        }catch (Exception e) {
            Log.e(TAG, " ", e);
        }
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

    //ADD/REMOVE FRUITS
    public static void toggleFruitsChallenge (View v){
        SharedPreferences.Editor editor = preferences.edit();
        Log.i(TAG, "toggleFruitsChallenge");
        if(!preferences.getBoolean("isFruitsDCactive",false)){
            editor.putBoolean("isFruitsDCactive", true);
            editor.commit();
        }
        else{
            editor.putBoolean("isFruitsDCactive", false);
            editor.commit();
        }
        initDrawables();
    }

    //ADD/REMOVE WATER
    public static void toggleWaterChallenge (View v){
        SharedPreferences.Editor editor = preferences.edit();
        Log.i(TAG, "toggleWaterChallenge");
        if(!preferences.getBoolean("isWaterDCactive",false)){
            editor.putBoolean("isWaterDCactive", true);
            editor.commit();
        }
        else{
            editor.putBoolean("isWaterDCactive", false);
            editor.commit();
        }
        initDrawables();
    }

    //ADD/REMOVE FRIES
    public static void toggleFriesChallenge (View v){
        SharedPreferences.Editor editor = preferences.edit();
        Log.i(TAG, "toggleFriesChallenge");
        if(!preferences.getBoolean("isFriesDCactive",false)){
            editor.putBoolean("isFriesDCactive", true);
            editor.commit();
        }
        else{
            editor.putBoolean("isFriesDCactive", false);
            editor.commit();
        }
        initDrawables();
    }
    
    //ADD/REMOVE COKE
    public static void toggleCokeChallenge (View v){
        SharedPreferences.Editor editor = preferences.edit();
        Log.i(TAG, "toggleCokeChallenge");
        if(!preferences.getBoolean("isCokeDCactive",false)){
            editor.putBoolean("isCokeDCactive", true);
            editor.commit();
        }
        else{
            editor.putBoolean("isCokeDCactive", false);
            editor.commit();
        }
        initDrawables();
    }

    //ADD/REMOVE BEER
    public static void toggleBeerChallenge (View v){
        SharedPreferences.Editor editor = preferences.edit();
        Log.i(TAG, "toggleBeerChallenge");
        if(!preferences.getBoolean("isBeerDCactive",false)){
            editor.putBoolean("isBeerDCactive", true);
            editor.commit();
        }
        else{
            editor.putBoolean("isBeerDCactive", false);
            editor.commit();
        }
        initDrawables();
    }

    //ADD/REMOVE FRIES2
    public static void toggleFries2Challenge (View v){
        SharedPreferences.Editor editor = preferences.edit();
        Log.i(TAG, "toggleFries2Challenge");
        if(!preferences.getBoolean("isFries2DCactive",false)){
            editor.putBoolean("isFries2DCactive", true);
            editor.commit();
        }
        else{
            editor.putBoolean("isFries2DCactive", false);
            editor.commit();
        }
        initDrawables();
    }

    public static void initDrawables(){
        if(preferences.getBoolean("isFruitsDCactive",false)) {
            ibAddRemoveFruit.setImageResource(R.drawable.ic_delete);
            ibAddRemoveFruit.setColorFilter(mContext.getResources().getColor(R.color.spiral_end));
        }
        else {
            ibAddRemoveFruit.setImageResource(R.drawable.ic_input_add);
            ibAddRemoveFruit.setColorFilter(mContext.getResources().getColor(R.color.spiral_walking));
        }

        if(preferences.getBoolean("isWaterDCactive",false)) {
            ibAddRemoveWater.setImageResource(R.drawable.ic_delete);
            ibAddRemoveWater.setColorFilter(mContext.getResources().getColor(R.color.spiral_end));
        }
        else {
            ibAddRemoveWater.setImageResource(R.drawable.ic_input_add);
            ibAddRemoveWater.setColorFilter(mContext.getResources().getColor(R.color.spiral_walking));
        }

        if(preferences.getBoolean("isFriesDCactive",false)) {
            ibAddRemoveFries.setImageResource(R.drawable.ic_delete);
            ibAddRemoveFries.setColorFilter(mContext.getResources().getColor(R.color.spiral_end));
        }
        else {
            ibAddRemoveFries.setImageResource(R.drawable.ic_input_add);
            ibAddRemoveFries.setColorFilter(mContext.getResources().getColor(R.color.spiral_walking));
        }

        if(preferences.getBoolean("isCokeDCactive",false)) {
            ibAddRemoveCoke.setImageResource(R.drawable.ic_delete);
            ibAddRemoveCoke.setColorFilter(mContext.getResources().getColor(R.color.spiral_end));
        }
        else {
            ibAddRemoveCoke.setImageResource(R.drawable.ic_input_add);
            ibAddRemoveCoke.setColorFilter(mContext.getResources().getColor(R.color.spiral_walking));
        }

        if(preferences.getBoolean("isBeerDCactive",false)) {
            ibAddRemoveBeer.setImageResource(R.drawable.ic_delete);
            ibAddRemoveBeer.setColorFilter(mContext.getResources().getColor(R.color.spiral_end));
        }
        else {
            ibAddRemoveBeer.setImageResource(R.drawable.ic_input_add);
            ibAddRemoveBeer.setColorFilter(mContext.getResources().getColor(R.color.spiral_walking));
        }
        
        if(preferences.getBoolean("isFries2DCactive",false)) {
            ibAddRemoveFries2.setImageResource(R.drawable.ic_delete);
            ibAddRemoveFries2.setColorFilter(mContext.getResources().getColor(R.color.spiral_end));
        }
        else {
            ibAddRemoveFries2.setImageResource(R.drawable.ic_input_add);
            ibAddRemoveFries2.setColorFilter(mContext.getResources().getColor(R.color.spiral_walking));
        }
    }

    public void finish (View v){
        finish();
    }
}
