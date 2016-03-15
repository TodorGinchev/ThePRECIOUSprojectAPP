package activity_tracker.precious.comnet.aalto;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

import ui.precious.comnet.aalto.precious.R;


public class ATFirstActivity extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.at_layout1, null);

        RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.RelativeLayoutMountains);
        rl.getLayoutParams().height = 900;  // change heigh of the layout

        ImageView iv;

        Drawable [] drawable = {v.getResources().getDrawable(R.drawable.greenmountain_250_130),
                v.getResources().getDrawable(R.drawable.greenmountain_250_150),
                v.getResources().getDrawable(R.drawable.greenmountain_250_170),
                v.getResources().getDrawable(R.drawable.greenmountain_250_190),
                v.getResources().getDrawable(R.drawable.greenmountain_250_210),
                v.getResources().getDrawable(R.drawable.greenmountain_250_230),
                v.getResources().getDrawable(R.drawable.greenmountain_250_250),
                v.getResources().getDrawable(R.drawable.greenmountain_250_270),
                v.getResources().getDrawable(R.drawable.greenmountain_250_290)};
        Random randomGenerator = new Random();

        //FOr the achievements
        for( int i=0; i<365; i++){
            iv = new ImageView(v.getContext());
            iv.setId(i);
            iv.setAlpha(255);
            iv.setImageDrawable(drawable[randomGenerator.nextInt(8)]);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            if(i>0) {
                params.addRule(RelativeLayout.ALIGN_LEFT, i - 1);
                params.setMargins((int) v.getResources().getDimension(R.dimen.distance_mountains), 0, 0, 0);
            }
            iv.setLayoutParams(params);
            rl.addView(iv);
        }


        final HorizontalScrollView hsv = (HorizontalScrollView) v.findViewById(R.id.horizontalScrollView);
        hsv.postDelayed(new Runnable() {
            public void run() {
                hsv.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 500L);
        return v;
    }
}
