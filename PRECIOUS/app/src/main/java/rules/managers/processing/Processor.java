package rules.managers.processing;

import java.util.Date;

import rules.database.DBHandlerInterface;
import rules.entities.Rule;
import rules.entities.actions.Action;
import rules.managers.data.DataManagerInterface;
import rules.processing.ActionProcessor;
import rules.processing.RuleProcessor;

import android.util.Log;

/**
 * Created by christopher on 07.06.16.
 */
public class Processor {

    private final Rule rule;
    private final DBHandlerInterface db;
    private final DataManagerInterface dataManager;

    private final String TAG = "Rules.Processor";

    public Processor(Rule rule, DBHandlerInterface db, DataManagerInterface dataManager) {
        this.rule = rule;
        this.db = db;
        this.dataManager = dataManager;
    }

    public int getPriority() {
        return rule.getPriority();
    }

    /**
     * This is the method checking whether a rule should be executed or not and hence
     * represents the main evaluation method of a rule. It gathers all relevant information
     * from the db needed to process the conditions, checks upon the
     * operations and their linkages and finally enqueues the rule's actions into the ActionQueue
     * if the rule is to be executed.
     */
    public Action[] process() throws Exception {

        Log.i(TAG, "Processing Rule "+rule.getName()+" (" + rule.getRuleId() + ") started");

        // Try rule processing, and catch errors
        Boolean result = null;
        try {
            result = this.processRule();
        } catch (Exception e) {
            Log.i(TAG, "ERROR while Processing Rule " + rule.getRuleId() + ", :" + e.toString());
            throw e;
        }

        // If we dont have a result, return
        if (result == null) {
            throw new Exception("processRule return with null result");
        } else {
            // Result was negative, nothing to do
            if (!result) {
                Log.i(TAG, "Processing Rule " + rule.getRuleId() + " Finished - Not Triggered");
                return new Action[0];
            } else {

                // Result was positive, actions need to be perforned
                // Get the rules' actions
                Action[] actions = rule.getActions();

                // Get data needed for actions
                try {
                    actions = ActionProcessor.transformActions(this.rule, actions, this.dataManager);
                } catch (Exception e) {
                    Log.i(TAG, "ERROR while Processing Rule Actions " + rule.getRuleId() + ", :" + e.toString());
                    throw e;
                }

                // Store that this rule was processed
                db.setRuleExecuted(rule, new Date());

                Log.i(TAG, "Processing Rule " + rule.getRuleId() + " Finished - Rule triggered, Actions fired");
                return actions;
            }
        }
    }

    public Boolean processRule() throws Exception {
            return RuleProcessor.processRule(this.rule, this.dataManager);
    }


}
