package activity_tracker.precious.comnet.aalto;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;

import java.util.Random;

import ui.precious.comnet.aalto.precious.R;

public class MountainViewActivity extends AppCompatActivity {
    public static Context appConext;
    public static final String PREFS_NAME = "IRsubappPreferences";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mountain_view_activity);
//        setContentView(new MountainView(this));
        appConext=getApplicationContext();
        drawMountainView();


    }
    /**
     *
     */
    void drawMountainView(){

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.RelativeLayoutMountains);
        rl.getLayoutParams().height = 900;  // change heigh of the layout
        rl.setBackgroundColor(Color.RED);

        MountainView mv;
        Random randomGenerator = new Random();

        //FOr the achievements
        for( int i=0; i<365; i++){
            mv = new MountainView(this);
            mv.setId(i);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            if(i>0) {
                params.addRule(RelativeLayout.ALIGN_LEFT, i - 1);
                params.setMargins((int) this.getResources().getDimension(R.dimen.distance_mountains), 0, 0, 0);
            }
            mv.setLayoutParams(params);
//            mv.bringToFront();
            rl.addView(mv);
        }


        final HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
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
            //Dibujar aquí
            Paint pincel = new Paint();
            pincel.setColor(Color.BLUE);
            pincel.setStrokeWidth(100);
            pincel.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(50, 50, 100, pincel);
        }
    }
}

