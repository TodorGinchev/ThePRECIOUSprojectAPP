package precious_rule_system.journeyview.assets;

import android.graphics.Matrix;
import android.graphics.RectF;

import precious_rule_system.journeyview.helpers.SVGHelper;
import precious_rule_system.journeyview.helpers.SizeCalculator;
import precious_rule_system.journeyview.helpers.SVGExtended;

/**
 * Created by christopher on 04.09.16.
 */

public class Path {

    // same sizes as in asset class
    public double unitWidth;
    public double unitHeight;
    public double unitWidthToHeightRatio;

    // position of the path in the view, specified in constants as percentage, i.e, 0.5
    public double pathPosition;

    // the path svg
    public SVGExtended svg;

    // the path rendered as android-path without offset
    private android.graphics.Path path = null;

    // path rendered with offset
    private android.graphics.Path offsetPath = null;

    // offset path rendered as boxes with the right strokewidth for collision detection
    private android.graphics.Path boxedPath = null;

    // the stored with and height of the boxed path for checking dirty states
    private int boxedWidth = 0;
    private int boxedHeight = 0;

    // the current activity size for checking dirty states
    private RectF currentBounds = null;

    // size calculator references
    SizeCalculator sizes;

    public Path(SizeCalculator sizes, double unitWidth, double unitHeight, double unitWidthToHeightRatio, double pathPosition, SVGExtended svg) {
        this.sizes = sizes;
        this.unitWidth = unitWidth;
        this.unitHeight = unitHeight;
        this.unitWidthToHeightRatio = unitWidthToHeightRatio;
        this.pathPosition = pathPosition;
        this.svg = svg;
    }

    /**
     * returns the path as android path, recalculates if viewwidth or height differ
     * @param viewWidth
     * @param viewHeight
     * @return
     */
    public android.graphics.Path getPath(int viewWidth, int viewHeight) {

        RectF desiredSize = sizes.calculatePathSize(this.unitWidth, this.unitHeight, this.unitWidthToHeightRatio, viewWidth, viewHeight);

        int desiredWidth = Math.round(desiredSize.width());
        int desiredHeight = Math.round(desiredSize.height());

        if (isDirty(desiredWidth, desiredHeight)) {
            this.path = renderPath(desiredWidth, desiredHeight);
            this.currentBounds = new RectF(0,0,desiredWidth,desiredHeight);
            this.offsetPath = generateOffsetPath(this.path, desiredWidth, desiredHeight, viewWidth, viewHeight);
        }

        return this.offsetPath;
    }

    /**
     * Returns the last calculated offset path
     * @return
     */
    public android.graphics.Path getLastPath() {
        return this.offsetPath;
    }

    /**
     * Checks whether the currently calculated paths are dirty
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    private boolean isDirty(int desiredWidth, int desiredHeight) {
        if (path == null || currentBounds == null) return true;
        if (Math.round(currentBounds.height()) != desiredHeight || Math.round(currentBounds.width()) != desiredWidth) return true;
        return false;
    }

    /**
     * Renders a path to an android path within the viewbox desiredwith and desiredheight
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    private android.graphics.Path renderPath(int desiredWidth, int desiredHeight) {
        android.graphics.Path p = SVGHelper.getPathWidthCleanedViewBox(svg);
        RectF rectF = new RectF();
        p.computeBounds(rectF, true);
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale((float) desiredWidth/rectF.width(), (float) desiredHeight/rectF.height(),0,0);
        p.transform(scaleMatrix);
        return p;
    }

    /**
     * Translates a path on the x-axis by pathposition - centers by translating by pathwidth/2
     * @param p
     * @param pathWidth
     * @param pathHeight
     * @param viewWidth
     * @param viewHeight
     * @return
     */
    private android.graphics.Path generateOffsetPath(android.graphics.Path p, int pathWidth, int pathHeight, int viewWidth, int viewHeight) {
        android.graphics.Path offset = new android.graphics.Path();
        double offsetPosition = this.pathPosition * (double) viewWidth - (double) pathWidth/2;
        Matrix transMatrix = new Matrix();
        transMatrix.setTranslate((float) offsetPosition, 0);
        p.transform(transMatrix, offset);
        return offset;
    }

    /**
     * Calculates the path in boxes for collision detection, recalculates if dirty
     * @param width
     * @param height
     * @return
     */
    public android.graphics.Path getBoxedPath(int width, int height) {

        if (boxedPath != null && this.boxedHeight == height && this.boxedWidth == width) return boxedPath;

        this.boxedWidth = width;
        this.boxedHeight = height;

        RectF desiredSize = sizes.calculatePathSize(this.unitWidth, this.unitHeight, this.unitWidthToHeightRatio, boxedWidth, boxedHeight);

        int desiredWidth = Math.round(desiredSize.width());
        int desiredHeight = Math.round(desiredSize.height());

        android.graphics.Path p = renderPath(desiredWidth, desiredHeight);
        android.graphics.Path pOffset = generateOffsetPath(p, desiredWidth, desiredHeight, width, height);

        android.graphics.Path rectanglePath = new android.graphics.Path();
        float rectW = sizes.getPathThickness(width, height);

        for(float i=0.0f; i<1.0001f; i+=0.01) {
            float[] t = SVGHelper.getPositionAlongPath(pOffset, i);
            rectanglePath.addRect(t[0]-rectW/2,t[1],t[0]+rectW/2,t[1]+0.01f*(float)height, android.graphics.Path.Direction.CW);
        }

        boxedPath = rectanglePath;

        return boxedPath;
    }

}
