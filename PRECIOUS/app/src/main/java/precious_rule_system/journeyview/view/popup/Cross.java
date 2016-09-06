package precious_rule_system.journeyview.view.popup;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/**
 * Created by christopher on 01.09.16.
 */

public class Cross extends View {

    int strokeWidth;
    int color;

    public Cross(Context context, int strokeWidth, int color) {
        super(context);
        this.strokeWidth = strokeWidth;
        this.color = color;
    }

    public void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        float w = canvas.getWidth();
        float h = canvas.getHeight();

        Paint p = new Paint();
        p.setColor(color);
        p.setStrokeWidth(strokeWidth);
        p.setStyle(Paint.Style.STROKE);
        p.setAntiAlias(true);

        Path p1 = new Path();
        p1.moveTo(0,0);
        p1.lineTo(w,h);
        canvas.drawPath(p1, p);

        Path p2 = new Path();
        p2.moveTo(0,h);
        p2.lineTo(w,0);
        canvas.drawPath(p2,p);

    }


}
