package precious_rule_system.journeyview_new.page;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import precious_rule_system.journeyview_new.assets.JourneyAssets;
import precious_rule_system.journeyview_new.page.items.PlayerCircleView;
import precious_rule_system.journeyview_new.page.items.PlayerIndicatorView;
import precious_rule_system.journeyview_new.page.items.RewardEventView;
import precious_rule_system.journeyview_new.utilities.SVGExtended;
import precious_rule_system.journeyview_new.utilities.Utilities;
import precious_rule_system.rewardsystem.entities.RewardEvent;
import rules.helpers.Tuple;

/**
 * Created by christopher on 31.08.16.
 */

public class JourneyPageViewWrapper extends RelativeLayout {

    Context context;
    JourneyAssets assets;
    int width;
    int height;

    JourneyPageView assetBackground;
    JourneyPageViewWrapperDelegate delegate;

    ArrayList<View> itemViews = new ArrayList<>();

    public JourneyPageViewWrapper(Context context, JourneyAssets assets, JourneyPageViewWrapperDelegate delegate) {
        super(context);
        this.context = context;
        this.assets = assets;
        this.delegate = delegate;

        // create the background view
        this.createAssetBackground();
    }

    // creates the background view
    private void createAssetBackground() {
        assetBackground = new JourneyPageView(context, this.assets);
        assetBackground.setId(Utilities.generateViewId());
        this.addView(assetBackground, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    // stores width and height
    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    // resets all events & assets
    public void reset() {
        assetBackground.addAssets(new ArrayList<Tuple<JourneyAssets.Size, double[]>>());
        for(View v:itemViews) {
            this.removeView(v);
        }
    }

    // adds background assets
    public void addAssets(ArrayList<Tuple<JourneyAssets.Size, double[]>> assetList) {
        assetBackground.addAssets(assetList);
    }

    // adds a reward event
    public void addRewardEvent(RewardEvent e, float position) {
        RewardEventView v = new RewardEventView(context, e);
        v.setId(Utilities.generateViewId());
        v.setOnClickListener(handler);

        int width = 0;
        int height = 0;
        float offset = 0.0f;

        if (e.isEvent()) {
            width = (int) Math.round(0.05f*this.width);
            height = width;
            offset = 0.0f;
        } else if (e.isMilestone()) {
            width = (int) Math.round(0.08f*this.width);
            height = width;
            offset = 0.0f;
        }

        this.addViewAtPosition(v, position, width, height, offset);
    }

    // adds the player to this view
    public void addPlayer(float position) {

        PlayerCircleView v1 = new PlayerCircleView(this.context);
        PlayerIndicatorView v2 = new PlayerIndicatorView(this.context);

        int circleSize = (int) Math.round(0.086f*this.width);
        int indicatorWidth = 2*circleSize;
        int indicatorHeight = (int) Math.round((float) circleSize / 1.5f);

        RelativeLayout layout = new RelativeLayout(this.context);

        RelativeLayout.LayoutParams v1params = new RelativeLayout.LayoutParams(circleSize, circleSize);
        v1params.leftMargin = 0; v1params.topMargin = 0;
        layout.addView(v1, v1params);

        RelativeLayout.LayoutParams v2params = new RelativeLayout.LayoutParams(indicatorWidth, indicatorHeight);
        v2params.leftMargin = circleSize; v2params.topMargin = (circleSize-indicatorHeight)/2;
        layout.addView(v2, v2params);

        this.addViewAtPosition(layout, position, circleSize + indicatorWidth, circleSize, -circleSize/2);

    }

    /*// adds the player to this view, and moves the player from position from to to
    public void addAnimatedPlayer(float fromPosition, float toPosition) {

    }

    // animates an existing player
    public void animatePlayer(float position) {

    }*/

    View.OnClickListener handler = new View.OnClickListener() {
        public void onClick(View v) {
            delegate.rewardEventClicked(((RewardEventView) v).event);
        }
    };


    private void addViewAtPosition(View v, float position, int width, int height, float offset) {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        float pos[] = convertPosition(position);

        params.leftMargin = (int) Math.round(pos[0]);
        params.topMargin = (int) Math.round(pos[1]);

        v.setTranslationX(offset);

        this.addView(v, params);
        this.itemViews.add(v);
    }

    private float[] convertPosition(float position) {
        float min = 0.04f;
        float max = 0.485f;
        float newPos = position*(max-min)+min;

        Tuple<float[], Float> res = SVGExtended.getPositionAlongPath(assets.path.path,newPos);
        float l = res.y;
        float[] pos = res.x;
        return pos;
    }

    private void positionTest() {
        for(float p=0.04f; p<=0.485; p+=0.001) {
            Tuple<float[], Float> res = SVGExtended.getPositionAlongPath(assets.path.path,p);
            Log.i("test",p + "," + res.x[0] + "," + res.x[1]);
        }
    }


}
