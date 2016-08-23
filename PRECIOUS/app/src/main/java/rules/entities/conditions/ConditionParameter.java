package rules.entities.conditions;

import org.json.JSONObject;

import java.util.ArrayList;

import rules.data.Data;
import rules.types.RuleTypes;

/**
 * Created by christopher on 07.06.16.
 */
public class ConditionParameter {

    enum DefinedBy {

        USER("user"),
        DATA("data"),
        LOOKUP("lookup");

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
    private Object userDefinedValue;
    private ConditionLookup lookupValue;

    private RuleTypes.DataKeys key;
    private ConditionDataOption dataOption;
    private ConditionHistoricRequirement historicRequirements;
    private RuleTypes.ArrayReduce arrayReduce;

    public ConditionParameter(Object userDefinedValue) {
        this.definedBy = DefinedBy.USER;
        this.userDefinedValue = userDefinedValue;
        this.key = null;
        this.dataOption = null;
        this.historicRequirements = null;
        this.arrayReduce = null;
    }

    public ConditionParameter(RuleTypes.DataKeys key, ConditionDataOption dataOption,
                              ConditionHistoricRequirement historicRequirements,
                              RuleTypes.ArrayReduce arrayReduce) {
        this.definedBy = DefinedBy.DATA;
        this.userDefinedValue = null;
        this.key = key;
        this.dataOption = dataOption;
        this.historicRequirements = historicRequirements;
        this.arrayReduce = arrayReduce;
    }

    public ConditionParameter(ConditionLookup lookup) {
        this.definedBy = DefinedBy.LOOKUP;
        this.lookupValue = lookup;
    }

    public ConditionLookup getConditionLookup() { return this.lookupValue; }

    public RuleTypes.ArrayReduce getArrayReduce() {
        return arrayReduce;
    }

    public ConditionDataOption getDataOption() {
        return dataOption;
    }

    public Object getUserDefinedValue() {
        return userDefinedValue;
    }

    public JSONObject toJSON() throws Exception {

        JSONObject json = new JSONObject();
        json.put("definedBy", definedBy.toString());

        if (definedBy == DefinedBy.USER) {
            json.put("userDefinedValue", userDefinedValue);
            return json;
        } else if (definedBy == DefinedBy.LOOKUP) {
            json.put("lookupValue", lookupValue.toJSON());
            return json;
        }

        if (this.historicRequirements != null) {
            JSONObject hR = historicRequirements.toJSON();
            if (hR.has("placeholder")) {
                json.put("historicRequirements", hR.get("placeholder"));
            } else {
                json.put("historicRequirements", hR);
            }
        }

        if (this.dataOption != null) {
            json.put("dataOption", this.dataOption.toJSON());
        }

        if (this.arrayReduce != null) {
            json.put("arrayReduce", this.arrayReduce.toString());
        }

        json.put("key", key.toString());

        return json;
    }

    public static ConditionParameter fromJSON(JSONObject json) throws Exception {

        String _definedBy = json.getString("definedBy");
        DefinedBy definedBy = DefinedBy.fromString(_definedBy);

        if (definedBy == null) {
            throw new Exception("Invalid definedBy Key");
        }

        if (definedBy == DefinedBy.USER) {
            return new ConditionParameter(json.get("userDefinedValue"));
        } else if (definedBy == DefinedBy.LOOKUP) {
            JSONObject lookupJSON = json.getJSONObject("lookupValue");
            return new ConditionParameter(ConditionLookup.fromJSON(lookupJSON));
        }

        String _key = json.getString("key");
        RuleTypes.DataKeys key = RuleTypes.DataKeys.fromString(_key);

        if(key == null) {
            throw new Exception("Invalid Data key detected");
        }

        ConditionHistoricRequirement historicRequirements = null;
        if (json.has("historicRequirements")) {
            historicRequirements = ConditionHistoricRequirement.fromJSON(json.get("historicRequirements"));
        }

        ConditionDataOption dataOption = null;

        if(json.has("dataOption")) {
            JSONObject _dataOption = json.getJSONObject("dataOption");
            dataOption = ConditionDataOption.fromJSON(_dataOption);
        }

        RuleTypes.ArrayReduce arrayReduce = null;

        if(json.has("arrayReduce")) {
            arrayReduce = RuleTypes.ArrayReduce.fromString(json.getString("arrayReduce"));
        } else {
            arrayReduce = RuleTypes.ArrayReduce.FIRST;
        }

        return new ConditionParameter(key,dataOption,historicRequirements,arrayReduce);
    }

    public Boolean isUserDefined() {
        return definedBy == DefinedBy.USER;
    }
    public Boolean isLookupDefined() { return definedBy == DefinedBy.LOOKUP; }
    public Boolean isDataDefined() { return definedBy == DefinedBy.DATA; }
    public RuleTypes.DataKeys getDataKey() {
        return key;
    }
    public ConditionHistoricRequirement getHistoricRequirements() {
        return historicRequirements;
    }

}
