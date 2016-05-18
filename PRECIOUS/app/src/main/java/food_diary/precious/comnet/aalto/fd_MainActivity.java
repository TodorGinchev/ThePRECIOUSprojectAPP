package food_diary.precious.comnet.aalto;


import android.app.Activity;
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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;

import aalto.comnet.thepreciousproject.R;
import activity_tracker.precious.comnet.aalto.atUtils;
import ui.precious.comnet.aalto.precious.ui_MainActivity;


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

    private static ArrayList<String> foodNameBreakfast;
    private static ArrayList<String> foodNameMorSnack;
    private static ArrayList<String> foodNameLunch;
    private static ArrayList<String> foodNameEveningSnack;
    private static ArrayList<String> foodNameDinner;

    private static ArrayList<String> foodCuantityBreakfast;
    private static ArrayList<String> foodCuantityMorSnack;
    private static ArrayList<String> foodCuantityLunch;
    private static ArrayList<String> foodCuantityEveningSnack;
    private static ArrayList<String> foodCuantityDinner;


//    private boolean fab_show = true;

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
//        final FloatingActionButton fab_1 = (FloatingActionButton) findViewById(R.id.fab_food_diary_1);
//        final FloatingActionButton fab_2 = (FloatingActionButton) findViewById(R.id.fab_food_diary_2);
//        final FloatingActionButton fab_3 = (FloatingActionButton) findViewById(R.id.fab_food_diary_3);
//        final FloatingActionButton fab_4 = (FloatingActionButton) findViewById(R.id.fab_food_diary_4);
//        final FloatingActionButton fab_5 = (FloatingActionButton) findViewById(R.id.fab_food_diary_5);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
////                if(fab_show) {
////                    fab_1.setVisibility(View.VISIBLE);
////                    fab_2.setVisibility(View.VISIBLE);
//////                    fab_3.setVisibility(View.VISIBLE);
//////                    fab_4.setVisibility(View.VISIBLE);
//////                    fab_5.setVisibility(View.VISIBLE);
////                    fab_show=false;
////                }
////                else{
////                    fab_1.setVisibility(View.GONE);
////                    fab_2.setVisibility(View.GONE);
//////                    fab_3.setVisibility(View.GONE);
//////                    fab_4.setVisibility(View.GONE);
//////                    fab_5.setVisibility(View.GONE);
////                    fab_show=true;
////                }
                Intent i =new Intent(fd_MainActivity.getContext(),fd_SelectFood.class);
                i.putExtra("timestamp",selectedDay);
                startActivity(i);
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
        TabHost.TabSpec spec;
