package precious_rule_system.journeyview.recycler.views.items;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import precious_rule_system.journeyview.constants.Constants;
import precious_rule_system.rewardsystem.entities.RewardEvent;

/**
 * Created by christopher on 05.09.16.
 */

public class RewardItemView extends View {

    public RewardEvent event;
    Path starPath = null;
    public float[] pivot;

    public RewardItemView(Context context, RewardEvent event) {
        super(context);
        this.event = event;
    }

    public void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        float w = canvas.getWidth();
        float h = canvas.getHeight();

        if (event.isEvent()) {

            Paint p = new Paint();
            p.setColor(Constants.rewardEventColor);
            p.setStrokeWidth(0);
            p.setStyle(Paint.Style.FILL);
            p.setAntiAlias(true);
            canvas.drawCircle(w/2,h/2,w/2,p);

        } else if (event.isMilestone()) {

            Paint p = new Paint();
            p.setColor(Constants.rewardMilestoneColor);
            p.setStrokeWidth(0);
            p.setStyle(Paint.Style.FILL);
            p.setAntiAlias(true);
            canvas.drawCircle(w/2,h/2,w/2,p);

            if (starPath == null) {
                starPath = createStarBySize(w/2, 6);
                Matrix m = new Matrix();
                m.setTranslate(w/4,h/4);
                starPath.transform(m);
            }

            p.setAntiAlias(true);
            p.setColor(Constants.rewardMilestoneStarColor);
            canvas.drawPath(starPath, p);
        }

    }

    private Path createStarBySize(float width, int steps) {
        float halfWidth = width / 2.0F;
        float bigRadius = halfWidth;
        float radius = halfWidth / 2.0F;
        float degreesPerStep = (float) Math.toRadians(360.0F / (float) steps);
        float halfDegreesPerStep = degreesPerStep / 2.0F;
        Path ret = new Path();
        ret.setFillType(Path.FillType.EVEN_ODD);
        float max = (float) (2.0F* Math.PI);
        ret.moveTo(width, halfWidth);
        for (double step = 0; step < max; step += degreesPerStep) {
            ret.lineTo((float)(halfWidth + bigRadius * Math.cos(step)), (float)(halfWidth + bigRadius * Math.sin(step)));
            ret.lineTo((float)(halfWidth + radius * Math.cos(step + halfDegreesPerStep)), (float)(halfWidth + radius * Math.sin(step + halfDegreesPerStep)));
        }
        ret.close();
        return ret;
    }
}
