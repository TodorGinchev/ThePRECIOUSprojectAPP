package precious_rule_system.rewardsystem;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import precious_rule_system.rewardsystem.entities.RewardEvent;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;

/**
 * Created by christopher on 14.08.16.
 */

public class RewardSystem {

    private final String MESSAGE = "RewardSystem.rewardUpdate";
    private final String TAG = "RewardSystem";

    public void postPointIncrease(int points) {
        this.postRewardEvent(RewardEvent.getPointIncrease(points, new Date().getTime()));
    }

    public void postEvent(String name, int points) {
        this.postRewardEvent(RewardEvent.getEvent(name, points, new Date().getTime()));
    }

    public void postMilestone(String name, String reason, int points) {
        this.postRewardEvent(RewardEvent.getMilestone(name, reason, points, new Date().getTime()));
    }

    private void postRewardEvent(RewardEvent e) {

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RewardEvent event = realm.copyToRealm(e);
        realm.commitTransaction();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "New reward system update detected, posting notification");
                Intent intent = new Intent(MESSAGE);
                LocalBroadcastManager.getInstance(PRECIOUS_APP.getInstance()).sendBroadcast(intent);
            }
        });
    }

    public int getTotalPoints() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RewardEvent> results = realm.where(RewardEvent.class).findAll();
        return results.sum("points").intValue();
    }

    public int getTotalPoints(long from, long to) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RewardEvent> results = realm.where(RewardEvent.class).between("date", from, to).findAll();
        return results.sum("points").intValue();
    }

    public int getTotalPoints(Date from, Date to) {
        return getTotalPoints(from.getTime(), to.getTime());
    }

    public ArrayList<RewardEvent> getAllEventsSortedByDate() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RewardEvent> results = realm.where(RewardEvent.class).findAllSorted("date");
        ArrayList<RewardEvent> arr = new ArrayList<RewardEvent>();
        for (RewardEvent e : results) {
            arr.add(e);
        }
        return arr;
    }

    public ArrayList<RewardEvent> getAllEventsSortedByDate(long from, long to) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RewardEvent> results = realm.where(RewardEvent.class).between("date", from, to).findAllSorted("date");
        ArrayList<RewardEvent> arr = new ArrayList<RewardEvent>();
        for (RewardEvent e : results) {
            arr.add(e);
        }
        return arr;
    }

    public ArrayList<RewardEvent> getAllEventsSortedByDate(Date from, Date to) {
        return getAllEventsSortedByDate(from.getTime(), to.getTime());
    }
}
