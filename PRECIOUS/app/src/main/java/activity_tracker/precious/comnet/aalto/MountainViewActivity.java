package activity_tracker.precious.comnet.aalto;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

import java.util.Random;

import ui.precious.comnet.aalto.precious.R;

public class MountainViewActivity extends AppCompatActivity implements View.OnTouchListener {
    public static Context appConext;
    public static final String PREFS_NAME = "IRsubappPreferences";
    public static final String TAG = "MountainViewActivity";
    private static int screen_width;
    private static int screen_height;
    private static final int NUM_MOUNTAINS=5; //TODO find optimal value
    private static int mountain_width;
    private static int layout_width;
    private static int layout_height; // depends on variable screen_height
    private static int mountain_top_margin;

    private int [] PA_data = new int[NUM_MOUNTAINS];
    private int [] Goals_data = new int[NUM_MOUNTAINS];

    private Path circle1;
    private float circle1_pos=0;
    private Path circle2;
    private float circle2_pos=0;
    private MountainView mv;

    private HorizontalScrollView hsv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mountain_view_activity);

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

        //Get screen size and calculate object sizes
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screen_width = size.x;
        int screen_height = size.y;
        layout_height = (int) 2*screen_height/3; //900
        mountain_width = screen_width/3;
        layout_width = (int)(mountain_width+(2*mountain_width/3)*(NUM_MOUNTAINS-3)+mountain_width*2);//90000;
        mountain_top_margin = screen_height/10;
        //Generate random PA data
        generatePAdata();
        //Draw mountains
        appConext=getApplicationContext();
        drawMountainView();
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

        mv = new MountainView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(layout_width,layout_height); //RelativeLayout.LayoutParams.WRAP_CONTENT);
        mv.setLayoutParams(params);
//            mv.bringToFront();
        rl.addView(mv);


//        final HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        hsv.postDelayed(new Runnable() {
            public void run() {
//                hsv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                hsv.scrollTo(layout_width-mountain_width*4,0);
            }
        }, 500L);

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
            Log.i(TAG, "H= " + h + " W= " + w);

//            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.precious_icon);
//            = b.getHeight();
//            int mountain_top_margin
            int x0_triangle;
            LinearGradient mountainShader;
            for (int i=0; i<NUM_MOUNTAINS; i++){
                //Draw mountain
                p = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
                pth = new Path();
                if(i>NUM_MOUNTAINS-3) {
                    x0_triangle = (NUM_MOUNTAINS - 3) * mountain_width * 2 / 3 + (i - (NUM_MOUNTAINS - 3)) * mountain_width;
                    p.setShader(new LinearGradient(x0_triangle, 0, x0_triangle + mountain_width, 0, 0x77dcedc8, 0x77689f38, Shader.TileMode.CLAMP));
                }
                else{
                    x0_triangle = i * mountain_width * 2 / 3;
                    p.setShader(new LinearGradient(x0_triangle, 0, x0_triangle + mountain_width, 0, 0xffdcedc8, 0xff689f38, Shader.TileMode.CLAMP));
                }
                pth.moveTo(x0_triangle, h);
                pth.lineTo(x0_triangle + mountain_width, h);
                pth.lineTo(x0_triangle + mountain_width / 2, h - PA_data[i]);
                pth.lineTo(x0_triangle, h);
                canvas.drawPath(pth, p);

                //Draw goal
                p = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
                p.setStyle(Paint.Style.FILL);
                if(i>NUM_MOUNTAINS-3) {
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
        Log.i(TAG, "Sum is: "+(float)(hsv.getScrollX()+arg1.getX()));
        PA_data[NUM_MOUNTAINS-2]= (int)arg1.getY();
        Goals_data[NUM_MOUNTAINS-2]= (int)arg1.getY();
        PA_data[NUM_MOUNTAINS-1]=(int) (layout_height-arg1.getY());
        Goals_data[NUM_MOUNTAINS-1]=(int) (layout_height-arg1.getY());
        mv.invalidate();
        return false;
    }
    /**
     *
     */
    private void generatePAdata(){
        Random randomGenerator = new Random();
        for(int i=0;i<NUM_MOUNTAINS-2;i++) {
            PA_data[i] = randomGenerator.nextInt((layout_height) - 10 - mountain_top_margin) + 10; //random number
            Goals_data[i] = randomGenerator.nextInt((layout_height) - 10 - mountain_top_margin) + 10; //random number
        }
        PA_data[NUM_MOUNTAINS-2]=layout_height/2;
        Goals_data[NUM_MOUNTAINS-2]=layout_height/2;
        PA_data[NUM_MOUNTAINS-1]=layout_height/2;
        Goals_data[NUM_MOUNTAINS-1]=layout_height/2;
    }
}

