package precious_rule_system.journeyview.helpers;

import com.caverock.androidsvg.SVG;

import java.lang.reflect.*;

import android.graphics.Matrix;
import android.graphics.PathMeasure;
import android.graphics.Region;

import rules.helpers.Tuple;

/**
 * Created by christopher on 11.08.16.
 */

public class SVGExtended extends SVG {

    public SVGExtended(SVG svg) throws Exception {
        Method retrieveItems = svg.getClass().getDeclaredMethod("getRootElement");
        retrieveItems.setAccessible(true);
        Object[] args = {};
        this.setRootElement((SVG.Svg) retrieveItems.invoke(svg, args));
    }

    public static Tuple<float[], Float> getPositionAlongPath(android.graphics.Path path, float position) {
        PathMeasure pm = new PathMeasure(path, false);
        float aCoordinates[] = {0f, 0f};
        pm.getPosTan(pm.getLength() * position, aCoordinates, null);
        float l = pm.getLength();
        return new Tuple<>(aCoordinates, l);
    }

    public android.graphics.Path getPath() {
        for (SVG.SvgObject obj: this.getRootElement().getChildren()) {
            if (obj instanceof SVG.Path) {
                SVG.Path path = (SVG.Path) obj;
                android.graphics.Path androidPath = (new PathConverter(path.d)).getPath();
                return androidPath;
            }
        }
        return null;
    }

    private class PathConverter implements PathInterface
    {
        android.graphics.Path path = new android.graphics.Path();
        float  lastX, lastY;

        public PathConverter(PathDefinition pathDef)
        {
            if (pathDef == null)
                return;
            pathDef.enumeratePath(this);
        }

        public android.graphics.Path getPath()
        {
            return path;
        }

        @Override
        public void moveTo(float x, float y)
        {
            path.moveTo(x, y);
            lastX = x;
            lastY = y;
        }

        @Override
        public void lineTo(float x, float y)
        {
            path.lineTo(x, y);
            lastX = x;
            lastY = y;
        }

        @Override
        public void cubicTo(float x1, float y1, float x2, float y2, float x3, float y3)
        {
            path.cubicTo(x1, y1, x2, y2, x3, y3);
            lastX = x3;
            lastY = y3;
        }

        @Override
        public void quadTo(float x1, float y1, float x2, float y2)
        {
            path.quadTo(x1, y1, x2, y2);
            lastX = x2;
            lastY = y2;
        }

        @Override
        public void arcTo(float rx, float ry, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag, float x, float y)
        {
            arcTo(lastX, lastY, rx, ry, xAxisRotation, largeArcFlag, sweepFlag, x, y);
            lastX = x;
            lastY = y;
        }

        @Override
        public void close()
        {
            path.close();
        }

