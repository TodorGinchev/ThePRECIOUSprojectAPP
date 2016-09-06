package precious_rule_system.journeyview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by christopher on 06.09.16.
 */

public class JourneyViewDisplay extends View {

    private String label = null;
    private String points = "";

    private final float strokeWidth = 6.0f;

    public JourneyViewDisplay(Context context) {
        super(context);
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

        if (label != null) {
            p.setStyle(Paint.Style.FILL);
            p.setTextSize(h/4);
            canvas.drawText(label, 20, 20+h/4, p);
        }

        p.setTextSize(h/4 * 0.5f);
        canvas.drawText(points, 20, 20+h/2, p);


    }

}
