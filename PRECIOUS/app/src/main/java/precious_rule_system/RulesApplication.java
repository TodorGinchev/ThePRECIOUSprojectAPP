package precious_rule_system;

import android.app.Application;
import android.content.Context;

import precious_rule_system.rewardsystem.RewardSystem;
import precious_rule_system.rules.RuleSystem;
import precious_rule_system.rules.your.implementations.actions.ActionManager;
import precious_rule_system.rules.your.implementations.data.DataManager;
import precious_rule_system.scheduler.AlarmReceiver;

import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rules.managers.action.ActionManagerInterface;
import rules.managers.data.DataManagerInterface;
import rules.types.RuleTypes;

/**
 * Created by christopher on 10.08.16.
 */

//public class RulesApplication extends Application {
//
//    private static RewardSystem rewardSystem;
//    private static RulesApplication instance;
//    private static Context context;
//    private AlarmReceiver alarmReceiver;
//
//    public RulesApplication() {
//        instance = this;
//    }
//
//    @Override
//    public void onCreate()
//    {
//        super.onCreate();
//        initSingletons();
//    }
//
//    protected void initSingletons()
//    {
//        // get the application context
//        context = this.getApplicationContext();
//
//        // initialise realm
//        RealmConfiguration realmConfig = new RealmConfiguration.Builder(context)
//                // Careful, this deletes all realm files whenever an update is made to the realm schema
//                // we should use a migration strategy for updates
//                // https://realm.io/docs/java/latest/#migrations
//                .deleteRealmIfMigrationNeeded()
//                .build();
//        Realm.setDefaultConfiguration(realmConfig);
//
//        // setup reward system
//        rewardSystem = new RewardSystem();
//
//        // get datamanager
//        DataManagerInterface dataManager = new DataManager();
//
//        // get actionmanager
//        ActionManagerInterface actionManager = new ActionManager();
//
//        // initialise our rule system
//        RuleSystem.initInstance(this.getApplicationContext(), dataManager, actionManager);
//
//        // initialise the alarmreceiver
//        this.alarmReceiver = new AlarmReceiver();
//        this.alarmReceiver.resetAlarm(this.getApplicationContext());
//    }
//
//    // convenience methods
//    public synchronized static RulesApplication getInstance() {
//
//        return instance;
//    }
//    public synchronized static RuleSystem getRuleSystem() {
//        return RuleSystem.getInstance();
//    }
//    public synchronized static void postEvent(RuleTypes.Key key, Map<String, Object> parameters) {
//        RulesApplication.getRuleSystem().postEvent(key,parameters);
//    }
//    public synchronized static RewardSystem getRewardSystem() {
//        return rewardSystem;
//    }
//    public synchronized static Context getContext() {return context;}
//}
public class RulesApplication  {

    private static RewardSystem rewardSystem;
    private static RulesApplication instance;
    private static Context context;
    private AlarmReceiver alarmReceiver;

    public RulesApplication() {
        instance = this;
    }


    public void init()
    {
        initSingletons();
    }

    protected void initSingletons()
    {
        // get the application context
        context = ui.precious.comnet.aalto.precious.preicousApp.getAppContext();

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
    public synchronized static RulesApplication getInstance() {

        return instance;
    }
    public synchronized static RuleSystem getRuleSystem() {
        return RuleSystem.getInstance();
    }
    public synchronized static void postEvent(RuleTypes.Key key, Map<String, Object> parameters) {
        RulesApplication.getRuleSystem().postEvent(key,parameters);
    }
    public synchronized static RewardSystem getRewardSystem() {
        return rewardSystem;
    }
    public synchronized static Context getContext() {return context;}
}

