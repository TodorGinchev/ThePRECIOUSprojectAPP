package precious_rule_system.journeyview.data;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

import precious_rule_system.rewardsystem.entities.RewardEvent;

/**
 * Created by christopher on 06.09.16.
 * Class that handles animation of adding reward events
 * and moving the player along the path in the recycler view
 */
public class StateAnimation {

    // the number of points the animation has to animate to
    private int pointsToAnimate = 0;
    // the starting point for the current animation
    private int animationStart = 0;

    // the value animator
    private ValueAnimator animator;

    // whether we are currently animating
    private boolean isAnimating = false;

    // list of rewardevents that are processed within this animation
    private ArrayList<RewardEvent> events = new ArrayList<>();

    // state reference
    private State state;

    // speed with which the animations should happen
    private int pointsPerSecond = 100;

    // a delta we keep record of, and which is reset once we find enough
    // rewardevents to add to the state
    int currentRewardDelta = 0;

    public StateAnimation(State state) {
        this.state = state;
    }

    /**
     * Convience method to call notify the state once this animation is finished
     */
    private synchronized void animationFinished() {
        state.animationFinished();
    }

    /**
     * Adds an array of rewardevents to this instance
     * if the animation is already running, these events will NOT
     * be processed, this has to be taken care of the state
     * @param events
     */
    public synchronized void addRewardEvents(ArrayList<RewardEvent> events) {
        for(RewardEvent e : events) {
            this.events.add(e);
            this.pointsToAnimate += e.getPoints();
        }
    }

    /**
     * A single animation step, with deltaPoints being the change in
     * points since the last animation step
     * @param deltaPoints
     */
    private synchronized void animationUpdate(int deltaPoints) {

        // deduct those points from the total number of points
        this.pointsToAnimate -= deltaPoints;

        // immediately add them in the state
        state.addPlayerPoints(deltaPoints);

        // return if no more events are to process
        if (events.size() == 0) return;

        // update our reward delta
        currentRewardDelta += deltaPoints;

        // keep count of how many points were added in this animation iteration
        int added = 0;

        Iterator<RewardEvent> it = events.iterator();
        ArrayList<RewardEvent> toAdd = new ArrayList<>();


        while (it.hasNext()) {

            RewardEvent e = it.next();
            int points = e.getPoints();

            // should the reward already be added to state?
            // if yes, increase added count and remove the rewardevent from the events array
            if (points + added <= currentRewardDelta) {
                added += points;
                toAdd.add(e);
                it.remove();
            } else {
                break;
            }
        }

        // update the reward delta again
        currentRewardDelta = currentRewardDelta - added;

        // if we have events to add, do this now and update the state
        if (toAdd.size() > 0) {
            state.addRewardEvents(toAdd, true, true);
        }

        // during animations we always scroll to the players current position
        state.scrollToPlayer();
        // invalidate the state, and hence trigger an update of the recyclerview
        state.postInvalidate();
    }

    /**
     * Whether we are currently animating
     * @return
     */
    public synchronized boolean isAnimating() {
        return this.isAnimating;
    }

    /**
     * Starts the animation, with the parameters given at this point in time
     * important variables are pointsToAnimate, which will be the final point count to reach
     * within the animation, and of course the list of events
     */
    public synchronized void startAnimating() {

        // set the flag
        this.isAnimating = true;

        // create the animator
        animator = ValueAnimator.ofInt(animationStart, pointsToAnimate);

        // calculate the duration given our pointsPerSecond variable
        animator.setDuration(Math.round((float) (pointsToAnimate-animationStart) / (float) pointsPerSecond) * 1000);

        // we keep track of the deltas here by constantly updating the animationStart value
        // call the animationUpdate method on every iteration
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = ((int) (animation.getAnimatedValue()));
                animationUpdate(value-animationStart);
                animationStart = value;
            }
        });

        // reset values on animation ending
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

        // notify the state
        state.animationStarted();
        // start it
        animator.start();

    }

    /**
     * Convience method to stop an animation
     * Unused yet so untested
     */
    public synchronized void stopAnimating() {
        this.isAnimating = false;
        if (animator != null) {
            animator.cancel();
            animator = null;
            currentRewardDelta = 0;
        }
    }
}
