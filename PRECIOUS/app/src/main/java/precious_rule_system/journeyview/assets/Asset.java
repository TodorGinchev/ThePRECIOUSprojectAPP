package precious_rule_system.journeyview.assets;

import android.graphics.*;

import com.caverock.androidsvg.SVG;

import precious_rule_system.journeyview.helpers.SVGHelper;
import precious_rule_system.journeyview.helpers.SizeCalculator;

/**
 * Created by christopher on 04.09.16.
 */

public class Asset {

    // dimensions read in from the json file
    // width in percent
    public double unitWidth;
    // height in percent
    public double unitHeight;
    // the ratio upon which width and height were calculated
    public double unitWidthToHeightRatio;

    // pre-calculated bitmap
    public Bitmap bitmap;
    public Bitmap flippedBitmap;

    // the svg
    public SVG svg;

    // reference to our calculator so we can determine the correct size of the asset at runtime
    SizeCalculator sizes;

    public Asset(SizeCalculator sizes, double unitWidth, double unitHeight, double unitWidthToHeightRatio, SVG svg) {
        this.sizes = sizes;
        this.unitWidth = unitWidth;
        this.unitHeight = unitHeight;
        this.unitWidthToHeightRatio = unitWidthToHeightRatio;
        this.svg = svg;
        this.bitmap = null;
    }

    /**
     * Renders a bitmap with given viewwidth and height
     * only renders of pre-calculated bitmap is dirty
     * @param viewWidth - width of the screen
     * @param viewHeight - height of the screen
     * @return
     */
    public Bitmap getBitmap(int viewWidth, int viewHeight, boolean flipped) {

        RectF desiredSize = sizes.calculateAssetSize(this.unitWidth, this.unitHeight, this.unitWidthToHeightRatio, viewWidth, viewHeight);
        int desiredWidth = Math.round(desiredSize.width());
        int desiredHeight = Math.round(desiredSize.height());

        if (isDirty(desiredWidth, desiredHeight)) {
            this.bitmap = renderBitmap(desiredWidth, desiredHeight, false);
            this.flippedBitmap = renderBitmap(desiredWidth, desiredHeight, true);
        }

        if (flipped) {
            return this.flippedBitmap;
        } else {
            return this.bitmap;
        }
    }

    /**
     * determines whether the calculated bitmap was rendered with different height or width
     * @param desiredWidth - desired width of the bitmap
     * @param desiredHeight - desired height of the bitmap
     * @return
     */
    private boolean isDirty(int desiredWidth, int desiredHeight) {
        if (bitmap == null) return true;
        if (bitmap.getHeight() != desiredHeight || bitmap.getWidth() != desiredWidth) return true;
        return false;
    }

    /**
     * renders a bitmap from a svg
     * @param desiredWidth
     * @param desiredHeight
     * @return
     */
    private Bitmap renderBitmap(int desiredWidth, int desiredHeight, boolean flipped) {
        return SVGHelper.renderSVG(svg, desiredWidth, desiredHeight, flipped);
    }

}
