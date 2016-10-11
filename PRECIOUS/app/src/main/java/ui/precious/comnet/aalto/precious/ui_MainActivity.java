//Documentation for android custom views:
//      http://android-developers.blogspot.fi/2015/05/android-design-support-library.html
//Documentation for action bar configuration:
//      http://blog.rhesoft.com/2015/03/30/tutorial-android-actionbar-with-material-design-and-search-field/
//Documentation for tutorial view
//      http://forum.xda-developers.com/showthread.php?t=2419939

package ui.precious.comnet.aalto.precious;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.Calendar;
import java.util.Vector;

import aalto.comnet.thepreciousproject.R;
import confidence_ruler.precious.comnet.aalto.CR_ThirdActivity;
import diet_challenges.precious.comnet.aalto.fi.dc_AddChallenge;
import firstbeatalbum.precious.comnet.aalto.FirstBeatAlbumActivity;
import food_diary.precious.comnet.aalto.fd_MainActivity;
import my_favourites.precious.comnet.aalto.FA_SecondActivity;
import my_favourites.precious.comnet.aalto.my_favourites_activity;
import pa_state_of_change.precious.comnet.aalto.PA_SOC_FirstActivity;
import precious_rule_system.journeyview.assets.Assets;
import precious_rule_system.journeyview.data.DataManager;
import precious_rule_system.journeyview.helpers.SizeCalculator;
import precious_rule_system.journeyview.recycler.RecyclerView;
import precious_rule_system.journeyview.view.JourneyView;
import precious_rule_system.precoiusinterface.PreciousApplicationActions;
import time_machine.precious.comnet.aalto.TM_SecondActivity;


public class ui_MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    private GoogleApiClient client;
    public static final String TAG = "ui_MainActivity";
    public static final String UP_PREFS_NAME = "UploaderPreferences";
    public static final String UI_PREFS_NAME = "UIPreferences";
    public static final int ONBOARDING_RESULT_CODE = 1012;
    final private int EXT_STORAGE_PERMISSION = 23;
    private SharedPreferences uploader_preferences;
    public static Context mContext;

    public static String [] boxOrganizer;
    private PRECIOUS_APP preciousRuleSystem;

    //For the tutorial
    public static ShowcaseView showcaseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preciousRuleSystem = PRECIOUS_APP.getInstance();

        initJurneyView();
//        uiUtils.firstStartConfig();
    }

    @Override
    public void onResume(){
        uiUtils.CheckIfAlarmAlive();
        Log.i(TAG,"onResume");
        mContext=this;
        uploader_preferences = this.getSharedPreferences(UP_PREFS_NAME, 0);
        SB_current_rows=0;
        SB_current_half_row=0;
        SB_current_half_col=0;
        //If Android version >=5.0, set status bar background color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(0x000000);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            //Add this for the jurney view
            {
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    Log.i(TAG,"Calling store.data.onPause();");
                    store.data.onPause();
                }
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    Log.i(TAG,"Calling store.data.onResume();");
                    store.data.onResume();
                }
            };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set username and location
        //View header = navigationView.inflateHeaderView(R.layout.ui_nav_header_main);
        //navigationView.addHeaderView(header);
        View header = navigationView.getHeaderView(0);
