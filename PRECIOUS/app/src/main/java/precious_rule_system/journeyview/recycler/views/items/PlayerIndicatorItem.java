package precious_rule_system.journeyview.recycler.views.items;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.View;

import precious_rule_system.journeyview.JourneyActivity;
import precious_rule_system.journeyview.constants.Constants;
import precious_rule_system.journeyview.helpers.UIHelper;

/**
 * Created by christopher on 05.09.16.
 */

public class PlayerIndicatorItem extends View {

    private String text = "You";
    JourneyActivity.JourneyStore store;

    public PlayerIndicatorItem(JourneyActivity.JourneyStore store, Context context) {
        super(context);
        this.store = store;
    }

    public void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        float w = canvas.getWidth();
        float h = canvas.getHeight();

        // Draw triangle
        Path trianglePath = new Path();
        trianglePath.setFillType(Path.FillType.EVEN_ODD);
        trianglePath.moveTo(0, h/2);
        trianglePath.lineTo(Constants.playerIndicatorTrianglePadding, h/4);
        trianglePath.lineTo(Constants.playerIndicatorTrianglePadding, h/4*3);
        trianglePath.lineTo(0, h/2);
        trianglePath.close();

        Paint p = new Paint();
        p.setColor(Constants.playerIndicatorBackgroundColor);
        p.setStrokeWidth(0);
        p.setStyle(Paint.Style.FILL);
        p.setAntiAlias(true);

        canvas.drawPath(trianglePath, p);

        Path rect = UIHelper.roundedRect(Constants.playerIndicatorTrianglePadding, 0, w-Constants.playerIndicatorTrianglePadding, h, Constants.playerIndicatorCorners, Constants.playerIndicatorCorners, false);
        rect.setFillType(Path.FillType.EVEN_ODD);

        // Draw rectangle
        canvas.drawPath(rect, p);

        // Draw text
        String text = this.text;
        float width = w-Constants.playerIndicatorTrianglePadding;
        float textWidth = width/2;
        float textHeight = h - 2*Constants.playerIndicatorTextPadding;

        p.setColor(Constants.playerIndicatorTextColor);
        UIHelper.setTextSizeForWidth(p,textWidth, textHeight, text);
        Rect bounds = new Rect();
        p.getTextBounds(text, 0, text.length(), bounds);

        canvas.drawText(text, (width-bounds.width())/2, bounds.height()+(h-bounds.height())/2, p);
    }


}
