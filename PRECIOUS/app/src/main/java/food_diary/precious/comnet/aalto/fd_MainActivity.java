package food_diary.precious.comnet.aalto;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


public class fd_MainActivity extends AppCompatActivity {

    public static final int ENERGY_GOAL=2000;
    public static final int FAT_GOAL=70;
    public static final int FASAT_GOAL=20;
    public static final int SUGAR_GOAL=90;
    public static final int NA_GOAL=6;

    private static final int FOOD_REMINDER_NOTIF_ID = 100034;

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

    private static FloatingActionButton fab;

    public static final String PREFS_NAME = "FoodPreferences";
    private static SharedPreferences preferences;


//    private boolean fab_show = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fd_main_activity);
        mContext = this;
        preferences=  getSharedPreferences(PREFS_NAME, 0);
        //If Android version >=5.0, set status bar background color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.foodDiary));
        }
        // Floating button
        fab = (FloatingActionButton) findViewById(R.id.fab_food_diary);
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
    protected void onPause() {
        //Store app usage
        try {
            sql_db.precious.comnet.aalto.DBHelper.getInstance(this).insertAppUsage(System.currentTimeMillis(), TAG, "onPause");
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
        mNotificationManager.cancel(FOOD_REMINDER_NOTIF_ID);

        updateView();

        //Store app usage
        try{
            sql_db.precious.comnet.aalto.DBHelper.getInstance(this).insertAppUsage(System.currentTimeMillis(), TAG, "onResume");
        }catch (Exception e){Log.e(TAG," ",e);}
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
        fab.setVisibility(View.VISIBLE);

        c_aux.setTimeInMillis(selectedDay);
        c_aux.set(Calendar.HOUR_OF_DAY, 0);
        c_aux.set(Calendar.SECOND, 0);
        c_aux.set(Calendar.MILLISECOND,0);

        ArrayList<ArrayList<Long>> foodData;
        ArrayList<ArrayList<String>> foodDataNames;

        try{
            foodData = sql_db.precious.comnet.aalto.DBHelper.getInstance(mContext).getFood(c_aux.getTimeInMillis(), c_aux.getTimeInMillis() + 24 * 3600 * 1000);
            foodDataNames = sql_db.precious.comnet.aalto.DBHelper.getInstance(mContext).getFoodNames(c_aux.getTimeInMillis(), c_aux.getTimeInMillis() + 24 * 3600 * 1000);
        }catch (Exception e) {
            Log.e(TAG," ",e);
            foodData = new ArrayList<>();
            foodDataNames = new ArrayList<>();
        }

        if (foodData.size() != foodDataNames.size()) {
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

        double totalFat=0;
        double totalFasat=0;
        double totalSugar=0;
        double totalNa1000=0;

        for(int i=0; i<foodData.size();i++){
            int index = -1;
            index = -1;
            for (int j = 0; j < str.length; j++) {
                if (str[j].equals(foodDataNames.get(i).get(0))) {
                    index = j;
                    break;
                }
            }
//            Log.i(TAG,"INDEX="+index+" VALUE="+mContext.getResources().getIntArray(R.array.food_db_fats)[index]);
            totalFat += (double)mContext.getResources().getIntArray(R.array.food_db_fats)[index]/1000000;
            totalFasat += (double)mContext.getResources().getIntArray(R.array.food_db_fasat)[index]/1000000;
            totalSugar += (double)mContext.getResources().getIntArray(R.array.food_db_sugar)[index]/1000000;
            totalNa1000 += (double)mContext.getResources().getIntArray(R.array.food_db_na_1000)[index]/1000000;


            Long buttonTag;
            switch (foodData.get(i).get(1).intValue()){
                case 1: //Breakfast
                    foodNameBreakfast.add(foodDataNames.get(i).get(0));
                    foodDescriptionBreakfast.add("");
                    foodCuantityBreakfast.add(foodData.get(i).get(2)+"g");
                    foodCuantityKcalBreakfast.add((int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000)+"Kcal");
                    totalBreakfastKcal+=(int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000);
                    buttonTag= 1000+(long)breakfastTAG.size();
                    breakfastTAG.add(buttonTag);
                    break;
                case 2: //Morning snack
                    foodNameMorSnack.add(foodDataNames.get(i).get(0));
                    foodDescriptionMorSnack.add("");
                    foodCuantityMorSnack.add(foodData.get(i).get(2)+"g");
                    foodCuantityKcalMorSnack.add((int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000)+"Kcal");
                    totalMorningSnackKcal+=(int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000);
                    buttonTag= 2000+(long)morningSnackTAG.size();
                    morningSnackTAG.add(buttonTag);
                    break;
                case 3: //Lunch
                    foodNameLunch.add(foodDataNames.get(i).get(0));
                    foodDescriptionLunch.add("");
                    foodCuantityLunch.add(foodData.get(i).get(2)+"g");
                    foodCuantityKcalLunch.add((int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000)+"Kcal");
                    totalLunchKcal+=(int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000);
                    buttonTag= 3000+(long)lunchTAG.size();
                    lunchTAG.add(buttonTag);
                    break;
                case 4: //Evening snack
                    foodNameEveningSnack.add(foodDataNames.get(i).get(0));
                    foodDescriptionEveningSnack.add("");
                    foodCuantityEveningSnack.add(foodData.get(i).get(2)+"g");
                    foodCuantityKcalEveningSnack.add((int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000)+"Kcal");
                    totalEveningSnackKcal+=(int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000);
                    buttonTag= 4000+(long)eveningSnackTAG.size();
                    eveningSnackTAG.add(buttonTag);
                    break;
                case 5: //Dinner
                    foodNameDinner.add(foodDataNames.get(i).get(0));
                    foodDescriptionDinner.add("");
                    foodCuantityDinner.add(foodData.get(i).get(2)+"g");
                    foodCuantityKcalDinner.add((int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000)+"Kcal");
                    totalDinnerKcal+=(int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000);
                    buttonTag= 5000+(long)dinnerTAG.size();
                    dinnerTAG.add(buttonTag);
                    break;
                default: break;
            }

        }
        //
        //Set Background color and overall nutritional info
        //
        View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
        Button bEnerc1000= (Button) rootView.findViewById(R.id.bEnerc1000);
        Button bFasat= (Button) rootView.findViewById(R.id.bFasat);
        Button bFats= (Button) rootView.findViewById(R.id.bFats);
        Button bNa1000= (Button) rootView.findViewById(R.id.bNa1000);
        Button bSugar= (Button) rootView.findViewById(R.id.bSugar);
        int totalEnergy = (int)(totalBreakfastKcal+totalMorningSnackKcal+totalLunchKcal+totalEveningSnackKcal+totalDinnerKcal);

        bEnerc1000.setText(mContext.getString(R.string.enerc2)+"\n" + totalEnergy+"\n"+(int)(100*totalEnergy/ENERGY_GOAL)+"%");
        bFasat.setText(mContext.getString(R.string.fasat2)+"\n" +String.format( "%.2f",totalFasat)+"\n"+(int)(100*totalFasat/FASAT_GOAL)+"%");
        bFats.setText(mContext.getString(R.string.fat2)+"\n" +String.format( "%.2f",totalFat)+"\n"+(int)(100*totalFat/FAT_GOAL)+"%");
        bNa1000.setText(mContext.getString(R.string.na2) + "\n" + String.format("%.2f", totalNa1000) + "\n" + (int) (100 * totalNa1000 / NA_GOAL) + "%");
        bSugar.setText(mContext.getString(R.string.sugar2)+"\n" + String.format( "%.2f",totalSugar)+"\n"+(int)(100*totalSugar/SUGAR_GOAL)+"%");

        if(totalEnergy==0){
            bEnerc1000.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nutritional_data_gray));
            bFasat.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nutritional_data_gray));
            bFats.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nutritional_data_gray));
            bNa1000.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nutritional_data_gray));
            bSugar.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nutritional_data_gray));
        }
        else{
            //ENERGY
            if((100*totalEnergy/ENERGY_GOAL)<=75)
                bEnerc1000.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nutritional_data_green));
            else if((100*totalEnergy/ENERGY_GOAL)<=125)
                bEnerc1000.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nutritional_data_ambar));
            else
                bEnerc1000.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nutritional_data_red));
            //FASAT
            if((100*totalFasat/FASAT_GOAL)<=75)
                bFasat.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nutritional_data_green));
            else if((100*totalFasat/FASAT_GOAL)<=125)
                bFasat.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nutritional_data_ambar));
            else
                bFasat.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nutritional_data_red));
            //FAT
            if((100*totalFat/FAT_GOAL)<=75)
                bFats.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nutritional_data_green));
            else if((100*totalFat/FAT_GOAL)<=125)
                bFats.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nutritional_data_ambar));
            else
                bFats.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nutritional_data_red));
            //SUGAR
            if((100*totalSugar/SUGAR_GOAL)<=75)
                bSugar.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nutritional_data_green));
            else if((100*totalSugar/SUGAR_GOAL)<=125)
                bSugar.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nutritional_data_ambar));
            else
                bSugar.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nutritional_data_red));
            //NA
            if((100*totalNa1000/NA_GOAL)<=75)
                bNa1000.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nutritional_data_green));
            else if((100*totalNa1000/NA_GOAL)<=125)
                bNa1000.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nutritional_data_ambar));
            else
                bNa1000.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.nutritional_data_red));

        }


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
        fab.setVisibility(View.GONE);
        View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);

        LineChart lineChartCalories = (LineChart) rootView.findViewById(R.id.chartCal);
        LineChart lineChartFat = (LineChart) rootView.findViewById(R.id.chartFat);
        LineChart lineChartFasat = (LineChart) rootView.findViewById(R.id.chartFasat);
        LineChart lineChartSugar = (LineChart) rootView.findViewById(R.id.chartSugar);
        LineChart lineChartNa = (LineChart) rootView.findViewById(R.id.chartNa);
        // creating list of entry
        ArrayList<Entry> entriesCalories = new ArrayList<>();
        ArrayList<Entry> entriesFats = new ArrayList<>();
        ArrayList<Entry> entriesFasat = new ArrayList<>();
        ArrayList<Entry> entriesSugar = new ArrayList<>();
        ArrayList<Entry> entriesNa = new ArrayList<>();
        // creating labels
        ArrayList<String> labelsDayWeek = new ArrayList<String>();
