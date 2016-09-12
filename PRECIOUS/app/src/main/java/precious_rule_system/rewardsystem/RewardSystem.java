package precious_rule_system.rewardsystem;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import io.realm.RealmConfiguration;
import precious_rule_system.rewardsystem.entities.RewardEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;

/**
 * Created by christopher on 14.08.16.
 */

public class RewardSystem {

    public final String MESSAGE = "RewardSystem.rewardUpdate";
    private final String TAG = "RewardSystem";

    public long dummyFrom = new Date().getTime()-24*60*60*1000;
    public long dummyTo = new Date().getTime();
    public int dummyN = 40;

    RealmConfiguration userConfig = null;

    public RewardSystem() {

    }

    public void setUser(String user) {
        userConfig = new RealmConfiguration.Builder(PRECIOUS_APP.getContext())
                .name(user + ".realm")
                .build();
    }

    private ArrayList<RewardEvent> createDummyEvents(int dummyN) {

        ArrayList<RewardEvent> dummyEvents = new ArrayList<>();

        // generate dummy values
        for(int i=0; i<dummyN; i++) {
            long date = dummyTo + (long) (Math.random() * (float) (dummyTo-dummyFrom));
            RewardEvent e;
            if (Math.random() > 0.7) {
                e = RewardEvent.getMilestone(UUID.randomUUID().toString(), UUID.randomUUID().toString(), 100, date);
            } else {
                e = RewardEvent.getEvent(UUID.randomUUID().toString(), 100, date);
            }
            dummyEvents.add(e);
        }

        // sort them according to date
        Collections.sort(dummyEvents, new Comparator<RewardEvent>() {
            @Override
            public int compare(RewardEvent o1, RewardEvent o2) {
                if (o1.getDate().getTime() == o2.getDate().getTime()) {
                    return 0;
                }
                return o1.getDate().getTime() < o2.getDate().getTime() ? - 1 : 1;
            }
        });

        int cnt = 0;
        for(RewardEvent e : dummyEvents) {
            e.setName("" + cnt++);
        }

        return dummyEvents;

    }

    public void postPointIncrease(int points) {
        this.postRewardEvent(RewardEvent.getPointIncrease(points, new Date().getTime()));
    }

    public void postEvent(String name, int points) {
        this.postRewardEvent(RewardEvent.getEvent(name, points, new Date().getTime()));
    }

    public void postMilestone(String name, String reason, int points) {
        this.postRewardEvent(RewardEvent.getMilestone(name, reason, points, new Date().getTime()));
    }

    private Realm getInstance() {
        Realm realm;
        if (userConfig != null) {
            realm = Realm.getInstance(this.userConfig);
        } else {
            realm = Realm.getDefaultInstance();
        }
        return realm;
    }

    private void postRewardEvent(RewardEvent e) {

        Realm realm = getInstance();
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
        Realm realm = getInstance();
        RealmResults<RewardEvent> results = realm.where(RewardEvent.class).findAll();
        return results.sum("points").intValue();
    }

    public int getTotalPoints(long from, long to) {
        Realm realm = getInstance();
        RealmResults<RewardEvent> results = realm.where(RewardEvent.class).between("date", from, to).findAll();
        return results.sum("points").intValue();
    }

    public int getTotalPoints(Date from, Date to) {
        return getTotalPoints(from.getTime(), to.getTime());
    }

    public ArrayList<RewardEvent> getAllEventsSortedByDate() {
        Realm realm = getInstance();
        RealmResults<RewardEvent> results = realm.where(RewardEvent.class).findAllSorted("date");
        ArrayList<RewardEvent> arr = new ArrayList<RewardEvent>();
        for (RewardEvent e : results) {
            arr.add(e);
        }
        return arr;
    }

    public ArrayList<RewardEvent> dummy_createDummyEvents(int n) {
        return this.createDummyEvents(n);
    }

    public ArrayList<RewardEvent> getAllEventsSortedByDate(long from, long to) {
        Realm realm = getInstance();
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
