package rules.entities;

import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import rules.entities.actions.Action;
import rules.entities.actions.ActionParameter;
import rules.entities.conditions.Condition;
import rules.entities.conditions.ConditionParameter;
import rules.entities.operation.Operation;
import rules.entities.trigger.Trigger;
import rules.types.RuleTypes;

/**
 * Created by christopher on 07.06.16.
 */
public class Rule {

    private String _id;
    private long id;

    private String name;
    private String description;
    private Date created;

    private Condition[] conditions;
    private Operation operation;

    private Action[] actions;
    private Trigger[] triggers;

    private int priority;

    public Rule(String _id, String name, String description,
                Date created, Condition[] conditions, Operation operation,
                Action[] actions, Trigger[] triggers, int priority) {
        this._id = _id;
        this.name = name;
        this.description = description;
        this.conditions = conditions;
        this.created = created;
        this.operation = operation;
        this.actions = actions;
        this.triggers = triggers;
        this.priority = priority;
    }

    public Rule() {
        this.conditions = new Condition[0];
        this.actions = new Action[0];
        this.triggers = new Trigger[0];
    }

    public void setTriggers(Trigger[] triggers) {
        this.triggers = triggers;
    }

    public JSONArray getTriggersJSON() throws Exception {
        JSONArray triggers = new JSONArray();
        for (Trigger t : this.triggers) {
            triggers.put(t.toJSON());
        }
        return triggers;
    }

    public JSONArray getConditionsJSON() throws Exception {
        JSONArray conditions = new JSONArray();
        for (Condition c : this.conditions) {
            conditions.put(c.toJSON());
        }
        return conditions;
    }

    public JSONArray getActionsJSON() throws Exception {
        JSONArray actions = new JSONArray();
        for (Action a: this.actions) {
            actions.put(a.toJSON());
        }
        return actions;
    }

    public JSONObject toJSON() throws Exception {

        JSONObject json = new JSONObject();

        json.put("_id", _id);
        json.put("name", name);
        if (description != null) json.put("description", description);
        json.put("created", created.getTime());
        json.put("triggers", getTriggersJSON());
        json.put("conditions", getConditionsJSON());
        json.put("actions", getActionsJSON());
        json.put("operation", operation.toJSON());
        json.put("priority", priority);

        return json;
    }

    public static Rule fromJSON(JSONObject json) throws Exception {

        // Basic variables
        String _id = json.getString("_id");
        String name = json.getString("name");

        // Description is optional
        String description = null;

        if (json.has("description")) {
            description  = json.getString("description");
        }

        // Parse created date (optional)
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        Date created = null;

        if (json.has("created")) {
            Object dO = json.get("created");

            if (dO instanceof Integer) {
                created = new Date((int) dO);
            } else if (dO instanceof Long) {
                created = new Date((long) dO);
            } else if (dO instanceof String) {
                try {
                   created = df.parse((String) dO);
                } catch (Exception e) {
                }
            }
        }

        // The triggers
        JSONArray _triggers = json.getJSONArray("triggers");
        Trigger[] triggers = new Trigger[_triggers.length()];
        for(int i=0; i<_triggers.length(); i++) {
            triggers[i] = new Trigger(_triggers.getJSONObject(i));
        }

        if (triggers.length == 0) {
            throw new Exception("Rule contains no triggers");
        }

        // The conditions
        JSONArray _conditions = json.getJSONArray("conditions");
        Condition[] conditions = new Condition[_conditions.length()];
        for(int i=0; i<_conditions.length(); i++) {
            conditions[i] = Condition.fromJSON(_conditions.getJSONObject(i));
        }

        if(conditions.length == 0) {
            throw new Exception("Rule contains no conditions");
        }

        // The Operation
        JSONObject _operation = json.getJSONObject("operation");
        Operation operation = Operation.fromJSON(_operation);

        // Actions
        JSONArray _actions = json.getJSONArray("actions");
        Action[] actions = new Action[_actions.length()];
        for(int i=0; i<_actions.length(); i++) {
            actions[i] = Action.fromJSON(_actions.getJSONObject(i));
        }

        if(actions.length == 0) {
            throw new Exception("Rule contains no Actions");
        }

        // Priority
        int priority = 0;
        if(json.has("priority")) {
            priority = json.getInt("priority");
        }

        return new Rule(_id, name, description, created, conditions, operation, actions, triggers, priority);

    }

    public static Rule fromSQLite(
            long id,
            String ruleId,
            String name,
            String description,
            long created,
            String _conditions,
            String _operation,
            String _actions,
            int priority)
    throws Exception {

        JSONArray conditionsJSON = new JSONArray(_conditions);
        JSONArray actionsJSON = new JSONArray(_actions);
        JSONObject operationJSON = new JSONObject(_operation);

        Condition[] conditions = new Condition[conditionsJSON.length()];
        for(int i=0; i<conditionsJSON.length(); i++) {
            conditions[i] = Condition.fromJSON(conditionsJSON.getJSONObject(i));
        }

        if(conditions.length == 0) {
            throw new Exception("Rule contains no conditions");
        }

        // The Operation
        Operation operation = Operation.fromJSON(operationJSON);

        // Actions
        Action[] actions = new Action[actionsJSON.length()];
        for(int i=0; i<actionsJSON.length(); i++) {
            actions[i] = Action.fromJSON(actionsJSON.getJSONObject(i));
        }

        // Empty Triggers
        Trigger[] triggers = new Trigger[0];

        Rule r = new Rule(ruleId, name, description, new Date(created), conditions, operation, actions, triggers, priority);
        r.setId(id);
        return r;
    }

    public int getPriority() {
        return priority;
    }
    public Condition[] getConditions() { return conditions; }
    public String getName() { return name; }
    public String getRuleId() { return _id; }
    public Operation getOperation() { return operation; }
    public Action[] getActions() { return actions; }
    public String getDescription() { return description; }
    public Date getCreated() { return created; }
    public Trigger[] getTriggers() { return triggers; }
    public void setId(long id) { this.id = id; };

    public void addAction(Action a) {
        int n = this.actions.length;
        Action[] actions = new Action[n+1];
        for(int i=0; i<n; i++) {
            actions[i] = this.actions[i];
        }
        actions[n] = a;
        this.actions = actions;
    }

    public void addAction(RuleTypes.ActionKeys key, ActionParameter[] parameters, int priority) {
        this.addAction(new Action(key, parameters, priority));
    }

    public void addAction(JSONObject json) throws Exception {
        this.addAction(Action.fromJSON(json));
    }

    public void addCondition(Condition c) {
        int n = this.conditions.length;
        Condition[] conditions = new Condition[n+1];
        for(int i=0; i<n; i++) {
            conditions[i] = this.conditions[i];
        }
        conditions[n] = c;
        this.conditions = conditions;
    }

    public void addCondition(String name, ConditionParameter[] parameters, RuleTypes.Comparator comparator) {
        Condition c = new Condition(name, parameters, comparator);
        this.addCondition(c);
    }

    public void addCondition(JSONObject json) throws Exception {
        this.addCondition(Condition.fromJSON(json));
    }

}
