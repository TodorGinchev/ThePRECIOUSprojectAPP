package activity_tracker.precious.comnet.aalto;


import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import aalto.comnet.thepreciousproject.R;

//For PA type-steps conversion: http://www.purdue.edu/walktothemoon/activities.html
public class MountainViewActivity extends Activity implements View.OnTouchListener {
    public static final String PREFS_NAME = "IRsubappPreferences";
    public static final String TAG = "MountainViewActivity";
    public static final double mountainLayoutHeightRatioBig = 0.675;
    public static final double mountainLayoutHeightRatioSmall = 0.35;
    private static final int PA_GOAL_REMINDER_NOTIF_ID = 100035;
    private static final int maxGoalHeight = 15000;
    private static final int maxMountainHeight = 1000 * (int) (5 * maxGoalHeight / 4000);
    private static final int GOAL_LIMIT_TIME = 23;
    private static final int DEFAULT_GOAL = 7000;
    public static Context appConext;
    /*
     * For the mountain canvas view
     */
    public static Canvas mainViewCanvas;
    private static int screen_width;
    private static int screen_height;
    private static int num_mountains;
    private static int mountain_width;
    private static int mountain_layout_width;
    private static int mountain_layout_height;
    private static int mountain_view_margin_left;
    private static int mountain_view_margin_right;
    private static int spiral_layout_width;
    private static int spiral_layout_height;
    private static int mountain_top_margin;
    private static int scrollPosition = -1;
    private long selectedDayTimeMillis;
    /*
     * PA data
     */
    //Vector for data storage
    private static Vector<Long> LogVectorDayResult = new Vector<>();
    private static Vector<Integer> LogVectorStill = new Vector<>();
    private static Vector<Integer> LogVectorWalk = new Vector<>();
    private static Vector<Integer> LogVectorBicycle = new Vector<>();
    private static Vector<Integer> LogVectorVehicle = new Vector<>();
    private static Vector<Integer> LogVectorRun = new Vector<>();
    private static Vector<Integer> LogVectorTilting = new Vector<>();
    private static Vector<Integer> LogVectorGoals = new Vector<>();
    private static ArrayList<ArrayList<Long>> paManualData;
    private static double[] pa_spiral_data = null;
    private final int enableGoalSettingThres = 9;
    public TextView tvDayWeek;
    public TextView tvDayMonth;
    public TextView tvMonthYear;
    Vector<Integer> LogVectorSteps = new Vector<>();
    boolean drawMountains;
    boolean drawDays;
    boolean drawGoals;
    boolean drawGoalHint;
    private int[] Goals_data;
    /*
     * Views
     */
    private MountainView mv;
    private HorizontalScrollView hsv;
    private HorizontalScrollView hsv_main;
    private int[] previous_actions = new int[3];
    private float[] previous_coordX = new float[3];
    private float[] previous_coordY = new float[3];
    private int enableGoalSettingTemporazer;
    private TextView tvSteps;
    private TextView tvGoal;
    private boolean dayViewActive;
    private DailyView dv;
    private Rect[] paTouchRect;
    private FloatingActionButton fab;
    private Paint[] paint_lines;
    private Paint[] paint_mountains;
    private Paint[] paint_days;
    private Paint[] paint_goals;
    private Paint[] paint_flags;
    private Boolean[] draw_flags;
    private Bitmap bmp_flag;
    private Paint paint_white_triangle;
    //    private Paint [] paint_rewards;
//    private Path [] path_lines;
    private Path[] path_mountains;
    //    private Path [] path_goals;
//    private Path [] path_rewards;
    private Path path_white_triangle;
    /*
     * For the daily canvas view
     */
    private int day_to_show = 0;
    private int prev_day_to_show = 0;
    private int stepsAcumul = 0;
    /*
     *  For the time
     */
    private String currentDay, currentMonth, currentYear;

    public static void setCanvas(Canvas canvas) {
        MountainViewActivity.mainViewCanvas = canvas;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_mountain_view_activity);
        //If Android version >=5.0, set status bar background color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.selfMonitoring));
        }
        // Floating button
        fab = (FloatingActionButton) findViewById(R.id.fab_mountain);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddActivity();
            }
        });

        //Declare views
        hsv = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        hsv_main = (HorizontalScrollView) findViewById(R.id.horizontalScrollViewMain);
        tvDayWeek = ((TextView) findViewById(R.id.textViewDayWeek));
        tvDayMonth = ((TextView) findViewById(R.id.textViewDayMonth));
        tvMonthYear = ((TextView) findViewById(R.id.textViewMonthYear));
        tvSteps = ((TextView) findViewById(R.id.textViewSteps));
        tvGoal = ((TextView) findViewById(R.id.textViewGoal));
        //Set toolbar title and icons
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.self_monitoring_title));
        toolbar.setTitleTextColor(getResources().getColor(R.color.selfMonitoring));
        toolbar.setNavigationIcon(R.drawable.sm_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Get screen size
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screen_width = size.x;
        // Calculate ActionBar height
        TypedValue tv_aux = new TypedValue();
        int actionBarHeight = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv_aux, true))
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv_aux.data, getResources().getDisplayMetrics());
        Log.i(TAG, "TOOLBAR HEIGHT=_" + actionBarHeight + "_");
        screen_height = size.y - (int) (2 * actionBarHeight);


        //Get application context
        appConext = getApplicationContext();
        //Get PA data
        dayViewActive = false;
        drawMountains = true;
        drawDays = true;
        drawGoals = true;
        drawGoalHint = false;
        updatePAdata(mountainLayoutHeightRatioBig);
        enableGoalSettingTemporazer = 0;
//        //Set onClick listener to relative layout
        RelativeLayout rlShowDayOverview = (RelativeLayout) findViewById(R.id.rlShowDayOverview);
        rlShowDayOverview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDayInfo();
                return false;
            }
        });
