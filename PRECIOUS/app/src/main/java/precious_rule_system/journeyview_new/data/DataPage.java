package precious_rule_system.journeyview_new.data;

import java.util.ArrayList;

import precious_rule_system.journeyview_new.assets.JourneyAssets;
import precious_rule_system.journeyview_new.utilities.Layout;
import precious_rule_system.rewardsystem.entities.RewardEvent;
import rules.helpers.Tuple;

/**
 * Created by christopher on 02.09.16.
 */

public class DataPage {

    ArrayList<Tuple<JourneyAssets.Size, double[]>> assets;
    ArrayList<Tuple<RewardEvent, Float>> events = new ArrayList<>();
    ArrayList<Boolean> eventsVisible = new ArrayList<>();
    JourneyAssets.Landscape landscape;
    int totalPoints = 0;
    DataUIManager data;
    Float playerPosition;

    public DataPage(JourneyAssets.Landscape landscape, DataUIManager data) {
        this.landscape = landscape;
        this.data = data;
        this.assets = data.generateAssets();
    }

    public boolean doesFitEvent(RewardEvent event) {
        if (event.getPoints() + totalPoints < (int) DataUIManager.pointsPerPage) return true;
        else return false;
    }

    public int getRemainingPoints() {
        return (int) DataUIManager.pointsPerPage - totalPoints;
    }

    public void addRewardEvent(RewardEvent event, int deductPoints, boolean visible) {
        int points = event.getPoints();
        totalPoints += points - deductPoints;
        float position = (float) totalPoints / DataUIManager.pointsPerPage;
        events.add(new Tuple<RewardEvent, Float>(event, position));
        eventsVisible.add(visible);
    }

    public void addRewardEventAtPosition(RewardEvent event, float position, boolean visible) {
        events.add(new Tuple<RewardEvent, Float>(event, position));
        eventsVisible.add(visible);
    }

    public void addPlayerAtPosition(float position) {
        this.playerPosition = position;
    }

    public void addPlayer() {
        float position = getLastPosition();
        this.addPlayerAtPosition(position);
    }

    public boolean isLastPositionOverMiddle() {
        if (events.size() == 0) return false;
        float position = events.get(events.size()-1).y;
        if (position > 0.5) return true;
        else return false;
    }

    public Float getLastPosition() {
        if (events.size() == 0) return null;
        float position = events.get(events.size()-1).y;
        return position;
    }

    public Tuple<Integer, Float> getCriticalPlayerPosition (int width, int height) {

        Float position = this.playerPosition;
        if (position == null) return null;

        int renderedSize = Math.round(DataUIManager.getPlayerHeight(width, height));
        int verticalPosition = Math.round(data.convertPosition(position)[1]) - renderedSize/2;

        // we
        if (verticalPosition < 0) {

            // position is too small
            float otherPagePosition = 1.0f - position;
            return new Tuple<>(-1, otherPagePosition);

        } else if (verticalPosition > height - renderedSize) {

            // position is too high
            float otherPagePosition = 1.0f - position;
            return new Tuple<>(+1, otherPagePosition);

        } else {
            return null;
        }

    }

    public Tuple<Integer, Float> getCriticalPosition (int width, int height) {

        if (events.size() == 0) return null;
        Tuple<RewardEvent, Float> last = events.get(events.size()-1);
        RewardEvent e = last.x;
        float position = last.y;

        int renderedSize = Math.round(DataUIManager.getRewardSize(e, width, height));
        int verticalPosition = Math.round(data.convertPosition(position)[1]) - renderedSize/2;

        // we
        if (verticalPosition < 0) {

            // position is too small
            float otherPagePosition = 1.0f - position;
            return new Tuple<>(-1, otherPagePosition);

        } else if (verticalPosition > height - renderedSize) {

            // position is too high
            float otherPagePosition = 1.0f - position;
            return new Tuple<>(+1, otherPagePosition);

        } else {
            return null;
        }
    }
}
