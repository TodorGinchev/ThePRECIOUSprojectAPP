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
    private static int layout_width;
    private static int layout_height; // depends on variable screen_height
    private static int mountain_top_margin;
    private static final int maxMountainHeight = 10000;
    private static int scrollPosition=-1;


    private int [] Goals_data;
    /*
     * PA data
     */
    //Vector for data storage
    private static Vector<String> LogVectorDateTimeline = new Vector<String>();
    private static Vector <String> LogVectorDayResult = new Vector<String>();
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
    private TextView tvDay;
    private TextView tvSteps;
    private int [] previous_actions = new int[3];
    /*
     * For the canvas view
     */
    public static Canvas canvas;
    private Paint [] paint_lines;
    private Paint [] paint_mountains;
    private Paint [] paint_days;
    private Paint [] paint_goals;
    private Paint [] paint_rewards;
    private Path [] path_lines;
    private Path [] path_mountains;
    private Path [] path_goals;
    private Path [] path_rewards;
    boolean drawMountains;
    boolean drawDays;
    boolean drawGoals;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mountain_view_activity);
        //Declare views
        tvDay =((TextView) findViewById(R.id.textViewDay));
        tvSteps =((TextView) findViewById(R.id.textViewSteps));
        hsv = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
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
        updatePAdata();
    }

    /**
     *
     */
    @Override
    public void onResume(){
        super.onResume();
        drawMountainView();
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
                Log.i(TAG,"?????"+event.toString());
                return false;
            }
        });
        super.onPause();
    }


    /**
     *
     */
    private void updatePAdata() {
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

        //Init canvas view objects
        paint_lines = new Paint[num_mountains];
        paint_mountains = new Paint[num_mountains];
        paint_goals = new Paint[num_mountains];
        paint_rewards = new Paint[num_mountains];
        paint_days = new Paint[num_mountains];
        path_lines = new Path[num_mountains];
        path_mountains = new Path[num_mountains];
        path_goals = new Path[num_mountains];
        path_rewards = new Path[num_mountains];

        //Get screen size and calculate object sizes
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screen_width = size.x;
        screen_height = size.y;
        layout_height = (int) 2*screen_height/3; //900
        mountain_width = screen_width/3;
        layout_width = (int)(mountain_width+(2*mountain_width/3)*(num_mountains-3)+mountain_width*2);//90000;
        mountain_top_margin = screen_height/10;
        generatePAdata();

        drawMountains=true;
        drawDays=true;
        drawGoals=true;
    }
    /**
     *
     */
    void drawMountainView(){
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.RelativeLayoutMountains);
        rl.getLayoutParams().height = layout_height;  // change height of the layout
        rl.getLayoutParams().width = layout_width;  // change width of the layout
        rl.setBackgroundColor(0xaab3e5fc);
        //Set click listener for the scroll view
        hsv.setOnTouchListener(this);

        mv = new MountainView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(layout_width,layout_height); //RelativeLayout.LayoutParams.WRAP_CONTENT);
        mv.setLayoutParams(params);
        rl.addView(mv);

        //Auto scroll effect
        hsv.postDelayed(new Runnable() {
            public void run() {
                hsv.scrollTo(layout_width - mountain_width * 4, 0);
            }
        }, 500L);
        hsv.postDelayed(new Runnable() {
            public void run() {
                scrollPosition = hsv.getScrollX();
                mv.invalidate();
            }
        }, 1000L);

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
        }

        public void updateCanvas(boolean drawMountains, boolean drawDays, boolean drawGoals){

//            int w = canvas.getWidth();
            int h = canvas.getHeight();
            String day="";
            int mountain_pos_init;
            int mountain_pos_center;
            int walk_time_sec;
            int mountain_height;
//            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.precious_icon);
//            = b.getHeight();
//            int mountain_top_margin
            int x0_triangle;
            int textSize = layout_height/20;
//            LinearGradient mountainShader
            for (int i=0; i<num_mountains; i++){
                //Get data
                x0_triangle = i * mountain_width * 2 / 3;
                mountain_pos_center = x0_triangle+mountain_width/2;
                mountain_pos_init= x0_triangle + mountain_width;
                walk_time_sec = Integer.parseInt(LogVectorWalk.get(i));
                mountain_height = Integer.parseInt(LogVectorWalk.get(i))*layout_height/maxMountainHeight;
                day = LogVectorDayResult.get(i);
                if(drawMountains) {
                    //Declare lines
                    paint_lines[i] = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                    //                path_lines[i] = new Path();
                    //Declare mountains
                    paint_mountains[i] = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                    path_mountains[i] = new Path();
                    paint_mountains[i].setShader(new LinearGradient(x0_triangle, 0, mountain_pos_init, 0, 0xffdcedc8, 0xff689f38, Shader.TileMode.CLAMP));
                    path_mountains[i].moveTo(x0_triangle, h);
                    path_mountains[i].lineTo(mountain_pos_init, h);
                    path_mountains[i].lineTo(mountain_pos_center, h - mountain_height);
                    path_mountains[i].lineTo(x0_triangle, h);
                    //Declare goals
                    paint_goals[i] = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                    paint_goals[i].setStyle(Paint.Style.FILL);
                    paint_goals[i].setColor(0x99ff0000);
                }
                //Draw lines, mountains and goals
                canvas.drawLine((float) mountain_pos_center, (float) 2 * textSize, (float) mountain_pos_center, (float) layout_height - mountain_height, paint_lines[i]);
                canvas.drawPath(path_mountains[i], paint_mountains[i]);
                canvas.drawCircle(mountain_pos_center, h - Goals_data[i], mountain_width / 10, paint_goals[i]);

                if(drawDays) {
                    //Declare days
                    paint_days[i] = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                    if ((scrollPosition + screen_width / 2) > (i * (2 * mountain_width / 3) + mountain_width / 2 - 2 * mountain_width / 6)
                            && (scrollPosition + screen_width / 2) < (i * (2 * mountain_width / 3) + mountain_width / 2 + 2 * mountain_width / 6)) {
                        paint_days[i].setColor(Color.YELLOW);
                        //Change name of the day
                        tvDay.setText(day);
                        tvSteps.setText((walk_time_sec / 60 * 80) + "/" + (Goals_data[i] *
                                maxMountainHeight / (layout_height - mountain_top_margin) / 60 * 80));
                    } else
                        paint_days[i].setColor(Color.BLACK);

                    paint_days[i].setStyle(Paint.Style.FILL);
                    paint_days[i].setTextSize(textSize);
                }
                //Draw days
                canvas.drawText(day, mountain_pos_center - textSize, textSize, paint_days[i]);

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
//                && layout_height-TouchY > Goals_data[num_mountains-2]-layout_height/5 && layout_height-TouchY < Goals_data[num_mountains-2]+layout_height/5) {
//            WalkTime_sec[num_mountains-2]=(int) (layout_height-arg1.getY());
//            Goals_data[num_mountains-2]=(int) (layout_height-arg1.getY());
//            performScroll=false;
//            mv.invalidate();
//        }
//        else if( TouchX > GoalSetMounStart+mountain_width && TouchX<GoalSetMounStart+2*mountain_width
//                && layout_height-TouchY > Goals_data[num_mountains-1]-layout_height/5 && layout_height-TouchY < Goals_data[num_mountains-1]+layout_height/5) {
//            WalkTime_sec[num_mountains - 1] = (int) (layout_height - arg1.getY());
//            Goals_data[num_mountains - 1] = (int) (layout_height - arg1.getY());
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
            drawMountains=false;
            drawGoals=false;
            mv.invalidate();
        }
        else
        //If scroll only
            hsv.postDelayed(new Runnable() {
                public void run() {
                scrollPosition = hsv.getScrollX();
                    drawMountains=false;
                    drawGoals=false;
                    mv.invalidate();
                }
            }, 1000L);



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
        Goals_data = new int[num_mountains];
        Random randomGenerator = new Random();
        for(int i=0;i<num_mountains-2;i++) {
            Goals_data[i] = randomGenerator.nextInt((layout_height) - 10 - mountain_top_margin) + 10; //random number
        }
//        WalkTime_sec[num_mountains-2]=layout_height/2;
//        WalkTime_sec[num_mountains-1]=layout_height/2;
        Goals_data[num_mountains-2]=layout_height/2;
        Goals_data[num_mountains-1]=layout_height/2;
    }

//    public void scalePA_data(){
//        Log.i(TAG,"layout height= "+layout_height+"; MaxValMount= "+maxMountainHeigh);
//        for (int i=0; i<WalkTime_sec.length;i++){
//            WalkTime_sec[i] = (WalkTime_sec[i]*layout_height/maxMountainHeigh);
//        }
//    }
    public static void setCanvas(Canvas canvas){
       MountainViewActivity.canvas = canvas;
    }
}

