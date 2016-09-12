package precious_rule_system.journeyview.recycler;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import ui.precious.comnet.aalto.precious.ui_MainActivity;

/**
 * Created by christopher on 07.09.16.
 */

public class RecyclerLayoutManager extends LinearLayoutManager {

    private static final int DEFAULT_EXTRA_LAYOUT_SPACE = 600;
    private int extraLayoutSpace = -1;
    private Context context;
    ui_MainActivity.JourneyStore store;

    public RecyclerLayoutManager(Context context, ui_MainActivity.JourneyStore store) {
        super(context);
        this.context = context;
        this.store = store;
        this.setOrientation(LinearLayoutManager.VERTICAL);

        // reverse the layout, so most up to date are last in the state's arrays
        this.setReverseLayout(true);
        this.setStackFromEnd(true);
    }

    public void setExtraLayoutSpace(int extraLayoutSpace) {
        this.extraLayoutSpace = extraLayoutSpace;
    }

    // this will precache offset views as well
    @Override
    protected int getExtraLayoutSpace(RecyclerView.State state) {
        if (extraLayoutSpace > 0) {
            return extraLayoutSpace;
        }
        return DEFAULT_EXTRA_LAYOUT_SPACE;
    }

    @Override
    public int scrollVerticallyBy(int dy, android.support.v7.widget.RecyclerView.Recycler recycler, android.support.v7.widget.RecyclerView.State state) {

        // always allow downward scrolling
        if (dy > 0) {
            return super.scrollVerticallyBy(dy, recycler, state);
        }

        RecyclerView v = this.store.getRecyclerView();
        float verticalOffset = v.computeVerticalScrollOffset();
        float height = v.getHeight();

        float remainingOffset = store.data.getState().getRemainingScrollOffsetToPlayer(verticalOffset, height, dy);

        if (remainingOffset <= 0) {
            return super.scrollVerticallyBy(Math.round(remainingOffset), recycler, state);
        } else {
            return 0;
        }
    }


}
