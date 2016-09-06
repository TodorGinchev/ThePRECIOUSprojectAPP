package precious_rule_system.journeyview.data;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

import precious_rule_system.rewardsystem.entities.RewardEvent;

/**
 * Created by christopher on 06.09.16.
 */

public class StateAnimation {

    private int pointsToAnimate = 0;
    private int animationStart = 0;

    private ValueAnimator animator;
    private boolean isAnimating = false;
    private ArrayList<RewardEvent> events = new ArrayList<>();
    private State state;
    private int pointsPerSecond = 100;
    int currentRewardDelta = 0;

    public StateAnimation(State state) {
        this.state = state;
    }

    private synchronized void animationFinished() {
        state.animationFinished();
    }

    public synchronized void addRewardEvents(ArrayList<RewardEvent> events) {

        for(RewardEvent e : events) {
            this.events.add(e);
            this.pointsToAnimate += e.getPoints();
        }

    }

    private synchronized void animationUpdate(int deltaPoints) {

        this.pointsToAnimate -= deltaPoints;

        state.addPlayerPoints(deltaPoints);
        if (events.size() == 0) return;

        currentRewardDelta += deltaPoints;

        int added = 0;

        Iterator<RewardEvent> it = events.iterator();
        ArrayList<RewardEvent> toAdd = new ArrayList<>();

        while (it.hasNext()) {

            RewardEvent e = it.next();
            int points = e.getPoints();

            if (points + added <= currentRewardDelta) {
                added += points;
                toAdd.add(e);
                it.remove();
            } else {
                break;
            }
        }

        currentRewardDelta = currentRewardDelta - added;

        if (toAdd.size() > 0) {
            //Log.i("JourneyState", "Adding " + toAdd.size() + " to the view via animation");
            state.addRewardEvents(toAdd, true, true);
        }

        state.scrollToPlayer();
        state.postInvalidate();
    }

    public synchronized boolean isAnimating() {
        return this.isAnimating;
    }

    public synchronized void startAnimating() {

        this.isAnimating = true;

        animator = ValueAnimator.ofInt(animationStart, pointsToAnimate);
        animator.setDuration(Math.round((float) (pointsToAnimate-animationStart) / (float) pointsPerSecond) * 1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = ((int) (animation.getAnimatedValue()));
                animationUpdate(value-animationStart);
                animationStart = value;
            }
        });

        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {

            }
            @Override
            public void onAnimationRepeat(Animator animator) {

            }
            @Override
            public void onAnimationEnd(Animator animator) {
                isAnimating = false;
                currentRewardDelta = 0;
                animationStart = 0;
                animationFinished();
            }
            @Override
            public void onAnimationCancel(Animator animator) {

            }
        });

        animator.start();

    }

    public synchronized void stopAnimating() {
        this.isAnimating = false;
        if (animator != null) {
            animator.cancel();
            animator = null;
            currentRewardDelta = 0;
        }
    }
}
