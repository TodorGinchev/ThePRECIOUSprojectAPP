package precious_rule_system.rules;

import android.content.Context;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;
import precious_rule_system.RulesApplication;
import precious_rule_system.rules.db.RuleDatabaseManager;
import precious_rule_system.rules.db.lookup.LookupDataManager;
import precious_rule_system.rules.jobs.EventJob;
import precious_rule_system.rules.settings.RuleSettings;

import java.util.Map;

import rules.database.DBHandlerInterface;
import rules.managers.action.ActionManagerInterface;
import rules.managers.data.DataManagerInterface;
import rules.managers.events.EventProcessor;
import rules.types.RuleTypes;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;

/**
 * Created by christopher on 10.08.16.
 */

public class RuleSystem {

    // global application context
    private Context context;

    // our global jobmanager handling jobs
    private JobManager jobManager;
    private RuleDatabaseManager db;
    private DataManagerInterface dataManager;
    private ActionManagerInterface actionManager;

    // singleton stuff
    private static RuleSystem mInstance = null;

    private RuleSystem(){}

    public static RuleSystem getInstance(){
        if(mInstance == null) {
            mInstance = new RuleSystem();
        }
        return mInstance;
    }

    // initialization method of the singleton
    public static void initInstance(Context context, DataManagerInterface dataManager, ActionManagerInterface actionManager) {
        RuleSystem.getInstance().setContext(context);
        RuleSystem.getInstance().setDataManager(dataManager);
        RuleSystem.getInstance().setActionManager(actionManager);
        RuleSystem.getInstance().initialize();
    }

    // private context setter, only accessible from the initInstance method
    private void setContext(Context context) {
        this.context = context;
    }
    private void setDataManager(DataManagerInterface dataManager) { this.dataManager = dataManager; }
    private void setActionManager(ActionManagerInterface actionManager) { this.actionManager = actionManager; }

    public synchronized DataManagerInterface getDataManager() {
        return this.dataManager;
    }

    public DBHandlerInterface getDatabase() {
        return this.db.getDatabase();
    }
    public LookupDataManager getLookupDatabase()
    {
        return this.db.getLookupDatabase();
    }

    public synchronized ActionManagerInterface getActionManager() {

        return this.actionManager;
    }

    private void initialize() {
        // configure the job manager
        this.configureJobManager();
        // configure the rule database
        this.configureRuledatabase();
    }

    private void configureRuledatabase() {
        this.db = new RuleDatabaseManager(this.context);
    }

    public void addJob(Job job) {
        synchronized (jobManager) {
            jobManager.addJob(job);
        }
    }

    public void postEvent(RuleTypes.Key key, Map<String, Object> parameters) {

        EventProcessor event = new EventProcessor(key, parameters, PRECIOUS_APP.getRuleSystem().getDatabase());
        EventJob job = new EventJob(event);
        this.addJob(job);
    }

    private void configureJobManager() {
        Configuration.Builder builder = new Configuration.Builder(this.context)
                .customLogger(new CustomLogger() {
                    private static final String TAG = "Jobs";
                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        //Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        //Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        //Log.e(TAG, String.format(text, args));
                    }

                    @Override
                    public void v(String text, Object... args) {

                    }
                })
                .minConsumerCount(RuleSettings.minConsumerCount)
                .maxConsumerCount(RuleSettings.maxConsumerCount)
                .loadFactor(RuleSettings.jobsPerConsumer)
                .consumerKeepAlive(RuleSettings.consumerKeepAliveSeconds);

        jobManager = new JobManager(builder.build());
    }


}
