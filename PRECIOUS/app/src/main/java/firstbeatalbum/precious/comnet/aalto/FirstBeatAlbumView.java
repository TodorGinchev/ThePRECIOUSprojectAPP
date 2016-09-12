package firstbeatalbum.precious.comnet.aalto;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by christopher on 12.09.16.
 */

public class FirstBeatAlbumView extends android.support.v7.widget.RecyclerView {

    public static final int numberOfColumns = 2;
    public static final int verticalPadding = 10;
    public static final int horizontalPadding = 10;

    FirstBeatAlbumActivity activity;
    FirstBeatAlbumAdapter adapter;
    Context context;

    public FirstBeatAlbumView(Context context) {
        super(context);
        this.context = context;
    }

    public FirstBeatAlbumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public FirstBeatAlbumView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void init(FirstBeatAlbumActivity activity, FirstBeatAlbumAdapter adapter) {

        // store the variables
        this.activity = activity;
        this.adapter = adapter;

        // set the layout manager
        this.setHasFixedSize(true);
        this.setLayoutManager(new GridLayoutManager(context, numberOfColumns));

        // set the adapter
        this.setAdapter(adapter);

        // set padding
        this.addItemDecoration(new FirstBeatAlbumDecoration(verticalPadding, horizontalPadding));
    }


}
