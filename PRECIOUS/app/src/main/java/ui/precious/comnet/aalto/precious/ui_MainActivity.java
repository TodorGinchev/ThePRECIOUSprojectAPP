//Documentation for android custom views:
//      http://android-developers.blogspot.fi/2015/05/android-design-support-library.html
//Documentation for action bar configuration:
//      http://blog.rhesoft.com/2015/03/30/tutorial-android-actionbar-with-material-design-and-search-field/

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
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Vector;

import aalto.comnet.thepreciousproject.R;
import food_diary.precious.comnet.aalto.fd_MainActivity;
import sql_db.precious.comnet.aalto.DBHelper;


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
    final private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 124;
    private SharedPreferences uploader_preferences;
    public static Context mContext;
    public static DBHelper dbhelp;

    public static String [] boxOrganizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;
        initDBhelper();
        uploader_preferences = this.getSharedPreferences(UP_PREFS_NAME, 0);
        //If Android version >=5.0, set status bar background color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(0x000000);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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

        if(uploader_preferences.getString("nickname","").equals("?"))
            actionBar.setTitle(getString(R.string.toolbar_name).concat(" ").concat(getString(R.string.not_logged_in)).concat("!"));
        else
            actionBar.setTitle(getString(R.string.toolbar_name).concat(" ").concat(uploader_preferences.getString("nickname","")).concat("!"));

        initSandBox();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        //Start location services for activity recognition
        Log.i("autostart recognition", "yes");
        uiUtils.firstStartConfig(this);

    }

    @Override
    public void onResume(){
        super.onResume();
        askForPermissions();
        //Check if user has logged in
        if(  !(uploader_preferences.getBoolean("isUserLoggedIn",false)) ) {
            dbhelp.dropAllTables();
            Intent i2 = new Intent(this,onboarding.precious.comnet.aalto.obMainActivity.class);
            this.startActivityForResult(i2, ONBOARDING_RESULT_CODE);
        }
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
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.report_problem_template_content));
            startActivity(Intent.createChooser(intent, "Send Email"));
        }
        else if (id == R.id.nav_logout) {
            SharedPreferences preferences = this.getSharedPreferences(UP_PREFS_NAME, 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isUserLoggedIn", false);
            editor.putString("email", "?");
            editor.putString("password", "?");
            editor.putString("weight", "?");
            editor.putString("height", "?");
            editor.putString("activityClass", "?");
            editor.putString("nickname", "?");
            editor.putString("birthdate", "?");
            editor.putString("gender", "?");
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

    private GridLayout gridLayout;
    private int LayoutWidth;
    private int BoxMargins;
    private int SB_cols=2;
    private int SB_rows=50;//TODO, very important parameter!!!
    private int SB_current_rows=0;
    private int SB_current_half_row=0;
    private int SB_current_half_col=0;
    private Vector<ImageView> SBelements = new Vector<ImageView>();

    void initSandBox() {
        SharedPreferences preferences_up = this.getSharedPreferences(UP_PREFS_NAME, 0);
        int groupID=preferences_up.getInt("group_ID", -1);

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
        boolean seven_days_passed=System.currentTimeMillis() > (c_aux.getTimeInMillis()+7*24*3600*1000);
        String nickname = uploader_preferences.getString("nickname","-1");
        int nicknameID=-1;
        try{
            nicknameID = Integer.parseInt(nickname);
        }catch (Exception e){
            nicknameID=-1;
            Log.i(TAG," ",e);
        }



        Log.i(TAG,"GroupID="+groupID);
        if(groupID==130 || nicknameID==130){
            //Fruit and Vegetable challenge- Motivation off after 7 days
            if(seven_days_passed){
                boxOrganizer = new String[]{"DC", "SM", "MF", "UP"};
                //TODO
            }
            else{
                boxOrganizer = new String[]{"OG", "IR", "DC", "SM", "MF", "UP"};
                //TODO
            }
        }
        else if(groupID==678|| nicknameID==678){
            //Fruit and Vegetable challenge- Motivation on after 7 days
            if(seven_days_passed){
                boxOrganizer = new String[]{"OG", "IR", "DC", "SM", "MF", "UP"};
                //TODO
            }
            else{
                boxOrganizer = new String[]{"DC", "SM", "MF", "UP"};
                //TODO
            }
        }
        else if(groupID==387 || nicknameID==387){
            //Diary- Motivation off after 7 days
            if(seven_days_passed){
                boxOrganizer = new String[]{ "SM", "MF", "MD", "UP"};
                //TODO
            }
            else{
                boxOrganizer = new String[]{"OG", "IR", "SM", "MF", "MD", "UP"};
                //TODO
            }
        }
        else if(groupID==827 || nicknameID==827){
            //Diary- Motivation on after 7 days
            if(seven_days_passed){
                boxOrganizer = new String[]{"OG", "IR", "SM", "MF", "MD", "UP"};
                //TODO
            }
            else{
                boxOrganizer = new String[]{ "SM", "MF", "MD", "UP"};
                //TODO
            }
        }
        else{
                boxOrganizer = new String[]{"OG", "IR", "SM", "MF","DC", "MD", "UP"};
        }


        gridLayout = (GridLayout) findViewById(R.id.grid_layout);
        gridLayout.removeAllViews();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        LayoutWidth = size.x;
        BoxMargins = LayoutWidth / 50;
        LayoutWidth = LayoutWidth - 3 * BoxMargins;
        gridLayout.setPadding(0, 0, BoxMargins / 2, BoxMargins);
        gridLayout.setColumnCount(SB_cols);
        gridLayout.setRowCount(SB_rows + 1);
        gridLayout.setVerticalScrollBarEnabled(true);

        SharedPreferences ui_preferences = this.getSharedPreferences(UI_PREFS_NAME, 0);
        if(ui_preferences.getBoolean("OGset",false))
//        Log.i(TAG,boxOrganizer[0]+"_"+boxOrganizer[1]+"_"+boxOrganizer[2]+"_"+boxOrganizer[3]+"_"+boxOrganizer[4]+"_"+boxOrganizer[5]+"_"+boxOrganizer[6]);
            moveSBtoEnd("OG");
//        Log.i(TAG, boxOrganizer[0] + "_" + boxOrganizer[1] + "_" + boxOrganizer[2] + "_" + boxOrganizer[3] + "_" + boxOrganizer[4] + "_" + boxOrganizer[5] + "_" + boxOrganizer[6]);
        if(ui_preferences.getBoolean("IRset",false))
            moveSBtoEnd("IR");

        moveSBtoEnd("DB");


        for (int i=0; i<boxOrganizer.length;i++)
            addView(boxOrganizer[i]);
    }

    void addSBelement (int resourceID, int relativeWidth, final Class activity){
        ImageView im = new ImageView(this);
        //im.setBackgroundColor(Color);
         im.setImageResource(resourceID);
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
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

        //Set onClick event
        im.setClickable(true);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.equals(firstbeat.precious.comnet.aalto.fbMainActivity.class)) {
                    uploader.precious.comnet.aalto.upUtils.setContext(mContext);
                    uploader.precious.comnet.aalto.upUtils.getBGimage("/data?key=BG2_REPORT_IMAGE&query=1");
                } else {
                    Intent i = new Intent(v.getContext(), activity);
                    startActivity(i);
                }
            }
        });
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


    public void addView(String name){
        switch (name){
            case "OG": addSBelement (R.drawable.outcome_goal, 1, outcomegoal.precious.comnet.aalto.outcomegoal_activity.class);break;
            case "IR": addSBelement(R.drawable.importance_ruler, 1, importance_ruler.precious.comnet.aalto.ImportanceRulerActivity.class);break;
            case "DC": addSBelement(R.drawable.diet_challenges, 1, diet_challenges.precious.comnet.aalto.fi.dc_MainActivity.class);break;
            case "SM": addSBelement(R.drawable.self_monitoring, 2, activity_tracker.precious.comnet.aalto.MountainViewActivity.class);break;
            case "FA": addSBelement(R.drawable.my_favourites, 1, outcomegoal.precious.comnet.aalto.outcomegoal_activity.class);break;
            case "MD": addSBelement(R.drawable.my_food_diary, 2, fd_MainActivity.class);break;
            case "DB": addSBelement(R.drawable.debug, 1, ui.precious.comnet.aalto.precious.Timeline.class);break;
            case "UP": addSBelement(R.drawable.uploader, 1, firstbeat.precious.comnet.aalto.fbMainActivity.class);break;
            default: break;
        }
    }

    public void moveSBtoEnd (String name){
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

    public static void initDBhelper(){


        dbhelp = new DBHelper(mContext);

    }

    /**
     *
     */
    @Override protected void onActivityResult (int requestCode,
                                               int resultCode, Intent data){
        if (requestCode== ONBOARDING_RESULT_CODE && resultCode==RESULT_OK) {
            if(data.getExtras().getBoolean("close_activity")) {
                Log.i(TAG,"finished");
                finish();
            }
        }
    }

    public void askForPermissions() {
// Here, thisActivity is the current activity

        boolean hasPermission = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

        if(hasPermission){
            Log.i(TAG,"WRITE_EXTERNAL_STORAGE permission granted");
        }
        else{

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions((Activity)this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

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
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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
}
