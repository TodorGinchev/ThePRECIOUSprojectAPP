package precious_rule_system.rules.your.implementations.data;

import precious_rule_system.rules.db.lookup.LookupData;
import precious_rule_system.rules.db.lookup.LookupDataManager;
import precious_rule_system.rules.your.implementations.data.UHtrial_data.UHTrialData;
import precious_rule_system.rules.your.implementations.data.application_data.ApplicationData;
import precious_rule_system.rules.your.implementations.data.test_data.TestData;

import java.util.Date;

import rules.data.Data;
import rules.entities.Rule;
import rules.helpers.Helpers;
import rules.managers.data.DataManagerInterface;
import rules.types.RuleTypes;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;

/**
 * Created by christopher on 11.08.16.
 */

public class DataManager implements DataManagerInterface {

    static int test_state = 0;
    static int test_time = 0;
    // this method needs to be implemented for all datakeys
    public Data[] getData(RuleTypes.DataKeys key, Date from, Date to, Rule rule) throws Exception {

        switch (key) {

            //Application Data Keys
            case CURRENT_OUTCOME_GOAL:
            case OUTCOME_GOAL_SET:
                return Helpers.wrapData(1);
            case GOAL_SET_FOR_TODAY:
                return Helpers.wrapData(0);
            case DAILY_GOAL_STEPS:
            case APPLICATION_NOT_OPENED_SINCE_HOURS:
            case SUGGESTED_APP:
            case CONSECUTIVE_PA_GOALS_ACHIEVED:
            case TOTAL_PA_GOALS_ACHIEVED:
            case STEPS_TODAY:
            case TIME_SINCE_REGISTRATION_DAYS:
                return ApplicationData.getApplicationData(key);



            // UH-Specific Data Keys
            case UH_PARTICIPANT_ID:
            case UH_INTERVENTION_STATE:
            case UH_TRIAL_CURRENT_DAY:
                return UHTrialData.getData(key);

            //test-related Data Keys
            case TEST_PARTICIPANT_ID:
            case TEST_INTERVENTION_STATE:
            case TEST_HOURS:
            case TEST_DAYS:
                return TestData.getData(key);

            // Things set by the user

            // Derived
            case DAILY_GOAL_PERCENTAGE:
                break;
            case DAILY_GOAL_COMPARISON_YESTERDAY:
                break;
            case CURRENT_TRIAL_DAY:
                break;


            // Rule-system
            case RULE_LAST_RUN_HOURS:
                Date lastExecuted = PRECIOUS_APP.getRuleSystem().getDatabase().getRuleLastExecuted(rule);
                if (lastExecuted == null) return Helpers.wrapData(Double.MAX_VALUE);
                else {
                    Long timeDiff = (new Date().getTime()-lastExecuted.getTime());
                    return Helpers.wrapData(timeDiff.doubleValue()/1000/60/60);
                }
            case RULE_LAST_RUN_SECONDS:
                lastExecuted = PRECIOUS_APP.getRuleSystem().getDatabase().getRuleLastExecuted(rule);
                if (lastExecuted == null) return Helpers.wrapData(Double.MAX_VALUE);
                else {
                    Long timeDiff = (new Date().getTime()-lastExecuted.getTime());
                    return Helpers.wrapData(timeDiff.doubleValue()/1000);
                }

        }
        throw new Exception("DataManager has not implemented this data type ("+key.toString()+")");
    }

    public Object getLookupTableDataWithId(String id, int row, int column)  throws Exception {

        LookupDataManager dataMgr = PRECIOUS_APP.getRuleSystem().getLookupDatabase();
        LookupData data = dataMgr.getLookupData(id);
        if (data == null) throw new Exception("Lookup not succeded with " + id);
        Object result = data.getValueAt(row, column);
        if (result == null) throw new Exception("Index " + row + ":" + column + " out of bounds for table " + id);
        return result;
    }

    @Override
    public int getLookupTableRowBounds(String id) throws Exception {
        LookupDataManager dataMgr = PRECIOUS_APP.getRuleSystem().getLookupDatabase();
        LookupData data = dataMgr.getLookupData(id);
        return data.getRows();
    }

    @Override
    public int getLookupTableColumnBounds(String id) throws Exception {
        LookupDataManager dataMgr = PRECIOUS_APP.getRuleSystem().getLookupDatabase();
        LookupData data = dataMgr.getLookupData(id);
        return data.getColumns();
    }
/*
    public  static int update_state() {
        test_state = test_state++ % 5;
        if (test_state == 0)
                return 1;
        return test_state;
    }
    public static int update_time() {
        test_time = test_time +1;
        return test_time % 24;
    }
    */
}
