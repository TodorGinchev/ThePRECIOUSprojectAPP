package precious_rule_system.journeyview;

import java.util.Date;

/**
 * Created by khatt on 8/10/2016.
 */
enum MilestoneType {TYPE1, TYPE2, TYPE3, TYPE4}

public class Milestone {
    private MilestoneType type;
    private int atStepCount;
    private Date atDate;

    Milestone(MilestoneType milestoneType, int associatedSteps,Date associatedDate) {
        this.type = milestoneType;
        this.atStepCount = associatedSteps;
        this.atDate = associatedDate;
    }

    public Date getAtDate() {
        return atDate;
    }

    public int getAtStepCount() {
        return atStepCount;
    }

    public MilestoneType getType() {
        return type;
    }
}

