package precious_rule_system.journeyview.assets;

import android.content.Context;
import android.content.res.AssetManager;

import com.caverock.androidsvg.SVG;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import precious_rule_system.journeyview.constants.Constants;
import precious_rule_system.journeyview.helpers.SizeCalculator;
import precious_rule_system.journeyview.helpers.SVGExtended;

/**
 * Created by christopher on 04.09.16.
 */

public class Assets {

    Context context;
    SizeCalculator sizes;

    // hashmap of our assets, containing another hashmap with the different sizes
    private Map<Constants.Landscape, Map<Constants.Size, Asset>> assets;
    // reference of our path object
    private Path path;

    public Assets(Context context, String path, SizeCalculator sizes) {
        this.assets = new HashMap<Constants.Landscape, Map<Constants.Size, Asset>>();
        this.context = context;
        this.sizes = sizes;

        // load the json from file
        JSONObject json = this.loadJSON(path);
        try {
            // load the assets
            this.loadAssets(json);
            // load the path
            this.loadPath(json);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Getter for assets of a given landscape
     * @param l
     * @return
     */
    public Map<Constants.Size, Asset> getAssets(Constants.Landscape l) {
        return assets.get(l);
    }

    /**
     * Loads assets from the local json file in the assets directory
     * @param json
     * @throws Exception
     */
    private void loadAssets(JSONObject json) throws Exception {

        for(Constants.Landscape l : Constants.Landscape.values()) {
            HashMap <Constants.Size, Asset> map = new HashMap<Constants.Size, Asset>();
            for(Constants.Size s : Constants.Size.values()) {
                SVG svg = SVG.getFromAsset(context.getAssets(), "journey/"+l.toString()+"/"+s.toString()+".svg");
                double w = json.getJSONObject(s.toString()).getDouble("width");
                double h = json.getJSONObject(s.toString()).getDouble("height");
                Asset asset = new Asset(sizes, w,h,Constants.constantWidthToHeightRatio,svg);
                map.put(s, asset);
            }
            assets.put(l, map);
        }
    }

    /**
     * Loads path dimensions from the local json file
     * @param json
     * @throws Exception
     */
    private void loadPath(JSONObject json) throws Exception {
        SVGExtended svg =  new SVGExtended(SVG.getFromAsset(context.getAssets(), "journey/global/path-single.svg"));
        double w = json.getJSONObject("path").getDouble("width");
        double h = json.getJSONObject("path").getDouble("height");
        path = new Path(sizes, w,h,Constants.constantWidthToHeightRatio, Constants.pathOffsetPosition, svg);
    }

    /**
     * returns a jsonobject from a path
     * @param path
     * @return
     */
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

    /**
     * Path getter
     * @return
     */
    public Path getPath() {
        return path;
    }
}
