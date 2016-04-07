package activity_tracker.precious.comnet.aalto;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;
import java.util.Vector;

import ui.precious.comnet.aalto.precious.R;

public class MountainViewActivity extends Activity implements View.OnTouchListener {
    public static Context appConext;
    public static final String PREFS_NAME = "IRsubappPreferences";
    public static final String TAG = "MountainViewActivity";
    private static int screen_width;
    private static int screen_height;
    private static int num_mountains;
    private static int mountain_width;
    private static int mountain_layout_width;
    private static int mountain_layout_height;
    private static int spiral_layout_width;
    private static int spiral_layout_height;
    private static int mountain_top_margin;
    private static final int maxMountainHeight = 10000;
    private static int scrollPosition=-1;


    private int [] Goals_data;
    /*
     * PA data
     */
    //Vector for data storage
    private static Vector<String> LogVectorDateTimeline = new Vector<String>();
    private static Vector <String[]> LogVectorDayResult = new Vector<String[]>();
    private static Vector <String> LogVectorStill = new Vector<String>();
    private static Vector <String> LogVectorWalk = new Vector<String>();
    private static Vector <String> LogVectorBicycle = new Vector<String>();
    private static Vector <String> LogVectorVehicle = new Vector<String>();
    private static Vector <String> LogVectorRun = new Vector<String>();
    private static Vector <String> LogVectorTilting = new Vector<String>();
    /*
     * Views
     */
    private MountainView mv;
    private HorizontalScrollView hsv;
    private int [] previous_actions = new int[3];
    public TextView tvDayWeek ;
    public TextView tvDayMonth;
    public TextView tvMonthYear;
    private TextView tvSteps;
    private TextView tvGoal;
    private boolean dayViewActive;
    private DailyView dv;
    /*
     * For the mountain canvas view
     */
    public static Canvas mainViewCanvas;
    private Paint [] paint_lines;
    private Paint [] paint_mountains;
    private Paint [] paint_days;
    private Paint [] paint_goals;
//    private Paint [] paint_rewards;
//    private Path [] path_lines;
    private Path [] path_mountains;
//    private Path [] path_goals;
//    private Path [] path_rewards;
    boolean drawMountains;
    boolean drawDays;
    boolean drawGoals;
    /*
     * For the daily canvas view
     */
    int day_to_show=0;

    public boolean randomDataGenetared=false;//TODO delete

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mountain_view_activity);
        //Declare views
        hsv = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        tvDayWeek =((TextView) findViewById(R.id.textViewDayWeek));
        tvDayMonth =((TextView) findViewById(R.id.textViewDayMonth));
        tvMonthYear =((TextView) findViewById(R.id.textViewMonthYear));
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
        //Get application context
        appConext=getApplicationContext();
        //Get PA data
        dayViewActive=false;
        updatePAdata(0.7);
//        //Set onClick listener to Textview14 and relative layout
        FrameLayout fl = (FrameLayout) findViewById(R.id.frameLayout);
//        rl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDayInfo();
//            }
//        });
        fl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showDayInfo();
                return false;
            }
        });