//        ImageView iv_profile = (ImageView) header.findViewById(R.id.imageViewProfile);
//        iv_profile.setImageResource(R.drawable.profile);
//        TextView tv_username = (TextView) header.findViewById(R.id.textViewNavDrawUsername);
//        tv_username.setText("");
////        tv_username.setText(uploader_preferences.getString("nickname",""));
//        TextView tv_location = (TextView) header.findViewById(R.id.textViewNavDrawLocation);
//        tv_location.setText("");

        //Change toolbar title
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        //actionBar.setDisplayShowTitleEnabled(true);

        if(uploader_preferences.getString("nickname","?").equals("?")) {
            actionBar.setTitle("REGISTER!");
        }
        else {
            try {
                String nickname = uploader_preferences.getString("nickname", "");
                actionBar.setTitle(getString(R.string.toolbar_name).concat(" ").concat(nickname).concat("!"));

                // @christopher
                // Set the nickname in the reward system as well
                if (!nickname.equals("")) {
                    PRECIOUS_APP.getRewardSystem().setUser(nickname);
                }


            }catch (Exception e){
                Log.e(TAG," ",e);
            }
        }

        super.onResume();
        askForPermissions();
        //Check if user has logged in
        if(  !(uploader_preferences.getBoolean("isUserLoggedIn",false)) ) {
            sql_db.precious.comnet.aalto.DBHelper.getInstance().dropAllTables();
            Intent i2 = new Intent(this,onboarding.precious.comnet.aalto.obMainActivity.class);
            this.startActivity(i2);
        }
        //Update AppNotOpenedSince timestamp
        SharedPreferences.Editor editor = uploader_preferences.edit();
        editor.putLong("AppNotOpenedSince",System.currentTimeMillis());
        editor.apply();

        gridLayout = (GridLayout) findViewById(R.id.grid_layout);
        display = getWindowManager().getDefaultDisplay();
        initSandBox();

