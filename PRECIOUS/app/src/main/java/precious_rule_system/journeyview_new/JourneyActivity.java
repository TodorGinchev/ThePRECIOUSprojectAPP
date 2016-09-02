package precious_rule_system.journeyview_new;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import precious_rule_system.journeyview_new.assets.JourneyAssets;
import precious_rule_system.journeyview_new.grid.JourneyGrid;
import precious_rule_system.journeyview_new.grid.JourneyGridAdapter;
import precious_rule_system.journeyview_new.utilities.Layout;
import precious_rule_system.journeyview_new.view.ViewWrapper;

/**
 * Created by christopher on 30.08.16.
 */

public class JourneyActivity extends AppCompatActivity {

    private int width = 0;
    private int height = 0;

    private Layout layout = new Layout();
    private JourneyAssets assets;
    private DataManager data;
    private static String jsonFile = "journey/journeyassets.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.assets = new JourneyAssets(this, jsonFile);
        this.getDimensions();
        this.data = new DataManager(assets,layout);
        this.setup();
    }

    private void getDimensions() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        width = displaymetrics.widthPixels;
        height = displaymetrics.heightPixels;
        this.assets.scaleAssets(width, height);
    }

    private void setup() {
        ViewWrapper view = new ViewWrapper(this, width, height, layout, assets, data);
        setContentView(view);
    }
}
