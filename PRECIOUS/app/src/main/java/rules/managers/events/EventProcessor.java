package rules.managers.events;

import java.util.Map;

import rules.database.DBHandlerInterface;
import rules.entities.Rule;
import rules.types.RuleTypes;

/**
 * Created by christopher on 07.06.16.
 */
public class EventProcessor {

    private final RuleTypes.Key key;
    private final Map<String, Object> parameters;
    private final DBHandlerInterface db;

    public EventProcessor(RuleTypes.Key key, Map<String, Object> parameters, DBHandlerInterface db) {
        this.key = key;
        this.parameters = parameters;
        this.db = db;
    }

    public RuleTypes.Key getKey() {
        return key;
    }

    /**
     * Method runs when Event is submitted to the ExecutorService and
     * retrieves all rules for specific key from the Datamanager, then
     * submits them to the processing Queue for further execution
     */
    public Rule[] process() {
        // Retrieve all rules for this particular key
        Rule[] rules = db.getRulesWithTrigger(this.key, this.parameters);
        return rules;
    }

}
