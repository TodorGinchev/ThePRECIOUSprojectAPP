package precious_rule_system.journeyview.recycler;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Random;

import precious_rule_system.journeyview.JourneyActivity;
import precious_rule_system.journeyview.assets.Asset;
import precious_rule_system.journeyview.assets.Assets;
import precious_rule_system.journeyview.constants.Constants;
import precious_rule_system.journeyview.data.StatePage;
import precious_rule_system.journeyview.data.StatePageRewardEvent;
import precious_rule_system.journeyview.helpers.AssetBackgroundGenerator;
import precious_rule_system.journeyview.helpers.Position;
import precious_rule_system.journeyview.recycler.views.AssetView;
import precious_rule_system.journeyview.recycler.views.PathView;
import precious_rule_system.journeyview.recycler.views.PlayerView;
import precious_rule_system.journeyview.recycler.views.RewardsView;
import rules.helpers.Tuple;

/**
 * Created by christopher on 04.09.16.
 */

public class RecyclerPageView extends RelativeLayout {

    JourneyActivity.JourneyStore store;
    Context context;

    PathView pathView;
    AssetView assetView;
    RewardsView rewardsView;
    PlayerView playerView;

    boolean debug = false;

    public RecyclerPageView(Context context, JourneyActivity.JourneyStore store) {

        super(context);
        this.store = store;
        this.context = context;

        // easier to debug pageview borders with different colors
        if (debug) {
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            this.setBackgroundColor(color);
        }

        this.setupAssetView();
        this.setupPathView();
        this.setupRewardsView();
        this.setupPlayerView();
    }

    private void setupPathView() {
        pathView = new PathView(context, store);
        this.addView(pathView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
    }

    private void setupAssetView() {
        assetView = new AssetView(context, store);
        this.addView(assetView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
    }

    private void setupRewardsView() {
        rewardsView = new RewardsView(store, context);
        this.addView(rewardsView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
    }

    private void setupPlayerView() {
        playerView = new PlayerView(store, context);
        this.addView(playerView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
    }

    public void updatePosition(int position) {
        rewardsView.setPosition(position);
    }

    public void updateBackground(AssetBackgroundGenerator.AssetBackground background, int width, int height, boolean isDirty) {
        assetView.setBackground(background);
        if (isDirty) {
            assetView.invalidate();
            pathView.invalidate();
        }
    }

    public void updateRewards(ArrayList<StatePageRewardEvent> rewards, int width, int height, boolean isDirty) {
        rewardsView.setRewards(rewards, width, height);
    }

    public void updateOverlappingRewards(ArrayList<StatePageRewardEvent> rewards) {
        for (StatePageRewardEvent r : rewards) {
            rewardsView.addOverlappingRewardEvent(r);
        }
    }

    public void updatePlayer(Float position, int width, int height) {
        playerView.setPlayer(position, width, height);
    }

    public void updateOverlappingPlayer(Position p) {
        if (p != null) playerView.setOverlappingPlayer(p);
    }

    public void cancelAnimations() {
        rewardsView.cancelAnimations();
    }

}
