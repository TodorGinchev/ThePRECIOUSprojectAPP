package firstbeatalbum.precious.comnet.aalto;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by christopher on 12.09.16.
 */

public class FirstBeatAlbumDecoration extends RecyclerView.ItemDecoration {

    private final int mVerticalSpaceHeight;
    private final int mHorizontalSpaceHeight;

    public FirstBeatAlbumDecoration(int mVerticalSpaceHeight, int mHorizontalSpaceHeight) {
        this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        this.mHorizontalSpaceHeight = mHorizontalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.set(mHorizontalSpaceHeight, mVerticalSpaceHeight, mHorizontalSpaceHeight, mVerticalSpaceHeight);
    }

}
