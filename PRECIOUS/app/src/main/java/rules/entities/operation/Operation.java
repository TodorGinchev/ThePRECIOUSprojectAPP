package rules.entities.operation;

import org.json.JSONArray;
import org.json.JSONObject;

import rules.types.RuleTypes;

/**
 * Created by christopher on 07.06.16.
 */
public class Operation {

    private RuleTypes.OperationType type;
    private Operation[] conditions;
    private Object value;

    public Operation(RuleTypes.OperationType type, Operation[] conditions, Object value) {
        this.type = type;
        this.conditions = conditions;
        this.value = value;
    }

    public Operation(Operation value) {
        this.type = RuleTypes.OperationType.OPERATION;
        this.conditions = null;
        this.value = value;
    }

    public Operation(int reference) {
        this.type = RuleTypes.OperationType.REFERENCE;
        this.value = reference;
        this.conditions = null;
    }

    public Operation(RuleTypes.OperationType type, Operation[] conditions) {
        assert(type != RuleTypes.OperationType.REFERENCE);
        assert(type != RuleTypes.OperationType.OPERATION);
        this.type = type;
        this.conditions = conditions;
        this.value = null;
    }

    public JSONObject toJSON() throws Exception {

        JSONObject json = new JSONObject();
        json.put("type", type.toString());

        if (type == RuleTypes.OperationType.OPERATION) {
            json.put("value", ((Operation) value).toJSON());
        } else if (type == RuleTypes.OperationType.REFERENCE) {
            json.put("value", (int) value);
        } else {
            JSONArray conditions = new JSONArray();
            for(Operation o : this.conditions) {
                conditions.put(o.toJSON());
            }
            json.put("conditions", conditions);
        }

        return json;
    }

    public static Operation fromJSON(JSONObject o) throws Exception {

        Object value = null;
        Operation[] conditions = null;

        String _type = o.getString("type");
        RuleTypes.OperationType type = RuleTypes.OperationType.fromString(_type);

        if(type == null) {
            throw new Exception("Invalid Operation Type detected");
        }

        if (type == RuleTypes.OperationType.OPERATION) {
            value = Operation.fromJSON(o.getJSONObject("value"));
        } else if (type == RuleTypes.OperationType.REFERENCE) {
            value = o.getInt("value");
        } else {
            JSONArray _conditions = o.getJSONArray("conditions");
            int n = _conditions.length();
            if (n == 0) {
                throw new Exception("Logical Operations must contain a condition array");
            }
            conditions = new Operation[n];
            for(int i=0; i<n; i++) {
                conditions[i] = Operation.fromJSON(_conditions.getJSONObject(i));
            }
        }

        return new Operation(type, conditions, value);

    }

    public RuleTypes.OperationType getType() {
        return type;
    }

    public Operation[] getConditions() {
        return conditions;
    }

    public Object getValue() {
        return value;
    }


}
