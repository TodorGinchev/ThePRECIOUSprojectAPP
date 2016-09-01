package precious_rule_system.journeyview_new.utilities;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.PictureDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.caverock.androidsvg.SVG;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by khatt on 8/8/2016.
 */

/**
 * Utilities Class containing different utlities used for rendering, I/O, Conversions etc
 */
public class Utilities {

    public enum LandscapeType {
        MOUNTAIN,
        FOREST,
        OCEAN
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    /**
     * Get Screen Width in DP
     * @param context
     * @return
     */
    public static float getScreenWidthDp(Context context) {
        DisplayMetrics metrics = getDisplayMetrics(context);
        float density = context.getResources().getDisplayMetrics().density;
        return metrics.widthPixels / density;
    }

    /**
     * Get Screen Width in Pixels
     * @param context
     * @return
     */
    public static float getScreenWidthPx(Context context) {
        return convertDpToPixel(context, getScreenWidthDp(context));
    }

    /**Convert DP to pixels
     *
     * @param context
     * @param dp
     * @return
     */
    public static float convertDpToPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    /**
     * Get the DisplayMetrics
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics;
    }

    /**
     * Get ActionBar Height in Pixels
     * @param activity
     * @return
     */
    public static int getActionBarHeight(Activity activity) {
        TypedValue tv = new TypedValue();
        int actionBarHeight;
        if (activity.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            // Used for native ActionBar
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
        } else if (activity.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            // Used for AppCompat ActionBar
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
        } else {
            actionBarHeight = 0;
        }
        return actionBarHeight;
    }

    /**
     * Get StatusBar height in Pixels
     * @param activity
     * @return
     */
    public static int getStatusBarHeightPixel(Activity activity) {
        Rect rect = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    /**
     * Get the Total Screen Height in Pixels
     * @param activity
     * @return
     */
    public static int getScreenHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        return height;
    }

    /**CreateBitmap Drawable for the SVG
     *
     * @param context
     * @param svg
     * @return
     */
    public static BitmapDrawable createBitmapDrawable(Context context, SVG svg) {
        Bitmap bitmap = Bitmap.createBitmap(svg.renderToPicture().getWidth(),
                svg.renderToPicture().getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        PictureDrawable drawable = new PictureDrawable(svg.renderToPicture());
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    /**
     * Load the JSON File containing height, positioning information of SVGs
     * @param context
     * @param fileName
     * @return
     */
    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        AssetManager assetManager = context.getResources().getAssets();
        try {
            InputStream is = assetManager.open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public class Size {

        private int width;
        private int height;

        public Size(int w, int h) {
            width = w;
            height = h;
        }

        public int getHeight() {
           return this.height;
        }
        public int getWidth() {
            return this.width;
        }
    }

}
