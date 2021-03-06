package importance_ruler.precious.comnet.aalto;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.CirclePageIndicator;

import aalto.comnet.thepreciousproject.R;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;


public class ImportanceRulerActivity extends AppCompatActivity {
    public static final String TAG = "ImportanceRulerActivity";
    public static Context appConext;
    public static final String PREFS_NAME = "IRsubappPreferences";
    public static final String OG_PREFS_NAME = "OGsubappPreferences";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private IRFragmentAdapter mAdapter;

    public static final String UI_PREFS_NAME = "UIPreferences";

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ir_activity);
        //If Android version >=5.0, set status bar background color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.importanceRuler));
        }
        appConext=getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        //setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.imporance_ruler_title));
        toolbar.setTitleTextColor(getResources().getColor(R.color.importanceRuler));


        toolbar.setNavigationIcon(R.drawable.ir_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mAdapter = new IRFragmentAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(appConext);
                    Intent i = new Intent("IR2_REFRESH");
                    lbm.sendBroadcast(i);
                }
                else if (position == 3) {
                    LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(appConext);
                    Intent i = new Intent("IR4_REFRESH");
                    lbm.sendBroadcast(i);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        CirclePageIndicator titleIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        titleIndicator.setViewPager(mViewPager);
    }

    @Override
    protected void onPause() {
        //Store app usage
        try {
            sql_db.precious.comnet.aalto.DBHelper.getInstance().insertAppUsage(System.currentTimeMillis(), TAG, "onPause");

            SharedPreferences ir_preferences = ImportanceRulerActivity.appConext.getSharedPreferences(PREFS_NAME, 0);
            sql_db.precious.comnet.aalto.DBHelper.getInstance().insertAppUsage(System.currentTimeMillis(), "ir_seekbarProgress", Integer.toString(ir_preferences.getInt("IRseekbarProgress", -2)+1));

            SharedPreferences og_preferences = ImportanceRulerActivity.appConext.getSharedPreferences(OG_PREFS_NAME, 0);
            sql_db.precious.comnet.aalto.DBHelper.getInstance().insertAppUsage(System.currentTimeMillis(), "ir_preferredBox1", Integer.toString(og_preferences.getInt("preferredBoxIR1", -1)));

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
            sql_db.precious.comnet.aalto.DBHelper.getInstance().insertAppUsage(System.currentTimeMillis(), "outcomegoal_activity", "onResume");
        }catch (Exception e) {
            Log.e(TAG, " ", e);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.ir_activity, container, false);
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
                case 3:
                    return "SECTION 4";
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        int position = mViewPager.getCurrentItem();
        if(position==0)
            finish();
        else
            mViewPager.setCurrentItem(position-1);
    }

    public void closeView(View v){
        SharedPreferences preferences = this.getSharedPreferences(UI_PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("IRset",true);
        editor.apply();
        finish();
    }
}