//        ArrayList<String> labelsGrams = new ArrayList<String>();

        Calendar c_aux = Calendar.getInstance();
        c_aux.set(Calendar.HOUR_OF_DAY, 0);
        c_aux.set(Calendar.SECOND, 0);
        c_aux.set(Calendar.MILLISECOND,0);

        String str[] = mContext.getResources().getStringArray(R.array.food_names);
        for(int k=-6;k<1;k++) {
            c_aux.setTimeInMillis(selectedDay + k * +24 * 3600 * 1000);
            ArrayList<ArrayList<Long>> foodData = sql_db.precious.comnet.aalto.DBHelper.getInstance(mContext).getFood(c_aux.getTimeInMillis(), c_aux.getTimeInMillis() + 24 * 3600 * 1000);
            ArrayList<ArrayList<String>> foodDataNames = sql_db.precious.comnet.aalto.DBHelper.getInstance(mContext).getFoodNames(c_aux.getTimeInMillis(), c_aux.getTimeInMillis() + 24 * 3600 * 1000);

            int totalEnergy = 0;
            int totalFat = 0;
            int totalFasat = 0;
            int totalSugar = 0;
            int totalNa1000 = 0;

            for (int i = 0; i < foodData.size(); i++) {
                int index = -1;
                index = -1;
                for (int j = 0; j < str.length; j++) {
                    if (str[j].equals(foodDataNames.get(i).get(0))) {
                        index = j;
                        break;
                    }
                }
//            Log.i(TAG,"INDEX="+index+" VALUE="+mContext.getResources().getIntArray(R.array.food_db_fats)[index]);
                totalEnergy +=(int) (0.239006*mContext.getResources().getIntArray(R.array.food_db_enerc1000KJ)[index] / 1000);
                totalFat += (double) mContext.getResources().getIntArray(R.array.food_db_fats)[index] / 1000000;
                totalFasat += (double) mContext.getResources().getIntArray(R.array.food_db_fasat)[index] / 1000000;
                totalSugar += (double) mContext.getResources().getIntArray(R.array.food_db_sugar)[index] / 1000000;
                totalNa1000 += (double) mContext.getResources().getIntArray(R.array.food_db_na_1000)[index] / 1000000;
            }
            Log.i(TAG, "ENER:" + totalEnergy + " PLACE:" + (k + 6));
            entriesCalories.add(new Entry(totalEnergy, k + 6));
            entriesFats.add(new Entry(totalFat, k + 6));
            entriesFasat.add(new Entry(totalFasat, k + 6));
            entriesSugar.add(new Entry(totalSugar, k+6));
            entriesNa.add(new Entry(totalNa1000, k + 6));
            c_aux.setTimeInMillis(c_aux.getTimeInMillis()+24 * 3600 * 1000);
            try {
                labelsDayWeek.add(activity_tracker.precious.comnet.aalto.atUtils.getDayWeek(mContext, c_aux).substring(0, 3));
            }catch (Exception e){
                Log.i(TAG," ",e);
            }
//            labelsGrams.add("");
        }

        LineDataSet datasetCalories = new LineDataSet(entriesCalories,mContext.getString(R.string.calories));
        datasetCalories.setDrawValues(false);
        LineData dataCalories = new LineData(labelsDayWeek, datasetCalories);
        lineChartCalories.setData(dataCalories); // set the data and list of lables into chart
        lineChartCalories.setDescription("");  // set the description


        LineDataSet datasetFats = new LineDataSet(entriesFats,mContext.getString(R.string.fat2)+" (g)");
        datasetFats.setColor(mContext.getResources().getColor(R.color.outcomeGoal));
        datasetFats.setCircleColor(mContext.getResources().getColor(R.color.outcomeGoal));
        datasetFats.setDrawValues(false);

        LineDataSet datasetFasat = new LineDataSet(entriesFasat,mContext.getString(R.string.fasat2)+" (g)");
        datasetFasat.setColor(mContext.getResources().getColor(R.color.importanceRuler));
        datasetFasat.setCircleColor(mContext.getResources().getColor(R.color.importanceRuler));
        datasetFasat.setDrawValues(false);

        LineDataSet datasetSugar = new LineDataSet(entriesSugar,mContext.getString(R.string.sugar2)+" (g)");
        datasetSugar.setColor(mContext.getResources().getColor(R.color.selfMonitoring));
        datasetSugar.setCircleColor(mContext.getResources().getColor(R.color.selfMonitoring));
        datasetSugar.setDrawValues(false);

        LineDataSet datasetNa = new LineDataSet(entriesNa,mContext.getString(R.string.na2)+" (g)");
        datasetNa.setColor(mContext.getResources().getColor(R.color.myFavourites));
        datasetNa.setCircleColor(mContext.getResources().getColor(R.color.myFavourites));
        datasetNa.setDrawValues(false);


        LineData dataFat = new LineData(labelsDayWeek, datasetFats);
        lineChartFat.setData(dataFat); // set the data and list of lables into chart
        lineChartFat.setDescription("");  // set the description

        LineData dataFasat = new LineData(labelsDayWeek, datasetFasat);
        lineChartFasat.setData(dataFasat); // set the data and list of lables into chart
        lineChartFasat.setDescription("");  // set the description

        LineData dataSugar = new LineData(labelsDayWeek, datasetSugar);
        lineChartSugar.setData(dataSugar); // set the data and list of lables into chart
        lineChartSugar.setDescription("");  // set the description

        LineData dataNa = new LineData(labelsDayWeek, datasetNa);
        lineChartNa.setData(dataNa); // set the data and list of lables into chart
        lineChartNa.setDescription("");  // set the description
