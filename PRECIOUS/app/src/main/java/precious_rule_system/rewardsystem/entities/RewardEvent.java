package precious_rule_system.rewardsystem.entities;

import android.os.Bundle;

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

    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putString("name", name);
        b.putString("reason", reason);

        b.putInt("points", points);
        b.putLong("date", date);

        b.putBoolean("isMilestone", isMilestone);
        b.putBoolean("isEvent", isEvent);
        b.putBoolean("isPointIncrease", isPointIncrease);

        return b;
    }

    public RewardEvent(Bundle b) {
        name = b.getString("name");
        reason = b.getString("reason");
        points = b.getInt("points");
        date = b.getLong("date");
        isMilestone = b.getBoolean("isMilestone");
        isEvent = b.getBoolean("isEvent");
        isPointIncrease = b.getBoolean("isPointIncrease");
    }

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

    public void setName(String name) {
        this.name = name;
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
