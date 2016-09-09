package precious_rule_system.journeyview;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import precious_rule_system.journeyview.assets.Assets;
import precious_rule_system.journeyview.data.DataManager;
import precious_rule_system.journeyview.helpers.SizeCalculator;
import precious_rule_system.journeyview.recycler.RecyclerView;
import precious_rule_system.journeyview.view.JourneyView;
import precious_rule_system.rewardsystem.entities.RewardEvent;


/**
 * Created by christopher on 04.09.16.
 */

public class JourneyActivity extends AppCompatActivity {

    // convenience class for storing all kinds of instances
    public class JourneyStore {

        public Assets assets;
        public DataManager data;
        public SizeCalculator sizes;
        public JourneyView journeyView;
        public JourneyActivity activity;

        public JourneyStore(JourneyActivity activity, Assets assets, SizeCalculator sizes) {
            this.assets = assets;
            this.data = null;
            this.sizes = sizes;
            this.activity = activity;
        }

        public void setDataManager(DataManager data) {
            this.data = data;
        }
        public void setJourneyView(JourneyView view) { this.journeyView = view; data.getState().setJourneyView(view); }
        public RecyclerView getRecyclerView() { return this.data.getState().getRecyclerView(); }
    }

    private static String jsonFile = "journey/journeyassets.json";
    JourneyStore store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SizeCalculator sizes = new SizeCalculator(this);
        Assets assets = new Assets(this, jsonFile, sizes);
        this.store = new JourneyStore(this, assets, sizes);
        DataManager data = new DataManager(this.store);
        store.setDataManager(data);
        this.setup();
    }

    @Override
    protected void onPause() {
        super.onPause();
        store.data.onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        store.data.onResume();
    }

    private void setup() {
        JourneyView view = new JourneyView(this, this.store);
        setContentView(view);
    }

    public void startPopupActivityForRewardEvent(RewardEvent e) {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = JourneyRewardPopupDialog.newInstance(e);
        newFragment.show(ft, "dialog");
    }

}
