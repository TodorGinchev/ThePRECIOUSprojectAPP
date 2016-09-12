package precious_rule_system.journeyview.recycler.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import precious_rule_system.journeyview.assets.Path;
import precious_rule_system.journeyview.constants.Constants;
import ui.precious.comnet.aalto.precious.ui_MainActivity;

/**
 * Created by christopher on 04.09.16.
 */

public class PathView extends View {

    ui_MainActivity.JourneyStore store;

    public PathView(Context context, ui_MainActivity.JourneyStore store) {
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
