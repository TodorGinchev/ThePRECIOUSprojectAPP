package diet_challenges.precious.comnet.aalto.fi;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import aalto.comnet.thepreciousproject.R;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;

public class dc_MainActivity extends AppCompatActivity {

    public static final String TAG = "dc_MainActivity";
    public static Context mContext;
    public static final String PREFS_NAME = "dc_prefs";
    public static final String UP_PREFS_NAME = "UploaderPreferences";
    private static final int DC_REMINDER_NOTIF_ID = 100036;

    public static Calendar c_aux;

    public static Boolean isFruitDCactive=false;
    public static Boolean isWaterDCactive=false;
    public static Boolean isFriesDCactive=false;
    public static Boolean isCokeDCactive=false;
    public static Boolean isBeerDCactive=false;
    public static Boolean isFries2DCactive=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dc_main_activity);

        mContext=this;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.dietaryChallenge));
        }
        // Floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_diet_challenge);

        SharedPreferences preferences_up = mContext.getSharedPreferences(UP_PREFS_NAME, 0);
        int groupID = preferences_up.getInt("group_ID", -1);
        if(groupID==517 || groupID==392 || groupID==599 || groupID==135
                || groupID==130 || groupID==678 || groupID==387 || groupID==827){
            fab.setVisibility(View.INVISIBLE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"fab touched");
                SharedPreferences preferences_up = mContext.getSharedPreferences(UP_PREFS_NAME, 0);
                int groupID = preferences_up.getInt("group_ID", -1);
                String nickname = preferences_up.getString("nickname", "-1");
                int nicknameID=-1;
                try{
                    nicknameID = Integer.parseInt(nickname);
                }catch (Exception e){
                    nicknameID=-1;
                    Log.i(TAG," ",e);
                }
                if(groupID==517 || groupID==392 || groupID==599 || groupID==135
                        || nicknameID==517 || nicknameID==392 || nicknameID==599 || nicknameID==135
                    ||
                        groupID==130 || groupID==678 || groupID==387 || groupID==827
                        || nicknameID==130 || nicknameID==678 || nicknameID==387 || nicknameID==827){
                    Toast.makeText(mContext,getString(R.string.not_allow_add_challenge),Toast.LENGTH_LONG).show();
                }
                else{
                    Intent i = new Intent(dc_MainActivity.getContext(), dc_AddChallenge.class);
                    startActivity(i);
                }

            }
        });

        //Set toolbar title and icons
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.dietary_challenge_title));
        toolbar.setTitleTextColor(getResources().getColor(R.color.dietaryChallenge));
        toolbar.setNavigationIcon(R.drawable.dc_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        c_aux = Calendar.getInstance();
        c_aux.setTimeInMillis(System.currentTimeMillis());
        c_aux.set(Calendar.HOUR_OF_DAY, 0);
        c_aux.set(Calendar.MINUTE, 0);
        c_aux.set(Calendar.SECOND, 0);
        c_aux.set(Calendar.MILLISECOND, 0);
    }

    @Override
    protected void onPause() {
        //Store app usage
        try {
            sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).insertAppUsage(System.currentTimeMillis(), TAG, "onPause");
        } catch (Exception e) {
            Log.e(TAG, " ", e);

        }
        super.onPause();
    }


    @Override
    public void onResume(){
        super.onResume();

        //Cancel food reminder notification
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(DC_REMINDER_NOTIF_ID);

        SharedPreferences preferences = this.getSharedPreferences(PREFS_NAME, 0);
        isFruitDCactive=preferences.getBoolean("isFruitsDCactive",false);
        isWaterDCactive=preferences.getBoolean("isWaterDCactive",false);
        isFriesDCactive=preferences.getBoolean("isFriesDCactive",false);
        isCokeDCactive=preferences.getBoolean("isCokeDCactive",false);
        isBeerDCactive=preferences.getBoolean("isBeerDCactive",false);
        isFries2DCactive=preferences.getBoolean("isFries2DCactive", false);

        getInfoFromDB();

        SharedPreferences preferences_up = mContext.getSharedPreferences(UP_PREFS_NAME, 0);
        int groupID = preferences_up.getInt("group_ID", -1);
        String nickname = preferences_up.getString("nickname", "-1");
        int nicknameID=-1;
        try{
            nicknameID = Integer.parseInt(nickname);
        }catch (Exception e){
            nicknameID=-1;
            Log.i(TAG," ",e);
        }
        if(groupID==517 || groupID==392 || groupID==599 || groupID==135
                || nicknameID==517 || nicknameID==392 || nicknameID==599 || nicknameID==135
                ||
                groupID==130 || groupID==678 || groupID==387 || groupID==827
                || nicknameID==130 || nicknameID==678 || nicknameID==387 || nicknameID==827){
            isFruitDCactive=true;
        }

        getInfoFromDB();

        //Store app usage
        try{
            sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).insertAppUsage(System.currentTimeMillis(), TAG, "onResume");
        }catch (Exception e){Log.e(TAG," ",e);}
    }

    public static Context getContext(){
        return mContext;
    }

    private void getInfoFromDB(){
        ArrayList<ArrayList<Long>> dcData;
        try {
            dcData = sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).getFoodChallenges(c_aux.getTimeInMillis() - 1, c_aux.getTimeInMillis() + 23 * 3600 * 1000);
        }
        catch (Exception e){
            Log.i(TAG," ",e);
            dcData = new ArrayList<>();
        }
        Log.i(TAG,"FROM:"+c_aux.getTimeInMillis()+" TO:"+ (c_aux.getTimeInMillis() + 23 * 3600 * 1000));
        Log.i(TAG,"Size: "+dcData.size());

        TextView tv29 = (TextView) findViewById(R.id.textView29);
        if(!isFruitDCactive && !isWaterDCactive && !isFriesDCactive && !isCokeDCactive && !isBeerDCactive && !isFries2DCactive){
            tv29.setVisibility(View.VISIBLE);
        }
        else{
            tv29.setVisibility(View.GONE);
        }

        TextView tvFruitsLevel = (TextView) findViewById(R.id.tvFruitsLevel);
        LinearLayout llFruits = (LinearLayout) findViewById(R.id.llFruits);
        if (isFruitDCactive) {
            tvFruitsLevel.setVisibility(View.VISIBLE);
            llFruits.setVisibility(View.VISIBLE);
        }
        TextView tvWaterLevel = (TextView) findViewById(R.id.tvWaterLevel);
        LinearLayout llWater = (LinearLayout) findViewById(R.id.llWater);
        if (isWaterDCactive) {
            tvWaterLevel.setVisibility(View.VISIBLE);
            llWater.setVisibility(View.VISIBLE);
        }
        TextView tvFriesLevel = (TextView) findViewById(R.id.tvFriesLevel);
        LinearLayout llFries = (LinearLayout) findViewById(R.id.llFries);
        if (isFriesDCactive) {
            tvFriesLevel.setVisibility(View.VISIBLE);
            llFries.setVisibility(View.VISIBLE);
        }
        TextView tvCokeLevel = (TextView) findViewById(R.id.tvCokeLevel);
        LinearLayout llCoke = (LinearLayout) findViewById(R.id.llCoke);
        if (isCokeDCactive) {
            tvCokeLevel.setVisibility(View.VISIBLE);
            llCoke.setVisibility(View.VISIBLE);
        }
        TextView tvBeerLevel = (TextView) findViewById(R.id.tvBeerLevel);
        LinearLayout llBeer = (LinearLayout) findViewById(R.id.llBeer);
        if (isBeerDCactive) {
            tvBeerLevel.setVisibility(View.VISIBLE);
            llBeer.setVisibility(View.VISIBLE);
        }
        TextView tvFries2Level = (TextView) findViewById(R.id.tvFries2Level);
        LinearLayout llFries2 = (LinearLayout) findViewById(R.id.llFries2);
        if (isFries2DCactive) {
            tvFries2Level.setVisibility(View.VISIBLE);
            llFries2.setVisibility(View.VISIBLE);
        }

        
        for(int i=0; i<dcData.size();i++){
            //Fruits
            try {
                int fruitsValue = dcData.get(i).get(1).intValue();
                if (isFruitDCactive) {
                    TextView tvFruits = (TextView) findViewById(R.id.tvFruits);
                    tvFruits.setText(""+fruitsValue);
                    String level = getString(R.string.fruit_challenge_title);
                    ImageButton ibFruits = (ImageButton) findViewById(R.id.imageButtonFruits);
                    if (fruitsValue <= 2) {
                        level = level.concat(":\n" + getString(R.string.beginner));
                        ibFruits.setColorFilter(getResources().getColor(R.color.spiral_end));
                    }
                    else if (fruitsValue <= 3) {
                        level = level.concat(":\n " + getString(R.string.novice));
                        ibFruits.setColorFilter(getResources().getColor(R.color.foodDiary));
                    }
                    else if (fruitsValue <= 4) {
                        level = level.concat(":\n" + getString(R.string.intermediate));
                        ibFruits.setColorFilter(getResources().getColor(R.color.yellow));
                    }
                    else if (fruitsValue <= 5) {
                        level = level.concat(":\n" + getString(R.string.skilled));
                        ibFruits.setColorFilter(getResources().getColor(R.color.spiral_circle));
                    }
                    else {
                        level = level.concat(":\n" + getString(R.string.professional));
                        ibFruits.setColorFilter(getResources().getColor(R.color.spiral_walking));
                    }
                    Log.i(TAG,"LEVEL:"+level);

                    tvFruitsLevel.setText(level);
                } else {
                    tvFruitsLevel.setVisibility(View.GONE);
                    llFruits.setVisibility(View.GONE);
                }
            }catch (Exception e){
                Log.e(TAG," ",e);
            }
            //Water
            try {
                int WaterValue = dcData.get(i).get(2).intValue();
                if (isWaterDCactive) {
                    TextView tvWater = (TextView) findViewById(R.id.tvWater);
                    tvWater.setText(""+WaterValue);
                    String level = getString(R.string.water_challenge_title);
                    ImageButton ibWater = (ImageButton) findViewById(R.id.imageButtonWater);
                    if (WaterValue <= 5) {
                        level = level.concat(":\n" + getString(R.string.beginner));
                        ibWater.setColorFilter(getResources().getColor(R.color.spiral_end));
                    }
                    else if (WaterValue <= 6) {
                        level = level.concat(":\n " + getString(R.string.novice));
                        ibWater.setColorFilter(getResources().getColor(R.color.foodDiary));
                    }
                    else if (WaterValue <= 7) {
                        level = level.concat(":\n" + getString(R.string.intermediate));
                        ibWater.setColorFilter(getResources().getColor(R.color.yellow));
                    }
                    else if (WaterValue <= 8) {
                        level = level.concat(":\n" + getString(R.string.skilled));
                        ibWater.setColorFilter(getResources().getColor(R.color.spiral_circle));
                    }
                    else {
                        level = level.concat(":\n" + getString(R.string.professional));
                        ibWater.setColorFilter(getResources().getColor(R.color.spiral_walking));
                    }
                    Log.i(TAG,"LEVEL:"+level);
                    tvWaterLevel.setText(level);
                } else {
                    tvWaterLevel.setVisibility(View.GONE);
                    llWater.setVisibility(View.GONE);
                }
            }catch (Exception e){
                Log.e(TAG," ",e);
            }
            //Fries
            try {
                int FriesValue = dcData.get(i).get(3).intValue();
                if (isFriesDCactive) {
                    TextView tvFries = (TextView) findViewById(R.id.tvFries);
                    tvFries.setText(""+FriesValue);
                    String level = getString(R.string.fries_challenge_title);
                    ImageButton ibFries = (ImageButton) findViewById(R.id.imageButtonFries);
                    if (FriesValue >=4) {
                        level = level.concat(":\n" + getString(R.string.beginner));
                        ibFries.setColorFilter(getResources().getColor(R.color.spiral_end));
                    }
                    else if (FriesValue >= 3) {
                        level = level.concat(":\n " + getString(R.string.novice));
                        ibFries.setColorFilter(getResources().getColor(R.color.foodDiary));
                    }
                    else if (FriesValue >= 2) {
                        level = level.concat(":\n" + getString(R.string.intermediate));
                        ibFries.setColorFilter(getResources().getColor(R.color.yellow));
                    }
                    else if (FriesValue >=1) {
                        level = level.concat(":\n" + getString(R.string.skilled));
                        ibFries.setColorFilter(getResources().getColor(R.color.spiral_circle));
                    }
                    else {
                        level = level.concat(":\n" + getString(R.string.professional));
                        ibFries.setColorFilter(getResources().getColor(R.color.spiral_walking));
                    }
                    Log.i(TAG,"LEVEL:"+level);
                    tvFriesLevel.setText(level);
                } else {
                    tvFriesLevel.setVisibility(View.GONE);
                    llFries.setVisibility(View.GONE);
                }
            }catch (Exception e){
                Log.e(TAG," ",e);
            }
            //Coke
            try {
                int CokeValue = dcData.get(i).get(4).intValue();
                if (isCokeDCactive) {
                    TextView tvCoke = (TextView) findViewById(R.id.tvCoke);
                    tvCoke.setText(""+CokeValue);
                    String level = getString(R.string.coke_challenge_title);
                    ImageButton ibCoke = (ImageButton) findViewById(R.id.imageButtonCoke);
                    if (CokeValue >= 4) {
                        ibCoke.setColorFilter(getResources().getColor(R.color.spiral_end));
                        level = level.concat(":\n" + getString(R.string.beginner));
                    }
                    else if (CokeValue >= 3) {
                        ibCoke.setColorFilter(getResources().getColor(R.color.foodDiary));
                        level = level.concat(":\n " + getString(R.string.novice));
                    }
                    else if (CokeValue >= 2) {
                        ibCoke.setColorFilter(getResources().getColor(R.color.yellow));
                        level = level.concat(":\n" + getString(R.string.intermediate));
                    }
                    else if (CokeValue >= 1) {
                        ibCoke.setColorFilter(getResources().getColor(R.color.spiral_circle));
                        level = level.concat(":\n" + getString(R.string.skilled));
                    }
                    else {
                        ibCoke.setColorFilter(getResources().getColor(R.color.spiral_walking));
                        level = level.concat(":\n" + getString(R.string.professional));
                    }
                    Log.i(TAG,"LEVEL:"+level);
                    tvCokeLevel.setText(level);
                } else {
                    tvCokeLevel.setVisibility(View.GONE);
                    llCoke.setVisibility(View.GONE);
                }
            }catch (Exception e){
                Log.e(TAG," ",e);
            }
            //Beer
            try {
                int BeerValue = dcData.get(i).get(5).intValue();
                if (isBeerDCactive) {
                    TextView tvBeer = (TextView) findViewById(R.id.tvBeer);
                    tvBeer.setText(""+BeerValue);
                    String level = getString(R.string.beer_challenge_title);
                    ImageButton ibBeer = (ImageButton) findViewById(R.id.imageButtonBeer);
                    if (BeerValue >= 4) {
                        level = level.concat(":\n" + getString(R.string.beginner));
                        ibBeer.setColorFilter(getResources().getColor(R.color.spiral_end));
                    }
                    else if (BeerValue >= 3) {
                        level = level.concat(":\n " + getString(R.string.novice));
                        ibBeer.setColorFilter(getResources().getColor(R.color.foodDiary));
                    }
                    else if (BeerValue >= 2) {
                        level = level.concat(":\n" + getString(R.string.intermediate));
                        ibBeer.setColorFilter(getResources().getColor(R.color.yellow));
                    }
                    else if (BeerValue >= 1) {
                        level = level.concat(":\n" + getString(R.string.skilled));
                        ibBeer.setColorFilter(getResources().getColor(R.color.spiral_circle));
                    }
                    else {
                        level = level.concat(":\n" + getString(R.string.professional));
                        ibBeer.setColorFilter(getResources().getColor(R.color.spiral_walking));
                    }
                    Log.i(TAG,"LEVEL:"+level);
                    tvBeerLevel.setText(level);
                } else {
                    tvBeerLevel.setVisibility(View.GONE);
                    llBeer.setVisibility(View.GONE);
                }
            }catch (Exception e){
                Log.e(TAG," ",e);
            }
            //Fries2
            try {
                int Fries2Value = dcData.get(i).get(6).intValue();
                if (isFries2DCactive) {
                    TextView tvFries2 = (TextView) findViewById(R.id.tvFries2);
                    tvFries2.setText(""+Fries2Value);
                    String level = getString(R.string.fries2_challenge_title);
                    ImageButton ibFries2 = (ImageButton) findViewById(R.id.imageButtonFries2);
                    if (Fries2Value >= 4) {
                        level = level.concat(":\n" + getString(R.string.beginner));
                        ibFries2.setColorFilter(getResources().getColor(R.color.spiral_end));
                    }
                    else if (Fries2Value >= 3) {
                        level = level.concat(":\n " + getString(R.string.novice));
                        ibFries2.setColorFilter(getResources().getColor(R.color.foodDiary));
                    }
                    else if (Fries2Value >= 2) {
                        level = level.concat(":\n" + getString(R.string.intermediate));
                        ibFries2.setColorFilter(getResources().getColor(R.color.yellow));
                    }
                    else if (Fries2Value >= 1) {
                        level = level.concat(":\n" + getString(R.string.skilled));
                        ibFries2.setColorFilter(getResources().getColor(R.color.spiral_circle));
                    }
                    else {
                        level = level.concat(":\n" + getString(R.string.professional));
                        ibFries2.setColorFilter(getResources().getColor(R.color.spiral_walking));
                    }
                    Log.i(TAG,"LEVEL:"+level);
                    tvFries2Level.setText(level);
                } else {
                    tvFries2Level.setVisibility(View.GONE);
                    llFries2.setVisibility(View.GONE);
                }
            }catch (Exception e){
                Log.e(TAG," ",e);
            }
        }
    }

    /*
    REMOVE/ADD FRUITS
     */
    public void removeFruits (View v){
        TextView tvFruits = (TextView) findViewById(R.id.tvFruits);
        int value=0;
        try {
            value = Integer.parseInt(tvFruits.getText().toString());
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
        try{
            if(value>0){
                sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).insertFoodChallenge(c_aux.getTimeInMillis(), 0, value - 1);
                sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).updateFoodChallenge(c_aux.getTimeInMillis(), 0, value - 1);}
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
        getInfoFromDB();
    }

    public void addFruits (View v){
        TextView tvFruits = (TextView) findViewById(R.id.tvFruits);
        int value=0;
        try {
            value = Integer.parseInt(tvFruits.getText().toString());
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
        try{
            sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).insertFoodChallenge(c_aux.getTimeInMillis(), 0, value + 1);
            sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).updateFoodChallenge(c_aux.getTimeInMillis(), 0, value + 1);
        }catch (Exception e){
            Log.i(TAG, " ", e);
        }
        getInfoFromDB();
    }

    /*
REMOVE/ADD WATER
 */
    public void removeWater (View v){
        TextView tvWater = (TextView) findViewById(R.id.tvWater);
        int value=0;
        try {
            value = Integer.parseInt(tvWater.getText().toString());
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
        try{
            if(value>0){
                sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).insertFoodChallenge(c_aux.getTimeInMillis(), 1, value - 1);
                sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).updateFoodChallenge(c_aux.getTimeInMillis(), 1, value - 1);}
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
        getInfoFromDB();
    }

    public void addWater (View v){
        TextView tvWater = (TextView) findViewById(R.id.tvWater);
        int value=0;
        try {
            value = Integer.parseInt(tvWater.getText().toString());
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
        try{
            sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).insertFoodChallenge(c_aux.getTimeInMillis(), 1, value + 1);
            sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).updateFoodChallenge(c_aux.getTimeInMillis(), 1, value + 1);
        }catch (Exception e){
            Log.i(TAG, " ", e);
        }
        getInfoFromDB();
    }


    /*
REMOVE/ADD FRIES
 */
    public void removeFries (View v){
        TextView tvFries = (TextView) findViewById(R.id.tvFries);
        int value=0;
        try {
            value = Integer.parseInt(tvFries.getText().toString());
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
        try{
            if(value>0){
                sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).insertFoodChallenge(c_aux.getTimeInMillis(), 2, value - 1);
                sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).updateFoodChallenge(c_aux.getTimeInMillis(), 2, value - 1);}
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
        getInfoFromDB();
    }

    public void addFries (View v){
        TextView tvFries = (TextView) findViewById(R.id.tvFries);
        int value=0;
        try {
            value = Integer.parseInt(tvFries.getText().toString());
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
        try{
            sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).insertFoodChallenge(c_aux.getTimeInMillis(), 2, value + 1);
            sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).updateFoodChallenge(c_aux.getTimeInMillis(), 2, value + 1);
        }catch (Exception e){
            Log.i(TAG, " ", e);
        }
        getInfoFromDB();
    }



    /*
REMOVE/ADD COKE
 */
    public void removeCoke (View v){
        TextView tvCoke = (TextView) findViewById(R.id.tvCoke);
        int value=0;
        try {
            value = Integer.parseInt(tvCoke.getText().toString());
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
        try{
            if(value>0){
                sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).insertFoodChallenge(c_aux.getTimeInMillis(), 3, value - 1);
                sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).updateFoodChallenge(c_aux.getTimeInMillis(), 3, value - 1);}
        }catch (Exception e){
           Log.e(TAG," ",e);
        }
        getInfoFromDB();
    }

    public void addCoke (View v){
        TextView tvCoke = (TextView) findViewById(R.id.tvCoke);
        int value=0;
        try {
            value = Integer.parseInt(tvCoke.getText().toString());
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
        try{
            sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).insertFoodChallenge(c_aux.getTimeInMillis(), 3, value + 1);
            sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).updateFoodChallenge(c_aux.getTimeInMillis(), 3, value + 1);
        }catch (Exception e){
            Log.i(TAG, " ", e);
        }
        getInfoFromDB();
    }


    /*
REMOVE/ADD BEER
 */
    public void removeBeer (View v){
        TextView tvBeer = (TextView) findViewById(R.id.tvBeer);
        int value=0;
        try {
            value = Integer.parseInt(tvBeer.getText().toString());
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
        try{
            if(value>0){
                sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).insertFoodChallenge(c_aux.getTimeInMillis(), 4, value - 1);
                sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).updateFoodChallenge(c_aux.getTimeInMillis(), 4, value - 1);}
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
        getInfoFromDB();
    }

    public void addBeer (View v){
        TextView tvBeer = (TextView) findViewById(R.id.tvBeer);
        int value=0;
        try {
            value = Integer.parseInt(tvBeer.getText().toString());
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
        try{
            sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).insertFoodChallenge(c_aux.getTimeInMillis(), 4,value+1);
            sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).updateFoodChallenge(c_aux.getTimeInMillis(), 4, value + 1);
        }catch (Exception e){
            Log.i(TAG," ",e);
        }
        getInfoFromDB();
    }


    /*
REMOVE/ADD FRIES2
 */
    public void removeFries2 (View v){
        TextView tvFries2 = (TextView) findViewById(R.id.tvFries2);
        int value=0;
        try {
            value = Integer.parseInt(tvFries2.getText().toString());
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
        try{
            if(value>0){
                sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).insertFoodChallenge(c_aux.getTimeInMillis(), 5, value - 1);
                sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).updateFoodChallenge(c_aux.getTimeInMillis(), 5, value - 1);}
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
        getInfoFromDB();
    }

    public void addFries2 (View v){
        TextView tvFries2 = (TextView) findViewById(R.id.tvFries2);
        int value=0;
        try {
            value = Integer.parseInt(tvFries2.getText().toString());
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
        try{
            sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).insertFoodChallenge(c_aux.getTimeInMillis(), 5, value + 1);
            sql_db.precious.comnet.aalto.DBHelper.getInstance(PRECIOUS_APP.getContext()).updateFoodChallenge(c_aux.getTimeInMillis(), 5, value + 1);
        }catch (Exception e){
            Log.i(TAG, " ", e);
        }
        getInfoFromDB();
    }
}
