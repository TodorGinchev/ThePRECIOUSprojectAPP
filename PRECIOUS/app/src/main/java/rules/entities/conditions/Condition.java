package rules.entities.conditions;

import org.json.JSONArray;
import org.json.JSONObject;

import rules.types.RuleTypes;

/**
 * Created by christopher on 07.06.16.
 */
public class Condition {

    private String name;
    private ConditionParameter[] parameters;
    private RuleTypes.Comparator comparator;

    public Condition(String name, ConditionParameter[] parameters, RuleTypes.Comparator comparator) {
        this.name = name;
        this.parameters = parameters;
        this.comparator = comparator;
    }

    public JSONObject toJSON() throws Exception {

        JSONObject json = new JSONObject();

        if (name != null) {
            json.put("name", name);
        }

        JSONArray parameters = new JSONArray();
        for(ConditionParameter p : this.parameters) {
            parameters.put(p.toJSON());
        }
        json.put("parameters", parameters);
        json.put("comparator", comparator.toString());
        return json;
    }

    public static Condition fromJSON(JSONObject json) throws Exception {

        String name = null;

        // Name (Optional)
        if (json.has("name")) {
            name = json.getString("name");
        }

        // Comparator
        RuleTypes.Comparator comparator = RuleTypes.Comparator.fromString(json.getString("comparator"));
        if(comparator == null) {
            throw new Exception("Invalid comparator detected");
        }

        // Parameters
        JSONArray _parameters = json.getJSONArray("parameters");
        ConditionParameter[] parameters = new ConditionParameter[_parameters.length()];
        for(int i=0; i<_parameters.length(); i++) {
            parameters[i] = ConditionParameter.fromJSON(_parameters.getJSONObject(i));
        }

        if (parameters.length == 0) {
            throw new Exception("Condition has to have at least one parameter");
        }

        // Return the condition
        return new Condition(name, parameters, comparator);
    }

    public ConditionParameter[] getParameters() {
        return parameters;
    }
    public RuleTypes.Comparator getComparator() {
        return comparator;
    }
    public String getName() {
        return name;
    }

}
