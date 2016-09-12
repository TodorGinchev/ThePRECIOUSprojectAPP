package precious_rule_system.journeyview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import ui.precious.comnet.aalto.precious.ui_MainActivity;

/**
 * Created by christopher on 06.09.16.
 */

public class JourneyViewDisplay extends View {

    private String label = null;
    private String points = "";
    GestureDetector gestureDetector;

    private final float strokeWidth = 6.0f;
    ui_MainActivity.JourneyStore store;

    public JourneyViewDisplay(ui_MainActivity.JourneyStore store, Context context) {
        super(context);
        gestureDetector = new GestureDetector(context, new GestureListener(store));
        this.store = store;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return gestureDetector.onTouchEvent(e);
    }


    public void update(String label, String points) {
        this.label = label;
        this.points = points;
        this.invalidate();
    }

    public void updatePoints(int points) {
        String newPoints = String.valueOf(points);
        if (!this.points.equals(newPoints)) {
            this.points = newPoints;
            this.invalidate();
        }
    }

    public boolean updateLabel(String label) {
        if (this.label != null && this.label.equals(label)) return false;
        this.label = label;
        this.invalidate();
        return true;
    }

    public void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        float w = canvas.getWidth();
        float h = canvas.getHeight();

        // TODO Put these values into constants
        Paint p = new Paint();
        p.setColor(Color.argb(200, 238, 234, 233));
        p.setStrokeWidth(0);
        p.setStyle(Paint.Style.FILL);
        p.setAntiAlias(true);

        canvas.drawRect(0,0,w,h,p);

        p.setColor(Color.argb(60, 54, 54, 54));
        p.setStrokeWidth(strokeWidth);
        p.setStyle(Paint.Style.STROKE);

        canvas.drawRect(strokeWidth/2, strokeWidth/2, w-strokeWidth/2,h-strokeWidth/2,p);

        p.setColor(Color.BLACK);

        if (label != null) {
            p.setStyle(Paint.Style.FILL);
            p.setTextSize(h/4);
            canvas.drawText(label, 20, 20+h/4, p);
        }

        p.setTextSize(h/4 * 0.5f);
        canvas.drawText("Points: " + points, 20, 20+h/2, p);


    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        ui_MainActivity.JourneyStore store;

        public GestureListener(ui_MainActivity.JourneyStore store) {
            super();
            this.store = store;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            store.data.startDummyDoubleTapActions();
            return true;
        }
    }

}
