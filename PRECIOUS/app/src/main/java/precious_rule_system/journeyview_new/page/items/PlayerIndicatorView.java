package precious_rule_system.journeyview_new.page.items;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by christopher on 01.09.16.
 */

public class PlayerIndicatorView extends View {

    private final float trianglePadding = 20.0f;
    private final float corners = 8.0f;
    private final float textPadding = 5.0f;

    public PlayerIndicatorView(Context context) {
        super(context);
    }

    public void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        float w = canvas.getWidth();
        float h = canvas.getHeight();

        // Draw triangle
        Path trianglePath = new Path();
        trianglePath.setFillType(Path.FillType.EVEN_ODD);
        trianglePath.moveTo(0, h/2);
        trianglePath.lineTo(trianglePadding, h/4);
        trianglePath.lineTo(trianglePadding, h/4*3);
        trianglePath.lineTo(0, h/2);
        trianglePath.close();

        Paint p = new Paint();
        p.setColor(Color.rgb(54, 54, 54));
        p.setStrokeWidth(0);
        p.setStyle(Paint.Style.FILL);
        p.setAntiAlias(true);

        canvas.drawPath(trianglePath, p);

        Path rect = roundedRect(trianglePadding, 0, w-trianglePadding, h, corners, corners, false);
        rect.setFillType(Path.FillType.EVEN_ODD);

        // Draw rectangle
        canvas.drawPath(rect, p);

        // Draw text
        String text = "You";
        float width = w-trianglePadding;
        float textWidth = width/2;
        float textHeight = h - 2*textPadding;

        p.setColor(Color.parseColor("#f6f6f6"));
        setTextSizeForWidth(p,textWidth, textHeight, text);
        Rect bounds = new Rect();
        p.getTextBounds(text, 0, text.length(), bounds);

        canvas.drawText(text, (width-bounds.width())/2, bounds.height()+(h-bounds.height())/2, p);
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

        path.moveTo(right, top + ry);
        path.rQuadTo(0, -ry, -rx, -ry);//top-right corner
        path.rLineTo(-widthMinusCorners, 0);
        path.rQuadTo(-rx, 0, -rx, ry); //top-left corner
        path.rLineTo(0, heightMinusCorners);

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

    private static void setTextSizeForWidth(Paint paint, float desiredWidth, float desiredHeight,
                                            String text) {

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        final float testTextSize = 48f;

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize1 = testTextSize * desiredWidth / bounds.width();
        float desiredTextSize2 = testTextSize * desiredHeight / bounds.height();

        // Set the paint for that size.
        paint.setTextSize(Math.min(desiredTextSize1,desiredTextSize2));
    }

}
