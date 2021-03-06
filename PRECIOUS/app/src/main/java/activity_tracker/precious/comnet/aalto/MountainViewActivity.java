package activity_tracker.precious.comnet.aalto;


import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import aalto.comnet.thepreciousproject.R;

//For PA type-steps conversion: http://www.purdue.edu/walktothemoon/activities.html
public class MountainViewActivity extends Activity implements View.OnTouchListener {
    public static final String PREFS_NAME = "IRsubappPreferences";
    public static final String PREFS_NAME_AT = "ATsubappPreferences";
    public static final String TAG = "MountainViewActivity";
    public static final double mountainLayoutHeightRatioBig = 0.675;
    public static final double mountainLayoutHeightRatioSmall = 0.35;
    private static final int PA_GOAL_REMINDER_NOTIF_ID = 100035;
    private static final int maxGoalHeight = 15000;
    private static final int maxMountainHeight = 1000 * (int) (5 * maxGoalHeight / 4000);
    private static final int GOAL_LIMIT_TIME = 23;
    private static final int DEFAULT_GOAL = 7000;
    public static Context appConext;
    public static Context mContext;
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
    private static ArrayList<ArrayList<Long>> paPlannedData;
    private static double[] pa_spiral_data = null;
    private final int enableGoalSettingThres = 9;
    public TextView tvDayWeek;
    public TextView tvDayMonth;
    public TextView tvMonthYear;
    public static Vector<Integer> LogVectorSteps = new Vector<>();
    public static boolean drawMountains;
    public static boolean drawDays;
    public static boolean drawGoals;
    public static boolean drawGoalHint;
    private static int[] Goals_data;
    /*
     * Views
     */
    private static MountainView mv;
    private static HorizontalScrollView hsv;
    private int[] previous_actions = new int[3];
    private float[] previous_coordX = new float[3];
    private float[] previous_coordY = new float[3];
    private int enableGoalSettingTemporazer;
    private TextView tvSteps;
    private TextView tvGoal;
    public static boolean dayViewActive;
    private DailyView dv;
    private Rect[] paTouchRect;
    private static FloatingActionButton fab;
    private static FloatingActionButton fab_log;
    private static FloatingActionButton fab_plan;
    private static TextView tv_log;
    private static TextView tv_plan;
    private static Paint[] paint_lines;
    private static Paint[] paint_mountains;
    private static Paint[] paint_days;
    private static Paint[] paint_goals;
    private static Paint[] paint_flags;
    private static Boolean[] draw_flags;
    private static Bitmap bmp_flag;
    private static Paint paint_white_triangle;
    //    private Paint [] paint_rewards;
//    private Path [] path_lines;
    private static Path[] path_mountains;
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

    private static boolean goalSetting; //true if user is currenly setting the goal
    private static Button goalSettingButton;

    public static ImageView bShowDayOverview;
    public static RelativeLayout rlShowDayOverview;

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
        tv_log = (TextView) findViewById(R.id.tv_log);
        fab_log = (FloatingActionButton) findViewById(R.id.fab_mountain_log);
        fab_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddActivity();
            }
        });
        fab_plan = (FloatingActionButton) findViewById(R.id.fab_mountain_plan);
        tv_plan = (TextView) findViewById(R.id.tv_plan);
        fab_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlanActivity();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (fab_log.getVisibility() == View.GONE) {
                   fab_log.setVisibility(View.VISIBLE);
                   fab_plan.setVisibility(View.VISIBLE);
                   tv_log.setVisibility(View.VISIBLE);
                   tv_plan.setVisibility(View.VISIBLE);
               } else {
                   fab_log.setVisibility(View.GONE);
                   fab_plan.setVisibility(View.GONE);
                   tv_log.setVisibility(View.GONE);
                   tv_plan.setVisibility(View.GONE);
               }
           }
       });

    //Declare views
    hsv = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
    tvDayWeek = ((TextView) findViewById(R.id.textViewDayWeek));
    tvDayMonth = ((TextView) findViewById(R.id.textViewDayMonth));
    tvMonthYear = ((TextView) findViewById(R.id.textViewMonthYear));
    tvSteps = ((TextView) findViewById(R.id.textViewSteps));
    tvGoal = ((TextView) findViewById(R.id.textViewGoal));
    goalSetting=false;
    goalSettingButton = (Button) findViewById(R.id.goalSettingButton);
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
    //Set onClick listener on info button
    ImageButton ib = (ImageButton) findViewById(R.id.showTutorial);
    ib.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startTutorial();
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
    mContext = this;
    //Get PA data
    dayViewActive = false;
    drawMountains = true;
    drawDays = true;
    drawGoals = true;
    drawGoalHint = false;
