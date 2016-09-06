package precious_rule_system.journeyview.recycler;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import precious_rule_system.journeyview.JourneyActivity;
import precious_rule_system.journeyview.assets.Asset;
import precious_rule_system.journeyview.assets.Assets;

/**
 * Created by christopher on 04.09.16.
 */

public class RecyclerView extends android.support.v7.widget.RecyclerView {

    JourneyActivity.JourneyStore store;

    public RecyclerView(Context context, final JourneyActivity.JourneyStore store) {

        super(context);

        this.store = store;
        this.setHasFixedSize(true);
        this.setItemViewCacheSize(10);

        this.addOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(android.support.v7.widget.RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                store.data.getState().scrollChange((float) recyclerView.computeVerticalScrollOffset() / (float) recyclerView.getHeight());
            }

        });
    }

    public void scrollToCustom(final int position, final int offset) {

        final LinearLayoutManager layoutManager = (LinearLayoutManager) this.getLayoutManager();
        this.post(new Runnable() {
            @Override
            public void run() {
                // TODO Smooth scrolling here
                layoutManager.scrollToPositionWithOffset(position, offset);
            }
        });

    }



}
