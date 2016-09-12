package precious_rule_system.journeyview.recycler;

import android.content.Context;
import android.support.v7.widget.*;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import precious_rule_system.journeyview.JourneyActivity;
import precious_rule_system.journeyview.assets.Asset;
import precious_rule_system.journeyview.assets.Assets;
import precious_rule_system.journeyview.data.StatePage;
import precious_rule_system.journeyview.data.StatePageRewardEvent;
import precious_rule_system.journeyview.helpers.AssetBackgroundGenerator;
import precious_rule_system.journeyview.helpers.Position;

/**
 * Created by christopher on 04.09.16.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    RecyclerPageView view;
    JourneyActivity.JourneyStore store;

    boolean isDirty = true;
    int width = 0;
    int height = 0;

    public RecyclerViewHolder(View v, JourneyActivity.JourneyStore store) {
        super(v);
        this.store = store;
        this.view = (RecyclerPageView) v;
    }

    public void update(int width, int height) {
        if (width != this.width || height != this.height) {
            this.width = width;
            this.height = height;
            this.isDirty = true;
            // update our path so we can use it later
            store.assets.getPath().getPath(width, height);
        } else {
            this.isDirty = false;
        }
    }

    public void cancelAnimations() {
        view.cancelAnimations();
    }

    public void updatePosition(int position) {
        view.updatePosition(position);
    }

    public void updateBackground(AssetBackgroundGenerator.AssetBackground background) {
        view.updateBackground(background, width, height, isDirty);
    }

    public void updateRewards(ArrayList<StatePageRewardEvent> rewards) {
        view.updateRewards(rewards, width, height, isDirty);
    }

    public void updatePlayer(Float position) {
        view.updatePlayer(position, width, height);
    }

    public void updateOverlappingRewards(ArrayList<StatePageRewardEvent> rewards) {
        view.updateOverlappingRewards(rewards);
    }

    public void updateOverlappingPlayer(Position p) {
        view.updateOverlappingPlayer(p);
    }

}