//    updatePAdata(mountainLayoutHeightRatioBig);
    enableGoalSettingTemporazer = 0;
//        //Set onClick listener to relative layout
    rlShowDayOverview = (RelativeLayout) findViewById(R.id.rlShowDayOverview);
    rlShowDayOverview.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            showDayInfo();
            return false;
        }
    });
    bShowDayOverview = (ImageView) findViewById(R.id.bShowDayOverview);
//        bShowDayOverview.setOnTouc
// hListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                showDayInfo();
//                return false;
//            }
//        });


    //CHAPUZAAA! YUHUUUU!
    hsv.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            for (int i = 0; i < 2000; i += 400) {
                //If scroll only
                hsv.postDelayed(new Runnable() {
                    public void run() {
                        scrollPosition = hsv.getScrollX();
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
                    sql_db.precious.comnet.aalto.DBHelper.getInstance().insertAppUsage(System.currentTimeMillis(), TAG, "onResume");
                } catch (Exception e) {
                    Log.e(TAG, " ", e);
                }

                SharedPreferences at_preferences =  this.getSharedPreferences(PREFS_NAME_AT, 0);
                if(!at_preferences.getBoolean("at_tutorial_completed",false)){
                    startTutorial();
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
                    sql_db.precious.comnet.aalto.DBHelper.getInstance().insertAppUsage(System.currentTimeMillis(), TAG, "onPause");
                } catch (Exception e) {
                    Log.e(TAG, " ", e);
                }
                super.onPause();
            }

            /**
             *
             */
            public static void updatePAdata(double mountainHeighRatio) {
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
                    Toast.makeText(mContext, "No activity data yet", Toast.LENGTH_LONG).show();
                    ((Activity)mContext).finish();
                }
                getGoalsData();

                //Init canvas view objects
                paint_lines = new Paint[num_mountains + 1];
                paint_mountains = new Paint[num_mountains];
                paint_goals = new Paint[num_mountains + 1];
                paint_flags = new Paint[num_mountains + 1];
                draw_flags = new Boolean[num_mountains + 1];
                bmp_flag = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.flag_65);
                paint_white_triangle = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                paint_white_triangle.setColor(mContext.getResources().getColor(R.color.white_triangle));
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
            public void drawMountainView(boolean autoScroll) {
                //
                RelativeLayout rl_mountain = (RelativeLayout) findViewById(R.id.RelativeLayoutMountains);
                rl_mountain.getLayoutParams().width = mountain_layout_width;  // change width of the layout

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
            public void drawDailyView(boolean drawView) {
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
                    if (TouchX > GoalSetMounStart_1 && TouchX < GoalSetMounStart_1 + mountain_width) {
                        //            WalkTime_sec[num_mountains-2]=(int) (mountain_layout_height-arg1.getY());
                        //            long time = System.currentTimeMillis();
                        Calendar c = Calendar.getInstance();

//                if (arg1.getAction() == 1) { //Action up
//                    //TODO use this to autoresize mountains
//                }
                        if (goalSetting) {
                            Goals_data[Goals_data.length - 1] = (int) (mountain_layout_height - TouchY) * maxMountainHeight / mountain_layout_height;
                            Goals_data[Goals_data.length - 1] = ((int) Goals_data[Goals_data.length - 1] / 100) * 100;
                            if(Goals_data[Goals_data.length - 1]<1)
                                Goals_data[Goals_data.length - 1]=0;
                            performScroll = false;
                            drawGoalHint = true;
                            storeInDB(System.currentTimeMillis(), Goals_data[num_mountains - 1]);
                            mv.invalidate();
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
                    if(!goalSetting) {
                        Log.i(TAG, "Scroll: " + hsv.getScrollX() + " Touch: " + TouchX);
                        scrollPosition = TouchX - screen_width / 2;
                        hsv.scrollTo(scrollPosition, 0);
                        //Show day view
                        bShowDayOverview = (ImageView) findViewById(R.id.bShowDayOverview);
                        dayViewActive = true;
                        bShowDayOverview.setBackgroundResource(R.drawable.arrow_down);
                        updatePAdata(mountainLayoutHeightRatioSmall);
                        drawMountains = true;
                        drawDays = false;
                        drawGoals = true;
                        drawMountainView(false);
                        dayViewActive = true;
                        drawDailyView(true);
//                        fab.setVisibility(View.VISIBLE);

                        drawMountains = true;
                        drawGoals = true;
                        mv.invalidate();
                    }
//            showDayInfo();
                } else {
                    for (int i = 1; i < 2500; i += 200) {
                        //If scroll only
                        hsv.postDelayed(new Runnable() {
                            public void run() {
                                scrollPosition = hsv.getScrollX();
                                hsv.setScrollX(scrollPosition);
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
            private static void getGoalsData() {
                //Check if last day is today
//        Calendar c = Calendar.getInstance();
//        c.setTimeInMillis(System.currentTimeMillis());
//        long timestamp_aux = c.getTimeInMillis()-(c.get(Calendar.HOUR_OF_DAY)*3600*1000+c.get(Calendar.MINUTE)*60*1000+c.get(Calendar.SECOND)*1000+c.get(Calendar.MILLISECOND));
//        if(timestamp_aux == LogVectorDayResult.get(LogVectorDayResult.size()-1)) {
                Goals_data = new int[LogVectorGoals.size()];
                try {
                    for (int i = 0; i < LogVectorGoals.size(); i++) {
                        if (i == 0 && LogVectorGoals.get(i) < 1)
                            Goals_data[i] = DEFAULT_GOAL;
                        else if (LogVectorGoals.get(i) < 1 && i < 7) {
                            int aux = 0;
                            for (int j = 0; j < i; j++)
                                aux += LogVectorSteps.get(j);
                            Goals_data[i] = aux / (i);
                        } else if (LogVectorGoals.get(i) < 1) {
                            int aux = 0;
                            int divider=0;
                            for (int j = i-7; j < i; j++) {
                                if(LogVectorSteps.get(j)>0)
                                {
                                    aux += LogVectorSteps.get(j);
                                    divider++;
                                }
                            }
                            if(divider==0)
                                Goals_data[i] = DEFAULT_GOAL;
                            else if(aux / divider <1000)
                                Goals_data[i] = DEFAULT_GOAL;
                            else
                                Goals_data[i] = aux / divider;
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
                    if (sql_db.precious.comnet.aalto.DBHelper.getInstance().getGoalData(timestamp_aux) > 10)
                        Goals_data[num_mountains - 1] = sql_db.precious.comnet.aalto.DBHelper.getInstance().getGoalData(timestamp_aux);
                    else
                        Goals_data[num_mountains - 1] = DEFAULT_GOAL;
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                }

            }

            public void showDayInfo() {
                bShowDayOverview = (ImageView) findViewById(R.id.bShowDayOverview);
                if (!dayViewActive && !goalSetting) {
                    bShowDayOverview.setBackgroundResource(R.drawable.arrow_down);
                    updatePAdata(mountainLayoutHeightRatioSmall);
                    drawMountains = true;
                    drawDays = false;
                    drawGoals = true;
                    drawMountainView(false);
                    dayViewActive = true;
                    drawDailyView(true);
//                    fab.setVisibility(View.VISIBLE);
                } else {
                    bShowDayOverview.setBackgroundResource(R.drawable.arrow_up);
                    updatePAdata(mountainLayoutHeightRatioBig);
                    drawMountains = true;
                    drawDays = true;
                    drawGoals = true;
                    drawMountainView(false);
                    dayViewActive = false;
                    drawDailyView(false);
//                    fab.setVisibility(View.GONE);
                    fab_log.setVisibility(View.GONE);
                    fab_plan.setVisibility(View.GONE);
                    tv_log.setVisibility(View.GONE);
                    tv_plan.setVisibility(View.GONE);
                }
            }

//    public void scalePA_data(){
//        Log.i(TAG,"layout height= "+mountain_layout_height+"; MaxValMount= "+maxMountainHeigh);
//        for (int i=0; i<WalkTime_sec.length;i++){
//            WalkTime_sec[i] = (WalkTime_sec[i]*mountain_layout_height/maxMountainHeigh);
//        }
//    }

    public void startAddActivity() {
        fab_plan.setVisibility(View.GONE);
        tv_plan.setVisibility(View.GONE);
        fab_log.setVisibility(View.GONE);
        tv_log.setVisibility(View.GONE);
        Intent i = new Intent(mContext, AddActivity.class);
        i.putExtra("date",selectedDayTimeMillis );
        startActivity(i);
    }

    public void startPlanActivity() {
        fab_plan.setVisibility(View.GONE);
        tv_plan.setVisibility(View.GONE);
        fab_log.setVisibility(View.GONE);
        tv_log.setVisibility(View.GONE);
        Intent i = new Intent(mContext, PlanActivity.class);
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
                    sql_db.precious.comnet.aalto.DBHelper.getInstance().insertGoal(timestamp_aux, value);
                    sql_db.precious.comnet.aalto.DBHelper.getInstance().updateGoal(timestamp_aux, value);
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
                        for (int j = paManualData.size(); j < paManualData.size()+paPlannedData.size(); j++) {
                            try {
                                if (paTouchRect[j].contains((int) event.getX(), (int) event.getY())) {
//                            System.out.println("Touched Rectangle, start activity.");
                                    Intent i = new Intent(appConext, activity_tracker.precious.comnet.aalto.PlanActivity.class);
                                    i.putExtra("timestamp", paPlannedData.get(j-paManualData.size()).get(0));
                                    i.putExtra("planned_activity_touched",true);
                                    startActivity(i);
                                    Log.i(TAG, " PLANNED RECT TOUCHED:_" + paPlannedData.get(j-paManualData.size()).get(4) + "_");
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
                LogVectorDayResult = atUtils.getLogVectorDayResult();
                LogVectorSteps = atUtils.getLogVectorSteps();
                LogVectorStill = atUtils.getLogVectorStill();
                LogVectorWalk = atUtils.getLogVectorWalk();
                LogVectorBicycle = atUtils.getLogVectorBicycle();
                LogVectorVehicle = atUtils.getLogVectorVehicle();
                LogVectorRun = atUtils.getLogVectorRun();
                LogVectorTilting = atUtils.getLogVectorTilting();
                LogVectorGoals = atUtils.getLogVectorGoals();
                //If day has changed, get pa info from db
//        if (prev_day_to_show != day_to_show) {
                stepsAcumul = 0;
                if (paManualData != null)
                    paManualData.clear();
                if(paPlannedData != null)
                    paPlannedData.clear();
//            Log.i(TAG, ("DAYS:" + prev_day_to_show + "_" + day_to_show + "_" + LogVectorDayResult.get(day_to_show) +"_" + (long) (LogVectorDayResult.get(day_to_show) + 24 * 3600 * 1000)));
                try {
                    prev_day_to_show = day_to_show;
                    paManualData = sql_db.precious.comnet.aalto.DBHelper.getInstance().getManPA(
                            LogVectorDayResult.get(day_to_show), (long) (LogVectorDayResult.get(day_to_show) + 24 * 3600 * 1000)
                    );
                    paPlannedData = sql_db.precious.comnet.aalto.DBHelper.getInstance().getPlannedPA(
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
                    aux.add((long) 0);//type
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
                    aux.add((long) 1);//type
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
                    aux.add((long) 2);//type
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
                    for (int i = 0; i < LogVectorDayResult.size(); i++) {
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
                        if(mountain_height>2*mountain_layout_height)
                            mountain_height=2*mountain_layout_height;
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
                            try {
                                selectedDayTimeMillis = LogVectorDayResult.get(i);
                            }catch (Exception e){
                                Log.e(TAG," ",e);
                            }
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
                                if(mountain_height>screen_height/20)
                                    draw_flags[i] = true;
                                else
                                    draw_flags[i] = false;
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
                        if (goalSetting && i == num_mountains - 1) {
                            Paint paintGoalHint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                            paintGoalHint.setStyle(Paint.Style.FILL);
                            paintGoalHint.setTextSize((int)(1.2*textSize));
                            paintGoalHint.setColor(getResources().getColor(R.color.selfMonitoring));
                            String goalValue = Goals_data[Goals_data.length - 1] + " " + getResources().getString(R.string.steps);

                            mainViewCanvas.drawText(getResources().getString(R.string.goal_set), mountain_pos_center - (int)(1.2*mountain_width), (float) (mountain_layout_height / 2 - textSize * 1.5), paintGoalHint);
                            mainViewCanvas.drawText(goalValue, mountain_pos_center - (int)(1.2*mountain_width), mountain_layout_height / 2, paintGoalHint);
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
                            if (goalSetting && i == num_mountains - 1) {
                                Bitmap bmp = BitmapFactory.decodeResource(this.getResources(),
                                        R.drawable.mountains_target_button);
                                mainViewCanvas.drawBitmap(bmp, mountain_pos_center - bmp.getWidth() / 2, h - goal_height - bmp.getHeight() / 2, paint_goals[i]);
                            } else
                                mainViewCanvas.drawCircle(mountain_pos_center, h - goal_height, goalSize, paint_goals[i]);
                        }
                        //Draw flags flags
                        if (draw_flags[i]) {
                            mainViewCanvas.drawBitmap(bmp_flag, mountain_pos_center - bmp_flag.getWidth() / 4, h - mountain_height - bmp_flag.getHeight(), paint_flags[i]);
                        }


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
                    try {
                        if(paManualData==null){
                            paManualData = new ArrayList<>();
                        }
                        if(paPlannedData==null)
                            paPlannedData = new ArrayList<>();
                        pa_spiral_data = new double[paManualData.size()+paPlannedData.size() + 2];
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
                        Log.i(TAG,"paManualData.size()="+paManualData.size()+";paManualData.size()+paPlannedData.size()="+(paManualData.size()+paPlannedData.size()));
                        for (int i = paManualData.size(); i < (paManualData.size()+paPlannedData.size()); i++) {
                            Log.i(TAG, ("Planned data " + i + "= " + paPlannedData.get(i-paManualData.size()).get(4)) + "");
                            //Convert activity to steps
                            int activityType = paPlannedData.get(i-paManualData.size()).get(1).intValue();
                            int intensity = paPlannedData.get(i-paManualData.size()).get(2).intValue();
                            int duration = paPlannedData.get(i-paManualData.size()).get(3).intValue();
                            int steps = paPlannedData.get(i-paManualData.size()).get(4).intValue();
                            pa_spiral_data[i + 2] = pa_spiral_data[i + 1] + steps * complete_circle / Goals_data[day_to_show];
                        }
                    }catch (Exception e){
                        Log.e("TAG"," ",e);
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
                            0xffE040FB, 0xff8BC34A, 0xffE1BEE7, 0xff8BC34A, 0xff8BC34A}; //green, red, peach, violet,
                    for(int i=0; i<colors.length;i++){
                        if(i>paManualData.size()-1+2)
                            colors[i]=0xFF777777;
                    }
                    // orange, blue, dark red  pink,green,dark pink,green
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
                    paTouchRect = new Rect[paManualData.size()+paPlannedData.size()];
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
                                vertical_alingment = (5 - paManualData.size()-paPlannedData.size()) * spiral_layout_height / 5 / 2;
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
                    }
                    for (int j = paManualData.size(); j < paPlannedData.size()+paManualData.size(); j++) {
                        String[] pa_names = getResources().getStringArray(R.array.pa_names);
//                int a = ;
                        if ((paPlannedData.get(j-paManualData.size()).get(1)).intValue() == -1)
                            continue;
                        String iconName = pa_names[(paPlannedData.get(j-paManualData.size()).get(1)).intValue()];
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
                            if (paPlannedData.size() >= 5)
                                vertical_alingment = 0;
                            else
                                vertical_alingment = (5 - paManualData.size()-paPlannedData.size()) * spiral_layout_height / 5 / 2;
//                    Log.i(TAG,"VER ALING=_"+vertical_alingment+"_");
                            int iconSize = 4 * bmp.getHeight() / 5;
                            pa_paint.setColorFilter(new PorterDuffColorFilter(colors[j + 2], PorterDuff.Mode.SRC_ATOP));
                            Rect rectangle = new Rect(leftMargin, (int) vertical_alingment + (int) (j * (iconSize + upper_margin)), leftMargin + iconSize, (int) vertical_alingment + (int) (j * (iconSize + upper_margin)) + iconSize);
                            canvas.drawBitmap(bmp, new Rect(0, 0, bmp.getHeight(), bmp.getWidth()), rectangle, pa_paint);

                            int textSize = iconSize / 2;
                            pa_paint.setTextSize(textSize);
                            String stringDuration = paPlannedData.get(j-paManualData.size()).get(3) + "" + getString(R.string.minutes);
                            String stringSteps = paPlannedData.get(j-paManualData.size()).get(4) + "" + getString(R.string.steps_lowercase);

                            canvas.drawText(stringDuration, leftMargin + iconSize + leftMargin, (int) (j * (iconSize + upper_margin)) + 3 * textSize / 4 + (int) vertical_alingment, pa_paint);
                            canvas.drawText(stringSteps, leftMargin + iconSize + leftMargin, (int) (j * (iconSize + upper_margin)) + 3 * textSize / 4 + textSize + (int) vertical_alingment, pa_paint);

                            //Update PA touch rect
                            paTouchRect[j] = new Rect(leftMargin, (int) vertical_alingment + (int) (j * (iconSize + upper_margin)), spiral_layout_width / 4, (int) vertical_alingment + (int) (j * (iconSize + upper_margin)) + iconSize);

                        } catch (Exception e) {
                            Log.e(TAG, "_", e);
                        }
                    }
                }
            }

            /**
             *
             */
            public static void startTutorial(){
                hsv.postDelayed(new Runnable() {
                    public void run() {
                        hsv.scrollTo(mountain_layout_width - screen_width / 2, 0);
                    }
                }, 500L);
                hsv.postDelayed(new Runnable() {
                    public void run() {
                        scrollPosition = hsv.getScrollX();
                        mv.invalidate();
                    }
                }, 1000L);
                if(dayViewActive) {
                    long downTime = SystemClock.uptimeMillis();
                    long eventTime = SystemClock.uptimeMillis() + 100;
                    float x = 0.0f;
                    float y = 0.0f;
// List of meta states found here: developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
                    int metaState = 0;
                    MotionEvent motionEvent = MotionEvent.obtain(
                            downTime,
                            eventTime,
                            MotionEvent.ACTION_UP,
                            x,
                            y,
                            metaState
                    );
                    // Dispatch touch event to view
                    rlShowDayOverview.dispatchTouchEvent(motionEvent);
                }

                final ShowcaseView showcaseView;
                showcaseView = new ShowcaseView.Builder((Activity)mContext)
                        .withMaterialShowcase()
                        .setContentTitle(mContext.getString(R.string.mountain_view_part1_title))
                        .setContentText(mContext.getString(R.string.mountain_view_part1_content))
                        .blockAllTouches()
//                .setTarget(target)
                        .setStyle(R.style.ShowcaseTheme_dark)
                        .build();
                showcaseView.setButtonText(mContext.getString(R.string.next));

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
                                //Create a dummy mountain
                                LogVectorSteps.set(LogVectorSteps.size()-1,3500);
                                mv.invalidate();
                                showcaseView.setContentTitle(mContext.getString(R.string.mountain_view_part2_title));
                                showcaseView.setContentText(mContext.getString(R.string.mountain_view_part2_content));
                                Target target = new Target() {
                                    @Override
                                    public Point getPoint() {
                                        return new Point(screen_width/2,3*screen_height/4);
                                    }
                                };
                                showcaseView.setShowcase(target, false);
                                showcaseView.offsetTopAndBottom(100);

//                            ContextualHelper.setContextualHelpPrefForMultipleItem(getActivity(), IPreferenceConstants.PREF_CH_SHARE);
//                            if (!allThree) {
//                                showcaseView.setButtonText(getString(R.string.ch_got_it));
//                            }
                                break;
                            case 2:
                                //Restore original mountain
                                updatePAdata(mountainLayoutHeightRatioBig);
                                mv.invalidate();

                                showcaseView.setContentTitle(mContext.getString(R.string.mountain_view_part3_title));
                                showcaseView.setContentText(mContext.getString(R.string.mountain_view_part3_content));
                                Target target2 = new ViewTarget(R.id.goalSettingButton, (Activity)mContext);
                                showcaseView.setShowcase(target2, false);
                                break;
                            case 3:
                                //Restore original mountain
                                goalSetting=true;
                                goalSettingButton.setText(R.string.ok);
                                updatePAdata(mountainLayoutHeightRatioBig);
                                mv.invalidate();
//                                fab.setVisibility(View.VISIBLE);

                                showcaseView.setContentTitle(mContext.getString(R.string.mountain_view_part4_title));
                                showcaseView.setContentText(mContext.getString(R.string.mountain_view_part4_content));
                                Target target3 = new Target() {
                                    @Override
                                    public Point getPoint() {
                                        return new Point(screen_width/2,screen_height/2+screen_height/20);
                                    }
                                };
                                showcaseView.setShowcase(target3, false);
                                break;
                            case 4:
                                //Restore original mountain
                                goalSetting=true;
                                goalSettingButton.setText(R.string.ok);
                                updatePAdata(mountainLayoutHeightRatioBig);
                                mv.invalidate();

                                showcaseView.setContentTitle(mContext.getString(R.string.mountain_view_part5_title));
                                showcaseView.setContentText(mContext.getString(R.string.mountain_view_part5_content));
                                Target target4 = new ViewTarget(R.id.goalSettingButton, (Activity)mContext);
                                showcaseView.setShowcase(target4, false);
                                break;
                            case 5:
                                //Show fab
//                                fab.setVisibility(View.INVISIBLE);
                                goalSetting=false;
                                goalSettingButton.setText(R.string.set_goal);
                                showcaseView.setContentTitle(mContext.getString(R.string.mountain_view_part6_title));
                                showcaseView.setContentText(mContext.getString(R.string.mountain_view_part6_content));
                                Target target5 = new ViewTarget(R.id.ll_steps, (Activity)mContext);
                                showcaseView.setShowcase(target5, false);
                                break;
                            case 6:
                                //Hide fab
//                                fab.setVisibility(View.VISIBLE);
                                showcaseView.setContentTitle(mContext.getString(R.string.mountain_view_part7_title));
                                showcaseView.setContentText(mContext.getString(R.string.mountain_view_part7_content));
                                Target target6 = new ViewTarget(R.id.fab_mountain, (Activity)mContext);
                                showcaseView.setShowcase(target6, false);
                                break;
                            case 7:
                                //Hide fab
//                                fab.setVisibility(View.VISIBLE);
                                showcaseView.setContentTitle(mContext.getString(R.string.mountain_view_part8_title));
                                showcaseView.setContentText(mContext.getString(R.string.mountain_view_part8_content));
                                Target target7 = new ViewTarget(R.id.fab_mountain, (Activity)mContext);
                                showcaseView.setShowcase(target7, false);
                                break;
                            case 8:
                                showcaseView.setContentTitle(mContext.getString(R.string.mountain_view_part9_title));
                                showcaseView.setContentText(mContext.getString(R.string.mountain_view_part9_content));
                                Target target8 = new ViewTarget(R.id.bShowDayOverview, (Activity)mContext);
                                showcaseView.setShowcase(target8, false);
                                break;
                            case 9:
                                //Show day info
                                // Obtain MotionEvent object
                                long downTime = SystemClock.uptimeMillis();
                                long eventTime = SystemClock.uptimeMillis() + 100;
                                float x = 0.0f;
                                float y = 0.0f;
// List of meta states found here: developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
                                int metaState = 0;
                                MotionEvent motionEvent = MotionEvent.obtain(
                                        downTime,
                                        eventTime,
                                        MotionEvent.ACTION_UP,
                                        x,
                                        y,
                                        metaState
                                );
                                // Dispatch touch event to view
                                rlShowDayOverview.dispatchTouchEvent(motionEvent);

//                        //Create a dummy mountain
//                        LogVectorSteps.set(LogVectorSteps.size()-1,3500);
//                        mv.invalidate();
//                        drawDailyView(true);
                                showcaseView.setContentTitle(mContext.getString(R.string.mountain_view_part10_title));
                                showcaseView.setContentText(mContext.getString(R.string.mountain_view_part10_content));
                                Target target9 = new Target() {
                                    @Override
                                    public Point getPoint() {
                                        return new Point(screen_width/2+screen_width/8,25*screen_height/26);
                                    }
                                };
                                showcaseView.setShowcase(target9, false);
                                break;
                            case 10:
                                showcaseView.setContentTitle(mContext.getString(R.string.mountain_view_part11_title));
                                showcaseView.setContentText(mContext.getString(R.string.mountain_view_part11_content));
                                break;
                            case 11:
//                        //Restore original mountain
//                        updatePAdata(mountainLayoutHeightRatioBig);
//                        mv.invalidate();
                                //Hide day info
                                // Obtain MotionEvent object
                                long downTime2 = SystemClock.uptimeMillis();
                                long eventTime2 = SystemClock.uptimeMillis() + 100;
                                float x2 = 0.0f;
                                float y2 = 0.0f;
                                // List of meta states found here: developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
                                int metaState2 = 0;
                                MotionEvent motionEvent2 = MotionEvent.obtain(
                                        downTime2,
                                        eventTime2,
                                        MotionEvent.ACTION_UP,
                                        x2,
                                        y2,
                                        metaState2
                                );

                                // Dispatch touch event to view
                                rlShowDayOverview.dispatchTouchEvent(motionEvent2);

                                SharedPreferences at_preferences =  mContext.getSharedPreferences(PREFS_NAME_AT, 0);
                                SharedPreferences.Editor editor = at_preferences.edit();
                                editor.putBoolean("at_tutorial_completed",true);
                                editor.apply();

                                showcaseView.hide();
                                break;
                        }
                    }
                });
            }
    public static void toggleGoalSetting(View v){
        goalSetting = !goalSetting;
        mv.invalidate();
        if(goalSetting)
            goalSettingButton.setText(R.string.ok);
        else
            goalSettingButton.setText(R.string.set_goal);
    }
        }
