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
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

import java.util.Random;

import ui.precious.comnet.aalto.precious.R;

public class MountainViewActivity extends AppCompatActivity {
    public static Context appConext;
    public static final String PREFS_NAME = "IRsubappPreferences";
    public static final String TAG = "MountainViewActivity";
    private static int screen_width;
    private static int screen_height;
    private static final int NUM_MOUNTAINS=60; //TODO find optimal value
    private static int mountain_width;
    private static int layout_width;
    private static int layout_height; // depends on variable screen_height
    private static int mountain_top_margin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mountain_view_activity);

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
        layout_width = (int)(2*mountain_width/3)*(NUM_MOUNTAINS+2);//90000;
        mountain_top_margin = screen_height/10;
        //Draw mountains
        appConext=getApplicationContext();
        drawMountainView();
    }
    /**
     *
     */
    void drawMountainView(){
        final HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
//        hsv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,layout_height));
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.RelativeLayoutMountains);
        rl.getLayoutParams().height = layout_height;  // change height of the layout
        rl.getLayoutParams().width = layout_width;  // change width of the layout
        rl.setBackgroundColor(0xaab3e5fc);


        MountainView mv = new MountainView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(layout_width,layout_height); //RelativeLayout.LayoutParams.WRAP_CONTENT);
        mv.setLayoutParams(params);
//            mv.bringToFront();
        rl.addView(mv);
//        //FOr the achievements
//        for( int i=0; i<365; i++){
//            mv = new MountainView(this);
//            mv.setId(i);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(300, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//            if(i>0) {
//                params.addRule(RelativeLayout.ALIGN_LEFT, i - 1);
////                params.setMargins((int) this.getResources().getDimension(R.dimen.distance_mountains), 0, 0, 0);
//                params.setMargins(200, 100, 100, 100);
//            }
//            mv.setLayoutParams(params);
////            mv.bringToFront();
//            rl.addView(mv);
//        }


//        final HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
        hsv.postDelayed(new Runnable() {
            public void run() {
                hsv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 500L);

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
            int w = getWidth();
            int h = getHeight();
            Log.i(TAG, "H= " + h + " W= " + w);
            Paint p;// = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
            Path pth;// = new Path();

//            int x0=49700;
//            int triangle_width=250;
//            int triangle_height=600;
//            pth.moveTo(x0, 900);
//            pth.lineTo(x0 + triangle_width, 900);
//            pth.lineTo(x0+triangle_width/2,900-triangle_height);
//            pth.lineTo(x0, 900);

//            p.setShader(new LinearGradient(0, 900, 0, 900-triangle_height, 0xff689f38, 0xffdcedc8, Shader.TileMode.CLAMP));
//            p.setShader(new LinearGradient(x0,y0,x1,y1,0xff000000, 0xffffffff, Shader.TileMode.CLAMP));
//            canvas.drawPath(pth,p);


//            int triangle_width=(int)((1.5)*(w/NUM_MOUNTAINS));

            int triangle_height;
            Random randomGenerator = new Random();
//            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.precious_icon);
//            = b.getHeight();
//            int mountain_top_margin
            int x0_triangle;
            for (int i=0; i<NUM_MOUNTAINS; i++){
                if(i>NUM_MOUNTAINS-3) {
                    x0_triangle = (NUM_MOUNTAINS - 3) * mountain_width * 2 / 3 + (i - (NUM_MOUNTAINS - 3)) * mountain_width;

                    //Draw mountain
                    p = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
                    pth = new Path();
                    triangle_height = randomGenerator.nextInt(h-10-mountain_top_margin) + 10; //random number between 10 and reward icon height
                    pth.moveTo(x0_triangle, h);
                    pth.lineTo(x0_triangle + mountain_width, h);
                    pth.lineTo(x0_triangle + mountain_width/2, h - layout_height/2);
                    pth.lineTo(x0_triangle, h);
                    p.setShader(new LinearGradient(x0_triangle, 0, x0_triangle + mountain_width, 0, 0x99dcedc8, 0x99689f38, Shader.TileMode.CLAMP));
                    canvas.drawPath(pth, p);

                    //Draw goal
                    int goal = randomGenerator.nextInt(h-10-mountain_top_margin) + 10;
                    p = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
                    p.setColor(0x55ff0000);
                    p.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(x0_triangle + mountain_width/2, layout_height/2, mountain_width/5, p);
                }
                else {
                    x0_triangle = i * mountain_width * 2 / 3;

                    //Draw mountain
                    p = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
                    pth = new Path();
                    triangle_height = randomGenerator.nextInt(h-10-mountain_top_margin) + 10; //random number between 10 and reward icon height
                    pth.moveTo(x0_triangle, h);
                    pth.lineTo(x0_triangle + mountain_width, h);
                    pth.lineTo(x0_triangle + mountain_width/2, h - triangle_height);
                    pth.lineTo(x0_triangle, h);
                    p.setShader(new LinearGradient(x0_triangle, 0, x0_triangle + mountain_width, 0, 0xffdcedc8, 0xff689f38, Shader.TileMode.CLAMP));
                    canvas.drawPath(pth, p);

                    //Draw goal
                    int goal = randomGenerator.nextInt(h-10-mountain_top_margin) + 10;
                    p = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
                    p.setColor(0x99ff0000);
                    p.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(x0_triangle + mountain_width/2, h-goal, mountain_width/10, p);
                }

//                //Draw diamond as reward if triangle is higher than goal
//                if(triangle_height > goal) {
//                    p = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
//                    p.setColor(Color.RED);
////                    canvas.drawBitmap(b, left, top, p);
//                    canvas.drawBitmap(b,x0_triangle + triangle_width/2 - b.getWidth()/2  , h - triangle_height - b.getHeight(), p);
//                }
                //TODO replace dot with diamond
                //TODO ice peak random generated
                //TODO round mountains???
                //TODO add the day selector and change info based on the selected day
                //TODO add days and add "this week" "two weeks ago"
                //TODO goal setting
            }





//            Paint paint = new Paint();
//            paint.setColor(Color.BLUE);
//            paint.setStrokeWidth(10);
//            paint.setStyle(Paint.Style.STROKE);
//            paint.setShader(new LinearGradient(0, 0, 0, getHeight(), Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));
//            canvas.drawCircle(49900, 100, 50, paint);
//
//
//
//            paint.setColor(android.graphics.Color.RED);
//            paint.setStyle(Paint.Style.FILL_AND_STROKE);
//            paint.setAntiAlias(true);
//
//
//            Point a = new Point(49800, 0);
//            Point b = new Point(49800, 100);
//            Point c = new Point(49887, 50);
//
//            Path path = new Path();
//            path.setFillType(Path.FillType.EVEN_ODD);
//            path.lineTo(b.x, b.y);
//            path.lineTo(c.x, c.y);
//            path.lineTo(a.x, a.y);
//            path.close();
//
//            canvas.drawPath(path, paint);

        }
    }
}

