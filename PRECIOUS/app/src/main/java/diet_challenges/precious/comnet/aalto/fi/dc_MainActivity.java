package diet_challenges.precious.comnet.aalto.fi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import aalto.comnet.thepreciousproject.R;
import sql_db.precious.comnet.aalto.DBHelper;

public class dc_MainActivity extends AppCompatActivity {

    public static final String TAG = "dc_MainActivity";
    public static Context mContext;
    public static final String PREFS_NAME = "dc_prefs";
    public static final String UP_PREFS_NAME = "UploaderPreferences";

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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences_up = mContext.getSharedPreferences(UP_PREFS_NAME, 0);
                int groupID = preferences_up.getInt("group_ID", -1);
                if(groupID==130 || groupID==678 || groupID==387 || groupID==827){
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

        SharedPreferences preferences = this.getSharedPreferences(PREFS_NAME, 0);
        isFruitDCactive=preferences.getBoolean("isFruitDCactive",true);
        isWaterDCactive=preferences.getBoolean("isWaterDCactive",false);
        isFriesDCactive=preferences.getBoolean("isFriesDCactive",false);
        isCokeDCactive=preferences.getBoolean("isCokeDCactive",false);
        isBeerDCactive=preferences.getBoolean("isBeerDCactive",false);
        isFries2DCactive=preferences.getBoolean("isFries2DCactive",false);


        c_aux = Calendar.getInstance();
        c_aux.setTimeInMillis(System.currentTimeMillis());
        c_aux.set(Calendar.HOUR_OF_DAY, 0);
        c_aux.set(Calendar.MINUTE, 0);
        c_aux.set(Calendar.SECOND, 0);
        c_aux.set(Calendar.MILLISECOND, 0);

        getInfoFromDB();

    }

    public static Context getContext(){
        return mContext;
    }

