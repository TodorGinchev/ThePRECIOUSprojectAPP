package rules.entities.conditions;

import org.json.JSONObject;

import rules.types.RuleTypes;

/**
 * Created by christopher on 07.06.16.
 */
public class ConditionDataOption {

    private String path;
    private RuleTypes.ArrayReduce arrayReduce;

    public ConditionDataOption(String path, RuleTypes.ArrayReduce arrayReduce) {
        this.path = path;
        this.arrayReduce = arrayReduce;
    }

    public JSONObject toJSON() throws Exception {
        JSONObject json = new JSONObject();
        if (path != null) {
            json.put("path", path);
        }

        if (arrayReduce != null) {
            json.put("arrayReduce", arrayReduce.toString());
        }
        return json;
    }

    public static ConditionDataOption fromJSON(JSONObject json) throws Exception {

        String path = null;

        if(json.has("path")) {
            path = json.getString("path");
        }

        RuleTypes.ArrayReduce arrayReduce = null;
        if(json.has("arrayReduce")) {
            arrayReduce = RuleTypes.ArrayReduce.fromString(json.getString("arrayReduce"));
            if(arrayReduce == null) {
                throw new Exception("Invalid arrayReduce key detected");
            }
        }

        if (path == null && arrayReduce == null) {
            return null;
        }

        return new ConditionDataOption(path, arrayReduce);
    }

    public String getPath() {
        return path;
    }

    public RuleTypes.ArrayReduce getArrayReduce() {
        return arrayReduce;
    }
}
