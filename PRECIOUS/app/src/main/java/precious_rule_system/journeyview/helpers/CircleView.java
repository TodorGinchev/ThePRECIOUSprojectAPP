package precious_rule_system.journeyview.helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import precious_rule_system.journeyview.constants.Constants;

/**
 * Created by christopher on 06.09.16.
 */

public class CircleView extends View {

    int color;

    public CircleView(Context context, int color) {
        super(context);
        this.color = color;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = canvas.getWidth();
        float h = canvas.getHeight();

        Paint p = new Paint();
        p.setColor(color);
        p.setStrokeWidth(0);
        p.setStyle(Paint.Style.FILL);
        p.setAntiAlias(true);
        canvas.drawCircle(w/2,h/2,w/2-Constants.playerStrokeWidth/2,p);
    }
}
