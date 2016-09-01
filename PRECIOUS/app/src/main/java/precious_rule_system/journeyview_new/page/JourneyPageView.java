package precious_rule_system.journeyview_new.page;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;

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

        /*Bitmap b = assets.path.bitmap;
        Path p = ((SVGExtended) assets.path.svg).getPath();
        Matrix scaleMatrix = new Matrix();
        RectF rectF = new RectF();
        p.computeBounds(rectF, true);
        scaleMatrix.setScale(b.getWidth()/rectF.width(), (b.getHeight())/rectF.height(),rectF.top,rectF.left);
        p.transform(scaleMatrix);
        Matrix transMatrix = new Matrix();
        transMatrix.setTranslate(pathOffset*w, 0);
        p.transform(transMatrix);*/

        Paint pathPaint = new Paint();
        pathPaint.setColor(Color.rgb(226, 222, 219));
        pathPaint.setStrokeWidth(1);
        pathPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        canvas.drawPath(assets.path.path,pathPaint);

        for(Tuple<JourneyAssets.Size, double[]> t : list) {
            JourneyAssets.Asset a = assets.assets.get(JourneyAssets.Landscape.MOUNTAIN).get(t.x);
            canvas.drawBitmap(a.bitmap, (float) t.y[0]*w, (float) t.y[1]*h, null);
        }

        /*Paint myPaint = new Paint();
        myPaint.setColor(Color.rgb(0, 0, 0));
        myPaint.setStrokeWidth(0);
        myPaint.setStyle(Paint.Style.FILL);

        for(float i=0.005f; i<=0.48; i+=0.05) {
            Tuple<float[], Float> res = SVGExtended.getPositionAlongPath(assets.path.path,i);
            float l = res.y;
            float[] pos = res.x;
            canvas.drawCircle(pos[0],pos[1],0.01f*w,myPaint);
        }*/



    }


}