//        LineData data2 = new LineData(labelsGrams, datasetFats);
//        data2.addDataSet(datasetFasat);
//        data2.addDataSet(datasetSugar);
//        data2.addDataSet(datasetNa);
//        lineChartGrams.setData(data2);
//
//        lineChartGrams.setDescription("");  // set the description
//
        lineChartCalories.setVisibility(View.GONE);
        lineChartFat.setVisibility(View.GONE);
        lineChartFasat.setVisibility(View.GONE);
        lineChartSugar.setVisibility(View.GONE);
        lineChartNa.setVisibility(View.GONE);
        Button btEnerc = (Button) ((Activity) mContext).findViewById(R.id.btEnerc);
        Button btFat = (Button) ((Activity) mContext).findViewById(R.id.btFat);
        Button btFasat = (Button) ((Activity) mContext).findViewById(R.id.btFasat);
        Button btSugar = (Button) ((Activity) mContext).findViewById(R.id.btSugar);
        Button btNa = (Button) ((Activity) mContext).findViewById(R.id.btNa);
        btEnerc.setBackgroundColor(mContext.getResources().getColor(R.color.fd_unselected_tab_background));
        btFat.setBackgroundColor(mContext.getResources().getColor(R.color.fd_unselected_tab_background));
        btFasat.setBackgroundColor(mContext.getResources().getColor(R.color.fd_unselected_tab_background));
        btSugar.setBackgroundColor(mContext.getResources().getColor(R.color.fd_unselected_tab_background));
        btNa.setBackgroundColor(mContext.getResources().getColor(R.color.fd_unselected_tab_background));
        switch (preferences.getInt("graphToShow",1)){
            case 1  :
                btEnerc.setBackgroundColor(mContext.getResources().getColor(R.color.fd_selected_tab_background));
                lineChartCalories.setVisibility(View.VISIBLE);
                lineChartCalories.invalidate();
                break;
            case 2  :
                btFat.setBackgroundColor(mContext.getResources().getColor(R.color.fd_selected_tab_background));
                lineChartFat.setVisibility(View.VISIBLE);
                lineChartFasat.invalidate();
                break;
            case 3  :
                btFasat.setBackgroundColor(mContext.getResources().getColor(R.color.fd_selected_tab_background));
                lineChartFasat.setVisibility(View.VISIBLE);
                lineChartFasat.invalidate();
                break;
            case 4  :
                btSugar.setBackgroundColor(mContext.getResources().getColor(R.color.fd_selected_tab_background));
                lineChartSugar.setVisibility(View.VISIBLE);
                lineChartSugar.invalidate();
                break;
            case 5  :
                btNa.setBackgroundColor(mContext.getResources().getColor(R.color.fd_selected_tab_background));
                lineChartNa.setVisibility(View.VISIBLE);
                lineChartNa.invalidate();
                break;
            default:
                btEnerc.setBackgroundColor(mContext.getResources().getColor(R.color.fd_selected_tab_background));
                lineChartCalories.setVisibility(View.VISIBLE);
                lineChartCalories.invalidate();
                break;
        }
    }

    public void toggleShow ( View v){
        SharedPreferences.Editor editor = preferences.edit();
        switch (v.getId()){
            case R.id.btEnerc   :
                editor.putInt("graphToShow", 1);
                break;
            case R.id.btFat   :
                editor.putInt("graphToShow", 2);
                break;
            case R.id.btFasat   :
                editor.putInt("graphToShow", 3);
                break;
            case R.id.btSugar   :
                editor.putInt("graphToShow", 4);
                break;
            case R.id.btNa   :
                editor.putInt("graphToShow", 5);
                break;
            default:
                break;
        }
        editor.apply();
        updateView();
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
        Long from = c_aux.getTimeInMillis()-3600*1000;
        Long to = from+24*3600*1000+3600*1000;
        sql_db.precious.comnet.aalto.DBHelper.getInstance(this).deleteFood(from-1,to+1,type,foodName,amount);
        Log.i(TAG, "Button tag is: " + buttonTAG + " type is: " + type + " name is: " + foodName + " from: " + from + " to: " + to + " amount: "+amount);
        updateView();
    }
}
