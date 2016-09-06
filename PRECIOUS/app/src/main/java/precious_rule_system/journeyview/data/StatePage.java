package precious_rule_system.journeyview.data;

import android.webkit.DownloadListener;

import java.util.ArrayList;

import precious_rule_system.journeyview.JourneyActivity;
import precious_rule_system.journeyview.assets.Assets;
import precious_rule_system.journeyview.constants.Constants;
import precious_rule_system.journeyview.helpers.AssetBackgroundGenerator;
import precious_rule_system.journeyview.helpers.Position;
import precious_rule_system.journeyview.helpers.SVGHelper;
import precious_rule_system.rewardsystem.entities.RewardEvent;
import rules.helpers.Tuple;

/**
 * Created by christopher on 04.09.16.
 */

public class StatePage {

    // width and height, used for checking dirty states
    int width = 0;
    int height = 0;

    // simple wrapper class for storing reward events along the path
    public class StatePageRewardEvent {

        public RewardEvent event;
        public float position;
        public Position absolutePosition;
        public boolean isNew = false;
        public boolean fromAnimation = false;
        public boolean isAnimating = false;

        public StatePageRewardEvent(RewardEvent event, float position) {
            this.event = event;
            this.position = position;
        }

        public void reset() {
            this.isNew = false;
            this.fromAnimation = false;
        }
    }

    // background, automatically generated whenever this class is instantiated
    AssetBackgroundGenerator.AssetBackground background;

    // the stored landscape for this page
    Constants.Landscape landscape;

    // list of all reward events
    public ArrayList<StatePageRewardEvent> events = new ArrayList<>();

    // number of points in this page
    int totalPoints = 0;

    // refernce to our store containing different class instances
    JourneyActivity.JourneyStore store;

    public StatePage(JourneyActivity.JourneyStore store, Constants.Landscape landscape) {
        this.landscape = landscape;
        this.store  = store;
        background  = null;
    }

    public boolean doesFitEvent(RewardEvent event) {
        if (event.getPoints() + totalPoints < Constants.pointsPerPage) return true;
        else return false;
    }

    public int getRemainingPoints() {
        return Constants.pointsPerPage - totalPoints;
    }

    public void addRewardEvent(RewardEvent event, int deductPoints, boolean isNew, boolean fromAnimation) {
        int points = event.getPoints();
        totalPoints += points - deductPoints;
        float position = (float) totalPoints / Constants.pointsPerPage;
        StatePageRewardEvent newEvent = new StatePageRewardEvent(event, position);
        newEvent.isNew = isNew;
        newEvent.fromAnimation = fromAnimation;
        events.add(newEvent);
    }

    public StatePageRewardEvent getPageRewardEvent(RewardEvent event, float position) {
        return new StatePageRewardEvent(event, position);
    }

    public boolean isLastPositionOverMiddle() {
        if (events.size() == 0) return false;
        float position = events.get(events.size()-1).position;
        if (position > 0.5) return true;
        else return false;
    }

    public StatePageRewardEvent getLast() {
        if (events.size() == 0) return null;
        return events.get(events.size()-1);
    }

    public StatePageRewardEvent getFirst() {
        if (events.size() == 0) return null;
        return events.get(0);
    }

    public void updateDimensions(int width, int height) {
        this.width = width;
        this.height = height;
        this.background = AssetBackgroundGenerator.calculateRandomBackground(store, landscape, width, height);
    }

    public enum Direction {
        DOWN, UP
    }

    public Tuple<Direction, Position> getOverlappingPosition(android.graphics.Path path, int width, int height, float position) {

        float[] renderedPosition = SVGHelper.getPositionAlongPath(path, position);

        int verticalPosition = Math.round(renderedPosition[1]) - height/2;
        int horizontalPosition = Math.round(renderedPosition[0]) - width/2;

        // we
        if (verticalPosition < 0) {
            return new Tuple<>(Direction.UP, new Position((float) horizontalPosition, this.height + verticalPosition, false));
        } else if (verticalPosition > this.height - height) {
            return new Tuple<>(Direction.DOWN, new Position((float) horizontalPosition,verticalPosition - this.height,false));
        } else {
            return null;
        }
    }



}