//        if(uploader_preferences.getString("nickname","?").equals("?")) {
//            finish();
//        }
        //Store app usage
        try{
            sql_db.precious.comnet.aalto.DBHelper.getInstance().insertAppUsage(System.currentTimeMillis(), "ui_MainActivity", "onResume");
        }catch (Exception e){
            Log.e(TAG," ",e);
        }


        //Check if user has registered and tutorial is done
        SharedPreferences at_preferences =  this.getSharedPreferences(UI_PREFS_NAME, 0);

        boolean OGisenabled = false;
        for (int i = 0; i<boxOrganizer.length;i++)
            if(boxOrganizer[i].equals("OG"))
                OGisenabled=true;

        if( uploader_preferences.getBoolean("isUserLoggedIn",false) && !at_preferences.getBoolean("at_tutorial_completed",false) && OGisenabled){
            startTutorial();
        }
        else if (showcaseView!=null)
            showcaseView.hide();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            String url = "http://www.thepreciousproject.eu/";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else if (id == R.id.nav_feedback) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"todor.a.ginchev@aalto.fi"});
            String version = "";
            try{
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                version = pInfo.versionName;
            } catch (Exception e) {
                Log.e(TAG, " ", e);
            }
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.report_problem_template_title).concat(version));
            String text = getString(R.string.report_problem_template_content);
            SharedPreferences preferences = this.getSharedPreferences(UP_PREFS_NAME, 0);
            intent.putExtra(Intent.EXTRA_TEXT,text);
            startActivity(Intent.createChooser(intent, "Send Email"));
        }
        else if (id == R.id.nav_logout) {
            SharedPreferences preferences = this.getSharedPreferences(UP_PREFS_NAME, 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();


            editor.putBoolean("isUserLoggedIn", false);
            editor.putString("email", "?");
            editor.putString("password", "?");
            editor.putString("weight", "?");
            editor.putString("height", "?");
            editor.putString("activityClass", "?");
            editor.putString("nickname", "");
            editor.putString("birthdate", "?");
            editor.putString("gender", "?");
            editor.apply();

            preferences = this.getSharedPreferences(CR_ThirdActivity.CR_PREFS_NAME, 0);
            editor = preferences.edit();
            editor.clear();
            editor.apply();

            preferences = this.getSharedPreferences(UI_PREFS_NAME, 0);
            editor = preferences.edit();
            editor.clear();
            editor.apply();

            preferences = this.getSharedPreferences(FA_SecondActivity.FA_PREFS_NAME, 0);
            editor = preferences.edit();
            editor.clear();
            editor.apply();

            preferences = this.getSharedPreferences(FA_SecondActivity.OG_PREFS_NAME, 0);
            editor = preferences.edit();
            editor.clear();
            editor.apply();

            preferences = this.getSharedPreferences(TM_SecondActivity.TM_PREFS_NAME, 0);
            editor = preferences.edit();
            editor.clear();
            editor.apply();

            preferences = this.getSharedPreferences(PA_SOC_FirstActivity.PA_SOC_PREFS_NAME, 0);
            editor = preferences.edit();
            editor.clear();
            editor.apply();

            preferences = this.getSharedPreferences(dc_AddChallenge.DC_PREF_NAME, 0);
            editor = preferences.edit();
            editor.clear();
            editor.apply();


            Intent i2 = new Intent(this,onboarding.precious.comnet.aalto.obMainActivity.class);
            this.startActivity(i2);

        }
//        else if (id == R.id.nav_manage) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static GridLayout gridLayout;
    private static Display display;
    private static int LayoutWidth;
    private static int BoxMargins;
    private static int SB_cols=2;
    private static int SB_rows=50;//TODO, very important parameter!!!
    private static int SB_current_rows=0;
    private static int SB_current_half_row=0;
    private static int SB_current_half_col=0;
    private Vector<ImageView> SBelements = new Vector<ImageView>();

    public static void initSandBox() {
        SharedPreferences preferences_up = mContext.getSharedPreferences(UP_PREFS_NAME, 0);
        int groupID = preferences_up.getInt("group_ID", -1);

        if(!preferences_up.getBoolean("GroupIDsent",false)){
            uploader.precious.comnet.aalto.upUtils.sendGroupIDToPreciousServer(groupID);
        }

        Calendar c_aux = Calendar.getInstance();
        Log.i(TAG,"Registration time="+preferences_up.getLong("rd", System.currentTimeMillis()));
        long timeNow=System.currentTimeMillis();
        long registrationTime = timeNow;
        try {
            registrationTime = preferences_up.getLong("rd", timeNow);
            SharedPreferences.Editor editor = preferences_up.edit();
            editor.putLong("rd",(long)registrationTime);
            editor.commit();
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
        c_aux.setTimeInMillis(registrationTime);
        c_aux.set(Calendar.HOUR_OF_DAY, 0);
        c_aux.set(Calendar.MINUTE, 0);
        c_aux.set(Calendar.SECOND, 0);
        c_aux.set(Calendar.MILLISECOND, 0);
        boolean seven_days_passed=System.currentTimeMillis() > (c_aux.getTimeInMillis()+8*24*3600*1000);

        Log.i(TAG,"GroupID="+groupID);
        if(groupID==130 || groupID==517){
            //Fruit and Vegetable challenge- Motivation off after 7 days
            boxOrganizer = new String[]{"DC"};
        }
        else if(groupID==678 || groupID==392){
            //Fruit and Vegetable challenge- Motivation on after 7 days
            if(seven_days_passed){
                boxOrganizer = new String[]{"OG", "IR", "DC"};
                //TODO
            }
            else{
                boxOrganizer = new String[]{"DC"};
                //TODO
            }
        }
        else if(groupID==387 || groupID==599){
                boxOrganizer = new String[]{ "MD"};
        }
        else if(groupID==827 || groupID==135){
            //Diary- Motivation on after 7 days
            if(seven_days_passed){
                boxOrganizer = new String[]{"OG", "IR", "MD"};
                //TODO
            }
            else{
                boxOrganizer = new String[]{"MD"};
                //TODO
            }
        }
        else if(groupID/1000==9){
//            precious_rule_system.precoiusinterface.PreciousApplicationActions.enableDisableSubapp(true, PreciousApplicationActions.SubAppKeys.IMPORTANCE_RULER);
            boxOrganizer = PreciousApplicationActions.getBoxOrganizer();
        }
        else{
//                addDivider("TEST");
                boxOrganizer = new String[]{"SAD","WR","OG","IR","OAD","MF","TM","PA_SOC","FA","CR","SM","MD","DC","UP"};
//            boxOrganizer = new String[]{"SAD","SM"};
//            boxOrganizer = new String[]{"OG","IR","MF","TM","PA_SOC","FA","CR","SM","MD","DC","UP"};
        }

        gridLayout.removeAllViews();
        Point size = new Point();
        display.getSize(size);

        LayoutWidth = size.x;
        BoxMargins = LayoutWidth / 50;
        LayoutWidth = LayoutWidth - 3 * BoxMargins;
        gridLayout.setPadding(0, 0, BoxMargins / 2, BoxMargins);
        gridLayout.setColumnCount(SB_cols);
        gridLayout.setRowCount(SB_rows + 1);
        gridLayout.setVerticalScrollBarEnabled(true);

        SharedPreferences ui_preferences = mContext.getSharedPreferences(UI_PREFS_NAME, 0);
        if(ui_preferences.getBoolean("OGset",false))
            moveSBtoEnd("OG");
        if(ui_preferences.getBoolean("IRset",false))
            moveSBtoEnd("IR");
        if(ui_preferences.getBoolean("PA_SOC_set",false))
            moveSBtoEnd("PA_SOC");
        if(ui_preferences.getBoolean("TM_set",false))
            moveSBtoEnd("TM");
        if(ui_preferences.getBoolean("FA_set",false))
            moveSBtoEnd("FA");
        if(ui_preferences.getBoolean("CR_set",false))
            moveSBtoEnd("CR");

//        moveSBtoEnd("DB");

        SB_current_half_row=0;
        SB_current_half_col=0;

        for (int i=0; i<boxOrganizer.length;i++)
            addView(boxOrganizer[i]);


        //TODO for the app tutorial
//        if(!ui_preferences.getBoolean("OGset",false) && groupID/1000==9 ) {
//            try {
//                Target target = new ViewTarget(R.id.OG_id, this);
//                ShowcaseView.Builder res = new ShowcaseView.Builder(this, true)
//                        .setTarget(target)
//                        .setContentTitle("Hello! I am a Precious sample introduction text! Be like me!")
//                        .setContentText("");
//                res.setStyle(R.style.CustomShowcaseTheme);
//                res.build();
//            } catch (Exception e) {
//                Log.e(TAG, " ", e);
//            }
//        }
    }

//    /**
//     *
//     * @param text
//     */
//    public static void addDivider(String text){
//        TextView tv = new TextView(mContext);
//        tv.setText(text);
//
//        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
//        param.height = LayoutWidth / SB_cols;
//        param.width = (2 * LayoutWidth / SB_cols) + BoxMargins;
//
//
//        param.setMargins(BoxMargins, BoxMargins, 0, 0);
//
//            param.columnSpec = GridLayout.spec(0, 2);
//            param.rowSpec = GridLayout.spec(SB_current_rows);
//
//        param.setGravity(Gravity.CENTER);
//        tv.setLayoutParams(param);
//        gridLayout.addView(tv);
//        SB_current_rows++;
//    }
    /**
     *
     * @param resourceID
     * @param relativeWidth
     * @param activity
     */
    public static void addSBelement (int resourceID, int relativeWidth, final Class activity){
//        Log.i(TAG,"Grid layout rows:"+gridLayout.getRowCount());
//        Log.i(TAG,"SB_current_half_row:"+SB_current_half_row);
//        Log.i(TAG,"SB_current_rows:"+SB_current_rows);
//        Log.i(TAG,"addSBelement, "+resourceID+","+relativeWidth+" ");
        ImageView im = new ImageView(mContext);
        //im.setBackgroundColor(Color);
         im.setImageResource(resourceID);
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        if(activity==null)
            param.height = LayoutWidth / SB_cols / 4;
        else
            param.height = LayoutWidth / SB_cols;
        if(relativeWidth==2) {
            param.width = (relativeWidth * LayoutWidth / SB_cols) + BoxMargins;
        }
        else {
            param.width = (relativeWidth * LayoutWidth / SB_cols);
        }
        param.setMargins(BoxMargins, BoxMargins, 0, 0);

        if(relativeWidth==2) {
            param.columnSpec = GridLayout.spec(0, relativeWidth);
            param.rowSpec = GridLayout.spec(SB_current_rows);
        }
        else{
            param.columnSpec = GridLayout.spec(SB_current_half_col, relativeWidth);
            param.rowSpec = GridLayout.spec(SB_current_half_row);
        }
        param.setGravity(Gravity.CENTER);
        im.setLayoutParams(param);

        //SBelements.add(im);
        if(resourceID==R.drawable.outcome_goal_tut)
            im.setId(R.id.OG_id_tut);
        gridLayout.addView(im);

        //Define location based on size of the element
        if(relativeWidth==2) {
            SB_current_rows++;
            if(SB_current_half_col==0)
                SB_current_half_row++;
        }
        if(relativeWidth==1){
            if(SB_current_half_col==0){
                SB_current_rows++;
                SB_current_half_col=1;
            }
            else{
                SB_current_half_row=SB_current_rows;
                SB_current_half_col=0;
            }
        }

        if(activity!=null){
            //Set onClick event
            im.setClickable(true);
            im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity.equals(FirstBeatAlbumActivity.class)) {
                        uploader.precious.comnet.aalto.upUtils.getBGimage("/data?key=BG2_REPORT_IMAGE&query=1");
                    } else {
                        Intent i = new Intent(v.getContext(), activity);
                        mContext.startActivity(i);
                    }
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        try {
//            client.connect();
//            Action viewAction = Action.newAction(
//                    Action.TYPE_VIEW, // TODO: choose an action type.
//                    "Main Page", // TODO: Define a title for the content shown.
//                    // TODO: If you have web page content that matches this app activity's content,
//                    // make sure this auto-generated web page URL is correct.
//                    // Otherwise, set the URL to null.
//                    Uri.parse("http://host/path"),
//                    // TODO: Make sure this auto-generated app deep link URI is correct.
//                    Uri.parse("android-app://aalto.comnet.thepreciousproject/http/host/path")
//            );
//            AppIndex.AppIndexApi.start(client, viewAction);
//        }catch (Exception e){
//            Log.e(TAG,"",e);
//        }
    }

    @Override
    public void onStop() {
        super.onStop();

//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Main Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app deep link URI is correct.
//                Uri.parse("android-app://aalto.comnet.thepreciousproject/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        client.disconnect();
    }

    @Override
    protected void onPause() {
        //Store app usage
        try {
            SharedPreferences preferences_up = this.getSharedPreferences(UP_PREFS_NAME, 0);
            int groupID = preferences_up.getInt("group_ID", -1);
            sql_db.precious.comnet.aalto.DBHelper.getInstance().insertAppUsage(System.currentTimeMillis(), "ui_MainActivity, Group"+groupID, "onPause");
        } catch (Exception e) {
            Log.e(TAG, " ", e);
        }
        super.onPause();
    }


    public static void addView(String name){
        switch (name){
            case "OG": addSBelement (R.drawable.outcome_goal, 1, outcomegoal.precious.comnet.aalto.outcomegoal_activity.class);break;
            case "OGtut": addSBelement (R.drawable.outcome_goal_tut, 1, outcomegoal.precious.comnet.aalto.outcomegoal_activity.class);break;
            case "IR": addSBelement(R.drawable.importance_ruler, 1, importance_ruler.precious.comnet.aalto.ImportanceRulerActivity.class);break;
            case "DC": addSBelement(R.drawable.diet_challenges, 1, diet_challenges.precious.comnet.aalto.fi.dc_MainActivity.class);break;
            case "SM": addSBelement(R.drawable.self_monitoring, 2, activity_tracker.precious.comnet.aalto.MountainViewActivity.class);break;
            case "MD": addSBelement(R.drawable.my_food_diary, 2, fd_MainActivity.class);break;
            case "DB": addSBelement(R.drawable.debug, 1, ui.precious.comnet.aalto.precious.Timeline.class);break;
            case "UP": addSBelement(R.drawable.uploader, 1, FirstBeatAlbumActivity.class);break;
            case "PA_SOC": addSBelement(R.drawable.pa_soc, 1, pa_state_of_change.precious.comnet.aalto.pa_soc_activity.class);break;
            case "TM": addSBelement(R.drawable.time_machine, 2,time_machine.precious.comnet.aalto.time_machine_activity.class);break;
            case "FA": addSBelement(R.drawable.my_favourites, 1, my_favourites_activity.class);break;
            case "CR": addSBelement(R.drawable.confidence_ruler, 1, confidence_ruler.precious.comnet.aalto.confidence_ruler_activity.class);break;
            case "WR": addSBelement(R.drawable.wearable, 1, wearable.precious.comnet.aalto.WearableMainActivity.class);break;
            case "SAD": addSBelement(R.drawable.suggested_apps, 2, null);break;
            case "OAD": addSBelement(R.drawable.other_apps, 2, null);break;
            default: break;
        }
    }

    public static void moveSBtoEnd (String name){
        switch (name){
            case "OG":
                for(int i=0;i<boxOrganizer.length;i++)
                    if(boxOrganizer[i].equals("OG")){
                        for(int j=i;j<boxOrganizer.length-1;j++)
                            boxOrganizer[j]=boxOrganizer[j+1];
                        boxOrganizer[boxOrganizer.length-1]="OG";
                    }
                break;
            case "DC":
                for(int i=0;i<boxOrganizer.length;i++)
                    if(boxOrganizer[i].equals("DC")){
                        for(int j=i;j<boxOrganizer.length-1;j++)
                            boxOrganizer[j]=boxOrganizer[j+1];
                        boxOrganizer[boxOrganizer.length-1]="DC";
                    }
                break;
            case "IR":
                for(int i=0;i<boxOrganizer.length;i++)
                    if(boxOrganizer[i].equals("IR")){
                        for(int j=i;j<boxOrganizer.length-1;j++)
                            boxOrganizer[j]=boxOrganizer[j+1];
                        boxOrganizer[boxOrganizer.length-1]="IR";
                    }
                break;
            case "SM":
                for(int i=0;i<boxOrganizer.length;i++)
                    if(boxOrganizer[i].equals("SM")){
                        for(int j=i;j<boxOrganizer.length-1;j++)
                            boxOrganizer[j]=boxOrganizer[j+1];
                        boxOrganizer[boxOrganizer.length-1]="SM";
                    } break;
            case "WR":
                for(int i=0;i<boxOrganizer.length;i++)
                    if(boxOrganizer[i].equals("WR")){
                        for(int j=i;j<boxOrganizer.length-1;j++)
                            boxOrganizer[j]=boxOrganizer[j+1];
                        boxOrganizer[boxOrganizer.length-1]="WR";
                    } break;
            case "FA":
                for(int i=0;i<boxOrganizer.length;i++)
                    if(boxOrganizer[i].equals("FA")){
                        for(int j=i;j<boxOrganizer.length-1;j++)
                            boxOrganizer[j]=boxOrganizer[j+1];
                        boxOrganizer[boxOrganizer.length-1]="FA";
                    }break;
            case "MD":
                for(int i=0;i<boxOrganizer.length;i++)
                    if(boxOrganizer[i].equals("MD")){
                        for(int j=i;j<boxOrganizer.length-1;j++)
                            boxOrganizer[j]=boxOrganizer[j+1];
                        boxOrganizer[boxOrganizer.length-1]="MD";
                    }break;
            case "DB":
                for(int i=0;i<boxOrganizer.length;i++)
                    if(boxOrganizer[i].equals("DB")){
                        for(int j=i;j<boxOrganizer.length-1;j++)
                            boxOrganizer[j]=boxOrganizer[j+1];
                        boxOrganizer[boxOrganizer.length-1]="DB";
                    } break;
            case "UP":
                for(int i=0;i<boxOrganizer.length;i++)
                    if(boxOrganizer[i].equals("UP")){
                        for(int j=i;j<boxOrganizer.length-1;j++)
                            boxOrganizer[j]=boxOrganizer[j+1];
                        boxOrganizer[boxOrganizer.length-1]="UP";
                    } break;
            case "PA_SOC":
                for(int i=0;i<boxOrganizer.length;i++)
                    if(boxOrganizer[i].equals("PA_SOC")){
                        for(int j=i;j<boxOrganizer.length-1;j++)
                            boxOrganizer[j]=boxOrganizer[j+1];
                        boxOrganizer[boxOrganizer.length-1]="PA_SOC";
                    } break;
            case "TM":
                for(int i=0;i<boxOrganizer.length;i++)
                    if(boxOrganizer[i].equals("TM")){
                        for(int j=i;j<boxOrganizer.length-1;j++)
                            boxOrganizer[j]=boxOrganizer[j+1];
                        boxOrganizer[boxOrganizer.length-1]="TM";
                    } break;
            case "CR":
                for(int i=0;i<boxOrganizer.length;i++)
                    if(boxOrganizer[i].equals("CR")){
                        for(int j=i;j<boxOrganizer.length-1;j++)
                            boxOrganizer[j]=boxOrganizer[j+1];
                        boxOrganizer[boxOrganizer.length-1]="CR";
                    } break;
            case "SAD":
                for(int i=0;i<boxOrganizer.length;i++)
                    if(boxOrganizer[i].equals("SAD")){
                        for(int j=i;j<boxOrganizer.length-1;j++)
                            boxOrganizer[j]=boxOrganizer[j+1];
                        boxOrganizer[boxOrganizer.length-1]="SAD";
                    } break;
            case "OAD":
                for(int i=0;i<boxOrganizer.length;i++)
                    if(boxOrganizer[i].equals("OAD")){
                        for(int j=i;j<boxOrganizer.length-1;j++)
                            boxOrganizer[j]=boxOrganizer[j+1];
                        boxOrganizer[boxOrganizer.length-1]="OAD";
                    } break;
            default: break;
        }
    }
//    /**
//     *
//     */
//    public Point getDisplaySize(){
//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        return size;
//    }

//    /**
//     *
//     */
//    @Override protected void onActivityResult (int requestCode,
//                                               int resultCode, Intent data){
//        if (requestCode== ONBOARDING_RESULT_CODE && resultCode==RESULT_OK) {
//            if(data.getExtras().getBoolean("close_activity")) {
//                Log.i(TAG,"finished");
//                finish();
//            }
//        }
//    }

    public Activity getActivity (){
        return this;
    }

    public void askForPermissions() {
// Here, thisActivity is the current activity

        Activity thisActivity = (Activity) this;

        boolean hasPermission = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

        if(hasPermission){
            Log.i(TAG,"WRITE_EXTERNAL_STORAGE permission granted");
        }
        else{

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(thisActivity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},EXT_STORAGE_PERMISSION
                        );

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case EXT_STORAGE_PERMISSION: {
                Log.i(TAG,"CHECK IF USER ACCEPTED EXT STORAGE PERMISSION");
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "USER ACCEPTED EXT STORAGE PERMISSION");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, getString(R.string.storage_permission_warning), Toast.LENGTH_LONG).show();
                    askForPermissions();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     *
     */
    public static void startTutorial(){
        if(showcaseView!=null)
            showcaseView.hide();
        showcaseView = new ShowcaseView.Builder((Activity)mContext,true)
//                .withMaterialShowcase()
                .setContentTitle(mContext.getString(R.string.tutorial_ui_part1_title))
                .setContentText(mContext.getString(R.string.tutorial_ui_part1_content))
//                .blockAllTouches()
//                .setTarget(target)
                .setStyle(R.style.ShowcaseTheme_very_dark)
                .build();
        showcaseView.setButtonText(mContext.getString(R.string.tutorial_ui_part1_button));
//        new ShowcaseView.Builder(this, true) .setTarget(viewTarget) .build();
//        showcaseView.setLayoutPosition(params);
//        showcase = showcaseView;
        showcaseView.show();
        showcaseView.overrideButtonClick(new View.OnClickListener() {
            int count1 = 0;

            @Override
            public void onClick(View v) {
                count1++;
                switch (count1) {
                    case 1:
                        gridLayout.scrollTo(0, gridLayout.getBottom());
                        //Add "OG" subapp temporarily at the end
                        if(gridLayout.getChildCount()%2==0){
                            addView("OGtut");
                        }
                        else {
                            addView("OG");
                            addView("OGtut");
                        }
                        showcaseView.setButtonText(mContext.getString(R.string.tutorial_ui_part2_button));
                        showcaseView.setContentTitle(mContext.getString(R.string.tutorial_ui_part2_title));
                        showcaseView.setContentText(mContext.getString(R.string.tutorial_ui_part2_content));
                        showcaseView.setStyle(R.style.ShowcaseTheme_bit_dark);
                        break;
                    case 2:
                        showcaseView.setContentTitle(mContext.getString(R.string.tutorial_ui_part3_title));
                        showcaseView.setContentText(mContext.getString(R.string.tutorial_ui_part3_content));
                        Target target3 = new ViewTarget(R.id.OG_id_tut, (Activity)mContext);
                        showcaseView.setShowcase(target3, false);
                        showcaseView.hideButton();
                        showcaseView.setStyle(R.style.ShowcaseTheme_dark);
                        break;
                    case 3:
//                        SharedPreferences at_preferences =  mContext.getSharedPreferences(UI_PREFS_NAME, 0);
//                        SharedPreferences.Editor editor = at_preferences.edit();
//                        editor.putBoolean("at_tutorial_completed",true);
//                        editor.apply();
                        initSandBox();
                        gridLayout.scrollTo(0,0);
                        showcaseView.hide();
                        break;
                }
            }
        });
    }


    /**
     * FOR THE JURNEY VIEW
     */
    // convenience class for storing all kinds of instances
    public class JourneyStore {

        public Assets assets;
        public DataManager data;
        public SizeCalculator sizes;
        public JourneyView journeyView;
        public ui_MainActivity activity;

        public JourneyStore(ui_MainActivity activity, Assets assets, SizeCalculator sizes) {
            this.assets = assets;
            this.data = null;
            this.sizes = sizes;
            this.activity = activity;
        }

        public void setDataManager(DataManager data) {
            this.data = data;
        }
        public void setJourneyView(JourneyView view) { this.journeyView = view; data.getState().setJourneyView(view); }
        public RecyclerView getRecyclerView() { return this.data.getState().getRecyclerView(); }
    }

    private static String jsonFile = "journey/journeyassets.json";
    JourneyStore store;


    protected void initJurneyView(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SizeCalculator sizes = new SizeCalculator(this);
        Assets assets = new Assets(this, jsonFile, sizes);
        this.store = new ui_MainActivity.JourneyStore(this, assets, sizes);
        DataManager data = new DataManager(this.store);
        store.setDataManager(data);
        this.setup();
    }

    private void setup() {
        JourneyView view = new JourneyView(this, this.store);
        LinearLayout ll_nv_journey_view = (LinearLayout) findViewById(R.id.ll_nv_journey_view);
        ll_nv_journey_view.addView(view);
//        setContentView(view);
    }

//    public void startPopupActivityForRewardEvent(RewardEvent e) {
//
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
//        if (prev != null) {
//            ft.remove(prev);
//        }
//        ft.addToBackStack(null);
//
//        // Create and show the dialog.
//        DialogFragment newFragment = JourneyRewardPopupDialog.newInstance(e);
//        newFragment.show(ft, "dialog");
//    }
}
