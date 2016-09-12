package precious_rule_system.journeyview.helpers;

import android.content.Context;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import precious_rule_system.journeyview.JourneyActivity;
import precious_rule_system.journeyview.assets.Asset;
import precious_rule_system.journeyview.assets.Assets;
import precious_rule_system.journeyview.constants.Constants;
import rules.helpers.Tuple;

/**
 * Created by christopher on 04.09.16.
 */

public class AssetBackgroundGenerator {

    public static class AssetBackground {

        public Constants.Landscape landscape;
        public ArrayList<Tuple<Constants.Size, Position>> assets;
        public ArrayList<Boolean> flipped;
        public ArrayList<RectF> boundingRects;
        public ArrayList<RectF> removedRects;

        public AssetBackground(Constants.Landscape landscape) {
            this.landscape = landscape;
            this.assets = new ArrayList<>();
            this.boundingRects = new ArrayList<>();
            this.removedRects = new ArrayList<>();
            this.flipped = new ArrayList<>();
        }
    }

    public static AssetBackground calculateRandomBackground(JourneyActivity.JourneyStore store, Constants.Landscape landscape, int width, int height) {

        // retreive the scaled path
        Path path = store.assets.getPath().getBoxedPath(width, height);
        Region clip = new Region(0, 0, width, height);

        // get the size of boxes in our scale-space
        Map<Constants.Size, Asset> assetMap = store.assets.getAssets(landscape);
        Map<Constants.Size, double[]> sizes = new HashMap<>();

        // get our sizes
        Asset large = assetMap.get(Constants.Size.LARGE);
        Asset medium = assetMap.get(Constants.Size.MEDIUM);
        Asset small = assetMap.get(Constants.Size.SMALL);

        // convert their sizes
        RectF largeConverted = store.sizes.calculateAssetSize(large.unitWidth, large.unitHeight, Constants.constantWidthToHeightRatio, width, height);
        RectF mediumConverted = store.sizes.calculateAssetSize(medium.unitWidth, medium.unitHeight, Constants.constantWidthToHeightRatio, width, height);
        RectF smallConverted = store.sizes.calculateAssetSize(small.unitWidth, small.unitHeight, Constants.constantWidthToHeightRatio, width, height);

        // store them
        double[] largeSizes = { largeConverted.width(), largeConverted.height() };
        double[] mediumSizes = { mediumConverted.width(), mediumConverted.height() };
        double[] smallSizes = { smallConverted.width(), smallConverted.height() };

        sizes.put(Constants.Size.LARGE, largeSizes);
        sizes.put(Constants.Size.MEDIUM, mediumSizes);
        sizes.put(Constants.Size.SMALL, smallSizes);

        // create the results
        AssetBackground result = new AssetBackground(landscape);

        // create the rects
        makeRects(path, clip, 0,0,width,height, result, sizes);

        // create flipped array
        for(int i=0; i<result.assets.size(); i++) {
            result.flipped.add(Math.random() > 0.5 && landscape.isFlipable() ? true : false);
        }

        // return
        return result;
    }