//        spec = host.newTabSpec(getResources().getString(R.string.photo_stream));
//        spec.setContent(R.id.tab1);
//        spec.setIndicator(getResources().getString(R.string.photo_stream));
//        host.addTab(spec);
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
//            tv.setTextSize(getResources().getDimension(R.dimen.fd_tab_host_text_size));
            host.getTabWidget().getChildAt(i).setBackgroundColor(
                    getResources().getColor(R.color.fd_unselected_tab_background)); //unselected
        }
        host.getTabWidget().getChildAt(host.getCurrentTab()).setBackgroundColor(
                getResources().getColor(R.color.fd_selected_tab_background)); // selected
        //Set TabHost listener
        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                fd_MainActivity.updateView();
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
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        setTvDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
        selectedDay = System.currentTimeMillis();
        //TODO DO THIS IN A PROPER WAY
        host.setCurrentTab(0);
        updateView();
    }

    @Override
    public void onResume(){
        super.onResume();
        updateView();
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
            Calendar c_aux = Calendar.getInstance();
            c_aux.set(Calendar.YEAR,year);
            c_aux.set(Calendar.MONTH,month);
            c_aux.set(Calendar.DAY_OF_MONTH,day);
            setSelectedDay(c_aux.getTimeInMillis());
            updateView();
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
        setSelectedDay(time);
        updateView();
    }

    public void setNextDay (View v){
        long time = fd_MainActivity.getSelectedDay()+24*3600*1000;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        fd_MainActivity.setTvDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
        setSelectedDay(time);
        updateView();
    }

    public static void updateView(){
        for (int i=0;i<host.getTabWidget().getChildCount();i++) {
            TextView tv = (TextView) host.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setAllCaps(false);
            tv.setTypeface(null, Typeface.NORMAL);
            host.getTabWidget().getChildAt(i).setBackgroundColor(
                    mContext.getResources().getColor(R.color.fd_unselected_tab_background)); //unselected
        }
        host.getTabWidget().getChildAt(host.getCurrentTab()).setBackgroundColor(
                mContext.getResources().getColor(R.color.fd_selected_tab_background)); // selected


//        if(selecteTab.equals(mContext.getString(R.string.daily_view))){
        if(host.getCurrentTab()==0)
            updateView0();
        else if (host.getCurrentTab()==1)
            updateView1();
    }

    /**
     *
     */
    public static void updateView0(){Calendar c_aux = Calendar.getInstance();
        c_aux.setTimeInMillis(selectedDay);
        c_aux.set(Calendar.HOUR_OF_DAY, 0);
        c_aux.set(Calendar.SECOND, 0);
        c_aux.set(Calendar.MILLISECOND,0);
        ArrayList<ArrayList<Long>> foodData = ui_MainActivity.dbhelp.getFood(c_aux.getTimeInMillis(), c_aux.getTimeInMillis()+24*3600*1000);
        ArrayList<ArrayList<String>> foodDataNames = ui_MainActivity.dbhelp.getFoodNames(c_aux.getTimeInMillis(), c_aux.getTimeInMillis() + 24 * 3600 * 1000);

        if(foodData.size()!=foodDataNames.size()){
            Log.e(TAG, "FOOD DB PROBLEM: foodData.size()!=foodDataNames.size() ");
            return;
        }

        //Get food names
        String str[] = mContext.getResources().getStringArray(R.array.food_names);
        //Breakfast
        foodNameBreakfast = new ArrayList<>();
        ArrayList<String> foodDescriptionBreakfast = new ArrayList<>();
        foodCuantityBreakfast = new ArrayList<>();
        ArrayList<String> foodCuantityKcalBreakfast = new ArrayList<>();
        ArrayList<Long> breakfastTAG = new ArrayList<>();
        int totalBreakfastKcal=0;
        //Morning snack
        foodNameMorSnack= new ArrayList<>();
        ArrayList<String> foodDescriptionMorSnack= new ArrayList<>();
        foodCuantityMorSnack = new ArrayList<>();
        ArrayList<String> foodCuantityKcalMorSnack= new ArrayList<>();
        ArrayList<Long> morningSnackTAG = new ArrayList<>();
        int totalMorningSnackKcal=0;
        //Lunch
        foodNameLunch= new ArrayList<>();
        ArrayList<String> foodDescriptionLunch= new ArrayList<>();
        foodCuantityLunch = new ArrayList<>();
        ArrayList<String> foodCuantityKcalLunch= new ArrayList<>();
        ArrayList<Long> lunchTAG = new ArrayList<>();
        int totalLunchKcal=0;
        //Evening snack
        foodNameEveningSnack= new ArrayList<>();
        ArrayList<String> foodDescriptionEveningSnack= new ArrayList<>();
        foodCuantityEveningSnack = new ArrayList<>();
        ArrayList<String> foodCuantityKcalEveningSnack= new ArrayList<>();
        ArrayList<Long> eveningSnackTAG = new ArrayList<>();
        int totalEveningSnackKcal=0;
        //Dinner
        foodNameDinner= new ArrayList<>();
        ArrayList<String> foodDescriptionDinner= new ArrayList<>();
        foodCuantityDinner = new ArrayList<>();
        ArrayList<String> foodCuantityKcalDinner= new ArrayList<>();
        ArrayList<Long> dinnerTAG = new ArrayList<>();
        int totalDinnerKcal=0;


        for(int i=0; i<foodData.size();i++){
            int index = -1;
            Long buttonTag;
            switch (foodData.get(i).get(1).intValue()){
                case 1: //Breakfast
                    foodNameBreakfast.add(foodDataNames.get(i).get(0));
                    foodDescriptionBreakfast.add("");
                    foodCuantityBreakfast.add(foodData.get(i).get(2)+"g");
                    index = -1;
                    for (int j = 0; j < str.length; j++) {
                        if (str[j].equals(foodDataNames.get(i).get(0))) {
                            index = j;
                            break;
                        }
                    }
                    foodCuantityKcalBreakfast.add((int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000)+"Kcal");
                    totalBreakfastKcal+=(int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000);
                    buttonTag= 1000+(long)breakfastTAG.size();
                    breakfastTAG.add(buttonTag);
                    break;
                case 2: //Morning snack
                    foodNameMorSnack.add(foodDataNames.get(i).get(0));
                    foodDescriptionMorSnack.add("");
                    foodCuantityMorSnack.add(foodData.get(i).get(2)+"g");
                    index = -1;
                    for (int j = 0; j < str.length; j++) {
                        if (str[j].equals(foodDataNames.get(i).get(0))) {
                            index = j;
                            break;
                        }
                    }
                    foodCuantityKcalMorSnack.add((int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000)+"Kcal");
                    totalMorningSnackKcal+=(int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000);
                    buttonTag= 2000+(long)morningSnackTAG.size();
                    morningSnackTAG.add(buttonTag);
                    break;
                case 3: //Lunch
                    foodNameLunch.add(foodDataNames.get(i).get(0));
                    foodDescriptionLunch.add("");
                    foodCuantityLunch.add(foodData.get(i).get(2)+"g");
                    index = -1;
                    for (int j = 0; j < str.length; j++) {
                        if (str[j].equals(foodDataNames.get(i).get(0))) {
                            index = j;
                            break;
                        }
                    }
                    foodCuantityKcalLunch.add((int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000)+"Kcal");
                    totalLunchKcal+=(int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000);
                    buttonTag= 3000+(long)lunchTAG.size();
                    lunchTAG.add(buttonTag);
                    break;
                case 4: //Evening snack
                    foodNameEveningSnack.add(foodDataNames.get(i).get(0));
                    foodDescriptionEveningSnack.add("");
                    foodCuantityEveningSnack.add(foodData.get(i).get(2)+"g");
                    index = -1;
                    for (int j = 0; j < str.length; j++) {
                        if (str[j].equals(foodDataNames.get(i).get(0))) {
                            index = j;
                            break;
                        }
                    }
                    foodCuantityKcalEveningSnack.add((int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000)+"Kcal");
                    totalEveningSnackKcal+=(int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000);
                    buttonTag= 4000+(long)eveningSnackTAG.size();
                    eveningSnackTAG.add(buttonTag);
                    break;
                case 5: //Dinner
                    foodNameDinner.add(foodDataNames.get(i).get(0));
                    foodDescriptionDinner.add("");
                    foodCuantityDinner.add(foodData.get(i).get(2)+"g");
                    index = -1;
                    for (int j = 0; j < str.length; j++) {
                        if (str[j].equals(foodDataNames.get(i).get(0))) {
                            index = j;
                            break;
                        }
                    }
                    foodCuantityKcalDinner.add((int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000)+"Kcal");
                    totalDinnerKcal+=(int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000);
                    buttonTag= 5000+(long)dinnerTAG.size();
                    dinnerTAG.add(buttonTag);
                    break;
                default: break;
            }

        }
        //
        //Set total Kcals TVs
        //
        View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
        TextView tvTotalKcal = (TextView) rootView.findViewById(R.id.textViewTotalKcal);
        int totalEnergy = (int)(totalBreakfastKcal+totalMorningSnackKcal+totalLunchKcal+totalEveningSnackKcal+totalDinnerKcal);
        tvTotalKcal.setText(mContext.getString(R.string.total_evergy)+" "+totalEnergy+"Kcal");


        //Breaskfast
        TextView tvBreakfastKcal = (TextView) rootView.findViewById(R.id.textViewBreakfastCal);
        tvBreakfastKcal.setText(totalBreakfastKcal+ "Kcal");
        //Morning snack
        TextView tvMorningSnackKcal = (TextView) rootView.findViewById(R.id.textViewMorningSnackCal);
        tvMorningSnackKcal.setText(totalMorningSnackKcal+ "Kcal");
        //Lunch
        TextView tvLunchKcal = (TextView) rootView.findViewById(R.id.textViewLunchCal);
        tvLunchKcal.setText(totalLunchKcal+ "Kcal");
        //Evening Snack
        TextView tvEveningSnack = (TextView) rootView.findViewById(R.id.textViewEveningSnackCal);
        tvEveningSnack.setText(totalEveningSnackKcal+ "Kcal");
        //Dinner
        TextView tvDinnerKcal = (TextView) rootView.findViewById(R.id.textViewDinnerCal);
        tvDinnerKcal.setText(totalDinnerKcal+ "Kcal");
        //
        //Convert breakfast arraylists to string arrays and load them into the adapter
        //
        //Breakfast
        String[] foodNameBreakfastArray= foodNameBreakfast.toArray(new String[foodNameBreakfast.size()]);
        String[] foodDescriptionBreakfastArray=foodDescriptionBreakfast.toArray( new String[foodDescriptionBreakfast.size()]);
        String[] foodCuantityBreakfastArray=foodCuantityBreakfast.toArray(new String[foodCuantityBreakfast.size()]);
        String[] foodCuantityKcalBreakfastArray=foodCuantityKcalBreakfast.toArray(new String[foodCuantityKcalBreakfast.size()]);
        Long[] breakfastTAGarray = breakfastTAG.toArray(new Long[breakfastTAG.size()]);
        fd_FoodListAdapter Breakfast_adapter = new fd_FoodListAdapter(mContext, foodNameBreakfastArray,
                foodDescriptionBreakfastArray, foodCuantityBreakfastArray,foodCuantityKcalBreakfastArray,breakfastTAGarray);
        lvBreakfast.setAdapter(Breakfast_adapter);
        setListViewHeightBasedOnChildren(lvBreakfast);
        //Morning snack
        String[] foodNameMorSnackArray=foodNameMorSnack.toArray(new String[foodNameMorSnack.size()]);
        String[] foodDescriptionMorSnackArray=foodDescriptionMorSnack.toArray(new String[foodDescriptionMorSnack.size()]);
        String[] foodCuantityMorSnackArray=foodCuantityMorSnack.toArray(new String[foodCuantityMorSnack.size()]);
        String[] foodCuantityKcalMorSnackArray=foodCuantityKcalMorSnack.toArray(new String[foodCuantityKcalMorSnack.size()]);
        Long[] morningSnackTAGarray=morningSnackTAG.toArray(new Long[morningSnackTAG.size()]);
        fd_FoodListAdapter adapterMorSnack = new fd_FoodListAdapter(mContext, foodNameMorSnackArray,
                foodDescriptionMorSnackArray, foodCuantityMorSnackArray,foodCuantityKcalMorSnackArray,morningSnackTAGarray);
        lvMorSnack.setAdapter(adapterMorSnack);
        setListViewHeightBasedOnChildren(lvMorSnack);
        //Lunch
        String[] foodNameLunchArray=foodNameLunch.toArray(new String[foodNameLunch.size()]);
        String[] foodDescriptionLunchArray=foodDescriptionLunch.toArray(new String[foodDescriptionLunch.size()]);
        String[] foodCuantityLunchArray=foodCuantityLunch.toArray(new String[foodCuantityLunch.size()]);
        String[] foodCuantityKcalLunchArray=foodCuantityKcalLunch.toArray(new String[foodCuantityKcalLunch.size()]);
        Long[] lunchTAGarray= lunchTAG.toArray(new Long[lunchTAG.size()]);
        fd_FoodListAdapter adapterLunch = new fd_FoodListAdapter(mContext, foodNameLunchArray,
                foodDescriptionLunchArray, foodCuantityLunchArray,foodCuantityKcalLunchArray,lunchTAGarray);
        lvLunch.setAdapter(adapterLunch);
        setListViewHeightBasedOnChildren(lvLunch);
        //Evening snack
        String[] foodNameEveSnackArray= foodNameEveningSnack.toArray(new String[foodNameEveningSnack.size()]);
        String[] foodDescriptionEveSnackArray= foodDescriptionEveningSnack.toArray(new String[foodDescriptionEveningSnack.size()]);
        String[] foodCuantityEveSnackArray= foodCuantityEveningSnack.toArray(new String[foodCuantityEveningSnack.size()]);
        String[] foodCuantityKcalEveSnackArray=foodCuantityKcalEveningSnack.toArray(new String[foodCuantityKcalEveningSnack.size()]);
        Long [] eveningSnackTAGaray= eveningSnackTAG.toArray(new Long[eveningSnackTAG.size()]);
        fd_FoodListAdapter adapterEveSnack = new fd_FoodListAdapter(mContext, foodNameEveSnackArray,
                foodDescriptionEveSnackArray, foodCuantityEveSnackArray,foodCuantityKcalEveSnackArray,eveningSnackTAGaray);
        lvEveSnack.setAdapter(adapterEveSnack);
        setListViewHeightBasedOnChildren(lvEveSnack);
        //Dinner
        String[] foodNameDinnerArray=foodNameDinner.toArray(new String[foodNameDinner.size()]);
        String[] foodDescriptionDinnerArray=foodDescriptionDinner.toArray(new String[foodDescriptionDinner.size()]);
        String[] foodCuantityDinnerArray=foodCuantityDinner.toArray(new String[foodCuantityDinner.size()]);
        String[] foodCuantityKcalDinnerArray=foodCuantityKcalDinner.toArray(new String[foodCuantityKcalDinner.size()]);
        Long [] dinnerTAGarray= dinnerTAG.toArray(new Long[dinnerTAG.size()]);
        fd_FoodListAdapter adapterDinner = new fd_FoodListAdapter(mContext, foodNameDinnerArray,
                foodDescriptionDinnerArray, foodCuantityDinnerArray,foodCuantityKcalDinnerArray,dinnerTAGarray);
        lvDinner.setAdapter(adapterDinner);
        setListViewHeightBasedOnChildren(lvDinner);
    }

    /**
     *
     */
    public static void updateView1(){
        View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
        LineChart lineChart = (LineChart) rootView.findViewById(R.id.chart);
        // creating list of entry
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(4f, 0));
        entries.add(new Entry(8f, 1));
        entries.add(new Entry(6f, 2));
        entries.add(new Entry(2f, 3));
        entries.add(new Entry(18f, 4));
        entries.add(new Entry(9f, 5));

        LineDataSet dataset = new LineDataSet(entries, "# of Calls");

        // creating labels
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");

        LineData data = new LineData(labels, dataset);
        lineChart.setData(data); // set the data and list of lables into chart

        lineChart.setDescription("Description");  // set the description
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

    public void onDeleteTouched(View v){
        Long buttonTAG = Long.parseLong(v.getTag().toString());
        long type = buttonTAG/1000;
        String foodName;
        String aux;
        Long amount;
        switch ((int)(type)){
            case 1: //breakfast
                foodName=foodNameBreakfast.get((int)(buttonTAG-type*1000));
                //VIVAN LAS CHAPUZAS!!!
                aux = foodCuantityBreakfast.get((int)(buttonTAG-type*1000));
                aux = aux.substring(0,aux.length()-1);
                amount = Long.parseLong(aux);
                break;
            case 2: //morning snack
                foodName=foodNameMorSnack.get((int)(buttonTAG-type*1000));
                aux = foodCuantityMorSnack.get((int)(buttonTAG-type*1000));
                aux = aux.substring(0,aux.length()-1);
                amount = Long.parseLong(aux);
                break;
            case 3: //lunch
                foodName=foodNameLunch.get((int)(buttonTAG-type*1000));
                aux = foodCuantityLunch.get((int)(buttonTAG-type*1000));
                aux = aux.substring(0,aux.length()-1);
                amount = Long.parseLong(aux);
                break;
            case 4: //evening snack
                foodName=foodNameEveningSnack.get((int)(buttonTAG-type*1000));
                aux = foodCuantityEveningSnack.get((int)(buttonTAG-type*1000));
                aux = aux.substring(0,aux.length()-1);
                amount = Long.parseLong(aux);
                break;
            case 5://dinner
                foodName=foodNameDinner.get((int)(buttonTAG - type * 1000));
                aux = foodCuantityDinner.get((int)(buttonTAG-type*1000));
                aux = aux.substring(0,aux.length()-1);
                amount = Long.parseLong(aux);
                break;
            default:
                foodName="";
                amount = Long.valueOf(0);
                break;
        }
        Calendar c_aux = Calendar.getInstance();
        c_aux.setTimeInMillis(selectedDay);
        c_aux.set(Calendar.HOUR_OF_DAY, 0);
        c_aux.set(Calendar.MINUTE, 0);
        c_aux.set(Calendar.MILLISECOND,0);
        Long from = c_aux.getTimeInMillis();
        Long to = from+24*3600*1000;
        ui_MainActivity.dbhelp.deleteFood(from-1,to+1,type,foodName,amount);
//        Log.i(TAG,"Button tag is: "+buttonTAG + " type is: "+type+" name is: "+foodName+" timestamp is: "+selectedDay);
        updateView();
    }
}
