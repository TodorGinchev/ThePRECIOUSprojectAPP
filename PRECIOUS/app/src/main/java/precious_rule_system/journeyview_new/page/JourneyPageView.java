package precious_rule_system.journeyview_new.page;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;

import precious_rule_system.journeyview_new.DataUIManager;
import precious_rule_system.journeyview_new.assets.JourneyAssets;
import precious_rule_system.journeyview_new.utilities.SVGExtended;
import precious_rule_system.journeyview_new.utilities.Layout;
import rules.helpers.Tuple;

/**
 * Created by christopher on 30.08.16.
 */

public class JourneyPageView extends View {

    JourneyAssets assets;
    ArrayList<Tuple<JourneyAssets.Size, double[]>> list;

    public JourneyPageView(Context context, JourneyAssets assets) {
        super(context);
        this.assets = assets;
        this.list = new ArrayList<>();
    }

    public void addAssets(ArrayList<Tuple<JourneyAssets.Size, double[]>> assets) {
        list = assets;
        this.invalidate();
    }

    public void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        float w = canvas.getWidth();
        float h = canvas.getHeight();

        Paint pathPaint = new Paint();
        pathPaint.setColor(Color.rgb(226, 222, 219));
        pathPaint.setStrokeWidth(w* DataUIManager.pathThinkness);
        pathPaint.setStyle(Paint.Style.STROKE);

        canvas.drawPath(assets.path.path,pathPaint);

        for(Tuple<JourneyAssets.Size, double[]> t : list) {
            JourneyAssets.Asset a = assets.assets.get(JourneyAssets.Landscape.MOUNTAIN).get(t.x);
            canvas.drawBitmap(a.bitmap, (float) t.y[0]*w, (float) t.y[1]*h, null);
        }


    }


}
