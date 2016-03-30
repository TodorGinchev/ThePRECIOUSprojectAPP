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
    private static int maxMountainHeigh;
    private static int scrollPosition=-1;

    private int [] PA_data;
    private int [] Goals_data;

    private TextView tvDay;
    private TextView tvSteps;

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
    //Text view to show pedometer data

    private MountainView mv;

    private HorizontalScrollView hsv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mountain_view_activity);

        tvDay =((TextView) findViewById(R.id.textViewDay));
        tvSteps =((TextView) findViewById(R.id.textViewSteps));

        updateInfo();

        hsv = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);

        //Set toolbar title and icons
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        //setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.self_monitoring_title));
        toolbar.setTitleTextColor(getResources().getColor(R.color.selfMonitoring));

        toolbar.setNavigationIcon(R.drawable.sm_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //Generate random PA data
//        generatePAdata();
        //Draw mountains
        appConext=getApplicationContext();
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
    private void updateInfo() {
        //        atUtils.startLocationUpdates(this);TODO
        //Get Info
        atUtils.getLog(this);
        //Update step count infoS
        //showTempTotalSteps();
//        for(int cont=0;cont<LogVectorDateTimeline.size();cont++)
//            Log.i("TIMELINE", LogVectorDateTimeline.get(cont));
        LogVectorWalk = atUtils.getLogVectorWalk();
        LogVectorDayResult = atUtils.getLogVectorDayResult();
        num_mountains = LogVectorDayResult.size()+2;
        PA_data = getPAdata(LogVectorWalk);
        for (int i=0; i<LogVectorWalk.size()-1; i++) {
            Log.i(TAG, "UPDATE INFO 1 Day"+LogVectorDayResult.get(i).toString()+"= "+PA_data[i]);
        }
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
        scalePA_data();
        generatePAdata();
    }
    /**
     *
     */
    void drawMountainView(){

//        hsv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,layout_height));
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.RelativeLayoutMountains);
        rl.getLayoutParams().height = layout_height;  // change height of the layout
        rl.getLayoutParams().width = layout_width;  // change width of the layout
        rl.setBackgroundColor(0xaab3e5fc);
        //Set click listener for the scroll view
        hsv.setOnTouchListener(this);


//        hsv.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                return true;
//            }
//        });


        mv = new MountainView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(layout_width,layout_height); //RelativeLayout.LayoutParams.WRAP_CONTENT);
        mv.setLayoutParams(params);
//            mv.bringToFront();
        rl.addView(mv);


//        final HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        hsv.postDelayed(new Runnable() {
            public void run() {
//                hsv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
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
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        Path pth = new Path();

        public MountainView (Context context) {
            super(context);
        }
        @Override
        protected void onDraw(Canvas canvas) {
            int w = getWidth();
            int h = getHeight();
            String day="";
            Log.i(TAG, "H= " + h + " W= " + w);

//            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.precious_icon);
//            = b.getHeight();
//            int mountain_top_margin
            int x0_triangle;
            int textSize = layout_height/20;
            LinearGradient mountainShader;
            for (int i=0; i<num_mountains; i++){
                //Draw lines
                p = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
                pth = new Path();
                if(i>num_mountains-3) {
                    x0_triangle = (num_mountains - 3) * mountain_width * 2 / 3 + (i - (num_mountains - 3)) * mountain_width;
                    day = (i==num_mountains-2)? "Tomorrow" : "Day after";
                }
                else {
                    x0_triangle = i * mountain_width * 2 / 3;
                    day= LogVectorDayResult.get(i).toString();
                }
                canvas.drawLine((float)x0_triangle+mountain_width/2,(float)2*textSize,(float)x0_triangle+mountain_width/2,(float)layout_height-PA_data[i],p);

                //Draw mountain
                p = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
                pth = new Path();
                if(i>num_mountains-3) {
                    p.setShader(new LinearGradient(x0_triangle, 0, x0_triangle + mountain_width, 0, 0x77dcedc8, 0x77689f38, Shader.TileMode.CLAMP));
                    Log.i(TAG, "x0 = " + x0_triangle);
                    Log.i(TAG,"mountai = "+mountain_width);
                }
                else{
                    p.setShader(new LinearGradient(x0_triangle, 0, x0_triangle + mountain_width, 0, 0xffdcedc8, 0xff689f38, Shader.TileMode.CLAMP));
                }
                pth.moveTo(x0_triangle, h);
                pth.lineTo(x0_triangle + mountain_width, h);
                pth.lineTo(x0_triangle + mountain_width / 2, h - PA_data[i]);
                pth.lineTo(x0_triangle, h);
                canvas.drawPath(pth, p);

                //Draw day
//                String day= LogVectorDayResult.get(i).toString();
                p = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
//                pth = new Path();
//                scrollPosition=12840;
//                Log.i(TAG,"???"+((i-1)*(2*mountain_width/3)+mountain_width/2)+"");
//                Log.i(TAG,"scroll"+(scrollPosition+screen_width/2));
                if( (scrollPosition+screen_width/2) > (i*(2*mountain_width/3)+mountain_width/2-2*mountain_width/6)
                    && (scrollPosition+screen_width/2) < (i*(2*mountain_width/3)+mountain_width/2+2*mountain_width/6) ) {
                    p.setColor(Color.YELLOW);
                    //Change name of the day
                    tvDay.setText(day);
                    tvSteps.setText((PA_data[i] * (maxMountainHeigh / layout_height) / 60 * 80)+"/"+(Goals_data[i]*
                            maxMountainHeigh/(layout_height-mountain_top_margin)/60*80) );
                }
                else
                    p.setColor(Color.BLACK);

                p.setStyle(Paint.Style.FILL);
                p.setTextSize(textSize);
                canvas.drawText(day, x0_triangle + mountain_width / 2 - textSize,textSize,p);


                //Draw goal
                p = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
                p.setStyle(Paint.Style.FILL);
                if(i>num_mountains-3) {
                    p.setColor(0x44ff0000);
                    canvas.drawCircle(x0_triangle + mountain_width/2, h-Goals_data[i], mountain_width/5, p);
                }
                else {
                    p.setColor(0x99ff0000);
                    canvas.drawCircle(x0_triangle + mountain_width/2, h-Goals_data[i], mountain_width/10, p);
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
        // TODO Auto-generated method stub
        Log.i(TAG,"Touch event: "+arg1.toString());
        Log.i(TAG, "Scroll is: " + hsv.getScrollX());
        Log.i(TAG, "Scroll center is: " + (hsv.getScrollX()+screen_width/2));
        Log.i(TAG, "Sum is: "+(float)(hsv.getScrollX()+arg1.getX()));
        int TouchX = (int)(hsv.getScrollX()+arg1.getX());
        int TouchY = (int)arg1.getY();

        int GoalSetMounStart = mountain_width+(num_mountains-3)*2*mountain_width/3;
        if( TouchX > GoalSetMounStart && TouchX<GoalSetMounStart+mountain_width
                && layout_height-TouchY > Goals_data[num_mountains-2]-50 && layout_height-TouchY < Goals_data[num_mountains-2]+50) {
            PA_data[num_mountains-2]=(int) (layout_height-arg1.getY());
            Goals_data[num_mountains-2]=(int) (layout_height-arg1.getY());
            mv.invalidate();
        }
        else if( TouchX > GoalSetMounStart+mountain_width && TouchX<GoalSetMounStart+2*mountain_width
                && layout_height-TouchY > Goals_data[num_mountains-1]-50 && layout_height-TouchY < Goals_data[num_mountains-1]+50) {
            PA_data[num_mountains - 1] = (int) (layout_height - arg1.getY());
            Goals_data[num_mountains - 1] = (int) (layout_height - arg1.getY());
            mv.invalidate();
        }
        //Update view after 1s
        else
            hsv.postDelayed(new Runnable() {
                public void run() {
                    scrollPosition = hsv.getScrollX();
                    mv.invalidate();
                }
            }, 1000L);

        return false;
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
        PA_data[num_mountains-2]=layout_height/2;
        PA_data[num_mountains-1]=layout_height/2;
        Goals_data[num_mountains-2]=layout_height/2;
        Goals_data[num_mountains-1]=layout_height/2;
    }
    /**
     *
     */
    public int[] getPAdata(Vector<String>  data) {
        maxMountainHeigh=-1;
        int[] output = new int[num_mountains];
        for (int i = 0; i < data.size() - 1; i++) {
            int data_int = Integer.parseInt(data.get(i).toString());
            output[i] = data_int;
            if(maxMountainHeigh<data_int)
                maxMountainHeigh=data_int;
        }
        maxMountainHeigh=4999;
        return  output;
    }
    public void scalePA_data(){
        Log.i(TAG,"layout height= "+layout_height+"; MaxValMount= "+maxMountainHeigh);
        for (int i=0; i<PA_data.length;i++){
            PA_data[i] = (int) (PA_data[i]*layout_height/maxMountainHeigh);
        }
    }
}

