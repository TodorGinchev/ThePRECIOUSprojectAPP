package precious_rule_system.journeyview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import precious_rule_system.journeyview.assets.Assets;
import precious_rule_system.journeyview.data.DataManager;
import precious_rule_system.journeyview.helpers.SizeCalculator;
import precious_rule_system.journeyview.view.JourneyView;


/**
 * Created by christopher on 04.09.16.
 */

public class JourneyActivity extends AppCompatActivity {

    public class JourneyStore {
        public Assets assets;
        public DataManager data;
        public SizeCalculator sizes;
        public JourneyView journeyView;

        public JourneyStore(Assets assets, SizeCalculator sizes) {
            this.assets = assets;
            this.data = null;
            this.sizes = sizes;
        }

        public void setDataManager(DataManager data) {
            this.data = data;
        }
        public void setJourneyView(JourneyView view) { this.journeyView = view; data.getState().setJourneyView(view); }
    }

    private static String jsonFile = "journey/journeyassets.json";
    JourneyStore store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SizeCalculator sizes = new SizeCalculator(this);
        Assets assets = new Assets(this, jsonFile, sizes);
        this.store = new JourneyStore(assets, sizes);
        DataManager data = new DataManager(this.store);
        store.setDataManager(data);
        this.setup();
    }

    private void setup() {
        JourneyView view = new JourneyView(this, this.store);
        setContentView(view);
    }
}
