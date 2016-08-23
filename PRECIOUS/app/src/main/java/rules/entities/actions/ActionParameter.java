package rules.entities.actions;

import org.json.JSONObject;

import rules.entities.conditions.ConditionParameter;

/**
 * A wrapper class for parameters for a specific actions
 * containing a key, and a value, whereas the value can be
 * pretty much everything depending on the key
 * Created by christopher on 13.06.16.
 */
public class ActionParameter {

    enum DefinedBy {

        USER("user"),
        PARAMETER("parameter");

        private String text;

        DefinedBy(String text) {
            this.text = text;
        }

        public String toString() {
            return this.text;
        }

        public static DefinedBy fromString(String text) {
            if (text != null) {
                for (DefinedBy b : DefinedBy.values()) {
                    if (text.equalsIgnoreCase(b.text)) {
                        return b;
                    }
                }
            }
            return null;
        }
    }

    private DefinedBy definedBy;
    private String key;
    private Object value;

    public ActionParameter(String key, Object value) {
        this.key = key;
        this.value = value;
        this.definedBy = DefinedBy.USER;
    }

    public ActionParameter(String key, ConditionParameter value) {
        this.key = key;
        this.value = value;
        this.definedBy = DefinedBy.PARAMETER;
    }

    public boolean isUserDefined() {
        return this.definedBy == DefinedBy.USER;
    }

    public boolean isParameterDefined() {
        return this.definedBy == DefinedBy.PARAMETER;
    }

    public ConditionParameter getValueAsConditionParameter() {
        return (ConditionParameter) value;
    }

    public JSONObject toJSON() throws Exception {

        JSONObject json = new JSONObject();
        json.put("key", key);
        json.put("definedBy", definedBy.toString());

        if (definedBy == DefinedBy.USER) {
            json.put("value", value);
        } else if (definedBy == DefinedBy.PARAMETER) {
            json.put("value", ((ConditionParameter) value).toJSON());
        } else {
            throw new Exception("Invalid Actionparameter detected");
        }

        return json;
    }

    public static ActionParameter fromJSON(JSONObject o) throws Exception {

        if (!o.has("definedBy")) {
            throw new Exception("Actionparameter is missing definedBy Key");
        }

        DefinedBy definedBy = DefinedBy.fromString(o.getString("definedBy"));

        if (definedBy == null) {
            throw new Exception("Actionparameter has invalid definedBy field");
        }

        if (definedBy == DefinedBy.USER) {
            return new ActionParameter(o.getString("key"), o.get("value"));
        } else if (definedBy == DefinedBy.PARAMETER) {
            JSONObject json = o.getJSONObject("value");
            return new ActionParameter(o.getString("key"), ConditionParameter.fromJSON(json));
        } else {
            throw new Exception("Actionparameter has invalid definedBy field (not implemented)");
        }
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
