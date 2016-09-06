package precious_rule_system.journeyview.recycler.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import precious_rule_system.journeyview.JourneyActivity;
import precious_rule_system.journeyview.assets.Assets;
import precious_rule_system.journeyview.assets.Path;
import precious_rule_system.journeyview.constants.Constants;
import precious_rule_system.journeyview.helpers.SizeCalculator;

/**
 * Created by christopher on 04.09.16.
 */

public class PathView extends View {

    JourneyActivity.JourneyStore store;

    public PathView(Context context, JourneyActivity.JourneyStore store) {
        super(context);
        this.store = store;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        Path p = store.assets.getPath();
        android.graphics.Path androidPath = p.getPath(width, height);

        Paint pathPaint = new Paint();
        pathPaint.setColor(Constants.pathColor);
        pathPaint.setStrokeWidth(store.sizes.getPathThickness(width, height));
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setAntiAlias(true);

        canvas.drawPath(androidPath,pathPaint);

    }
}
