package precious_rule_system.journeyview_new.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by christopher on 01.09.16.
 */

public class ViewDisplay extends View {

    private String label = "The Mountains";
    private String points = "376";

    private final float strokeWidth = 6.0f;

    public ViewDisplay(Context context) {
        super(context);
    }

    public void update(String label, String points) {
        this.label = label;
        this.points = points;
        this.invalidate();
    }

    public void updatePoints(int points) {
        this.points = String.valueOf(points);
        this.invalidate();
    }

    public boolean updateLabel(String label) {
        if (this.label.equals(label)) return false;
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

    }


}
