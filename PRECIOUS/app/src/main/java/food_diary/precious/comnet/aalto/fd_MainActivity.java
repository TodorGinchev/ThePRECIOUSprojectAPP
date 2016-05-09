package food_diary.precious.comnet.aalto;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.Calendar;

import aalto.comnet.thepreciousproject.R;
import activity_tracker.precious.comnet.aalto.atUtils;


public class fd_MainActivity extends AppCompatActivity {

    public static final String TAG = "fd_MainActivity";
    public static TextView tvDayWeek ;
    public static TextView tvDayMonth;
    public static TextView tvMonthYear;
    private static Context mContext;
    private static long selectedDay;
    private static TabHost host;

    private static ListView lvBreakfast;
    private static ListView lvMorSnack;
    private static ListView lvLunch;
    private static ListView lvEveSnack;
    private static ListView lvDinner;

    private boolean fab_show = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fd_main_activity);
        mContext = this;
        //If Android version >=5.0, set status bar background color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.foodDiary));
        }
        // Floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_food_diary);
        final FloatingActionButton fab_1 = (FloatingActionButton) findViewById(R.id.fab_food_diary_1);
        final FloatingActionButton fab_2 = (FloatingActionButton) findViewById(R.id.fab_food_diary_2);
        final FloatingActionButton fab_3 = (FloatingActionButton) findViewById(R.id.fab_food_diary_3);
        final FloatingActionButton fab_4 = (FloatingActionButton) findViewById(R.id.fab_food_diary_4);
        final FloatingActionButton fab_5 = (FloatingActionButton) findViewById(R.id.fab_food_diary_5);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(fab_show) {
//                    fab_1.setVisibility(View.VISIBLE);
//                    fab_2.setVisibility(View.VISIBLE);
////                    fab_3.setVisibility(View.VISIBLE);
////                    fab_4.setVisibility(View.VISIBLE);
////                    fab_5.setVisibility(View.VISIBLE);
//                    fab_show=false;
//                }
//                else{
//                    fab_1.setVisibility(View.GONE);
//                    fab_2.setVisibility(View.GONE);
////                    fab_3.setVisibility(View.GONE);
////                    fab_4.setVisibility(View.GONE);
////                    fab_5.setVisibility(View.GONE);
//                    fab_show=true;
//                }
                startActivity(new Intent(fd_MainActivity.getContext(),fd_SelectFood.class));
            }
        });

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
                fd_MainActivity.updateView(tabId);
            }
        });
        //Declare TextViews
        tvDayWeek =((TextView) findViewById(R.id.textViewDayWeek));
        tvDayMonth =((TextView) findViewById(R.id.textViewDayMonth));
        tvMonthYear =((TextView) findViewById(R.id.textViewMonthYear));
        //Declare ListViews
        lvBreakfast = (ListView) findViewById(R.id.listViewBreakfast);
        //This listener is to disable crolling in the listview, allowing in the parent (scrollview)
//        lvBreakfast.setOnTouchListener(new View.OnTouchListener() {
//            // Setting on Touch Listener for handling the touch inside ScrollView
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // Disallow the touch request for parent scroll on touch of child view
//                v.getParent().requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });
//        View header = getLayoutInflater().inflate(R.layout.header, null);
//        View footer = getLayoutInflater().inflate(R.layout.footer, null);
//        listView.addHeaderView(header);
//        listView.addFooterView(footer);
        lvMorSnack = (ListView) findViewById(R.id.listViewMorningSnack);
        lvLunch = (ListView) findViewById(R.id.listViewLunch);
        lvEveSnack = (ListView) findViewById(R.id.listViewEveningSnack);
        lvDinner = (ListView) findViewById(R.id.listViewDinner);
        //TODO more
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
            fd_MainActivity.setTvDate(year, month + 1, day);
        }
    }
    public void openDatePicker(View v){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static void setTvDate(int year, int month, int dayOfMonth){
        String monthYear = atUtils.getMonth(fd_MainActivity.getContext(), ""+month).concat(",  ").concat("" + year);
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
        fd_MainActivity.setSelectedDay(c.getTimeInMillis());
    }

    public static long getSelectedDay(){
        return selectedDay;
    }
    public static void setSelectedDay( long date){
        selectedDay = date;
    }

    public void setPreviousDay (View v){
        long time = fd_MainActivity.getSelectedDay()-24*3600*1000;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        fd_MainActivity.setTvDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }

    public void setNextDay (View v){
        long time = fd_MainActivity.getSelectedDay()+24*3600*1000;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        fd_MainActivity.setTvDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
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


        if(selecteTab.equals("Daily view")){
            //Breakfast
            Log.i(TAG, selecteTab);
            String[] foodNameBreakfast={"Pancakes","Eggs with bacon","Milk"};
            String[] foodDescriptionBreakfast={"With honey and cream","Not healthy"," "};
            String[] foodCuantityBreakfast={"300g","100g","300ml"};
            String[] foodCuantityKcalBreakfast={"700Kcal","300Kcal","100Kcal"};
            fd_FoodListAdapter Breakfast_adapter = new fd_FoodListAdapter(mContext, foodNameBreakfast,
                    foodDescriptionBreakfast, foodCuantityBreakfast,foodCuantityKcalBreakfast);
            lvBreakfast.setAdapter(Breakfast_adapter);
            setListViewHeightBasedOnChildren(lvBreakfast);
            //Morning snack
            String[] foodNameMorSnack={"French fries"};
            String[] foodDescriptionMorSnack={"My favourite"};
            String[] foodCuantityMorSnack={"1000g"};
            String[] foodCuantityKcalMorSnack={"11000Kcal"};
            fd_FoodListAdapter adapterMorSnack = new fd_FoodListAdapter(mContext, foodNameMorSnack,
                    foodDescriptionMorSnack, foodCuantityMorSnack,foodCuantityKcalMorSnack);
            lvMorSnack.setAdapter(adapterMorSnack);
            setListViewHeightBasedOnChildren(lvMorSnack);
            //Lunch
            String[] foodNameLunch={"Paella"};
            String[] foodDescriptionLunch={"My favourite too"};
            String[] foodCuantityLunch={"300g"};
            String[] foodCuantityKcalLunch={"500Kcal"};
            fd_FoodListAdapter adapterLunch = new fd_FoodListAdapter(mContext, foodNameLunch,
                    foodDescriptionLunch, foodCuantityLunch,foodCuantityKcalLunch);
            lvLunch.setAdapter(adapterLunch);
            setListViewHeightBasedOnChildren(lvLunch);
            //Evening snack
            String[] foodNameEveSnack={};
            String[] foodDescriptionEveSnack={};
            String[] foodCuantityEveSnack={};
            String[] foodCuantityKcalEveSnack={};
            fd_FoodListAdapter adapterEveSnack = new fd_FoodListAdapter(mContext, foodNameEveSnack,
                    foodDescriptionEveSnack, foodCuantityEveSnack,foodCuantityKcalEveSnack);
            lvEveSnack.setAdapter(adapterEveSnack);
            setListViewHeightBasedOnChildren(lvEveSnack);
            //Dinner
            String[] foodNameDinner={"Chicken soup"};
            String[] foodDescriptionDinner={"Healthy"};
            String[] foodCuantityDinner={"200g"};
            String[] foodCuantityKcalDinner={"500Kcal"};
            fd_FoodListAdapter adapterDinner = new fd_FoodListAdapter(mContext, foodNameDinner,
                    foodDescriptionDinner, foodCuantityDinner,foodCuantityKcalDinner);
            lvDinner.setAdapter(adapterDinner);
            setListViewHeightBasedOnChildren(lvDinner);
        }
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
