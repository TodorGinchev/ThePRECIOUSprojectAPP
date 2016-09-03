package precious_rule_system.journeyview_new.grid;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ListView;

import precious_rule_system.journeyview_new.data.DataUIManager;
import precious_rule_system.journeyview_new.utilities.Utilities;

/**
 * Created by christopher on 30.08.16.
 */

public class JourneyGrid extends GridView {

    JourneyGridDelegate delegate;

    public JourneyGrid(Context context, final JourneyGridDelegate delegate, int height) {

        super(context);

        // store the scrolling delegate
        this.delegate = delegate;

        this.setId(Utilities.generateViewId());
        this.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.FILL_PARENT,GridView.LayoutParams.FILL_PARENT));

        // set the background color, which stays the same for all landscapes
        this.setBackgroundColor(DataUIManager.gridBackgroundColor);
        // only one columns
        this.setNumColumns(1);
        // fit the column to the parent
        this.setColumnWidth(GridView.AUTO_FIT);
        this.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        // no spacing
        this.setVerticalSpacing(0);
        this.setHorizontalSpacing(0);
        // disable bars
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);

        // set a scroll listener so we know when the user scrolled/or when we used automated scrolling
        this.setOnScrollListener( new OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                delegate.onScroll(firstVisibleItem);
            }
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
        });

    }

    /**
     * Convenience function for setting a scroll position
     * @param item - the item id for which to set the top
     * @param offset - the offset in pixels from the top, positive means downward
     */
    public void customScroll(final int item, final int offset) {
        final GridView grid = this;
        this.post(new Runnable() {
            @Override
            public void run() {
                grid.smoothScrollToPositionFromTop(item,offset,0);
            }
        });
    }

}
