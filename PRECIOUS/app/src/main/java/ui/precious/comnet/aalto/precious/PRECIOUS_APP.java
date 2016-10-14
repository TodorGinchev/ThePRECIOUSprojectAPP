package ui.precious.comnet.aalto.precious;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import precious_rule_system.precoiusinterface.PreciousApplicationData;
import precious_rule_system.rewardsystem.RewardSystem;
import precious_rule_system.rules.RuleSystem;
import precious_rule_system.rules.your.implementations.actions.ActionManager;
import precious_rule_system.rules.your.implementations.data.DataManager;
import precious_rule_system.scheduler.AlarmReceiver;
import precious_rule_system.scheduler.TestTime;
import rules.managers.action.ActionManagerInterface;
import rules.managers.data.DataManagerInterface;
import rules.types.RuleTypes;


public class PRECIOUS_APP extends Application {

    String TAG = "PRECIOUS_APP";
    private static RewardSystem rewardSystem;
    private static PRECIOUS_APP instance;
    private static Context context;
    private AlarmReceiver alarmReceiver;


    public static Context getAppContext() {
        return context;
    }

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        initSingletons();
    }

    public PRECIOUS_APP() {
        instance = this;
        }

    protected void initSingletons()
    {
        // initialise realm
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context)
                // Careful, this deletes all realm files whenever an update is made to the realm schema
                // we should use a migration strategy for updates
                // https://realm.io/docs/java/latest/#migrations
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);

        // setup reward system
        rewardSystem = new RewardSystem();

        // get datamanager
        DataManagerInterface dataManager = new DataManager();

        // get actionmanager
        ActionManagerInterface actionManager = new ActionManager();

        // initialise our rule system
        RuleSystem.initInstance(context, dataManager, actionManager);

        // initialise the alarmreceiver
        this.alarmReceiver = new AlarmReceiver();
        this.alarmReceiver.resetAlarm(context);
    }

    // convenience methods
    public synchronized static PRECIOUS_APP getInstance() {

        return instance;
    }
    public synchronized static RuleSystem getRuleSystem() {

        return RuleSystem.getInstance();
    }

    public synchronized static void postEvent(RuleTypes.Key key, Map<String, Object> parameters) {

        PRECIOUS_APP.getRuleSystem().postEvent(key,parameters);
    }
    public synchronized static RewardSystem getRewardSystem() {

        return rewardSystem;
    }
    public synchronized static Context getContext() {

        return context;
    }
}
