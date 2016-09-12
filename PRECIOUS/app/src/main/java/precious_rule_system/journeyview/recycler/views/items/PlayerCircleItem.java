package precious_rule_system.journeyview.recycler.views.items;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import precious_rule_system.journeyview.JourneyActivity;
import precious_rule_system.journeyview.constants.Constants;

/**
 * Created by christopher on 05.09.16.
 */

public class PlayerCircleItem extends View {

    JourneyActivity.JourneyStore store;

    public PlayerCircleItem(JourneyActivity.JourneyStore store, Context context) {
        super(context);
        this.store = store;
    }

    public void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        float w = canvas.getWidth();
        float h = canvas.getHeight();

        Paint p = new Paint();
        p.setColor(Constants.playerCircleColor);
        p.setStrokeWidth(Constants.playerStrokeWidth);
        p.setStyle(Paint.Style.STROKE);
        p.setAntiAlias(true);

        canvas.drawCircle(w/2,h/2,w/2-Constants.playerStrokeWidth/2,p);
    }

}
