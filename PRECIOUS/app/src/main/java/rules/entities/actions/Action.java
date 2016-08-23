package rules.entities.actions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import rules.types.RuleTypes;

/**
 * A wrapper class for actions to be performed on the device
 * Actions contain a specific key determining which action is to be performed,
 * an optional array of parameters that can accompany an actions,
 * as well as a numeric priority parameter determining in which order
 * actions are to be performed
 * Created by christopher on 07.06.16.
 */
public class Action {

    // Default priority of actions is zero
    public static int defaultPriority = 0;

    // The key for this specific action
    private RuleTypes.ActionKeys key;

    // An optional array of parameters for this action
    private ActionParameter[] parameters;

    // An optional priority value, default is zero
    private int priority;


    public Action(RuleTypes.ActionKeys key, int priority) {
        this.key = key;
        this.parameters = new ActionParameter[0];
        this.priority = priority;
    }

    public Action(RuleTypes.ActionKeys key, ActionParameter[] parameters, int priority) {
        this.key = key;
        this.parameters = parameters;
        this.priority = priority;
    }

    public void addParameter(ActionParameter p) {
        ArrayList<ActionParameter> list = new ArrayList<ActionParameter>();
        for (ActionParameter oldParameter : this.parameters) {
            list.add(oldParameter);
        }
        list.add(p);
        this.parameters = list.toArray(new ActionParameter[0]);
    }

    public JSONObject toJSON() throws Exception {
        JSONObject json = new JSONObject();
        json.put("key", key);
        JSONArray parameters = new JSONArray();
        for (ActionParameter p : this.parameters) {
            parameters.put(p.toJSON());
        }
        json.put("parameters", parameters);
        json.put("priority", priority);
        return json;
    }

    /**
     * Returns an Action instance from a JSON object
     * @param o - the JSONObject
     * @return
     * @throws Exception
     */
    public static Action fromJSON(JSONObject o) throws Exception {

        // Default priority of zero
        int priority = defaultPriority;

        if(o.has("priority")) {
            priority = o.getInt("priority");
        }

        // Go through optional ActionParameters
        ActionParameter[] parameters = new ActionParameter[0];
        if(o.has("parameters")) {
            JSONArray _parameters = o.getJSONArray("parameters");
            parameters = new ActionParameter[_parameters.length()];
            for(int i=0; i<_parameters.length(); i++) {
                parameters[i] = ActionParameter.fromJSON(_parameters.getJSONObject(i));
            }
        }

        // Parse the actionKey, which has to be valid
        RuleTypes.ActionKeys key = RuleTypes.ActionKeys.fromString(o.getString("key"));
        if(key == null) {
            throw new Exception("Invalid Action key detected");
        }

        return new Action(key, parameters, priority);
    }

    // Getters
    public int getPriority() {
        return priority;
    }
    public ActionParameter[] getParameters() {
        return parameters;
    }
    public RuleTypes.ActionKeys getKey() {
        return key;
    }
}
