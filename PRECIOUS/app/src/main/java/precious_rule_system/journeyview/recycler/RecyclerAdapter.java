package precious_rule_system.journeyview.recycler;

import android.view.ViewGroup;
import android.widget.LinearLayout;

import ui.precious.comnet.aalto.precious.ui_MainActivity;

/**
 * Created by christopher on 04.09.16.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    RecyclerView view;
    ui_MainActivity.JourneyStore store;


    public RecyclerAdapter(ui_MainActivity.JourneyStore store) {
        this.store = store;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerPageView view = new RecyclerPageView(parent.getContext(), store);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new RecyclerViewHolder(view, store);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        // get dimensions
        int width = this.view.getWidth();
        int height = this.view.getHeight();

        // update state
        store.data.updateDimensions(width, height);

        // update dimensions
        holder.update(width, height);

        // update position
        holder.updatePosition(position);

        // update the background
        holder.updateBackground(store.data.getBackground(position));

        // update reward events
        holder.updateRewards(store.data.getRewardEvents(position));

        // update the player
        holder.updatePlayer(store.data.getPlayer(position));

        // update optional overlapping rewards
        holder.updateOverlappingRewards(store.data.getOverlappingRewardEvents(position));

        // update optional overlapping player positions
        holder.updateOverlappingPlayer(store.data.getOverlappingPlayerPosition(position));

    }

    @Override
    public int getItemCount() {
        return store.data.getPageCount();
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.cancelAnimations();
    }

    @Override
    public void onAttachedToRecyclerView(android.support.v7.widget.RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.view = (RecyclerView) recyclerView;
    }


}
