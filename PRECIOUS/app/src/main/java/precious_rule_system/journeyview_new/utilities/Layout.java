package precious_rule_system.journeyview_new.utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import precious_rule_system.journeyview_new.assets.JourneyAssets;
import rules.helpers.Tuple;

/**
 * Created by christopher on 30.08.16.
 */

public class Layout {

    public ArrayList<double[]> rects;
    public ArrayList<double[]> rectsRemoved;

    public ArrayList<Tuple<JourneyAssets.Size, double[]>> calculateRandomSizes(Map<JourneyAssets.Size, double[]> sizes, double pathOffset, JourneyAssets assets) {

        rects = new ArrayList<>();

        double[] large = sizes.get(JourneyAssets.Size.LARGE);
        double[] medium = sizes.get(JourneyAssets.Size.MEDIUM);
        double[] small = sizes.get(JourneyAssets.Size.SMALL);

        ArrayList<Tuple<JourneyAssets.Size, double[]>> result = new ArrayList<Tuple<JourneyAssets.Size, double[]>>();
        makeRects(0,0,1.0,1.0,result, sizes);

        /*double[] small = sizes.get(JourneyAssets.Size.SMALL);

        for(double x=0;x<=1.0;x+=small[0]) {
            for(double y=0;y<=1.0;y+=small[1]) {
                double[] co = {x,y};
                result.add(new Tuple<JourneyAssets.Size, double[]>(JourneyAssets.Size.SMALL, co));
            }
        }*/

        removeCollidingRects(result, assets, sizes);
        return result;
    }

    private void removeCollidingRects(ArrayList<Tuple<JourneyAssets.Size, double[]>> result, JourneyAssets assets, Map<JourneyAssets.Size, double[]> sizes) {

        rectsRemoved = new ArrayList<>();

        Iterator<Tuple<JourneyAssets.Size, double[]>> i = result.iterator();
        while (i.hasNext()) {
            Tuple<JourneyAssets.Size, double[]> s = i.next();

            double x = s.y[0];
            double y = s.y[1];
            double w = sizes.get(s.x)[0];
            double h = sizes.get(s.x)[1];

            if (SVGExtended.isColliding(assets.path, (float) x, (float) y, (float) w, (float) h)) {
                i.remove();
                double[] rect = {x,y,w,h};
                rectsRemoved.add(rect);
            }
        }

    }

    private void makeRects(double x, double y, double w, double h, ArrayList<Tuple<JourneyAssets.Size, double[]>> list, Map<JourneyAssets.Size, double[]> sizes) {

        double[] large = sizes.get(JourneyAssets.Size.LARGE);
        double[] medium = sizes.get(JourneyAssets.Size.MEDIUM);
        double[] small = sizes.get(JourneyAssets.Size.SMALL);

        JourneyAssets.Size dr = shouldSubdivide(w, h, sizes);
        boolean shouldPlaceRect = w > small[0] && h > small[1];

        if (dr == null && shouldPlaceRect) {
            if (random(0, w+h) < w) {
                double split = random(0, w);
                //split = Math.random() >= 0.5? w/2-split : w/2+split;
                makeRects(x,y,split,h,list,sizes);
                makeRects(x+split,y,w-split,h,list,sizes);
            } else {
                double split = random(0, h);
                //split = Math.random() >= 0.5? h/2-split : h/2+split;
                makeRects(x,y,w,split,list,sizes);
                makeRects(x,y+split,w,h-split,list,sizes);
            }
        } else {
            if (shouldPlaceRect) {

                double[] bounding = randomRect(x,y,w,h,dr,sizes,list);
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

                    if (Math.random() < 1.0-(la/sr1[2]*sr1[3])) makeRects(sr1[0], sr1[1], sr1[2], sr1[3],list, sizes);
                    if (Math.random() < 1.0-(la/sr2[2]*sr2[3])) makeRects(sr2[0], sr2[1], sr2[2], sr2[3],list, sizes);
                    if (Math.random() < 1.0-(la/sr3[2]*sr3[3])) makeRects(sr3[0], sr3[1], sr3[2], sr3[3],list, sizes);
                    if (Math.random() < 1.0-(la/sr4[2]*sr4[3])) makeRects(sr4[0], sr4[1], sr4[2], sr4[3],list, sizes);
                    if (Math.random() < 1.0-(la/sr5[2]*sr5[3])) makeRects(sr5[0], sr5[1], sr5[2], sr5[3],list, sizes);
                    if (Math.random() < 1.0-(la/sr6[2]*sr6[3])) makeRects(sr6[0], sr6[1], sr6[2], sr6[3],list, sizes);
                    if (Math.random() < 1.0-(la/sr7[2]*sr7[3])) makeRects(sr7[0], sr7[1], sr7[2], sr7[3],list, sizes);
                    if (Math.random() < 1.0-(la/sr8[2]*sr8[3])) makeRects(sr8[0], sr8[1], sr8[2], sr8[3],list, sizes);

                }
            }
        }
    }

    private JourneyAssets.Size shouldSubdivide(double w, double h, Map<JourneyAssets.Size, double[]> sizes) {

        double[] large = sizes.get(JourneyAssets.Size.LARGE);
        double[] medium = sizes.get(JourneyAssets.Size.MEDIUM);
        double[] small = sizes.get(JourneyAssets.Size.SMALL);

        double area = w*h;

        if (w < small[0] && h < small[1]) return null;
        if (w >= large[0]*2 && h >= large[1]*2) return null;

        if (w >= large[0] && h >= large[1]) {
            double a = large[0]*large[1];
            double prob = 1.0 - a/area;
            if (Math.random() >= prob/1.5) {
                return JourneyAssets.Size.LARGE;
            }
        }

        if (w >= medium[0] && h >= medium[1]) {
            double a = medium[0]*medium[1];
            double prob = 1.0 - a/area;
            if (Math.random() >= prob/1.2) {
                return JourneyAssets.Size.MEDIUM;
            }
        }

        if (w >= small[0] && h >= small[1]) {
            double a = small[0]*small[1];
            double prob = 1.0 - a/area;
            if (Math.random() >= prob) {
                return JourneyAssets.Size.SMALL;
            }
        }

        return null;

    }

    private double[] randomRect(double x, double y, double w, double h, JourneyAssets.Size s, Map<JourneyAssets.Size, double[]> sizes, ArrayList<Tuple<JourneyAssets.Size, double[]>> list) {
        double[] desired = sizes.get(s);
        double insetX = random(0, w-desired[0]);
        double insetY = random(0, h-desired[1]);
        double[] coords = { insetX+x, insetY+y };
        double[] rect = {x,y,w,h};
        double[] bounding = {coords[0], coords[1], desired[0], desired[1]};
        rects.add(rect);
        list.add(new Tuple<JourneyAssets.Size, double[]>(s, coords));
        return bounding;
    }

    private double random(double start, double end) {
        double random = new Random().nextDouble();
        return start + (random * (end - start));
    }

}
