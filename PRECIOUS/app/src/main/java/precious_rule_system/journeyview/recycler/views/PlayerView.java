package precious_rule_system.journeyview.recycler.views;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import precious_rule_system.journeyview.JourneyActivity;
import precious_rule_system.journeyview.helpers.Position;
import precious_rule_system.journeyview.helpers.SVGHelper;
import precious_rule_system.journeyview.recycler.views.items.PlayerCircleItem;
import precious_rule_system.journeyview.recycler.views.items.PlayerIndicatorItem;

/**
 * Created by christopher on 04.09.16.
 */

public class PlayerView extends RelativeLayout {

    JourneyActivity.JourneyStore store;
    ArrayList<View> itemViews = new ArrayList<>();
    Context context;
    int height = 0;
    int width = 0;

    public PlayerView(JourneyActivity.JourneyStore store, Context context) {
        super(context);
        this.store = store;
        this.context = context;
    }

    public void setPlayer(Float position, int width, int height) {

        this.width = width;
        this.height = height;

        for(View v:itemViews) {
            this.removeView(v);
        }

        if(position == null) return;

        PlayerCircleItem v1 = new PlayerCircleItem(this.store, this.context);
        PlayerIndicatorItem v2 = new PlayerIndicatorItem(this.store, this.context);

        int[] sizes = store.sizes.getPlayerIndicatorSizes(width);

        int circleSize = sizes[0];
        int indicatorWidth = sizes[1];
        int indicatorHeight = sizes[2];

        RelativeLayout layout = new RelativeLayout(this.context);

        RelativeLayout.LayoutParams v1params = new RelativeLayout.LayoutParams(circleSize, circleSize);
        v1params.leftMargin = 0; v1params.topMargin = 0;
        layout.addView(v1, v1params);

        RelativeLayout.LayoutParams v2params = new RelativeLayout.LayoutParams(indicatorWidth, indicatorHeight);
        v2params.leftMargin = circleSize; v2params.topMargin = (circleSize-indicatorHeight)/2;
        layout.addView(v2, v2params);

        this.addViewAtPosition(layout, position, circleSize + indicatorWidth, circleSize, (circleSize + indicatorWidth)/2 - circleSize/2);

    }

    public void setOverlappingPlayer(Position position) {

        PlayerCircleItem v1 = new PlayerCircleItem(this.store, this.context);
        PlayerIndicatorItem v2 = new PlayerIndicatorItem(this.store, this.context);

        int[] sizes = store.sizes.getPlayerIndicatorSizes(width);

        int circleSize = sizes[0];
        int indicatorWidth = sizes[1];
        int indicatorHeight = sizes[2];

        RelativeLayout layout = new RelativeLayout(this.context);

        RelativeLayout.LayoutParams v1params = new RelativeLayout.LayoutParams(circleSize, circleSize);
        v1params.leftMargin = 0; v1params.topMargin = 0;
        layout.addView(v1, v1params);

        RelativeLayout.LayoutParams v2params = new RelativeLayout.LayoutParams(indicatorWidth, indicatorHeight);
        v2params.leftMargin = circleSize; v2params.topMargin = (circleSize-indicatorHeight)/2;
        layout.addView(v2, v2params);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(circleSize + indicatorWidth, circleSize);
        params.leftMargin = Math.round(position.left);
        layout.setY(Math.round(position.top));
        layout.setTranslationX((circleSize+indicatorWidth)/2-circleSize/2);

        this.addView(layout, params);
        this.itemViews.add(layout);
    }

    private void addViewAtPosition(View v, float position, int width, int height, float offset) {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        float pos[] = SVGHelper.getPositionAlongPath(store.assets.getPath().getLastPath(), position);

        params.leftMargin = Math.round(pos[0])-width/2;

        v.setY(Math.round(pos[1])-height/2);
        v.setTranslationX(offset);

        this.addView(v, params);

        this.itemViews.add(v);
    }

}
