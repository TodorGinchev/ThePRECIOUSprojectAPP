package precious_rule_system.journeyview.recycler.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import precious_rule_system.journeyview.constants.Constants;
import precious_rule_system.journeyview.helpers.AssetBackgroundGenerator;
import precious_rule_system.journeyview.helpers.Position;
import rules.helpers.Tuple;
import ui.precious.comnet.aalto.precious.ui_MainActivity;

/**
 * Created by christopher on 04.09.16.
 */

public class AssetView extends View {

    ui_MainActivity.JourneyStore store;
    AssetBackgroundGenerator.AssetBackground background;
    boolean debug = false;

    public AssetView(Context context, ui_MainActivity.JourneyStore store) {
        super(context);
        this.store = store;
    }

    public void setBackground(AssetBackgroundGenerator.AssetBackground background) {
        this.background = background;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        Paint p = new Paint();
        p.setStrokeWidth(2);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.BLACK);

        if (this.background == null || this.background.assets.size() == 0) return;

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        int cnt = 0;
        for(Tuple<Constants.Size, Position> t : this.background.assets) {
            Constants.Size s = t.x;
            Position pos = t.y;
            Bitmap b = store.assets.getAssets(this.background.landscape).get(s).getBitmap(width, height, background.flipped.get(cnt));
            canvas.drawBitmap(b, pos.left, pos.top, null);
            if (debug) {
                canvas.drawCircle(pos.left, pos.top, 10, p);
                canvas.drawCircle(pos.left, pos.top, 10, p);
            }
            cnt++;
        }

        if (!debug) return;

        for(RectF r : this.background.boundingRects) {
            canvas.drawRect(r.left, r.top, r.right, r.bottom, p) ;
        }

        p.setColor(Color.RED);

        for(RectF r : this.background.removedRects) {
            canvas.drawRect(r.left, r.top, r.right, r.bottom, p) ;
        }

    }


}