        private void arcTo(float lastX, float lastY, float rx, float ry, float angle, boolean largeArcFlag, boolean sweepFlag, float x, float y)
        {
            if (lastX == x && lastY == y) {
                // If the endpoints (x, y) and (x0, y0) are identical, then this
                // is equivalent to omitting the elliptical arc segment entirely.
                // (behaviour specified by the spec)
                return;
            }

            // Handle degenerate case (behaviour specified by the spec)
            if (rx == 0 || ry == 0) {
                this.lineTo(x, y);
                return;
            }

            // Sign of the radii is ignored (behaviour specified by the spec)
            rx = Math.abs(rx);
            ry = Math.abs(ry);

            // Convert angle from degrees to radians
            float angleRad = (float) Math.toRadians(angle % 360.0);
            float cosAngle = (float) Math.cos(angleRad);
            float sinAngle = (float) Math.sin(angleRad);

            // We simplify the calculations by transforming the arc so that the origin is at the
            // midpoint calculated above followed by a rotation to line up the coordinate axes
            // with the axes of the ellipse.

            // Compute the midpoint of the line between the current and the end point
            float dx2 = (lastX - x) / 2.0f;
            float dy2 = (lastY - y) / 2.0f;

            // Step 1 : Compute (x1', y1') - the transformed start point
            float x1 = (cosAngle * dx2 + sinAngle * dy2);
            float y1 = (-sinAngle * dx2 + cosAngle * dy2);

            float rx_sq = rx * rx;
            float ry_sq = ry * ry;
            float x1_sq = x1 * x1;
            float y1_sq = y1 * y1;

            // Check that radii are large enough.
            // If they are not, the spec says to scale them up so they are.
            // This is to compensate for potential rounding errors/differences between SVG implementations.
            float radiiCheck = x1_sq / rx_sq + y1_sq / ry_sq;
            if (radiiCheck > 1) {
                rx = (float) Math.sqrt(radiiCheck) * rx;
                ry = (float) Math.sqrt(radiiCheck) * ry;
                rx_sq = rx * rx;
                ry_sq = ry * ry;
            }

            // Step 2 : Compute (cx1, cy1) - the transformed centre point
            float sign = (largeArcFlag == sweepFlag) ? -1 : 1;
            float sq = ((rx_sq * ry_sq) - (rx_sq * y1_sq) - (ry_sq * x1_sq)) / ((rx_sq * y1_sq) + (ry_sq * x1_sq));
            sq = (sq < 0) ? 0 : sq;
            float coef = (float) (sign * Math.sqrt(sq));
            float cx1 = coef * ((rx * y1) / ry);
            float cy1 = coef * -((ry * x1) / rx);

            // Step 3 : Compute (cx, cy) from (cx1, cy1)
            float sx2 = (lastX + x) / 2.0f;
            float sy2 = (lastY + y) / 2.0f;
            float cx = sx2 + (cosAngle * cx1 - sinAngle * cy1);
            float cy = sy2 + (sinAngle * cx1 + cosAngle * cy1);

            // Step 4 : Compute the angleStart (angle1) and the angleExtent (dangle)
            float ux = (x1 - cx1) / rx;
            float uy = (y1 - cy1) / ry;
            float vx = (-x1 - cx1) / rx;
            float vy = (-y1 - cy1) / ry;
            float p, n;

            // Compute the angle start
            n = (float) Math.sqrt((ux * ux) + (uy * uy));
            p = ux; // (1 * ux) + (0 * uy)
            sign = (uy < 0) ? -1.0f : 1.0f;
            float angleStart = (float) Math.toDegrees(sign * Math.acos(p / n));

            // Compute the angle extent
            n = (float) Math.sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy));
            p = ux * vx + uy * vy;
            sign = (ux * vy - uy * vx < 0) ? -1.0f : 1.0f;
            double angleExtent = Math.toDegrees(sign * Math.acos(p / n));
            if (!sweepFlag && angleExtent > 0) {
                angleExtent -= 360f;
            } else if (sweepFlag && angleExtent < 0) {
                angleExtent += 360f;
            }
            angleExtent %= 360f;
            angleStart %= 360f;

            // Many elliptical arc implementations including the Java2D and Android ones, only
            // support arcs that are axis aligned.  Therefore we need to substitute the arc
            // with bezier curves.  The following method call will generate the beziers for
            // a unit circle that covers the arc angles we want.
            float[]  bezierPoints = arcToBeziers(angleStart, angleExtent);

            // Calculate a transformation matrix that will move and scale these bezier points to the correct location.
            Matrix m = new Matrix();
            m.postScale(rx, ry);
            m.postRotate(angle);
            m.postTranslate(cx, cy);
            m.mapPoints(bezierPoints);

            // The last point in the bezier set should match exactly the last coord pair in the arc (ie: x,y). But
            // considering all the mathematical manipulation we have been doing, it is bound to be off by a tiny
            // fraction. Experiments show that it can be up to around 0.00002.  So why don't we just set it to
            // exactly what it ought to be.
            bezierPoints[bezierPoints.length-2] = x;
            bezierPoints[bezierPoints.length-1] = y;

            // Final step is to add the bezier curves to the path
            for (int i=0; i<bezierPoints.length; i+=6)
            {
                this.cubicTo(bezierPoints[i], bezierPoints[i+1], bezierPoints[i+2], bezierPoints[i+3], bezierPoints[i+4], bezierPoints[i+5]);
            }
        }

        private float[] arcToBeziers(double angleStart, double angleExtent)
        {
            int    numSegments = (int) Math.ceil(Math.abs(angleExtent) / 90.0);

            angleStart = Math.toRadians(angleStart);
            angleExtent = Math.toRadians(angleExtent);
            float  angleIncrement = (float) (angleExtent / numSegments);

            // The length of each control point vector is given by the following formula.
            double  controlLength = 4.0 / 3.0 * Math.sin(angleIncrement / 2.0) / (1.0 + Math.cos(angleIncrement / 2.0));

            float[] coords = new float[numSegments * 6];
            int     pos = 0;

            for (int i=0; i<numSegments; i++)
            {
                double  angle = angleStart + i * angleIncrement;
                // Calculate the control vector at this angle
                double  dx = Math.cos(angle);
                double  dy = Math.sin(angle);
                // First control point
                coords[pos++]   = (float) (dx - controlLength * dy);
                coords[pos++] = (float) (dy + controlLength * dx);
                // Second control point
                angle += angleIncrement;
                dx = Math.cos(angle);
                dy = Math.sin(angle);
                coords[pos++] = (float) (dx + controlLength * dy);
                coords[pos++] = (float) (dy - controlLength * dx);
                // Endpoint of bezier
                coords[pos++] = (float) dx;
                coords[pos++] = (float) dy;
            }
            return coords;
        }



    }
}
