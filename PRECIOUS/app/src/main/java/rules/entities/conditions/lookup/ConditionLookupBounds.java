package rules.entities.conditions.lookup;

import org.json.JSONObject;

/**
 * Created by christopher on 12.09.16.
 */

public class ConditionLookupBounds {

    String tableId;
    String dimension;

    public ConditionLookupBounds(String tableId, String dimension) {
        this.tableId = tableId;
        this.dimension = dimension;
    }

    public JSONObject toJSON() throws Exception {
        JSONObject json = new JSONObject();
        json.put("tableId", tableId);
        json.put("dimension", dimension);
        return json;
    }

    public String getTableId() {
        return tableId;
    }

    public String getDimension() {
        return dimension;
    }

    public static ConditionLookupBounds fromJSON(JSONObject json) throws Exception {
        String tableId = json.getString("tableId");
        String dimension = json.getString("dimension");
        if (!dimension.equals("row") && !dimension.equals("column")) {
            throw new Exception("ConditionLookupBounds dimension must be either row or column");
        }
        return new ConditionLookupBounds(tableId, dimension);
    }


}