    private void getInfoFromDB(){
        ArrayList<ArrayList<Long>> dcData;
        try {
            dcData = ui.precious.comnet.aalto.precious.ui_MainActivity.dbhelp.getFoodChallenges(c_aux.getTimeInMillis() - 1, c_aux.getTimeInMillis() + 23 * 3600 * 1000);
        }
        catch (Exception e){
            DBHelper dbhelp = new DBHelper(this);
            dcData = dbhelp.getFoodChallenges(c_aux.getTimeInMillis()-1, c_aux.getTimeInMillis() + 23 * 3600 * 1000);
        }
        Log.i(TAG,"FROM:"+c_aux.getTimeInMillis()+" TO:"+ (c_aux.getTimeInMillis() + 23 * 3600 * 1000));
        Log.i(TAG,"Size: "+dcData.size());
        for(int i=0; i<dcData.size();i++){
                //Fruits
            try {
                int fruitsValue = dcData.get(i).get(1).intValue();
                TextView tvFruitsLevel = (TextView) findViewById(R.id.tvFruitsLevel);
                LinearLayout llFruits = (LinearLayout) findViewById(R.id.llFruits);
                if (isFruitDCactive) {
                    tvFruitsLevel.setVisibility(View.VISIBLE);
                    llFruits.setVisibility(View.VISIBLE);
                    TextView tvFruits = (TextView) findViewById(R.id.tvFruits);
                    tvFruits.setText(""+fruitsValue);
                    String level = getString(R.string.fruit_challenge_title);
                    if (fruitsValue <= 2)
                        level= level.concat(":\n" + getString(R.string.beginner));
                    else if (fruitsValue <= 3)
                        level= level.concat(":\n " + getString(R.string.novice));
                    else if (fruitsValue <= 4)
                        level= level.concat(":\n" + getString(R.string.intermediate));
                    else if (fruitsValue <= 5)
                        level= level.concat(":\n" + getString(R.string.skilled));
                    else
                        level= level.concat(":\n" + getString(R.string.professional));
                    Log.i(TAG,"LEVEL:"+level);
                    tvFruitsLevel.setText(level);
                } else {
                    tvFruitsLevel.setVisibility(View.GONE);
                    llFruits.setVisibility(View.GONE);
                }
            }catch (Exception e){
                Log.e(TAG," ",e);
            }
//
//                case 1: //Water
//                    TextView tvWaterLevel = (TextView) findViewById(R.id.tvWaterLevel);
//                    LinearLayout llWater = (LinearLayout) findViewById(R.id.llWater);
//                    if(isWaterDCactive){
//                        tvWaterLevel.setVisibility(View.VISIBLE);
//                        llWater.setVisibility(View.VISIBLE);
//                    }
//                    else{
//                        tvWaterLevel.setVisibility(View.GONE);
//                        llWater.setVisibility(View.GONE);
//                    }
//                    break;
//                case 2://Fries
//                    TextView tvFriesLevel = (TextView) findViewById(R.id.tvFriesLevel);
//                    LinearLayout llFries = (LinearLayout) findViewById(R.id.llFries);
//                    if(isFriesDCactive){
//                        tvFriesLevel.setVisibility(View.VISIBLE);
//                        llFries.setVisibility(View.VISIBLE);
//                    }
//                    else{
//                        tvFriesLevel.setVisibility(View.GONE);
//                        llFries.setVisibility(View.GONE);
//                    }
//                    break;
//                case 3://Coke
//                    TextView tvCokeLevel = (TextView) findViewById(R.id.tvCokeLevel);
//                    LinearLayout llCoke = (LinearLayout) findViewById(R.id.llCoke);
//                    if(isCokeDCactive){
//                        tvCokeLevel.setVisibility(View.VISIBLE);
//                        llCoke.setVisibility(View.VISIBLE);
//                    }
//                    else{
//                        tvCokeLevel.setVisibility(View.GONE);
//                        llCoke.setVisibility(View.GONE);
//                    }
//                    break;
//                case 4://Beer
//                    TextView tvBeerLevel = (TextView) findViewById(R.id.tvBeerLevel);
//                    LinearLayout llBeer = (LinearLayout) findViewById(R.id.llBeer);
//                    if(isBeerDCactive){
//                        tvBeerLevel.setVisibility(View.VISIBLE);
//                        llBeer.setVisibility(View.VISIBLE);
//                    }
//                    else{
//                        tvBeerLevel.setVisibility(View.GONE);
//                        llBeer.setVisibility(View.GONE);
//                    }
//                    break;
//                case 5://Fries2
//                    TextView tvFries2Level = (TextView) findViewById(R.id.tvFries2Level);
//                    LinearLayout llFries2 = (LinearLayout) findViewById(R.id.llFries2);
//                    if(isFries2DCactive){
//                        tvFries2Level.setVisibility(View.VISIBLE);
//                        llFries2.setVisibility(View.VISIBLE);
//                    }
//                    else{
//                        tvFries2Level.setVisibility(View.GONE);
//                        llFries2.setVisibility(View.GONE);
//                    }
//                    break;
//                default: break;
//            }
        }
    }

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
                ui.precious.comnet.aalto.precious.ui_MainActivity.dbhelp.insertFoodChallenge(c_aux.getTimeInMillis(), 0,value-1);
                ui.precious.comnet.aalto.precious.ui_MainActivity.dbhelp.updateFoodChallenge(c_aux.getTimeInMillis(), 0, value-1);}
        }catch (Exception e){
            DBHelper dbhelp = new DBHelper(this);
            if(value>0) {
                dbhelp.insertFoodChallenge(c_aux.getTimeInMillis(), 0, value-1);
                dbhelp.updateFoodChallenge(c_aux.getTimeInMillis(), 0,value-1);
            }
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
            ui.precious.comnet.aalto.precious.ui_MainActivity.dbhelp.insertFoodChallenge(c_aux.getTimeInMillis(), 0,value+1);
            ui.precious.comnet.aalto.precious.ui_MainActivity.dbhelp.updateFoodChallenge(c_aux.getTimeInMillis(), 0,value+1);
        }catch (Exception e){
            DBHelper dbhelp = new DBHelper(this);
            dbhelp.insertFoodChallenge(c_aux.getTimeInMillis(), 0,value+1);
            dbhelp.updateFoodChallenge(c_aux.getTimeInMillis(), 0,value+1);
        }

        getInfoFromDB();
    }






}
