package precious_rule_system.journeyview_new.assets;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.RectF;
import android.graphics.Region;

import com.caverock.androidsvg.SVG;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import precious_rule_system.journeyview_new.utilities.SVGExtended;

/**
 * Created by christopher on 30.08.16.
 */

public class JourneyAssets {

    public class Asset {
        public double width;
        public double height;
        public Bitmap bitmap;
        public SVG svg;
        public android.graphics.Path path = null;
        public float offset = 0;
        public Region region = null;
        public Region clip = null;
        public android.graphics.Path boxedPath = null;
    }

    public enum Landscape {

        MOUNTAIN("mountain");

        private final String text;
        private Landscape(final String text) { this.text = text; }
        public String toString() { return text; }
    }

    public enum Size {

        SMALL("small"),
        MEDIUM("medium"),
        LARGE("large");

        private final String text;
        private Size(final String text) { this.text = text; }
        public String toString() { return text; }
    }

    Context context;

    public Asset path;
    public Map<Landscape, Map<Size, Asset>> assets;
    public Map<Size, double[]> assetSizes;

    JSONObject json;

    public JourneyAssets(Context context, String path) {
        this.context = context;
        this.assets = new HashMap<Landscape, Map<Size, Asset>>();
        this.assetSizes = new HashMap<>();

        this.json = this.loadJSON(path);
        try {
            this.loadAssets();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void scaleAssets(int width, int height) {

        // scale items
        Iterator it = assets.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry _pair = (Map.Entry) it.next();
            Landscape landscape = (Landscape) _pair.getKey();
            Iterator itt = ((Map) _pair.getValue()).entrySet().iterator();
            while (itt.hasNext()) {
                Map.Entry pair = (Map.Entry) itt.next();
                Size size = (Size) pair.getKey();
                Asset asset = (Asset) pair.getValue();
                scaleAsset(asset, width, height);
            }
        }

        // scale the path
        this.scaleAsset(path, width, height);

        // derive the path at the right place for dynamic drawing
        path.offset = (((float) width - ((float) path.width)*((float) width))/2)/(float) width;

        Bitmap b = path.bitmap;
        Path p = ((SVGExtended) path.svg).getPath();
        Matrix scaleMatrix = new Matrix();
        RectF rectF = new RectF();
        p.computeBounds(rectF, true);
        scaleMatrix.setScale(b.getWidth()/rectF.width(), (b.getHeight())/rectF.height(),rectF.top,rectF.left);
        p.transform(scaleMatrix);
        Matrix transMatrix = new Matrix();
        transMatrix.setTranslate(path.offset*width, 0);
        p.transform(transMatrix);
        path.path = p;

        // create the region for collision detection
        this.createBoxedPathRegion();

    }

    private void createBoxedPathRegion() {

        Path androidPath = ((SVGExtended) path.svg).getPath();

        RectF rectF = new RectF();
        androidPath.computeBounds(rectF, true);

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(((float) path.width)*1000/rectF.width(), 1000/rectF.height(),rectF.top,rectF.left);

        Matrix transMatrix = new Matrix();
        transMatrix.setTranslate(path.offset*1000,0);

        androidPath.transform(scaleMatrix);
        androidPath.transform(transMatrix);

        path.boxedPath = androidPath;
        path.clip = new Region(0, 0, 1000, 1000);
        path.region = new Region();
        path.region.setPath(androidPath,path.clip);

    }

    private void scaleAsset(Asset asset, int width, int height) {

        int correctHeight = (int) ((double) height * asset.height);
        int correctWidth = (int) ((double) width * asset.width);

        asset.svg.setDocumentHeight(correctHeight);
        asset.svg.setDocumentWidth(correctWidth);

        asset.bitmap =  Bitmap.createBitmap (correctWidth,correctHeight, Bitmap.Config.ARGB_8888);
        Canvas svgCanvas = new Canvas(asset.bitmap);
        Picture svgPicture = asset.svg.renderToPicture();
        svgCanvas.drawPicture(svgPicture);
    }

    private double[] setAssetDimensions(Asset asset, JSONObject json) throws Exception {
        double w = json.getDouble("width");
        double h = json.getDouble("height");
        asset.height = h;
        asset.width = w;
        double[] res = {w,h};
        return res;
    }

    private void loadAssets() throws Exception {

        // Path
        path = new Asset();
        path.svg = new SVGExtended(SVG.getFromAsset(context.getAssets(), "journey/global/path.svg"));
        setAssetDimensions(path, json.getJSONObject("path"));

        // Other elements
        for(Landscape l : Landscape.values()) {
            HashMap <Size, Asset> map = new HashMap<Size, Asset>();
            for(Size s : Size.values()) {
                Asset asset = new Asset();
                asset.svg = SVG.getFromAsset(context.getAssets(), "journey/"+l.toString()+"/"+s.toString()+".svg");
                double[] dims = setAssetDimensions(asset, json.getJSONObject(s.toString()));
                this.assetSizes.put(s, dims);
                map.put(s, asset);
            }
            assets.put(l, map);
        }
    }

    private JSONObject loadJSON(String path) {
        String json = null;
        AssetManager assetManager = context.getResources().getAssets();
        try {
            InputStream is = assetManager.open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            return new JSONObject(json);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
