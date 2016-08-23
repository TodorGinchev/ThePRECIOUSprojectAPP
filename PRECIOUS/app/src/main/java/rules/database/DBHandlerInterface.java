package rules.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import rules.entities.Rule;
import rules.types.RuleTypes;
import org.json.JSONObject;

/**
 * An interface for our DBHandler, abstracted here
 * so we can theoretically create mockups for testing
 * Created by christopher on 07.06.16.
 */
public interface DBHandlerInterface {

    /**
     * Adds a rule to the database
     * @param rule
     */
    public long addRule(JSONObject rule) throws Exception;
    public long addRule(Rule rule) throws Exception;

    /**
     *
     */
    public void removeRule(Rule rule) throws Exception;

    /**
     * Returns all rules from the database
     * @return
     */
    public ArrayList<Rule> getAllRules();

    /**
     * Returns all rules from the database with a specific trigger
     * @param keys
     * @return
     */
    public ArrayList<Rule> getAllRulesForTrigger(RuleTypes.Key key);
    public ArrayList<Rule> getAllRulesForTriggers(RuleTypes.Key[] keys);
    public Rule[] getRulesWithTrigger(RuleTypes.Key key, Map<String, Object> parameters);

    /**
     * Returns the number of rules available in the database
     * @return
     */
    public int getRuleCount();

    /**
     * Returns the date when a specific rule was last executed
     * @param rule
     * @return
     */
    public Date getRuleLastExecuted(Rule rule);

    /**
     * Sets the date for when a rule was last executed
     * @param rule
     * @param date
     */
    public void setRuleExecuted(Rule rule, Date date);
}
