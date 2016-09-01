package precious_rule_system.journeyview_new.grid;

import android.content.Context;
import android.graphics.Color;
import android.widget.AbsListView;
import android.widget.GridView;

import precious_rule_system.journeyview_new.utilities.Utilities;

/**
 * Created by christopher on 30.08.16.
 */

public class JourneyGrid extends GridView {

    JourneyGridDelegate delegate;

    public JourneyGrid(Context context, final JourneyGridDelegate delegate) {

        super(context);
        this.delegate = delegate;
        this.setId(Utilities.generateViewId());
        this.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.FILL_PARENT,GridView.LayoutParams.FILL_PARENT));
        this.setBackgroundColor(Color.parseColor("#eeeae9"));
        this.setNumColumns(1);
        this.setColumnWidth(GridView.AUTO_FIT);
        this.setVerticalSpacing(0);
        this.setHorizontalSpacing(0);
        this.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);

        this.setOnScrollListener( new OnScrollListener() {

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                delegate.onScroll(firstVisibleItem);
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }


        });

    }
}
