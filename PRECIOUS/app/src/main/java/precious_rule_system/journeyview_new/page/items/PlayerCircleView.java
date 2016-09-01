package precious_rule_system.journeyview_new.page.items;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;

import precious_rule_system.rewardsystem.entities.RewardEvent;

/**
 * Created by christopher on 01.09.16.
 */

public class PlayerCircleView extends View {

    private final float strokeWidth = 5.0f;
    public PlayerCircleView(Context context) {
        super(context);
    }

    public void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        float w = canvas.getWidth();
        float h = canvas.getHeight();

        Paint p = new Paint();
        p.setColor(Color.rgb(249, 190, 112));
        p.setStrokeWidth(strokeWidth);
        p.setStyle(Paint.Style.STROKE);
        p.setAntiAlias(true);

        canvas.drawCircle(w/2,h/2,w/2-strokeWidth/2,p);
    }
}
