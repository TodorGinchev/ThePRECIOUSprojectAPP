package precious_rule_system.journeyview.recycler.views;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import precious_rule_system.journeyview.JourneyActivity;
import precious_rule_system.journeyview.data.StatePage;
import precious_rule_system.journeyview.helpers.SVGHelper;
import precious_rule_system.journeyview.recycler.views.items.RewardItemView;
import precious_rule_system.journeyview.helpers.Utilities;
import precious_rule_system.rewardsystem.entities.RewardEvent;

/**
 * Created by christopher on 04.09.16.
 */

public class RewardsView extends RelativeLayout {

    JourneyActivity.JourneyStore store;
    ArrayList<View> itemViews = new ArrayList<>();
    Context context;
    int height = 0;
    int width = 0;
    ArrayList<Animation> animations = new ArrayList<>();
    int id = Utilities.generateViewId();

    public RewardsView(JourneyActivity.JourneyStore store, Context context) {
        super(context);
        this.store = store;
        this.context = context;
        this.setId((int)id);
    }

    public void setRewards(ArrayList<StatePage.StatePageRewardEvent> rewards, int width, int height) {

        this.width = width;
        this.height = height;

        for(View v:itemViews) {
            this.removeView(v);
        }

        for(StatePage.StatePageRewardEvent reward : rewards) {
            this.addRewardEvent(reward);
        }
    }

    private void addRewardEvent(final StatePage.StatePageRewardEvent reward) {

        int size = Math.round(store.sizes.getRewardSize(this.width, this.height, reward.event));

        RewardItemView v = new RewardItemView(this.context, reward.event);
        v.setId(Utilities.generateViewId());
        v.setOnClickListener(handler);

        float[] pivot = this.addViewAtPosition(v, reward.position, size, size, 0.0f);

        v.pivot = pivot;

        if (reward.isNew && !reward.isAnimating) {

            reward.isAnimating = true;

            Animation expandOut = new ScaleAnimation(0,1.2f,0,1.2f,size/2,pivot[1]);
            expandOut.setDuration(300);

            Animation expandIn = new ScaleAnimation(1.2f,1.0f,1.2f,1.0f,size/2,pivot[1]);
            expandIn.setDuration(200);
            expandIn.setStartOffset(300);

            AnimationSet set = new AnimationSet(false);//false means don't share interpolators
            set.addAnimation(expandOut);
            set.addAnimation(expandIn);

            animations.add(set);

            v.startAnimation(set);

            expandIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    reward.reset();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }
    }

    public void addOverlappingRewardEvent(StatePage.StatePageRewardEvent reward) {

        int size = Math.round(store.sizes.getRewardSize(this.width, this.height, reward.event));

        RewardItemView v = new RewardItemView(this.context, reward.event);
        v.setId(Utilities.generateViewId());
        v.setOnClickListener(handler);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
        params.leftMargin = Math.round(reward.absolutePosition.left);

        v.setY(Math.round(reward.absolutePosition.top));

        float[] pivot = {Math.round(reward.absolutePosition.left), Math.round(reward.absolutePosition.top)};
        v.pivot = pivot;

        this.addView(v, params);

        this.itemViews.add(v);
    }

    View.OnClickListener handler = new View.OnClickListener() {
        public void onClick(View v) {

            RewardEvent e = ((RewardItemView) v).event;
            float[] pivot = ((RewardItemView) v).pivot;
            int size = v.getWidth();

            Animation expandOut = new ScaleAnimation(1.0f,1.2f,1.0f,1.2f, size/2,pivot[1]);
            expandOut.setDuration(100);

            Animation expandIn = new ScaleAnimation(1.2f,1.0f,1.2f,1.0f, size/2,pivot[1]);
            expandIn.setDuration(100);
            expandIn.setStartOffset(100);

            final AnimationSet set = new AnimationSet(false);//false means don't share interpolators
            set.addAnimation(expandOut);
            set.addAnimation(expandIn);

            animations.add(set);

            v.startAnimation(set);

            store.journeyView.rewardEventClicked(e);
        }
    };

    private float[] addViewAtPosition(View v, float position, int width, int height, float offset) {

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);

        float pos[] = SVGHelper.getPositionAlongPath(store.assets.getPath().getLastPath(), position);

        params.leftMargin = Math.round(pos[0])-width/2;

        v.setY(Math.round(pos[1])-height/2);
        v.setTranslationX(offset);

        this.addView(v, params);
        this.itemViews.add(v);

        float[] pivot = { Math.round(pos[0]) , Math.round(pos[1]) + offset };
        return pivot;
    }

    public void cancelAnimations() {
        for(Animation a : animations) {
            a.cancel();
        }
        animations.clear();
    }



}