//        TextView tv14 = (TextView) findViewById(R.id.textView14);
//        tv14.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                showDayInfo();
//                return false;
//            }
//        });
    }

    /**
     *
     */
    @Override
    public void onResume(){
        super.onResume();
        drawMountainView(true);
        drawDailyView(false);
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
        super.onPause();
    }


    /**
     *
     */
    private void updatePAdata(double mountainHeighRatio) {
        //        atUtils.startLocationUpdates(this);TODO
        //Get Info
        atUtils.getLog(this);
        LogVectorDateTimeline = atUtils.getLogVectorDateTimeline();
        LogVectorDayResult = atUtils.getLogVectorDayResult();
        LogVectorStill = atUtils.getLogVectorStill();
        LogVectorWalk = atUtils.getLogVectorWalk();
        LogVectorBicycle = atUtils.getLogVectorBicycle();
        LogVectorVehicle = atUtils.getLogVectorVehicle();
        LogVectorRun = atUtils.getLogVectorRun();
        LogVectorTilting = atUtils.getLogVectorTilting();
//        for(int cont=0;cont<LogVectorDateTimeline.size();cont++)
//            Log.i("TIMELINE", LogVectorDateTimeline.get(cont));
        num_mountains = LogVectorDayResult.size();
        //Generate random goals
        generatePAdata();
        //Init canvas view objects
        paint_lines = new Paint[num_mountains];
        paint_mountains = new Paint[num_mountains];
        paint_goals = new Paint[num_mountains];
//        paint_rewards = new Paint[num_mountains];
        paint_days = new Paint[num_mountains];
//        path_lines = new Path[num_mountains];
        path_mountains = new Path[num_mountains];
//        path_goals = new Path[num_mountains];
//        path_rewards = new Path[num_mountains];

        //Get screen size and calculate object sizes
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screen_width = size.x;
        screen_height = size.y;
        mountain_layout_height = (int) ( mountainHeighRatio*(double)screen_height); //900
        mountain_width = screen_width/3;
        mountain_layout_width = (int)(mountain_width+(2*mountain_width/3)*(num_mountains-3)+mountain_width*2);//90000;
        mountain_top_margin = screen_height/7;

        drawMountains=true;
        drawDays=true;
        drawGoals=true;
    }
    /**
     *
     */
    void drawMountainView(boolean autoScroll){

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
        if(autoScroll) {
            hsv.postDelayed(new Runnable() {
                public void run() {
                    hsv.scrollTo(mountain_layout_width - mountain_width * 4, 0);
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
    public class MountainView extends View {
        public MountainView (Context context) {
            super(context);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            MountainViewActivity.setCanvas(canvas);
            updateCanvas(drawMountains, drawDays, drawGoals);
            drawMountains=true;
            drawDays=true;
            drawGoals=true;
            if(dayViewActive)
                drawDailyView(true);
        }
        public void updateCanvas(boolean drawMountains, boolean drawDays, boolean drawGoals){
//            int w = canvas.getWidth();
            int h = mainViewCanvas.getHeight();
            String dayWeek="";
            String dayMonth="";
            String monthYear="";
            int mountain_pos_init;
            int mountain_pos_center;
            int walk_time_sec;
            int mountain_height;
            int goal_height;
//            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.precious_icon);
//            = b.getHeight();
//            int mountain_top_margin
            int x0_triangle;
            int textSize = (int)tvSteps.getTextSize();//mountain_layout_height/20;
//            LinearGradient mountainShader
            for (int i=0; i<num_mountains; i++){
                //Get data
                x0_triangle = i * mountain_width * 2 / 3;
                mountain_pos_center = x0_triangle+mountain_width/2;
                mountain_pos_init= x0_triangle + mountain_width;
                walk_time_sec = Integer.parseInt(LogVectorWalk.get(i));
                mountain_height = Integer.parseInt(LogVectorWalk.get(i))/60*80* mountain_layout_height /maxMountainHeight;
                goal_height = Goals_data[i]* mountain_layout_height /maxMountainHeight;
                dayWeek = LogVectorDayResult.get(i)[0];
                dayMonth = LogVectorDayResult.get(i)[1];
                monthYear = atUtils.getMonth(LogVectorDayResult.get(i)[2]).concat(",  ").concat(LogVectorDayResult.get(i)[3]);
                if(drawMountains) {
                    //Declare lines
                    paint_lines[i] = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                    paint_lines[i].setColor(getResources().getColor(R.color.mountain_line));
                    //                path_lines[i] = new Path();
                    //Declare mountains
                    paint_mountains[i] = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                    path_mountains[i] = new Path();
                    if(mountain_height<goal_height)
                        paint_mountains[i].setShader(new LinearGradient(x0_triangle, 0,
                                mountain_pos_init, 0, getResources().getColor(R.color.mountainNotAchieved_start),
                                getResources().getColor(R.color.mountainNotAchieved_end), Shader.TileMode.CLAMP));
                    else
                        paint_mountains[i].setShader(new LinearGradient(x0_triangle, 0,
                                mountain_pos_init, 0, getResources().getColor(R.color.mountainAchieved_start),
                                getResources().getColor(R.color.mountainAchieved_end), Shader.TileMode.CLAMP));
                    path_mountains[i].moveTo(x0_triangle, h);
                    path_mountains[i].lineTo(mountain_pos_init, h);
                    path_mountains[i].lineTo(mountain_pos_center, h - mountain_height);
                    path_mountains[i].lineTo(x0_triangle, h);
                    //Declare goals
                    paint_goals[i] = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                    paint_goals[i].setStyle(Paint.Style.FILL);
                    paint_goals[i].setColor(getResources().getColor(R.color.goalCircle));
                }
                //Draw lines, mountains and goals

                mainViewCanvas.drawLine((float) mountain_pos_center, (float) 2 * textSize, (float) mountain_pos_center, (float) mountain_layout_height - mountain_height, paint_lines[i]);
                mainViewCanvas.drawPath(path_mountains[i], paint_mountains[i]);
                mainViewCanvas.drawCircle(mountain_pos_center, h -goal_height , mountain_layout_height / 30, paint_goals[i]);

                if(drawDays) {
                    //Declare days
                    paint_days[i] = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                    if ((scrollPosition + screen_width / 2) > (i * (2 * mountain_width / 3) + mountain_width / 2 - 2 * mountain_width / 6)
                            && (scrollPosition + screen_width / 2) < (i * (2 * mountain_width / 3) + mountain_width / 2 + 2 * mountain_width / 6)) {
                        paint_days[i].setColor(Color.YELLOW);
                        day_to_show=i;
                        //Change name of the day
//                        tvDayWeek.setTextSize((int) (textSize / 1.5));
                        tvDayWeek.setText(dayWeek);
//                        tvDayMonth.setTextSize(textSize);
                        tvDayMonth.setText(dayMonth);
//                        tvMonthYear.setTextSize((int) (textSize / 1.5));
                        tvMonthYear.setText(monthYear);
//                        tvSteps.setTextSize(textSize);
                        tvSteps.setText((walk_time_sec / 60 * 80) + "/");
//                        tvGoal.setTextSize((int) (textSize / 1.5));
                        tvGoal.setTextColor(getResources().getColor(R.color.selfMonitoring));
                        tvGoal.setText(""+Goals_data[i]);
                    } else
                        paint_days[i].setColor(Color.BLACK);

                    paint_days[i].setStyle(Paint.Style.FILL);
                    paint_days[i].setTextSize(textSize);
                }
                //Draw days
                paint_days[i].setTextAlign(Paint.Align.CENTER);
                mainViewCanvas.drawText(dayWeek.substring(0,3), mountain_pos_center, textSize, paint_days[i]);

                if(drawGoals) {
                }
            }
            //TODO replace dot with diamond
            //TODO ice peak random generated
            //TODO round mountains???
            //TODO add the day selector and change info based on the selected day
            //TODO add days and add "this week" "two weeks ago"
            //TODO goal setting
        }
    }


    /**
     *
     */
    void drawDailyView(boolean drawView) {
        RelativeLayout rl2 = (RelativeLayout) findViewById(R.id.dayInfoLayout);
        rl2.setOnTouchListener(this);
        if(drawView) {
            rl2.setVisibility(View.VISIBLE);
            rl2.postDelayed(new Runnable() {
                public void run() {
                    RelativeLayout rl2 = (RelativeLayout) findViewById(R.id.dayInfoLayout);
                    spiral_layout_height = rl2.getHeight();
                    spiral_layout_width = rl2.getWidth();
                    rl2.setBackgroundColor(getResources().getColor(R.color.day_info_background));
                    dv = new DailyView(appConext);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(spiral_layout_width, spiral_layout_height); //RelativeLayout.LayoutParams.WRAP_CONTENT);
                    dv.setLayoutParams(params);
                    rl2.removeAllViews();
                    rl2.addView(dv);
                }
            }, 10);
        }
        else rl2.setVisibility(View.GONE);
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
            double centerX=screen_width/2;
            double centerY= spiral_layout_height /2;
            double spinStart=3.5*pi;
            double complete_circle=2*pi;
            int walk_time_sec = Integer.parseInt(LogVectorWalk.get(day_to_show));
            int steps = walk_time_sec*80/60;

            //Draw spiral progress
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            paint.setStyle(Paint.Style.FILL);
            Path path = new Path();
            double [] data ={0,spinStart,spinStart+complete_circle*steps/Goals_data[day_to_show]};
            int [] colors = {0xffE040FB,0xff8BC34A,0xffE1BEE7,0xff8BC34A,0xff8BC34A}; //pink,green,dark pink,green
            colors[2]=getResources().getColor(R.color.spiral_walking);
            double growing_rate=spiral_layout_height/42.5;
            double x,y;
            for(int j=1;j<data.length;j++) {
                path = new Path();
                path.moveTo((float) centerX, (float) centerY);
                for (double t = data[j-1]; t < data[j]; t+=0.1) {
                    x = centerX +  growing_rate*t * Math.cos(t);
                    y = centerY +  growing_rate*t * Math.sin(t);
                    path.lineTo((float) x, (float) y);
                }
                path.moveTo((float) centerX, (float) centerY);
                paint.setColor(colors[j]);
                canvas.drawPath(path, paint);
            }

            //Draw contours
            Paint border_paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            border_paint.setColor(getResources().getColor(R.color.spiral_contours));
            double prevX=centerX +  growing_rate*spinStart * Math.cos(spinStart);
            double prevY=centerY +  growing_rate*spinStart * Math.sin(spinStart);
            for (double t = spinStart; t < data[data.length-1]; t +=0.1) {
                x = centerX +  growing_rate*t * Math.cos(t);
                y = centerY +  growing_rate*t * Math.sin(t);
                path.lineTo( (float)x, (float) y);
                canvas.drawLine((float) prevX, (float) prevY, (float) x, (float) y, border_paint);
                prevX=x;
                prevY=y;
            }
            Paint circle_paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            circle_paint.setColor(getResources().getColor(R.color.spiral_circle));
            canvas.drawCircle((float) centerX, (float) centerY, (float) (centerY / 2), circle_paint);

            //Draw text
            Paint paint_text = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            paint_text.setTextAlign(Paint.Align.CENTER);
            paint_text.setColor(getResources().getColor(R.color.mountain_text));
            paint_text.setTextSize((float) (centerY/3));
            String text = (int)(100*(double)steps/(double)Goals_data[day_to_show])+"%";
            //get text size
            float [] font_size = new float[text.length()];
            paint_text.getTextWidths(text,font_size);
            float string_length=0;
            for(int i=0; i<font_size.length;i++)
                string_length+=font_size[i];
            string_length/=font_size.length;
            canvas.drawText(text,(float) centerX,(float)centerY
                    -(paint_text.descent()+paint_text.ascent())/2,paint_text);

        }
    }

    /**
     *
     */
    public boolean onTouch(View arg0, MotionEvent arg1) {
        Boolean performScroll=true;
        // TODO Auto-generated method stub
        Log.i(TAG, "Touch event: " + arg1.toString());
        Log.i(TAG,"Action: "+arg1.getAction());
        Log.i(TAG, "Scroll is: " + hsv.getScrollX());
        Log.i(TAG, "Scroll center is: " + (hsv.getScrollX()+screen_width/2));
        Log.i(TAG, "Sum is: "+(float)(hsv.getScrollX()+arg1.getX()));

        int TouchX = (int)(hsv.getScrollX()+arg1.getX());
        int TouchY = (int)arg1.getY();

//        int GoalSetMounStart = mountain_width+(num_mountains-3)*2*mountain_width/3;
//        if( TouchX > GoalSetMounStart && TouchX<GoalSetMounStart+mountain_width
//                && mountain_layout_height-TouchY > Goals_data[num_mountains-2]-mountain_layout_height/5 && mountain_layout_height-TouchY < Goals_data[num_mountains-2]+mountain_layout_height/5) {
//            WalkTime_sec[num_mountains-2]=(int) (mountain_layout_height-arg1.getY());
//            Goals_data[num_mountains-2]=(int) (mountain_layout_height-arg1.getY());
//            performScroll=false;
//            mv.invalidate();
//        }
//        else if( TouchX > GoalSetMounStart+mountain_width && TouchX<GoalSetMounStart+2*mountain_width
//                && mountain_layout_height-TouchY > Goals_data[num_mountains-1]-mountain_layout_height/5 && mountain_layout_height-TouchY < Goals_data[num_mountains-1]+mountain_layout_height/5) {
//            WalkTime_sec[num_mountains - 1] = (int) (mountain_layout_height - arg1.getY());
//            Goals_data[num_mountains - 1] = (int) (mountain_layout_height - arg1.getY());
//
//            performScroll=false;
//            mv.invalidate();
//        }
        //Update view after 1s
//        else
        if ( (previous_actions[2]==0 || previous_actions[1]==0 || previous_actions[0]==0)
                && arg1.getAction()==1){
        // If the action was a simple touch
            Log.i(TAG,"Scroll: "+hsv.getScrollX()+" Touch: "+TouchX);
            scrollPosition = TouchX-screen_width/2;
            hsv.scrollTo(scrollPosition, 0);
            drawMountains=true;
            drawGoals=true;
            mv.invalidate();
//            showDayInfo();
        }
        else {
            for (int i=1; i<2500; i+=200){
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
        }



        //DOWN=0, UP=1, MOVE=2
        previous_actions[0]=previous_actions[1];
        previous_actions[1]=previous_actions[2];
        previous_actions[2]=arg1.getAction();
        return !performScroll;
    }
    /**
     *
     */
    private void generatePAdata(){
        if(!randomDataGenetared) {
            Goals_data = new int[num_mountains];
            Random randomGenerator = new Random();
            for (int i = 0; i < num_mountains - 2; i++) {
                Goals_data[i] = 5000+ 40 * randomGenerator.nextInt(100 - 1) + 1; //random number
            }
//        WalkTime_sec[num_mountains-2]=mountain_layout_height/2;
//        WalkTime_sec[num_mountains-1]=mountain_layout_height/2;
            Goals_data[num_mountains - 2] = 5000;
            Goals_data[num_mountains - 1] = 5000;
            randomDataGenetared=true;
        }
    }

//    public void scalePA_data(){
//        Log.i(TAG,"layout height= "+mountain_layout_height+"; MaxValMount= "+maxMountainHeigh);
//        for (int i=0; i<WalkTime_sec.length;i++){
//            WalkTime_sec[i] = (WalkTime_sec[i]*mountain_layout_height/maxMountainHeigh);
//        }
//    }





    public void showDayInfo(){
        TextView tv = (TextView) findViewById(R.id.textView14);
        if(!dayViewActive) {
            tv.setText("v");
            updatePAdata(0.33);
            drawMountains=true;
            drawDays=true;
            drawGoals=true;
            drawMountainView(false);
            dayViewActive= true;
            drawDailyView(true);
        }
        else{
            tv.setText("ᴧ");
            updatePAdata(0.7);
            drawMountains=true;
            drawDays=true;
            drawGoals=true;
            drawMountainView(false);
            dayViewActive=false;
            drawDailyView(false);
        }
    }




    public static void setCanvas(Canvas canvas){
       MountainViewActivity.mainViewCanvas = canvas;
    }
}

