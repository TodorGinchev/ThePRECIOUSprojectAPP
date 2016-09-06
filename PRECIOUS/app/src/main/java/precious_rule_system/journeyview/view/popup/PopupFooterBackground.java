package precious_rule_system.journeyview.view.popup;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/**
 * Created by christopher on 01.09.16.
 */

public class PopupFooterBackground extends View {

    Context context;
    private final float corners = 20.0f;

    public PopupFooterBackground(Context context) {
        super(context);
        this.context = context;
    }

    public void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        float w = canvas.getWidth();
        float h = canvas.getHeight();

        Paint p = new Paint();
        p.setColor(Color.parseColor("#f8ac46"));
        p.setStrokeWidth(0);
        p.setStyle(Paint.Style.FILL);
        p.setAntiAlias(true);

        Path rect = roundedRect(0,0,w,h,corners,corners,false);
        canvas.drawPath(rect, p);

    }

    static public Path roundedRect(float left, float top, float right, float bottom, float rx, float ry, boolean conformToOriginalPost) {

        Path path = new Path();
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        float width = right - left;
        float height = bottom - top;
        if (rx > width/2) rx = width/2;
        if (ry > height/2) ry = height/2;
        float widthMinusCorners = (width - (2 * rx));
        float heightMinusCorners = (height - (2 * ry));

        /*path.moveTo(right, top + ry);
        path.rQuadTo(0, -ry, -rx, -ry);//top-right corner
        path.rLineTo(-widthMinusCorners, 0);
        path.rQuadTo(-rx, 0, -rx, ry); //top-left corner
        path.rLineTo(0, heightMinusCorners);*/

        path.moveTo(right, top);
        path.rLineTo(-width, 0);
        path.rLineTo(0, heightMinusCorners + ry);


        if (conformToOriginalPost) {
            path.rLineTo(0, ry);
            path.rLineTo(width, 0);
            path.rLineTo(0, -ry);
        }
        else {
            path.rQuadTo(0, ry, rx, ry);//bottom-left corner
            path.rLineTo(widthMinusCorners, 0);
            path.rQuadTo(rx, 0, rx, -ry); //bottom-right corner
        }

        path.rLineTo(0, -heightMinusCorners);

        path.close();//Given close, last lineto can be removed.

        return path;
    }
}
