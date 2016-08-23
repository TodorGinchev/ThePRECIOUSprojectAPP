package rules.entities.conditions;

import org.json.JSONObject;

import rules.entities.conditions.lookup.ConditionLookupTable;

/**
 * Created by christopher on 10.08.16.
 */

public class ConditionLookup {

    public enum LookupType {

        TABLE("table");

        private String text;

        LookupType(String text) {
            this.text = text;
        }

        public String toString() {
            return this.text;
        }

        public static LookupType fromString(String text) {
            if (text != null) {
                for (LookupType b : LookupType.values()) {
                    if (text.equalsIgnoreCase(b.text)) {
                        return b;
                    }
                }
            }
            return null;
        }
    }

    private LookupType type;
    private Object parameters;

    public LookupType getType() {
        return type;
    }

    public Object getParameters() {
        return parameters;
    }

    public ConditionLookup(ConditionLookupTable table) {
        this.type = LookupType.TABLE;
        this.parameters = table;
    }

    public JSONObject toJSON() throws Exception {

        JSONObject json = new JSONObject();
        json.put("type", type.toString());

        if (this.type == LookupType.TABLE) {
            ConditionLookupTable table = (ConditionLookupTable) parameters;
            json.put("parameter", table.toJSON());
            return json;
        }
        throw new Exception("Invalid Lookup parameter provided");
    }

    public static ConditionLookup fromJSON(JSONObject json) throws Exception {

        LookupType type = LookupType.fromString(json.getString("type"));

        if (type == null) {
            throw new Exception("Invalid Lookup Type detected");
        }

        if (type == LookupType.TABLE) {
            ConditionLookupTable table = ConditionLookupTable.fromJSON(json.getJSONObject("parameter"));
            return new ConditionLookup(table);
        }

        throw new Exception("Invalid Lookup Type detected");
    }
}
