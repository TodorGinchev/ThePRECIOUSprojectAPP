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
import precious_rule_system.journeyview.data.StatePageRewardEvent;
import precious_rule_system.journeyview.helpers.SVGHelper;
import precious_rule_system.journeyview.helpers.Utilities;
import precious_rule_system.journeyview.recycler.views.items.RewardItemWrapper;

/**
 * Created by christopher on 04.09.16.
 */

public class RewardsView extends RelativeLayout {

    JourneyActivity.JourneyStore store;
    ArrayList<View> itemViews = new ArrayList<>();
    Context context;
    int height = 0;
    int width = 0;
    ArrayList<StatePageRewardEvent> rewards = new ArrayList<>();
    int id = Utilities.generateViewId();
    int position;

    public RewardsView(JourneyActivity.JourneyStore store, Context context) {
        super(context);
        this.store = store;
        this.context = context;
        this.setId((int)id);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setRewards(ArrayList<StatePageRewardEvent> rewards, int width, int height) {

        this.width = width;
        this.height = height;
        this.rewards = rewards;

        for(View v:itemViews) {
            this.removeView(v);
        }

        for(StatePageRewardEvent reward : rewards) {
            this.addRewardEvent(reward);
        }
    }

    private void addRewardEvent(final StatePageRewardEvent reward) {

        int size = Math.round(store.sizes.getRewardSize(this.width, this.height, reward.event));
        int innerSize = Math.round(store.sizes.getInnerRewardSize(this.width, this.height, reward.event));

        RewardItemWrapper v = new RewardItemWrapper(this.context, reward, innerSize);
        v.setId(Utilities.generateViewId());
        v.setOnClickListener(handler);
        reward.removeClickListeners(this.position);
        reward.addClickListener(this.position, v.getId(), this);

        float[] pivot = this.addViewAtPosition(v, reward.position, size, size, 0.0f);
        v.pivot = pivot;

        if (reward.isNew && !reward.isAnimating) {

            // TODO: this doesn't work ?? on overlapping positions?
            reward.isAnimating = true;

            Animation expandOut = new ScaleAnimation(0,1.2f,0,1.2f,size/2,pivot[1]);
            expandOut.setDuration(300);

            Animation expandIn = new ScaleAnimation(1.2f,1.0f,1.2f,1.0f,size/2,pivot[1]);
            expandIn.setDuration(200);
            expandIn.setStartOffset(300);

            AnimationSet set = new AnimationSet(false);//false means don't share interpolators
            set.addAnimation(expandOut);
            set.addAnimation(expandIn);

            //animations.add(set);

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

    public void addOverlappingRewardEvent(StatePageRewardEvent reward) {

        int size = Math.round(store.sizes.getRewardSize(this.width, this.height, reward.event));
        int innerSize = Math.round(store.sizes.getInnerRewardSize(this.width, this.height, reward.event));

        RewardItemWrapper v = new RewardItemWrapper(this.context, reward, innerSize);
        v.setId(Utilities.generateViewId());
        v.setOnClickListener(handler);
        reward.removeClickListeners(this.position);
        reward.addClickListener(this.position, v.getId(), this);

        float[] pivot = this.addViewAtAbsolutePosition(v,Math.round(reward.absolutePosition.left), Math.round(reward.absolutePosition.top), size, size, 0);
        v.pivot = pivot;

    }

    private Animation createClickAnimation(float pivotX, float pivotY) {
        Animation expandOut = new ScaleAnimation(1.0f,1.2f,1.0f,1.2f, pivotX,pivotY);
        expandOut.setDuration(100);
        Animation expandIn = new ScaleAnimation(1.2f,1.0f,1.2f,1.0f, pivotX, pivotY);
        expandIn.setDuration(100);
        expandIn.setStartOffset(100);
        final AnimationSet set = new AnimationSet(false);
        set.addAnimation(expandOut);
        set.addAnimation(expandIn);
        return set;
    }

    public void clickTriggered(int viewId) {
        for(View v : itemViews) {
            if (v.getId() == viewId) {
                StatePageRewardEvent e = ((RewardItemWrapper) v).event;
                float[] pivot = ((RewardItemWrapper) v).pivot;
                int size = v.getWidth();
                v.startAnimation(createClickAnimation(size/2, pivot[1]));
                break;
            }
        }
    }

    View.OnClickListener handler = new View.OnClickListener() {

        public void onClick(View v) {

            StatePageRewardEvent e = ((RewardItemWrapper) v).event;
            float[] pivot = ((RewardItemWrapper) v).pivot;
            int size = v.getWidth();

            v.startAnimation(createClickAnimation(size/2,  pivot[1]));
            e.triggerClick(position);

            store.journeyView.rewardEventClicked(e.event);
        }
    };

    private float[] addViewAtPosition(View v, float position, int width, int height, float offset) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        float pos[] = SVGHelper.getPositionAlongPath(store.assets.getPath().getLastPath(), position);
        return this.addViewAtAbsolutePosition(v, Math.round(pos[0])-width/2, Math.round(pos[1])-height/2, width, height, offset);
    }

    private float[] addViewAtAbsolutePosition(View v, int left, int top, int width, int height, float offset) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.leftMargin = left;
        v.setY(top);
        v.setTranslationX(offset);

        this.addView(v, params);
        this.itemViews.add(v);

        float[] pivot = {left+width/2, top+height/2};
        return pivot;
    }

    public void cancelAnimations() {

        for(StatePageRewardEvent r : rewards) {
            r.removeClickListeners(this.position);
        }

        /*for(Animation a : animations) {
            a.cancel();
        }
        animations.clear();*/
    }



}
