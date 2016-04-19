//Documentation for android custom views:
//      http://android-developers.blogspot.fi/2015/05/android-design-support-library.html
//Documentation for action bar configuration:
//      http://blog.rhesoft.com/2015/03/30/tutorial-android-actionbar-with-material-design-and-search-field/

package ui.precious.comnet.aalto.precious;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import android.widget.TextView;

import java.util.Vector;

import aalto.comnet.thepreciousproject.R;
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
    private SharedPreferences preferences;
    public static Context mContext;
    public static DBHelper dbhelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;
        dbhelp = new DBHelper(mContext);
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
        ImageView iv_profile = (ImageView) header.findViewById(R.id.imageViewProfile);
        iv_profile.setImageResource(R.drawable.profile);
        TextView tv_username = (TextView) header.findViewById(R.id.textViewNavDrawUsername);
        tv_username.setText("Mr. Anderson");
        TextView tv_location = (TextView) header.findViewById(R.id.textViewNavDrawLocation);
        tv_location.setText("Espoo, Finland");
        
        //Change toolbar title
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        //actionBar.setDisplayShowTitleEnabled(true);
        preferences = this.getSharedPreferences(UP_PREFS_NAME, 0);
        actionBar.setTitle(getString(R.string.toolbar_name).concat(" ").concat(preferences.getString("nickname","")).concat("!"));

        initSandBox();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        //Start location services for activity recognition
        Log.i("autostart recognition", "yes");
        uiUtils.firstStartConfig(this);

        //Check if user has logged in

        if(  !(preferences.getBoolean("isUserLoggedIn",false)) ) {
            Intent i2 = new Intent(this,onboarding.precious.comnet.aalto.obMainActivity.class);
            this.startActivity(i2);
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
            // Handle the camera action
        } else if (id == R.id.nav_feedback) {

        } else if (id == R.id.nav_logout) {
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

        } else if (id == R.id.nav_manage) {

        }

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

        //LayoutWidth=100;
        addSBelement (R.drawable.outcome_goal, 1, outcomegoal.precious.comnet.aalto.outcomegoal_activity.class);
        addSBelement(R.drawable.importance_ruler, 1, importance_ruler.precious.comnet.aalto.ImportanceRulerActivity.class);
        addSBelement(R.drawable.self_monitoring, 2, activity_tracker.precious.comnet.aalto.MountainViewActivity.class);
        addSBelement(R.drawable.my_favourites, 1, outcomegoal.precious.comnet.aalto.outcomegoal_activity.class);
        addSBelement(R.drawable.my_day, 1, food_diary.precious.comnet.aalto.MainActivity.class);
        addSBelement(R.drawable.debug, 1, ui.precious.comnet.aalto.precious.Timeline.class);
        addSBelement(R.drawable.uploader, 1, firstbeat.precious.comnet.aalto.fbMainActivity.class);
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
                if(activity.equals(firstbeat.precious.comnet.aalto.fbMainActivity.class)) {
                    uploader.precious.comnet.aalto.upUtils.setContext(mContext);
                    uploader.precious.comnet.aalto.upUtils.getBGimage("/data?key=BG2_REPORT_IMAGE&query=1");
                }
                else {
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


    /**
     *
     */
    public Point getDisplaySize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }
}
