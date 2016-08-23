package rules.entities.conditions.lookup;

import org.json.JSONObject;

import rules.entities.conditions.ConditionLookup;
import rules.entities.conditions.ConditionParameter;

/**
 * Created by christopher on 10.08.16.
 */

public class ConditionLookupTable {

    String tableId;
    ConditionParameter row;
    ConditionParameter column;

    public String getTableId() {
        return tableId;
    }

    public ConditionParameter getRow() {
        return row;
    }

    public ConditionParameter getColumn() {
        return column;
    }

    public ConditionLookupTable(String tableId, ConditionParameter row, ConditionParameter column) {
        this.tableId = tableId;
        this.row = row;
        this.column = column;
    }

    public JSONObject toJSON() throws Exception {
        JSONObject json = new JSONObject();
        json.put("tableId", tableId);
        json.put("row", row.toJSON());
        json.put("column", column.toJSON());
        return json;
    }

    public static ConditionLookupTable fromJSON(JSONObject json) throws Exception {
        String tableId = json.getString("tableId");
        ConditionParameter row = ConditionParameter.fromJSON(json.getJSONObject("row"));
        ConditionParameter column = ConditionParameter.fromJSON(json.getJSONObject("column"));
        return new ConditionLookupTable(tableId, row, column);
    }
}