//        ImageView bShowDayOverview = (ImageView) findViewById(R.id.bShowDayOverview);
//        bShowDayOverview.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                showDayInfo();
//                return false;
//            }
//        });


        //CHAPUZAAA! YUHUUUU!
        hsv_main.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                for (int i = 0; i < 2000; i += 400) {
                    //If scroll only
                    hsv.postDelayed(new Runnable() {
                        public void run() {
                            hsv.setScrollX(hsv_main.getScrollX());
                            scrollPosition = hsv.getScrollX();
                            hsv_main.setScrollX(scrollPosition);
                            drawMountains = true;
                            drawGoals = true;
                            mv.invalidate();
                        }
                    }, i);
                }

                return false;
            }
        });

        //Set current day
        long time = System.currentTimeMillis();
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int iCurrentDay = c.get(Calendar.DAY_OF_MONTH);
        int iCurrentMonth = c.get(Calendar.MONTH) + 1;
        currentYear = "" + c.get(Calendar.YEAR);
        currentDay = (iCurrentDay > 9) ? "" + iCurrentDay : "0" + iCurrentDay;
        currentMonth = (iCurrentMonth > 9) ? "" + iCurrentMonth : "0" + iCurrentMonth;
    }

    /**
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        //Cancel food reminder notification
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(PA_GOAL_REMINDER_NOTIF_ID);
        if (dayViewActive) {
            updatePAdata(mountainLayoutHeightRatioSmall);
            drawMountainView(false);
            drawDailyView(true);
        } else {
            updatePAdata(mountainLayoutHeightRatioBig);
            drawMountainView(true);
            drawDailyView(false);
        }
        //Store app usage
        try {
            sql_db.precious.comnet.aalto.DBHelper.getInstance(this).insertAppUsage(System.currentTimeMillis(), TAG, "onResume");
        } catch (Exception e) {
            Log.e(TAG, " ", e);
        }
    }

    /**
     *
     */
    @Override
    protected void onPause() {
//        atUtils.stopLocationUpdates(); TODO
        hsv.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                Log.i(TAG, "?????" + event.toString());
                return false;
            }
        });
        //Store app usage
        try {
            sql_db.precious.comnet.aalto.DBHelper.getInstance(this).insertAppUsage(System.currentTimeMillis(), TAG, "onPause");
        } catch (Exception e) {
            Log.e(TAG, " ", e);
        }
        super.onPause();
    }

    /**
     *
     */
    private void updatePAdata(double mountainHeighRatio) {
        //        atUtils.startLocationUpdates(this);TODO
        //Get Info
        Log.i(TAG,"updatePAdata()");
        atUtils.getLog();
//        atUtils.loadVectors();
        LogVectorDayResult = atUtils.getLogVectorDayResult();
        LogVectorSteps = atUtils.getLogVectorSteps();
        LogVectorStill = atUtils.getLogVectorStill();
        LogVectorWalk = atUtils.getLogVectorWalk();
        LogVectorBicycle = atUtils.getLogVectorBicycle();
        LogVectorVehicle = atUtils.getLogVectorVehicle();
        LogVectorRun = atUtils.getLogVectorRun();
        LogVectorTilting = atUtils.getLogVectorTilting();
        LogVectorGoals = atUtils.getLogVectorGoals();
//        for(int cont=0;cont<LogVectorDayResult.size();cont++)
//            Log.i("TIMELINE", LogVectorDayResult.get(cont)+"");
        num_mountains = LogVectorDayResult.size();
        if (num_mountains == 0) {
            Toast.makeText(this, "No activity data yet", Toast.LENGTH_LONG).show();
            finish();
        }
        getGoalsData();
        //Init canvas view objects
        paint_lines = new Paint[num_mountains + 1];
        paint_mountains = new Paint[num_mountains];
        paint_goals = new Paint[num_mountains + 1];
        paint_flags = new Paint[num_mountains + 1];
        draw_flags = new Boolean[num_mountains + 1];
        bmp_flag = BitmapFactory.decodeResource(getResources(), R.drawable.flag_65);
        paint_white_triangle = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint_white_triangle.setColor(getResources().getColor(R.color.white_triangle));
//        paint_rewards = new Paint[num_mountains];
        paint_days = new Paint[num_mountains + 1];
//        path_lines = new Path[num_mountains];
        path_mountains = new Path[num_mountains];
//        path_goals = new Path[num_mountains];
//        path_rewards = new Path[num_mountains];


        mountain_layout_height = (int) (mountainHeighRatio * (double) screen_height); //900
        mountain_width = screen_width / 3;
//        mountain_layout_width = (int)(mountain_width+(2*mountain_width/3)*(num_mountains-3)+mountain_width*2);//90000;
        mountain_layout_width = (int) (mountain_width + (2 * mountain_width / 3) * (num_mountains - 2));//90000;
        mountain_view_margin_left = (int) (screen_width * 0.5);
        mountain_view_margin_right = (int) (screen_width * 0.4);
        mountain_layout_width += mountain_view_margin_left + mountain_view_margin_right;
        mountain_top_margin = mountain_layout_height / 7;
    }

    /**
     *
     */
    void drawMountainView(boolean autoScroll) {
        //
        RelativeLayout rl_main = (RelativeLayout) findViewById(R.id.RelativeLayoutMain);
        rl_main.getLayoutParams().width = mountain_layout_width;  // change width of the layout

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.RelativeLayoutMountains);
        rl.getLayoutParams().height = mountain_layout_height;  // change height of the layout
        rl.getLayoutParams().width = mountain_layout_width;  // change width of the layout
        rl.setBackgroundColor(getResources().getColor(R.color.mountain_background));
        //Set click listener for the scroll view
        hsv.setOnTouchListener(this);

        mv = new MountainView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mountain_layout_width, mountain_layout_height); //RelativeLayout.LayoutParams.WRAP_CONTENT);
        mv.setLayoutParams(params);
        rl.removeAllViews();
        rl.addView(mv);

        //Auto scroll effect
        if (autoScroll) {
            hsv.postDelayed(new Runnable() {
                public void run() {
                    hsv.scrollTo(mountain_layout_width - screen_width / 2, 0);
                    hsv_main.scrollTo(mountain_layout_width - screen_width / 2, 0);
                }
            }, 500L);
            hsv.postDelayed(new Runnable() {
                public void run() {
                    scrollPosition = hsv.getScrollX();
                    mv.invalidate();
                }
            }, 1000L);
        }

    }

    /**
     *
     */
    void drawDailyView(boolean drawView) {
        RelativeLayout rl2 = (RelativeLayout) findViewById(R.id.dayInfoLayout);
        rl2.setOnTouchListener(this);
        if (drawView) {
            rl2.setVisibility(View.VISIBLE);
            rl2.postDelayed(new Runnable() {
                public void run() {
                    RelativeLayout rl2 = (RelativeLayout) findViewById(R.id.dayInfoLayout);
                    spiral_layout_height = rl2.getHeight();
                    spiral_layout_width = rl2.getWidth();
                    rl2.setBackgroundColor(getResources().getColor(R.color.day_info_background));
                    dv = new DailyView(appConext);
                    setDailyViewOnTouchListener();
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(spiral_layout_width, spiral_layout_height); //RelativeLayout.LayoutParams.WRAP_CONTENT);
                    dv.setLayoutParams(params);
                    rl2.removeAllViews();
                    rl2.addView(dv);
                }
            }, 10);
        } else rl2.setVisibility(View.GONE);
    }

    /**
     *
     */
    public boolean onTouch(View arg0, MotionEvent arg1) {
        Boolean performScroll = true;
//        Log.i(TAG, "Touch event: " + arg1.toString());
//        Log.i(TAG, "Action: " + arg1.getAction());
//        Log.i(TAG, "Scroll is: " + hsv.getScrollX());
//        Log.i(TAG, "Scroll center is: " + (hsv.getScrollX() + screen_width / 2));
//        Log.i(TAG, "Sum is: " + (float) (hsv.getScrollX() + arg1.getX()));

        int TouchX = (int) (hsv.getScrollX() + arg1.getX());
        int TouchY = (int) arg1.getY();

        int GoalSetMounStart_1 = 2 * mountain_view_margin_left / 3 + (num_mountains - 1) * mountain_width * 2 / 3;
        if (arg1.getAction() == 1) //Action is MOVE
            enableGoalSettingTemporazer = 0;
        //mountain_width+(num_mountains-3)*2*mountain_width/3;
        if (!dayViewActive) {
            int goalSize = mountain_layout_height / 15;
            if (TouchX > GoalSetMounStart_1 && TouchX < GoalSetMounStart_1 + mountain_width
                    && mountain_layout_height - TouchY > Goals_data[num_mountains - 1] * mountain_layout_height / maxMountainHeight - mountain_layout_height / 5 && mountain_layout_height - TouchY < Goals_data[num_mountains - 1] * mountain_layout_height / maxMountainHeight + mountain_layout_height / 5) {
                //            WalkTime_sec[num_mountains-2]=(int) (mountain_layout_height-arg1.getY());
                //            long time = System.currentTimeMillis();
                Calendar c = Calendar.getInstance();

//                if (arg1.getAction() == 1) { //Action up
//                    //TODO use this to autoresize mountains
//                }

                if (c.get(Calendar.HOUR_OF_DAY) < GOAL_LIMIT_TIME && TouchY > goalSize && TouchY < mountain_layout_height - goalSize) {
                    if (enableGoalSettingTemporazer == enableGoalSettingThres) {
                        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        v.vibrate(300);
                    }
                    if (enableGoalSettingTemporazer > enableGoalSettingThres) {
                        Goals_data[Goals_data.length - 1] = (int) (mountain_layout_height - TouchY) * maxMountainHeight / mountain_layout_height;
                        Goals_data[Goals_data.length - 1] = ((int) Goals_data[Goals_data.length - 1] / 100) * 100;
                        performScroll = false;
                        drawGoalHint = true;
                        storeInDB(System.currentTimeMillis(), Goals_data[num_mountains - 1]);
                        mv.invalidate();
                    } else if (arg1.getAction() == 2) //Action is MOVE
                        enableGoalSettingTemporazer++;
                } else {
                    if (arg1.getAction() == 0
                            && mountain_layout_height - TouchY > Goals_data[num_mountains - 1] * mountain_layout_height / maxMountainHeight - mountain_layout_height / 15 && mountain_layout_height - TouchY < Goals_data[num_mountains - 1] * mountain_layout_height / maxMountainHeight + mountain_layout_height / 15) //Action down
                        Toast.makeText(this, getResources().getString(R.string.no_allow_goal_setting), Toast.LENGTH_LONG).show();
                }
            }
        }
        //Update view after 1s
//        else
        int touchMargin = 30;
        if ((previous_actions[2] == 0 || previous_actions[1] == 0 || previous_actions[0] == 0)
                && arg1.getAction() == 1
                && previous_coordX[2] - touchMargin < previous_coordX[1] && previous_coordX[2] + touchMargin > previous_coordX[1]
                && previous_coordY[2] - touchMargin < previous_coordY[1] && previous_coordY[2] + touchMargin > previous_coordY[1]
                ) {
            // If the action was a simple touch
            Log.i(TAG, "Scroll: " + hsv.getScrollX() + " Touch: " + TouchX);
            scrollPosition = TouchX - screen_width / 2;
            hsv.scrollTo(scrollPosition, 0);
            hsv_main.scrollTo(scrollPosition, 0);
            //Show day view
            ImageView bShowDayOverview = (ImageView) findViewById(R.id.bShowDayOverview);
            dayViewActive = true;
            bShowDayOverview.setBackgroundResource(R.drawable.arrow_down);
            updatePAdata(mountainLayoutHeightRatioSmall);
            drawMountains = true;
            drawDays = false;
            drawGoals = true;
            drawMountainView(false);
            dayViewActive = true;
            drawDailyView(true);
            fab.setVisibility(View.VISIBLE);

            drawMountains = true;
            drawGoals = true;
            mv.invalidate();
//            showDayInfo();
        } else {
            for (int i = 1; i < 2500; i += 200) {
                //If scroll only
                hsv.postDelayed(new Runnable() {
                    public void run() {
                        scrollPosition = hsv.getScrollX();
                        hsv_main.setScrollX(scrollPosition);
                        drawMountains = true;
                        drawGoals = true;
                        mv.invalidate();
                    }
                }, i);
            }
        }

        //DOWN=0, UP=1, MOVE=2
        previous_actions[0] = previous_actions[1];
        previous_actions[1] = previous_actions[2];
        previous_actions[2] = arg1.getAction();
        previous_coordX[0] = previous_coordX[1];
        previous_coordX[1] = previous_coordX[2];
        previous_coordX[2] = arg1.getX();
        previous_coordY[0] = previous_coordY[1];
        previous_coordY[1] = previous_coordY[2];
        previous_coordY[2] = arg1.getY();

        return !performScroll;
    }

    /**
     *
     */
    private void getGoalsData() {
        //Check if last day is today
//        Calendar c = Calendar.getInstance();
//        c.setTimeInMillis(System.currentTimeMillis());
//        long timestamp_aux = c.getTimeInMillis()-(c.get(Calendar.HOUR_OF_DAY)*3600*1000+c.get(Calendar.MINUTE)*60*1000+c.get(Calendar.SECOND)*1000+c.get(Calendar.MILLISECOND));
//        if(timestamp_aux == LogVectorDayResult.get(LogVectorDayResult.size()-1)) {
        Goals_data = new int[num_mountains];
        try {
            for (int i = 0; i < LogVectorGoals.size(); i++) {
                if (i == 0 && LogVectorGoals.get(i) < 1)
                    Goals_data[i] = DEFAULT_GOAL;
                else if (LogVectorGoals.get(i) < 1 && i < 7) {
                    int aux = 0;
                    for (int j = 0; j < i; j++)
                        aux += Goals_data[j];
                    Goals_data[i] = aux / (i);
                } else if (LogVectorGoals.get(i) < 1) {
                    int aux = 0;
                    for (int j = 0; j < 7; j++)
                        aux += Goals_data[j];
                    Goals_data[i] = aux / 7;
                } else
                    Goals_data[i] = LogVectorGoals.get(i);
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        //Get goal for today
//            Goals_data[num_mountains-1]=ui_MainActivity.dbhelp.getGoalData(timestamp_aux);
//        Log.i(TAG,"TODAY GOAL="+ui_MainActivity.dbhelp.getGoalData(timestamp_aux));
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        long timestamp_aux = c.getTimeInMillis() - (c.get(Calendar.HOUR_OF_DAY) * 3600 * 1000 + c.get(Calendar.MINUTE) * 60 * 1000 + c.get(Calendar.SECOND) * 1000 + c.get(Calendar.MILLISECOND));

        try {
            if (sql_db.precious.comnet.aalto.DBHelper.getInstance(this).getGoalData(timestamp_aux) > 10)
                Goals_data[num_mountains - 1] = sql_db.precious.comnet.aalto.DBHelper.getInstance(this).getGoalData(timestamp_aux);
            else
                Goals_data[num_mountains - 1] = DEFAULT_GOAL;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }

    }

    public void showDayInfo() {
        ImageView bShowDayOverview = (ImageView) findViewById(R.id.bShowDayOverview);
        if (!dayViewActive) {
            bShowDayOverview.setBackgroundResource(R.drawable.arrow_down);
            updatePAdata(mountainLayoutHeightRatioSmall);
            drawMountains = true;
            drawDays = false;
            drawGoals = true;
            drawMountainView(false);
            dayViewActive = true;
            drawDailyView(true);
            fab.setVisibility(View.VISIBLE);
        } else {
            bShowDayOverview.setBackgroundResource(R.drawable.arrow_up);
            updatePAdata(mountainLayoutHeightRatioBig);
            drawMountains = true;
            drawDays = true;
            drawGoals = true;
            drawMountainView(false);
            dayViewActive = false;
            drawDailyView(false);
            fab.setVisibility(View.GONE);
        }
    }

//    public void scalePA_data(){
//        Log.i(TAG,"layout height= "+mountain_layout_height+"; MaxValMount= "+maxMountainHeigh);
//        for (int i=0; i<WalkTime_sec.length;i++){
//            WalkTime_sec[i] = (WalkTime_sec[i]*mountain_layout_height/maxMountainHeigh);
//        }
//    }

    private void startAddActivity() {
        Intent i = new Intent(this, AddActivity.class);
        i.putExtra("date",selectedDayTimeMillis );
        startActivity(i);
    }

    /**
     *
     */
    public void storeInDB(long timestamp, int value) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        long timestamp_aux = c.getTimeInMillis() - (c.get(Calendar.HOUR_OF_DAY) * 3600 * 1000 + c.get(Calendar.MINUTE) * 60 * 1000 + c.get(Calendar.SECOND) * 1000 + c.get(Calendar.MILLISECOND));
        try {
            Log.i(TAG, "Storing in DB_" + timestamp_aux + "_" + value);
            sql_db.precious.comnet.aalto.DBHelper.getInstance(this).insertGoal(timestamp_aux, value);
            sql_db.precious.comnet.aalto.DBHelper.getInstance(this).updateGoal(timestamp_aux, value);
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

//    public void readDB(int Value) {
//        try {
//            DBHelper mydb;
//            mydb = new DBHelper(this);
//            Cursor rs = mydb.getData(Value);
//            rs.moveToFirst();
//
//            String nam = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_NAME));
//            String phon = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_PHONE));
//            String emai = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_EMAIL));
//            String stree = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_STREET));
//            String plac = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_CITY));
//
//            if (!rs.isClosed()) {
//                rs.close();
//            }
//
//            Log.i(TAG, "_" + nam + "_" + phon + "_" + emai + "_" + stree + "_" + plac + "_");
//        } catch (Exception e) {
//            Log.e(TAG, "", e);
//        }
//    }

    /**
     *
     */
    private void setDailyViewOnTouchListener() {
        dv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                for (int j = 0; j < paManualData.size(); j++) {
                    try {
                        if (paTouchRect[j].contains((int) event.getX(), (int) event.getY())) {
//                            System.out.println("Touched Rectangle, start activity.");
                            Intent i = new Intent(appConext, activity_tracker.precious.comnet.aalto.AddActivity.class);
                            i.putExtra("timestamp", paManualData.get(j).get(0));
                            startActivity(i);
                            Log.i(TAG, "RECT TOUCHED:_" + paManualData.get(j).get(4) + "_");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "_", e);
                    }
                }
                return false;
            }
        });
    }

    /**
     * @return
     */
    private void getPAvector() {

        //If day has changed, get pa info from db
//        if (prev_day_to_show != day_to_show) {
        stepsAcumul = 0;
        if (paManualData != null)
            paManualData.clear();
//            Log.i(TAG, ("DAYS:" + prev_day_to_show + "_" + day_to_show + "_" + LogVectorDayResult.get(day_to_show) +"_" + (long) (LogVectorDayResult.get(day_to_show) + 24 * 3600 * 1000)));
        try {
            prev_day_to_show = day_to_show;
            paManualData = sql_db.precious.comnet.aalto.DBHelper.getInstance(this).getManPA(
                    LogVectorDayResult.get(day_to_show), (long) (LogVectorDayResult.get(day_to_show) + 24 * 3600 * 1000)
            );
            for (int i = 0; i < paManualData.size(); i++) {
                stepsAcumul += (paManualData.get(i).get(4));
            }
        } catch (Exception e) {
            Log.e("loadVectors", " ", e);
        }

        //For WALK
        int walk_duration = LogVectorWalk.get(day_to_show);
        if (walk_duration > 0) {
            ArrayList<Long> aux = new ArrayList<>();
            aux.add(LogVectorDayResult.get(day_to_show));//timestamp
            aux.add((long) 27 - 1);//type
            aux.add((long) (1));//intensity
            aux.add((long) (walk_duration / 60));//duration
            aux.add((long) (walk_duration * 84 / 60));//steps
            paManualData.add(0, aux);
            stepsAcumul += walk_duration * 84 / 60;
        }
        //For RUN
        int run_duration = LogVectorRun.get(day_to_show);
        if (run_duration > 120) {
            ArrayList<Long> aux = new ArrayList<>();
            aux.add(LogVectorDayResult.get(day_to_show));//timestamp
            aux.add((long) 38 - 1);//type
            aux.add((long) (1));//intensity
            aux.add((long) (run_duration / 60));//duration
            aux.add((long) (run_duration * 222 / 60));//steps
            paManualData.add(0, aux);
            stepsAcumul += run_duration * 222 / 60;
        }
        //For CYCLING
        int bicycle_duration = LogVectorBicycle.get(day_to_show);
        if (bicycle_duration > 120) {
            ArrayList<Long> aux = new ArrayList<>();
            aux.add(LogVectorDayResult.get(day_to_show));//timestamp
            aux.add((long) 36 - 1);//type
            aux.add((long) (1));//intensity
            aux.add((long) (bicycle_duration / 60));//duration
            aux.add((long) (bicycle_duration * 170 / 60));//steps
            paManualData.add(0, aux);
            stepsAcumul += bicycle_duration * 170 / 60;
        }
//        }
    }

    /**
     *
     */
    public class MountainView extends View {
        public MountainView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            MountainViewActivity.setCanvas(canvas);
            updateCanvas(drawMountains, drawDays, drawGoals);
            drawMountains = true;
            drawDays = true;
            drawGoals = true;
            if (dayViewActive) {
                if (prev_day_to_show != day_to_show) {
                    drawDailyView(true);
                }
                drawDays = false;
            }
        }

        /**
         * @param drawMountains
         * @param drawDays
         * @param drawGoals
         */
        public void updateCanvas(boolean drawMountains, boolean drawDays, boolean drawGoals) {
//            int w = canvas.getWidth();
            int h = mainViewCanvas.getHeight();
            String dayWeek = "";
            String dayMonth = "";
            String month = "";
            String year = "";
            String monthYear = "";
            int mountain_pos_end;
            int mountain_pos_center;
            int walk_time_sec;
            int mountain_height;
            int goal_height;

//            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.precious_icon);
//            = b.getHeight();
//            int mountain_top_margin
            int x0_triangle;
            int textSize = (int) (tvSteps.getTextSize() * 0.9);//mountain_layout_height/20;
//            LinearGradient mountainShader
            Calendar calendar = Calendar.getInstance();
            for (int i = 0; i < num_mountains; i++) {
                //Get data
                x0_triangle = 2 * mountain_view_margin_left / 3 + i * mountain_width * 2 / 3;
                mountain_pos_center = x0_triangle + mountain_width / 2;
                mountain_pos_end = x0_triangle + mountain_width;
                int goalSize = mountain_layout_height / 30;

                try {
                    if (Goals_data[i] == -1)
                        goal_height = -1;
                    else
                        goal_height = Goals_data[i] * mountain_layout_height / maxMountainHeight;
                } catch (Exception e) {
                    Log.e(TAG, " ", e);
                    goal_height = -1;
                }

//                if (i < num_mountains-2) {
//                    walk_time_sec = LogVectorWalk.get(i);

                mountain_height = LogVectorSteps.get(i) * mountain_layout_height / maxMountainHeight;
//                mountain_height = 87 * walk_time_sec / 60 * mountain_layout_height / maxMountainHeight;

                calendar.setTimeInMillis(LogVectorDayResult.get(i));
                dayWeek = atUtils.getDayWeek(getApplicationContext(), calendar);
                dayMonth = atUtils.getDayMonth(calendar);
                month = atUtils.getMonth(calendar);
                year = atUtils.getYear(calendar);
                monthYear = atUtils.getMonth(getApplicationContext(), month).concat(",  ").concat(year);

                //                Log.i(TAG,"_current_day=_"+currentDay+"_"+currentMonth+"_"+currentYear+"_");
                //                Log.i(TAG,"_drawing_dat=_"+dayMonth+"_"+month+"_"+year+"_");

                boolean isSelectedDay = (scrollPosition + screen_width / 2) > 2 * mountain_view_margin_left / 3 + (i * (2 * mountain_width / 3) + mountain_width / 2 - 2 * mountain_width / 6)
                        && (scrollPosition + screen_width / 2) < 2 * mountain_view_margin_left / 3 + (i * (2 * mountain_width / 3) + mountain_width / 2 + 2 * mountain_width / 6);
                if (isSelectedDay) {
                    selectedDayTimeMillis=LogVectorDayResult.get(i);
                }

                if (drawMountains) {
                    //Declare lines
                    paint_lines[i] = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                    if (isSelectedDay)
                        paint_lines[i].setColor(getResources().getColor(R.color.selected_mountain_line));
                    else
                        paint_lines[i].setColor(getResources().getColor(R.color.mountain_line));
                    //                path_lines[i] = new Path();
                    //Declare mountains
                    paint_mountains[i] = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                    path_mountains[i] = new Path();
                    if (mountain_height < goal_height || goal_height == -1) {
                        paint_mountains[i].setShader(new LinearGradient(x0_triangle, 0,
                                mountain_pos_end, 0, getResources().getColor(R.color.mountainNotAchieved_start),
                                getResources().getColor(R.color.mountainNotAchieved_end), Shader.TileMode.CLAMP));
                        draw_flags[i] = false;

                    } else {
                        paint_mountains[i].setShader(new LinearGradient(x0_triangle, 0,
                                mountain_pos_end, 0, getResources().getColor(R.color.mountainAchieved_start),
                                getResources().getColor(R.color.mountainAchieved_end), Shader.TileMode.CLAMP));
                        draw_flags[i] = true;
                    }
                    path_mountains[i].moveTo(x0_triangle, h);//left corner of the triangle
                    path_mountains[i].lineTo(mountain_pos_end, h);//right corner
                    path_mountains[i].lineTo(mountain_pos_center, h - mountain_height);//upper corner
                    path_mountains[i].lineTo(x0_triangle, h);
                }
                //Declare days for TextView and change color of current day
                paint_days[i] = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                if (isSelectedDay) {
                    paint_days[i].setColor(getResources().getColor(R.color.selected_mountain_text));
                    day_to_show = i;
                    tvDayWeek.setText(dayWeek);
                    tvDayMonth.setText(dayMonth);
                    tvMonthYear.setText(monthYear);
                    tvSteps.setText(LogVectorSteps.get(i) + "/");
                    tvGoal.setTextColor(getResources().getColor(R.color.selfMonitoring));
                    if (Goals_data[i] < 1)
                        tvGoal.setText(getResources().getString(R.string.no_goal_set));
                    else
                        tvGoal.setText("" + Goals_data[i]);
                } else {
                    paint_days[i].setColor(getResources().getColor(R.color.mountain_text));
                }

                if (drawGoals) {
                    //Declare goals

                    paint_goals[i] = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                    paint_goals[i].setStyle(Paint.Style.FILL);
                    paint_goals[i].setColor(getResources().getColor(R.color.goalCircle));
                    if ((i == num_mountains - 1)) {
                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(System.currentTimeMillis());
                        if (c.get(Calendar.HOUR_OF_DAY) < GOAL_LIMIT_TIME)
                            goalSize = 0;//mountain_layout_height / 15;
                        else
                            goalSize = mountain_layout_height / 30;
                    } else {
                        goalSize = mountain_layout_height / 30;
                    }
                }


                //Draw lines mountains
                mainViewCanvas.drawPath(path_mountains[i], paint_mountains[i]);

                //Draw small white triangle
                if (i == day_to_show || i == day_to_show + 1) {
                    //Declare small triangle on the middle of the screen
                    path_white_triangle = new Path();
                    path_white_triangle.moveTo(scrollPosition + screen_width / 2 - h / 30, h);//left
                    path_white_triangle.lineTo(scrollPosition + screen_width / 2 + h / 30, h);//right
                    path_white_triangle.lineTo(scrollPosition + screen_width / 2, h - h / 30);//up
                    path_white_triangle.lineTo(scrollPosition + screen_width / 2 - h / 30, h);
                    mainViewCanvas.drawPath(path_white_triangle, paint_white_triangle);
                }
//                }
                //This is only for the today's goal setting
////                else {
////                    if (drawDays) {
////                        //draw goal
////                        paint_goals[i] = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
////                        paint_goals[i].setStyle(Paint.Style.FILL);
////                        paint_goals[i].setColor(0x77FFFFFF & getResources().getColor(R.color.goalCircle));
////                        goalSize = mountain_layout_height / 15;
////                        mainViewCanvas.drawCircle(mountain_pos_center, h - goal_height, goalSize, paint_goals[i]);
////                        //draw lines
////                        paint_lines[i] = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
////                        paint_lines[i].setColor(getResources().getColor(R.color.mountain_line));
////                        mainViewCanvas.drawLine((float) mountain_pos_center, (float) 1.3 * textSize, (float) mountain_pos_center, (float) mountain_layout_height, paint_lines[i]);
////                        //draw days
////                        paint_days[i] = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
////                        paint_days[i].setStyle(Paint.Style.FILL);
////                        paint_days[i].setTextSize(textSize);
////                        paint_days[i].setTextAlign(Paint.Align.CENTER);
////                        mainViewCanvas.drawText(getResources().getString(R.string.today), mountain_pos_center, textSize, paint_days[i]);
////                    }
//                }
                //Show text with the goal set
                if (drawGoalHint && i == num_mountains - 1) {
                    Paint paintGoalHint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                    paintGoalHint.setStyle(Paint.Style.FILL);
                    paintGoalHint.setTextSize(textSize);
                    paintGoalHint.setColor(getResources().getColor(R.color.selfMonitoring));
                    String goalValue = Goals_data[Goals_data.length - 1] + " " + getResources().getString(R.string.steps);

                    mainViewCanvas.drawText(getResources().getString(R.string.goal_set), mountain_pos_center - mountain_width, (float) (mountain_layout_height / 2 - textSize * 1.5), paintGoalHint);
                    mainViewCanvas.drawText(goalValue, mountain_pos_center - mountain_width, mountain_layout_height / 2, paintGoalHint);
                }

                //Draw text days
                if (drawDays) {
                    //draw lines
                    mainViewCanvas.drawLine((float) mountain_pos_center, (float) 1.3 * textSize, (float) mountain_pos_center, (float) mountain_layout_height - mountain_height, paint_lines[i]);
                    //draw days
                    paint_days[i].setStyle(Paint.Style.FILL);
                    paint_days[i].setTextSize(textSize);
                    paint_days[i].setTextAlign(Paint.Align.CENTER);
                    if ((i == num_mountains - 1) && currentDay.equals(dayMonth) && currentMonth.equals(month) && currentYear.equals(year))
                        mainViewCanvas.drawText(getResources().getString(R.string.today), mountain_pos_center, textSize, paint_days[i]);
                    else
                        mainViewCanvas.drawText(dayWeek.substring(0, 3), mountain_pos_center, textSize, paint_days[i]);
                } else
                    mainViewCanvas.drawLine((float) mountain_pos_center, (float) 0, (float) mountain_pos_center, (float) mountain_layout_height - mountain_height, paint_lines[i]);


                //Draw goals
                if (goal_height != -1 || i == num_mountains - 1) {
                    if (goalSize == 0) {
                        Bitmap bmp = BitmapFactory.decodeResource(this.getResources(),
                                R.drawable.mountains_target_button);
                        mainViewCanvas.drawBitmap(bmp, mountain_pos_center - bmp.getWidth() / 2, h - goal_height - bmp.getHeight() / 2, paint_goals[i]);
                    } else
                        mainViewCanvas.drawCircle(mountain_pos_center, h - goal_height, goalSize, paint_goals[i]);
                }
                //Draw flags flags
                if (draw_flags[i])
                    mainViewCanvas.drawBitmap(bmp_flag, mountain_pos_center - bmp_flag.getWidth() / 4, h - mountain_height - bmp_flag.getHeight(), paint_flags[i]);


            }
            drawMountains = true;
            drawDays = true;
            drawGoals = true;
            drawGoalHint = false;
            //TODO replace dot with diamond
            //TODO ice peak random generated
            //TODO add the day selector and change info based on the selected day
            //TODO add days and add "this week" "two weeks ago"
            //TODO goal setting
        }
    }

    /**
     *
     */
    public class DailyView extends View {
        public DailyView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {

            double pi = 3.14159265359;
            double centerX = 0.625 * screen_width;
            double centerY = 0.45 * spiral_layout_height;
            double spinStart = 5.5 * pi;
            double spiralLimit1 = 9.00 * pi;
            double spiralLimit2 = 10.00 * pi;
            double complete_circle = 2 * pi;

            getPAvector();
            pa_spiral_data = new double[paManualData.size() + 2];
            pa_spiral_data[0] = 0;
            pa_spiral_data[1] = spinStart;
            for (int i = 0; i < paManualData.size(); i++) {
                Log.i(TAG, ("Manual data " + i + "= " + paManualData.get(i).get(4)) + "");
                //Convert activity to steps
                int activityType = paManualData.get(i).get(1).intValue();
                int intensity = paManualData.get(i).get(2).intValue();
                int duration = paManualData.get(i).get(3).intValue();
                int steps = paManualData.get(i).get(4).intValue();
                pa_spiral_data[i + 2] = pa_spiral_data[i + 1] + steps * complete_circle / Goals_data[day_to_show];
            }

//            if (Goals_data[day_to_show]<1){
//                Paint circle_paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
//                circle_paint.setColor(getResources().getColor(R.color.spiral_circle));
//                canvas.drawCircle((float) centerX, (float) centerY, (float) (centerY * 0.4), circle_paint);
//                //Draw text
//                Paint paint_text = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
//                paint_text.setTextAlign(Paint.Align.CENTER);
//                paint_text.setColor(getResources().getColor(R.color.mountain_text));
//                paint_text.setTextSize((float) (centerY / 4));
//                String text = getResources().getString(R.string.no_goal_set);
//                //get text size
//                float[] font_size = new float[text.length()];
//                paint_text.getTextWidths(text, font_size);
//                float string_length = 0;
//                for (int i = 0; i < font_size.length; i++)
//                    string_length += font_size[i];
//                string_length /= font_size.length;
//                canvas.drawText(text, (float) centerX, (float) centerY
//                        - (paint_text.descent() + paint_text.ascent()) / 2, paint_text);
//                return;
//            }


            //Draw spiral progress
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            paint.setStyle(Paint.Style.FILL);
            Path path = new Path();
            int[] colors = {0xff00bc95, 0xffe96386, 0xffff866f, 0xff8237da, 0xffff9100, 0xff0098d9, 0xffb5498b,
                    0xffE040FB, 0xff8BC34A, 0xffE1BEE7, 0xff8BC34A, 0xff8BC34A}; //green, red, peach, violet, orange, blue, dark red  pink,green,dark pink,green
//            colors[2] = getResources().getColor(R.color.spiral_walking);
            double growing_rate = spiral_layout_height / 42.5 / 2;
            double x, y;
//            for (int j = 1; j < pa_spiral_data.length; j++) {
            for (int j = pa_spiral_data.length - 1; j > 1; j--) {
                path = new Path();
                double prevX = centerX, prevY = centerY;
                path.moveTo((float) centerX, (float) centerY);
                //If spiral limit is exceeded
                if (pa_spiral_data[j] >= spiralLimit1) {
                    double limit = (pa_spiral_data[j] > spiralLimit2) ? spiralLimit2 : pa_spiral_data[j];
                    for (double t = spiralLimit1 - 0.1; t <= limit; t += 0.03) {
                        path = new Path();
                        path.moveTo((float) centerX, (float) centerY);
                        path.lineTo((float) prevX, (float) prevY);
                        x = centerX + growing_rate * t * Math.cos(t);
                        y = centerY + growing_rate * t * Math.sin(t);
                        path.lineTo((float) x, (float) y);
                        prevX = x;
                        prevY = y;
                        path.moveTo((float) centerX, (float) centerY);
                        int color_mask = (int) (((spiralLimit2 - t) / (spiralLimit2 - spiralLimit1)) * 0xFF);
                        color_mask = (color_mask << 24) + 0x00FFFFFF;
                        paint.setColor(color_mask & colors[j]);
//                        paint.setColor(getResources().getColor(R.color.spiral_walking));
                        canvas.drawPath(path, paint);
                    }

                    path = new Path();
                    path.moveTo((float) centerX, (float) centerY);
                    for (double t = pa_spiral_data[j - 1] - 0.1; t <= spiralLimit1 + 0.1; t += 0.1) {
                        x = centerX + growing_rate * t * Math.cos(t);
                        y = centerY + growing_rate * t * Math.sin(t);
                        path.lineTo((float) x, (float) y);
                    }
                    path.moveTo((float) centerX, (float) centerY);
                    paint.setColor(colors[j]);
                    canvas.drawPath(path, paint);
                    //break;
//                    continue;
                } else {
                    for (double t = pa_spiral_data[j - 1] - 0.1; t <= pa_spiral_data[j]; t += 0.1) {
                        x = centerX + growing_rate * t * Math.cos(t);
                        y = centerY + growing_rate * t * Math.sin(t);
                        path.lineTo((float) x, (float) y);
                    }
                    path.moveTo((float) centerX, (float) centerY);
                    paint.setColor(colors[j]);
                    canvas.drawPath(path, paint);
                }
            }

            //Draw contours
            Paint border_paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            border_paint.setColor(getResources().getColor(R.color.spiral_contours));
            double prevX = centerX + growing_rate * spinStart * Math.cos(spinStart);
            double prevY = centerY + growing_rate * spinStart * Math.sin(spinStart);
            for (double t = pa_spiral_data[1] - 0.1; t < pa_spiral_data[pa_spiral_data.length - 1]; t += 0.1) {
//            for (double t = spinStart; t < data[data.length-1]; t +=0.1) {
                if (t >= spiralLimit1)
                    break;
                x = centerX + growing_rate * t * Math.cos(t);
                y = centerY + growing_rate * t * Math.sin(t);
                path.lineTo((float) x, (float) y);
                canvas.drawLine((float) prevX, (float) prevY, (float) x, (float) y, border_paint);
                prevX = x;
                prevY = y;
            }
            Paint circle_paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            circle_paint.setColor(getResources().getColor(R.color.spiral_circle));
            canvas.drawCircle((float) centerX, (float) centerY, (float) (centerY * 0.4), circle_paint);

            //Draw goal % text
            Paint paint_text = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            paint_text.setTextAlign(Paint.Align.CENTER);
            paint_text.setColor(getResources().getColor(R.color.mountain_text));
            paint_text.setTextSize((float) (centerY / 4));
            String text = (int) (100 * (double) stepsAcumul / (double) Goals_data[day_to_show]) + "%";
            //get text size
            float[] font_size = new float[text.length()];
            paint_text.getTextWidths(text, font_size);
            float string_length = 0;
            for (int i = 0; i < font_size.length; i++)
                string_length += font_size[i];
            string_length /= font_size.length;
            canvas.drawText(text, (float) centerX, (float) centerY
                    - (paint_text.descent() + paint_text.ascent()) / 2, paint_text);

            //Draw pa name + duration text
//            for (int j=pa_spiral_data.length-1;j>1;j--) {
            Paint pa_paint = new Paint();
            paTouchRect = new Rect[paManualData.size()];
            for (int j = 0; j < paManualData.size(); j++) {
                String[] pa_names = getResources().getStringArray(R.array.pa_names);
//                int a = ;
                if ((paManualData.get(j).get(1)).intValue() == -1)
                    continue;
                String iconName = pa_names[(paManualData.get(j).get(1)).intValue()];
                String ActivityTypeNoSpace = iconName.replace(" ", "_");
                ActivityTypeNoSpace = ActivityTypeNoSpace.replace("á", "a");
                ActivityTypeNoSpace = ActivityTypeNoSpace.replace("é", "e");
                ActivityTypeNoSpace = ActivityTypeNoSpace.replace("í", "i");
                ActivityTypeNoSpace = ActivityTypeNoSpace.replace("ó", "o");
                ActivityTypeNoSpace = ActivityTypeNoSpace.replace("ú", "u");


//                String iconName = "activity"+a+"x48";´---
                try {
                    int icon_id = getResources().getIdentifier(ActivityTypeNoSpace, "drawable", appConext.getPackageName());
                    Bitmap bmp = BitmapFactory.decodeResource(appConext.getResources(), icon_id);
//                    ColorFilter filter = new LightingColorFilter(colors[j], 1);
                    int leftMargin = 5;//15;
                    double upper_margin = 1;//20;
                    double vertical_alingment;
                    if (paManualData.size() >= 5)
                        vertical_alingment = 0;
                    else
                        vertical_alingment = (5 - paManualData.size()) * spiral_layout_height / 5 / 2;
//                    Log.i(TAG,"VER ALING=_"+vertical_alingment+"_");
                    int iconSize = 4 * bmp.getHeight() / 5;
                    pa_paint.setColorFilter(new PorterDuffColorFilter(colors[j + 2], PorterDuff.Mode.SRC_ATOP));
                    Rect rectangle = new Rect(leftMargin, (int) vertical_alingment + (int) (j * (iconSize + upper_margin)), leftMargin + iconSize, (int) vertical_alingment + (int) (j * (iconSize + upper_margin)) + iconSize);
                    canvas.drawBitmap(bmp, new Rect(0, 0, bmp.getHeight(), bmp.getWidth()), rectangle, pa_paint);

                    int textSize = iconSize / 2;
                    pa_paint.setTextSize(textSize);
                    String stringDuration = paManualData.get(j).get(3) + "" + getString(R.string.minutes);
                    String stringSteps = paManualData.get(j).get(4) + "" + getString(R.string.steps_lowercase);

                    canvas.drawText(stringDuration, leftMargin + iconSize + leftMargin, (int) (j * (iconSize + upper_margin)) + 3 * textSize / 4 + (int) vertical_alingment, pa_paint);
                    canvas.drawText(stringSteps, leftMargin + iconSize + leftMargin, (int) (j * (iconSize + upper_margin)) + 3 * textSize / 4 + textSize + (int) vertical_alingment, pa_paint);

                    //Update PA touch rect
                    paTouchRect[j] = new Rect(leftMargin, (int) vertical_alingment + (int) (j * (iconSize + upper_margin)), spiral_layout_width / 4, (int) vertical_alingment + (int) (j * (iconSize + upper_margin)) + iconSize);

                } catch (Exception e) {
                    Log.e(TAG, "_", e);
                }
//                Log.i(TAG, "SPIRAL:_" + j + "_=_" + pa_names[a-1]);
//                Log.i(TAG,"ICON NAME:_"+iconName+"_");
            }
        }
    }
}


