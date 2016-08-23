package precious_rule_system.journeyview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.precious.christopher.precious_rule_system.journeyview.utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by khatt on 8/8/2016.
 */

/**
 *
 */
public class LandscapeView extends View {
    private static String TAG = "LANDSCAPE_VIEW";
    private static String jsonFile = "journey/journey.json";
    private int viewWidth = 0;
    private int viewHeight = 0;
    private Utilities.LandscapeType landscapeType;
    private Context context;
    private SVG svgLarge;
    private SVG svgMedium;
    private SVG svgSmall;
    private SVG svgPath;
    private String jsonString;
    private JSONObject jsonRoot;
    private String landscapeDirectory;

    /**
     *The LandscapeView constructor loads the required files for assets based on the landscapeType provided
     * in the constructor parameters
     * @param mContext
     * @param landscapeType
     */
    public LandscapeView(Context mContext, Utilities.LandscapeType landscapeType) {
        super(mContext);
        context = mContext;
        this.landscapeType = landscapeType;

        switch(landscapeType) {
            case FOREST:
                landscapeDirectory = "journey/forest/";
                break;
            case OCEAN:
                landscapeDirectory = "journey/ocean/";
                break;
            case MOUNTAIN:
            default:
                landscapeDirectory = "journey/mountain/";
                break;
        }
        try {
            jsonString = Utilities.loadJSONFromAsset(context, jsonFile);
            jsonRoot = new JSONObject(jsonString);

            svgLarge = SVG.getFromAsset(context.getAssets(), landscapeDirectory + "large.svg");
            svgMedium = SVG.getFromAsset(context.getAssets(),landscapeDirectory + "medium.svg");
            svgSmall = SVG.getFromAsset(context.getAssets(),landscapeDirectory + "small.svg");

            svgPath = SVG.getFromAsset(context.getAssets(), "journey/global/path.svg");
        }
        catch (SVGParseException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        float actionBarHeightPx = Utilities.getActionBarHeight((Activity) context);
        float statusBarHeightPx = Utilities.getStatusBarHeightPixel((Activity) context);
        int screenHeight = Utilities.getScreenHeight((Activity)context);
        viewHeight = screenHeight - ( int) actionBarHeightPx - (int) statusBarHeightPx;
        this.setMeasuredDimension(viewWidth, viewHeight);
        this.setLayoutParams(new LinearLayout.LayoutParams(viewWidth, viewHeight));
    //    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        try {
            DrawView(canvas);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
/*
            Canvas bitmapLargeCanvas = new Canvas(bitmapLarge);
            Canvas bitmapSmallCanvas = new Canvas(bitmapSmall);
            Picture pictureLarge = svgLarge.renderToPicture();
            bitmapLargeCanvas.drawPicture(pictureLarge);
            Picture pictureSmall = svgSmall.renderToPicture();
            bitmapSmallCanvas.drawPicture(pictureSmall);
            canvas.drawBitmap(bitmapLarge,100,100,null);
            canvas.drawBitmap(bitmapSmall,200,200,null);

*/


    }

    private void DrawView(Canvas mainCanvas) throws JSONException {
        mainCanvas.drawColor(Color.WHITE);
        if (svgLarge.getDocumentWidth() == -1 ||
                svgSmall.getDocumentHeight() == -1 ||
                svgMedium.getDocumentWidth() == -1 ||
                svgPath.getDocumentWidth() == -1){
            Log.e(TAG,"One or more SVGs missing");
            return;
        }
        if (jsonRoot == null) {
            Log.e(TAG,"JSON file not found");
            return;
        }

        JSONObject pathJson = jsonRoot.getJSONObject("path");
        JSONObject smallJson =  jsonRoot.getJSONObject("small");
        JSONObject mediumJson = jsonRoot.getJSONObject("medium");
        JSONObject largeJson = jsonRoot.getJSONObject("large");

        DrawElement(pathJson, svgPath, mainCanvas);
        DrawElement(smallJson,svgSmall,mainCanvas);
        DrawElement(mediumJson,svgMedium,mainCanvas);
        DrawElement(largeJson,svgLarge,mainCanvas);

    }

    private void DrawElement(JSONObject elementJson, SVG svg, Canvas mainCanvas) throws JSONException {

        float svgHeight = 0;
        float svgWidth = 0;
        JSONArray positionArray = null;
        JSONObject positionElement = null ;
        Bitmap svgBitmap = null;
        Canvas svgCanvas = null;
        Picture svgPicture = null;
        int svgPositionLeftResized = 0;
        int svgPositionTopResized = 0;
        int svgHeightResized = 0;
        int svgWidthResized = 0;

        svgHeight = (float) elementJson.getDouble("height");
        svgWidth = (float)  elementJson.getDouble("width");
        positionArray = elementJson.getJSONArray("position");

        for(int i=0; i< positionArray.length(); i++ ) {
            positionElement = positionArray.getJSONObject(i);

            // Recalculate Top and Left using Json attributes and screen size
            svgPositionLeftResized = (int)(positionElement.getDouble("left")* viewWidth);
            svgPositionTopResized = (int) (positionElement.getDouble("top") * viewHeight);

            //Recalculate the size of SVG based on size from
            svgHeightResized =  (int)Math.ceil(svgHeight*viewHeight);
            svgWidthResized = (int) Math.ceil(svgWidth*viewWidth);
            svg.setDocumentHeight(svgHeightResized);
            svg.setDocumentWidth(svgWidthResized);

            //Create a bitmap equal tp the size of the new SVG and put a canvas on it
            svgBitmap = Bitmap.createBitmap (svgWidthResized,svgHeightResized, Bitmap.Config.ARGB_8888);
            svgCanvas = new Canvas(svgBitmap);
            //Render the svg as a picture using canvas
            svgPicture = svg.renderToPicture();
            svgCanvas.drawPicture(svgPicture);
            //Draw the new bitmap for svg on top of the main canvas using recalculated positions & sizes
            mainCanvas.drawBitmap(svgBitmap,svgPositionLeftResized,svgPositionTopResized,null);
        }
    }
}

