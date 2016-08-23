package rules.entities.trigger;

import org.json.JSONObject;

import rules.types.RuleTypes;

/**
 * Created by christopher on 07.06.16.
 */
public class Trigger {

    private RuleTypes.Key key;

    public Trigger(JSONObject json) throws Exception {

        String _key = json.getString("key");
        RuleTypes.DataKeys _k1 = RuleTypes.DataKeys.fromString(_key);
        RuleTypes.TriggerKeys _k2 = RuleTypes.TriggerKeys.fromString(_key);

        if(_k1 == null && _k2 == null) {
            throw new Exception("Invalid Trigger detected");
        } else if (_k1 != null) {
            this.key = _k1;
        } else {
            this.key = _k2;
        }

    }

    public JSONObject toJSON() throws Exception {
        JSONObject json = new JSONObject();
        json.put("key", key.toString());
        return json;
    }

    public Trigger(String _key) throws Exception {
        RuleTypes.DataKeys _k1 = RuleTypes.DataKeys.fromString(_key);
        RuleTypes.TriggerKeys _k2 = RuleTypes.TriggerKeys.fromString(_key);

        if(_k1 == null && _k2 == null) {
            throw new Exception("Invalid Trigger detected");
        } else if (_k1 != null) {
            this.key = _k1;
        } else {
            this.key = _k2;
        }
    }

    public static Trigger fromJSON(JSONObject json) throws Exception {
        return new Trigger(json);
    }

    public RuleTypes.Key getKey() {
        return key;
    }


}
