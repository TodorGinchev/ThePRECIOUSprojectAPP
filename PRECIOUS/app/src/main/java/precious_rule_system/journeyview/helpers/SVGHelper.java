package precious_rule_system.journeyview.helpers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Picture;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;

import com.caverock.androidsvg.SVG;

/**
 * Created by christopher on 04.09.16.
 */

public class SVGHelper {

    public static Bitmap renderSVG(SVG svg, int width, int height, boolean flipped) {

        svg.setDocumentHeight(height);
        svg.setDocumentWidth(width);

        Bitmap bitmap =  Bitmap.createBitmap (width,height, Bitmap.Config.ARGB_8888);
        Canvas svgCanvas = new Canvas(bitmap);
        Picture svgPicture = svg.renderToPicture();
        svgCanvas.drawPicture(svgPicture);

        if (flipped) {
            bitmap = flip(bitmap);
        }

        return bitmap;

    }

    public static Bitmap flip(Bitmap src)
    {
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return dst;
    }


    public static android.graphics.Path getPathWidthCleanedViewBox(SVGExtended svg){
        Path p = svg.getPath();
        RectF rectF = new RectF();
        p.computeBounds(rectF, true);
        Matrix offsetMatrix = new Matrix();
        offsetMatrix.setTranslate(-rectF.left,-rectF.top);
        p.transform(offsetMatrix);
        return p;
    }

    public static boolean isColliding(Path p, Region clip, float x, float y, float w, float h, float boxScale) {

        android.graphics.Path rect = new android.graphics.Path();
        rect.addRect(x*boxScale,y*boxScale,x*boxScale+w*boxScale,y*boxScale+h*boxScale,android.graphics.Path.Direction.CW);

        Region region = new Region();
        region.setPath(rect, clip);

        Region path = new Region();
        path.setPath(p, clip);

        if (!path.quickReject(region) && path.op(region, Region.Op.INTERSECT)) {
            return true;
        } else {
            return false;
        }
    }

    public static float[] getPositionAlongPath(android.graphics.Path path, float position) {
        float addVertical = 0.0f;

        if (position < 0 || position > 1) {
            RectF bnds = new RectF();
            path.computeBounds(bnds, true);

            if (position < 0) {
                addVertical = bnds.height();
                position = 1.0f + position;
            } else if (position > 1) {
                addVertical = -bnds.height();
                position = position - 1.0f;
            }
        }

        PathMeasure pm = new PathMeasure(path, false);
        float aCoordinates[] = {0f, 0f};
        pm.getPosTan(pm.getLength() * position, aCoordinates, null);
        aCoordinates[1]+=addVertical;
        return aCoordinates;
    }

    public static float getPathPositionForVerticalPosition(android.graphics.Path path, int verticalPosition) {

        RectF bnds = new RectF();
        path.computeBounds(bnds, true);
        if (verticalPosition < 0 || verticalPosition > bnds.height()) return 0;
        if (verticalPosition == 0) return 1.0f;
        if (verticalPosition == bnds.height()) return 0.0f;

        float start = 1.0f-verticalPosition/bnds.height();
        float end;
        float pos[] = getPositionAlongPath(path, start);
        int y = Math.round(pos[1]);

        if (y > verticalPosition) {
            end = 1.0f;
        } else if (y < verticalPosition) {
            end = start;
            start = 0.0f;
        } else {
            return start;
        }

        float foundPosition = 0.0f;
        int safe = 0;

        while(true && safe < 20) {

            foundPosition = start + (end-start)/2;
            pos = getPositionAlongPath(path, foundPosition);
            y = Math.round(pos[1]);

            if (y > verticalPosition) {
                start = foundPosition;
            } else if (y < verticalPosition) {
                end = foundPosition;
            } else {
                return foundPosition;
            }

            safe++;
        }

        assert(false);
        return foundPosition;


    }
}
