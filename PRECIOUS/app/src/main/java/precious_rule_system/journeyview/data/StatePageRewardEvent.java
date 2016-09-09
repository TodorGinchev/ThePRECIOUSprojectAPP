package precious_rule_system.journeyview.data;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import precious_rule_system.journeyview.helpers.Position;
import precious_rule_system.journeyview.recycler.views.RewardsView;
import precious_rule_system.rewardsystem.entities.RewardEvent;
import rules.helpers.Tuple;

/**
 * Created by christopher on 08.09.16.
 */

public class StatePageRewardEvent {

    // the stored event
    public RewardEvent event;

    // this represents a position along the path
    public float position;

    // this represents an absolute position
    public Position absolutePosition;

    // whether this event is new
    public boolean isNew = false;
    // whether this event was adding during an animation
    public boolean fromAnimation = false;
    // whether this event is currently animating
    public boolean isAnimating = false;

    // click animations
    HashMap<Integer, HashMap<Integer, RewardsView>> clickListeners = new HashMap<>();

    public StatePageRewardEvent(RewardEvent event, float position) {
        this.event = event;
        this.position = position;
    }

    /**
     * Convenience method to set the boolean flags
     */
    public void reset() {
        this.isNew = false;
        this.fromAnimation = false;
    }

    /**
     * Sets an absolute position for this reward event
     * @param p
     */
    public void setOverlapPosition(Position p) {
        this.absolutePosition = p;
    }


    public void addClickListener(int position, int viewId, RewardsView v) {
        HashMap<Integer, RewardsView> mapForPosition = clickListeners.get(position);
        if (mapForPosition == null) {
            mapForPosition = new HashMap<>();
            clickListeners.put(position, mapForPosition);
        }
        mapForPosition.put(viewId, v);
    }

    public void removeClickListeners(int position) {
        clickListeners.remove(position);
    }

    public void triggerClick(int position) {
        Iterator it = clickListeners.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if ((int) pair.getKey() == position) continue;
            HashMap<Integer, RewardsView> m = (HashMap<Integer, RewardsView>) pair.getValue();
            Iterator itt = m.entrySet().iterator();
            while(itt.hasNext()) {
                Map.Entry pair2 = (Map.Entry) itt.next();
                RewardsView v = (RewardsView) pair2.getValue();
                int viewId = (int) pair2.getKey();
                v.clickTriggered(viewId);
            }

        }
    }

}
