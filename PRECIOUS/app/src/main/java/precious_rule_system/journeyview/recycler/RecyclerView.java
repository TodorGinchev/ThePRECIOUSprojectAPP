package precious_rule_system.journeyview.recycler;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;

import ui.precious.comnet.aalto.precious.ui_MainActivity;

/**
 * Created by christopher on 04.09.16.
 */

public class RecyclerView extends android.support.v7.widget.RecyclerView {

    ui_MainActivity.JourneyStore store;

    // simple class for handling touch input, i.e. to turn off and on input to the recyclerview
    public class RecyclerViewTouch implements android.support.v7.widget.RecyclerView.OnItemTouchListener {

        @Override
        public boolean onInterceptTouchEvent(android.support.v7.widget.RecyclerView rv, MotionEvent e) {
            return true;
        }
        @Override
        public void onTouchEvent(android.support.v7.widget.RecyclerView rv, MotionEvent e) {}
        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
    }

    RecyclerView.OnItemTouchListener disable = new RecyclerViewTouch();

    public RecyclerView(Context context, final ui_MainActivity.JourneyStore store) {

        super(context);

        this.store = store;
        // we have fixed size, this increases performance
        this.setHasFixedSize(true);
        // keep at least 10 items in memory
        this.setItemViewCacheSize(10);

        this.addOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(android.support.v7.widget.RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(android.support.v7.widget.RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // call the state and notify a scroll change
                float verticalOffset = (float) recyclerView.computeVerticalScrollOffset();
                store.data.getState().scrollChange(verticalOffset, (float) recyclerView.getHeight(), dy);
            }

        });
    }


    public void scrollToCustom(final int position, final int offset) {

        final LinearLayoutManager layoutManager = (LinearLayoutManager) this.getLayoutManager();
        this.post(new Runnable() {
            @Override
            public void run() {
                // TODO Smooth scrolling here to be indepdent of the number of points per view
                layoutManager.scrollToPositionWithOffset(position, offset);
            }
        });

    }

    public void enableTouch() {
        this.removeOnItemTouchListener(disable);
    }

    public void disableTouch() {
        this.addOnItemTouchListener(disable);
    }



}
