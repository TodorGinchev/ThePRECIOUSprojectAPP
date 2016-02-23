package ui.precious.comnet.aalto.precious;

import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Set username and location
        //View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
        //navigationView.addHeaderView(header);
        View header = navigationView.getHeaderView(0);
        ImageView iv_profile = (ImageView) header.findViewById(R.id.imageViewProfile);
        iv_profile.setImageResource(R.drawable.profile);
        TextView tv_username = (TextView) header.findViewById(R.id.textViewNavDrawUsername);
        tv_username.setText("Mr. Anderson");
        TextView tv_location = (TextView) header.findViewById(R.id.textViewNavDrawLocation);
        tv_location.setText("Espoo, Finland");

        initSandBox();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void initSandBox() {
        GridLayout gridLayout = (GridLayout) findViewById(R.id.grid_layout);
        gridLayout.removeAllViews();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int LayoutWidth = size.x;
        int BoxMargins = LayoutWidth / 50;
        LayoutWidth = LayoutWidth - 4 * BoxMargins;
        gridLayout.setPadding(0, 0, BoxMargins / 2, 0);
        int total = 24;//TODO, very important parameter!!!
        int column = 3;
        int row = total / column;
        gridLayout.setColumnCount(column);
        gridLayout.setRowCount(row + 1);
        gridLayout.setVerticalScrollBarEnabled(true);


        //LayoutWidth=100;

        ImageView im1 = new ImageView(this);
        im1.setBackgroundColor(Color.RED);
        int relativeWidth = 1;
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.width = relativeWidth * LayoutWidth / column;
        param.height = LayoutWidth / column;
        param.setMargins(BoxMargins, BoxMargins, 0, 0);
        param.columnSpec = GridLayout.spec(0, relativeWidth);
        param.rowSpec = GridLayout.spec(0);
        param.setGravity(Gravity.CENTER);
        im1.setLayoutParams(param);
        gridLayout.addView(im1);

        ImageView im2 = new ImageView(this);
        im2.setBackgroundColor(Color.BLUE);
        relativeWidth = 2;
        param = new GridLayout.LayoutParams();
        param.width = relativeWidth * LayoutWidth / column;
        param.height = LayoutWidth / column;
        param.setMargins(BoxMargins, BoxMargins, 0, 0);
        param.columnSpec = GridLayout.spec(1, relativeWidth);
        param.rowSpec = GridLayout.spec(0);
        param.setGravity(Gravity.CENTER);
        im2.setLayoutParams(param);
        gridLayout.addView(im2);

        ImageView im3 = new ImageView(this);
        im3.setBackgroundColor(Color.CYAN);
        relativeWidth = 2;
        param = new GridLayout.LayoutParams();
        param.width = relativeWidth * LayoutWidth / column;
        param.height = LayoutWidth / column;
        param.setMargins(BoxMargins, BoxMargins, 0, 0);
        param.columnSpec = GridLayout.spec(0, relativeWidth);
        param.rowSpec = GridLayout.spec(1);
        param.setGravity(Gravity.CENTER);
        im3.setLayoutParams(param);
        gridLayout.addView(im3);

        ImageView im4 = new ImageView(this);
        im4.setBackgroundColor(Color.GREEN);
        relativeWidth = 1;
        param = new GridLayout.LayoutParams();
        param.width = relativeWidth * LayoutWidth / column;
        param.height = LayoutWidth / column;
        param.setMargins(BoxMargins, BoxMargins, 0, 0);
        param.columnSpec = GridLayout.spec(2, relativeWidth);
        param.rowSpec = GridLayout.spec(1);
        param.setGravity(Gravity.CENTER);
        im4.setLayoutParams(param);
        gridLayout.addView(im4);

        ImageView im5 = new ImageView(this);
        im5.setBackgroundColor(Color.YELLOW);
        relativeWidth = 3;
        param = new GridLayout.LayoutParams();
        param.width = relativeWidth * LayoutWidth / column;
        param.height = LayoutWidth / column;
        param.setMargins(BoxMargins, BoxMargins, 0, 0);
        param.columnSpec = GridLayout.spec(0, relativeWidth);
        param.rowSpec = GridLayout.spec(2);
        param.setGravity(Gravity.CENTER);
        im5.setLayoutParams(param);
        gridLayout.addView(im5);


        ImageView im6 = new ImageView(this);
        im6.setBackgroundColor(Color.YELLOW);
        relativeWidth = 3;
        param = new GridLayout.LayoutParams();
        param.width = relativeWidth * LayoutWidth / column;
        param.height = LayoutWidth / column;
        param.setMargins(BoxMargins, BoxMargins, 0, 0);
        param.columnSpec = GridLayout.spec(0, relativeWidth);
        param.rowSpec = GridLayout.spec(3);
        param.setGravity(Gravity.CENTER);
        im6.setLayoutParams(param);
        gridLayout.addView(im6);


        ImageView im7 = new ImageView(this);
        im7.setBackgroundColor(Color.YELLOW);
        relativeWidth = 3;
        param = new GridLayout.LayoutParams();
        param.width = relativeWidth * LayoutWidth / column;
        param.height = LayoutWidth / column;
        param.setMargins(BoxMargins, BoxMargins, 0, 0);
        param.columnSpec = GridLayout.spec(0, relativeWidth);
        param.rowSpec = GridLayout.spec(4);
        param.setGravity(Gravity.CENTER);
        im7.setLayoutParams(param);
        gridLayout.addView(im7);


        ImageView im8 = new ImageView(this);
        im8.setBackgroundColor(Color.YELLOW);
        relativeWidth = 3;
        param = new GridLayout.LayoutParams();
        param.width = relativeWidth * LayoutWidth / column;
        param.height = LayoutWidth / column;
        param.setMargins(BoxMargins, BoxMargins, 0, 0);
        param.columnSpec = GridLayout.spec(0, relativeWidth);
        param.rowSpec = GridLayout.spec(5);
        param.setGravity(Gravity.CENTER);
        im8.setLayoutParams(param);
        gridLayout.addView(im8);


        ImageView im9 = new ImageView(this);
        im9.setBackgroundColor(Color.YELLOW);
        relativeWidth = 3;
        param = new GridLayout.LayoutParams();
        param.width = relativeWidth * LayoutWidth / column;
        param.height = LayoutWidth / column;
        param.setMargins(BoxMargins, BoxMargins, 0, 0);
        param.columnSpec = GridLayout.spec(0, relativeWidth);
        param.rowSpec = GridLayout.spec(6);
        param.setGravity(Gravity.CENTER);
        im9.setLayoutParams(param);
        gridLayout.addView(im9);


//        for(int i =0, c = 0, r = 0; i < total; i++, c++)
//        {
//            if(c == column)
//            {
//                c = 0;
//                r++;
//            }
//            ImageView oImageView = new ImageView(this);
//            oImageView.setImageResource(R.drawable.ic_menu_camera);
//            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
//            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
//            param.width = GridLayout.LayoutParams.WRAP_CONTENT;
//            param.rightMargin = 5;
//            param.topMargin = 5;
//            param.setGravity(Gravity.CENTER);
//            param.columnSpec = GridLayout.spec(c);
//            param.rowSpec = GridLayout.spec(r);
//            oImageView.setLayoutParams (param);
//            gridLayout.addView(oImageView);
//        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://ui.precious.comnet.aalto.precious/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://ui.precious.comnet.aalto.precious/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
