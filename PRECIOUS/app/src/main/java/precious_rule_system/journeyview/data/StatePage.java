package precious_rule_system.journeyview.data;

import java.util.ArrayList;

import precious_rule_system.journeyview.constants.Constants;
import precious_rule_system.journeyview.helpers.AssetBackgroundGenerator;
import precious_rule_system.journeyview.helpers.Position;
import precious_rule_system.journeyview.helpers.SVGHelper;
import precious_rule_system.rewardsystem.entities.RewardEvent;
import rules.helpers.Tuple;
import ui.precious.comnet.aalto.precious.ui_MainActivity;

/**
 * Created by christopher on 04.09.16.
 * A class that keeps track of the items that are to be rendered on a single page
 * i.e. a single card in the recycler view
 */
public class StatePage {

    // width and height, used for checking dirty states
    int width = 0;
    int height = 0;

    // background, automatically generated whenever this class is instantiated
    AssetBackgroundGenerator.AssetBackground background;

    // the stored landscape for this page
    Constants.Landscape landscape;

    // list of all reward events
    public ArrayList<StatePageRewardEvent> events = new ArrayList<>();

    // number of points in this page
    int totalPoints = 0;

    // refernce to our store containing different class instances
    ui_MainActivity.JourneyStore store;

    public StatePage(ui_MainActivity.JourneyStore store, Constants.Landscape landscape) {
        this.landscape = landscape;
        this.store  = store;
        background  = null;
    }

    /**
     * Check whether the passed event can still fit into this page
     * @param event
     * @return
     */
    public boolean doesFitEvent(RewardEvent event) {
        if (event.getPoints() + totalPoints < Constants.pointsPerPage) return true;
        else return false;
    }

    /**
     * Returns the remaining points within this page
     * @return
     */
    public int getRemainingPoints() {
        return Constants.pointsPerPage - totalPoints;
    }

    /**
     * Adds a RewardEvent to this page
     * @param event - the rewardevent to be added
     * @param deductPoints - number of points to deduct before adding this event
     *                       this can happen if a previous view still has remaining points
     *                       but the added reward event exceeds those points
     * @param isNew - whether this reward event is new
     * @param fromAnimation - whether this reward event was added from animation
     */
    public void addRewardEvent(RewardEvent event, int deductPoints, boolean isNew, boolean fromAnimation) {
        int points = event.getPoints();
        totalPoints += points - deductPoints;
        float position = (float) totalPoints / Constants.pointsPerPage;
        StatePageRewardEvent newEvent = new StatePageRewardEvent(event, position);
        newEvent.isNew = isNew;
        newEvent.fromAnimation = fromAnimation;
        events.add(newEvent);
    }

    /**
     * Convience method to create a StatePageRewardEvent instance
     * @param event
     * @param position
     * @return
     */
    public StatePageRewardEvent getPageRewardEvent(RewardEvent event, float position) {
        return new StatePageRewardEvent(event, position);
    }

    /**
     * Checks whether the position of the reward event that was last added to
     * this page is over the vertical middle of the path
     * this is being used for checking whether a new page needs to be added to the state
     * @return
     */
    public boolean isLastPositionOverMiddle() {
        if (events.size() == 0) return false;
        float position = events.get(events.size()-1).position;
        if (position > 0.5) return true;
        else return false;
    }

    /**
     * Returns the last StatePageRewardEvent on this page
     * @return
     */
    public StatePageRewardEvent getLast() {
        if (events.size() == 0) return null;
        return events.get(events.size()-1);
    }

    /**
     * Returns the first StatePageRewardEvent on this page
     * @return
     */
    public StatePageRewardEvent getFirst() {
        if (events.size() == 0) return null;
        return events.get(0);
    }

    /**
     * Update the dimensions of this page, automatically triggers an update
     * of the assetbackground as it needs to be recalculated
     * @param width
     * @param height
     */
    public void updateDimensions(int width, int height) {
        this.width = width;
        this.height = height;
        this.background = AssetBackgroundGenerator.calculateRandomBackground(store, landscape, width, height);
    }

    // simple enum for direction indication when calculating overlapping positions
    public enum Direction {
        DOWN, UP
    }

    /**
     * Method calculating whether an object with given width and height will be overlapping this page along the specified path
     * at a specified position
     * @param path - the used path for this calculation
     * @param width - viewcontainer width
     * @param height - viewcontainer height
     * @param position - position 0..1 along the given path
     * @return a Tuple of the Direction in which the overlap occurs, and an absolute position of where the overlapping item
     * should be positioned in the respective view
     * returns NULL if no overlapping occurs
     */
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
