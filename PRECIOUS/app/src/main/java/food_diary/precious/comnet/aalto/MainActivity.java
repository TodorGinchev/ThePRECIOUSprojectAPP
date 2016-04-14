package food_diary.precious.comnet.aalto;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.Calendar;

import aalto.comnet.thepreciousproject.R;
import activity_tracker.precious.comnet.aalto.atUtils;


public class MainActivity extends AppCompatActivity {

    public static TextView tvDayWeek ;
    public static TextView tvDayMonth;
    public static TextView tvMonthYear;
    private static Context mContext;
    private static long selectedDay;
    private static TabHost host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fd_main_activity);
        mContext = this;
        //If Android version >=5.0, set status bar background color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.foodDiary));
        }

        //Set toolbar title and icons
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.food_diary_title));
        toolbar.setTitleTextColor(getResources().getColor(R.color.foodDiary));
        toolbar.setNavigationIcon(R.drawable.fd_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Init Tab host
        host = (TabHost)findViewById(R.id.tabHost);
        host.setup();


        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec(getResources().getString(R.string.photo_stream));
        spec.setContent(R.id.tab1);
        spec.setIndicator(getResources().getString(R.string.photo_stream));
        host.addTab(spec);
        //Tab 2
        spec = host.newTabSpec(getResources().getString(R.string.daily_view));
        spec.setContent(R.id.tab2);
        spec.setIndicator(getResources().getString(R.string.daily_view));
        host.addTab(spec);
        //Tab 3
        spec = host.newTabSpec(getResources().getString(R.string.weekly_view));
        spec.setContent(R.id.tab3);
        spec.setIndicator(getResources().getString(R.string.weekly_view));
        host.addTab(spec);
        //Tab 4
        spec = host.newTabSpec(getResources().getString(R.string.monthly_view));
        spec.setContent(R.id.tab4);
        spec.setIndicator(getResources().getString(R.string.monthly_view));
        host.addTab(spec);
        //Set tab titles to lowercase
        for (int i=0;i<host.getTabWidget().getChildCount();i++) {
            TextView tv = (TextView) host.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setAllCaps(false);
            tv.setTypeface(null, Typeface.NORMAL);
            host.getTabWidget().getChildAt(i).setBackgroundColor(
                    getResources().getColor(R.color.fd_unselected_tab_background)); //unselected
        }
        host.getTabWidget().getChildAt(host.getCurrentTab()).setBackgroundColor(
                getResources().getColor(R.color.fd_selected_tab_background)); // selected
        //Set TabHost listener
        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                MainActivity.updateView (tabId);
            }
        });
        //Declare TextViews
        tvDayWeek =((TextView) findViewById(R.id.textViewDayWeek));
        tvDayMonth =((TextView) findViewById(R.id.textViewDayMonth));
        tvMonthYear =((TextView) findViewById(R.id.textViewMonthYear));

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        setTvDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH));
    }

    public static Context getContext(){
        return mContext;
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            MainActivity.setTvDate(year,month+1,day);
        }
    }
    public void openDatePicker(View v){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static void setTvDate(int year, int month, int dayOfMonth){
        String monthYear = atUtils.getMonth(MainActivity.getContext(), ""+month).concat(",  ").concat("" + year);
        tvDayMonth.setText(""+dayOfMonth);
        tvMonthYear.setText(monthYear);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH,month-1);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        c.set(Calendar.YEAR, year);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        String sDayWeek="";
        switch (dayOfWeek){
            case 2	:	sDayWeek=mContext.getString(R.string.monday);break;
            case 3	:	sDayWeek=mContext.getString(R.string.tuesday);break;
            case 4	:	sDayWeek=mContext.getString(R.string.wednesday);break;
            case 5	:	sDayWeek=mContext.getString(R.string.thursday);break;
            case 6	:	sDayWeek=mContext.getString(R.string.friday);break;
            case 7	:	sDayWeek=mContext.getString(R.string.saturday);break;
            case 1	:	sDayWeek=mContext.getString(R.string.sunday);break;
            default	:	sDayWeek=null;break;
        }
        tvDayWeek.setText(""+sDayWeek);

        //Set selected day
        MainActivity.setSelectedDay(c.getTimeInMillis());
    }

    public static long getSelectedDay(){
        return selectedDay;
    }
    public static void setSelectedDay( long date){
        selectedDay = date;
    }

    public void setPreviousDay (View v){
        long time = MainActivity.getSelectedDay()-24*3600*1000;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        MainActivity.setTvDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH));
    }

    public void setNextDay (View v){
        long time = MainActivity.getSelectedDay()+24*3600*1000;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        MainActivity.setTvDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH));
    }

    public static void updateView( String selecteTab){
        for (int i=0;i<host.getTabWidget().getChildCount();i++) {
            TextView tv = (TextView) host.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setAllCaps(false);
            tv.setTypeface(null, Typeface.NORMAL);
            host.getTabWidget().getChildAt(i).setBackgroundColor(
                    mContext.getResources().getColor(R.color.fd_unselected_tab_background)); //unselected
        }
        host.getTabWidget().getChildAt(host.getCurrentTab()).setBackgroundColor(
                mContext.getResources().getColor(R.color.fd_selected_tab_background)); // selected
    }
}

