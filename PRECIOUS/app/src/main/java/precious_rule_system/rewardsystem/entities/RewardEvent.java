package precious_rule_system.rewardsystem.entities;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by christopher on 14.08.16.
 */

public class RewardEvent extends RealmObject {

    private String name;
    private String reason;

    private int points;
    private long date;
    private boolean isMilestone;
    private boolean isEvent;
    private boolean isPointIncrease;

    public RewardEvent() {}

    public static RewardEvent getPointIncrease(int points, long date) {
        RewardEvent e = new RewardEvent();
        e.points = points;
        e.date = date;
        e.isPointIncrease = true;
        e.isEvent = false;
        e.isMilestone = false;
        return e;
    }

    public static RewardEvent getEvent(String name, int points, long date) {
        RewardEvent e = new RewardEvent();
        e.name = name;
        e.points = points;
        e.date = date;
        e.isPointIncrease = false;
        e.isEvent = true;
        e.isMilestone = false;
        return e;
    }

    public static RewardEvent getMilestone(String name, String reason, int points, long date) {
        RewardEvent e = new RewardEvent();
        e.name = name;
        e.reason = reason;
        e.points = points;
        e.date = date;
        e.isPointIncrease = false;
        e.isEvent = false;
        e.isMilestone = true;
        return e;
    }


    public boolean isPointIncrease() {
        return isPointIncrease;
    }

    public boolean isEvent() {
        return isEvent;
    }

    public boolean isMilestone() {
        return isMilestone;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public Date getDate() {
        return new Date(date);
    }
}