    private static void makeRects(Path path, Region clip,double x, double y, double w, double h, AssetBackground result, Map<Constants.Size, double[]> sizes) {

        double[] large = sizes.get(Constants.Size.LARGE);
        double[] medium = sizes.get(Constants.Size.MEDIUM);
        double[] small = sizes.get(Constants.Size.SMALL);

        Constants.Size dr = shouldSubdivide(w, h, sizes);
        boolean shouldPlaceRect = w > small[0] && h > small[1];
        boolean isColliding = SVGHelper.isColliding(path, clip, (float) x, (float) y, (float) w, (float) h, 1.0f);

        if (dr == null && shouldPlaceRect || (isColliding && shouldPlaceRect)) {
            if (random(0, w+h) < w) {
                double split = random(0, w);
                makeRects(path, clip,x,y,split,h,result,sizes);
                makeRects(path, clip,x+split,y,w-split,h,result,sizes);
            } else {
                double split = random(0, h);
                makeRects(path, clip,x,y,w,split,result,sizes);
                makeRects(path, clip,x,y+split,w,h-split,result,sizes);
            }
        } else {
            if (shouldPlaceRect) {

                double[] bounding = randomRect(x,y,w,h,dr,sizes,result);

                double _x = bounding[0]-x;
                double _y = bounding[1]-y;
                double _w = bounding[2];
                double _h = bounding[3];

                double ba = _w*_h;
                double area = w*h;
                double prob = ba/area;

                if (Math.random() > prob) {

                    double a = _x;
                    double b = _w;
                    double c = w - (a+b);
                    double d = _y;
                    double e = _h;
                    double f = h - (d+e);

                    double[] sr1 = {x,y,a,d};
                    double[] sr2 = {x+a,y,b,d};
                    double[] sr3 = {x+a+b,y,c,d};
                    double[] sr4 = {x+a+b,y+d,c,e};
                    double[] sr5 = {x+a+b,y+d+e,c,f};
                    double[] sr6 = {x+a, y+d+e,b,f};
                    double[] sr7 = {x,y+d+e, a,f};
                    double[] sr8 = {x,y+d,a,e};

                    double la = large[0]*large[1];

                    if (Math.random() < 1.0-(la/sr1[2]*sr1[3])) makeRects(path, clip,sr1[0], sr1[1], sr1[2], sr1[3],result, sizes);
                    if (Math.random() < 1.0-(la/sr2[2]*sr2[3])) makeRects(path, clip,sr2[0], sr2[1], sr2[2], sr2[3],result, sizes);
                    if (Math.random() < 1.0-(la/sr3[2]*sr3[3])) makeRects(path, clip,sr3[0], sr3[1], sr3[2], sr3[3],result, sizes);
                    if (Math.random() < 1.0-(la/sr4[2]*sr4[3])) makeRects(path, clip,sr4[0], sr4[1], sr4[2], sr4[3],result, sizes);
                    if (Math.random() < 1.0-(la/sr5[2]*sr5[3])) makeRects(path, clip,sr5[0], sr5[1], sr5[2], sr5[3],result, sizes);
                    if (Math.random() < 1.0-(la/sr6[2]*sr6[3])) makeRects(path, clip,sr6[0], sr6[1], sr6[2], sr6[3],result, sizes);
                    if (Math.random() < 1.0-(la/sr7[2]*sr7[3])) makeRects(path, clip,sr7[0], sr7[1], sr7[2], sr7[3],result, sizes);
                    if (Math.random() < 1.0-(la/sr8[2]*sr8[3])) makeRects(path, clip,sr8[0], sr8[1], sr8[2], sr8[3],result, sizes);

                }
            }
        }
    }

    private static Constants.Size shouldSubdivide(double w, double h, Map<Constants.Size, double[]> sizes) {

        double[] large = sizes.get(Constants.Size.LARGE);
        double[] medium = sizes.get(Constants.Size.MEDIUM);
        double[] small = sizes.get(Constants.Size.SMALL);

        double area = w*h;

        if (w < small[0] && h < small[1]) return null;
        if (w >= large[0]*2 && h >= large[1]*2) return null;

        if (w >= large[0] && h >= large[1]) {
            double a = large[0]*large[1];
            double prob = 1.0 - a/area;
            if (Math.random() >= prob/1.4) {
                return Constants.Size.LARGE;
            }
        }

        if (w >= medium[0] && h >= medium[1]) {
            double a = medium[0]*medium[1];
            double prob = 1.0 - a/area;
            if (Math.random() >= prob/1.2) {
                return Constants.Size.MEDIUM;
            }
        }

        if (w >= small[0] && h >= small[1]) {
            double a = small[0]*small[1];
            double prob = 1.0 - a/area;
            if (Math.random() >= prob) {
                return Constants.Size.SMALL;
            }
        }

        return null;

    }

    private static double[] randomRect(double x, double y, double w, double h, Constants.Size s, Map<Constants.Size, double[]> sizes, AssetBackground result) {
        double[] desired = sizes.get(s);
        double insetX = random(0, w-desired[0]);
        double insetY = random(0, h-desired[1]);
        double[] coords = { insetX+x, insetY+y };
        double[] rect = {x,y,w,h};
        double[] bounding = {coords[0], coords[1], desired[0], desired[1]};
        result.boundingRects.add(new RectF((float)coords[0],(float)coords[1],(float)coords[0] + (float)desired[0],(float)coords[1] + (float)desired[1]));
        Position p = new Position((float)coords[0], (float)coords[1], false);
        result.assets.add(new Tuple<Constants.Size, Position>(s, p));
        return bounding;
    }

    private static double random(double start, double end) {
        double random = new Random().nextDouble();
        return start + (random * (end - start));
    }

    private static double random(double end) {
        return random(0, end);
    }
}
