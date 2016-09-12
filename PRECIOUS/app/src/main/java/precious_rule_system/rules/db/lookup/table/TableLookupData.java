package precious_rule_system.rules.db.lookup.table;

import precious_rule_system.rules.db.lookup.LookupData;

import org.json.JSONArray;
import org.json.JSONObject;

import io.realm.RealmObject;

/**
 * Created by christopher on 14.08.16.
 */

public class TableLookupData extends RealmObject implements LookupData {

    private String name;
    private String _id;
    private String type;

    // stored as a simple string for the sake of simplicity for now
    private String value;

    public TableLookupData() {
    }

    public TableLookupData(String _id, String name, String type, String value) {
        this._id = _id;
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getId() {
        return _id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Object getValueAt(int row, int column) {
        try {
            JSONObject value = new JSONObject(this.value);
            JSONArray columns = value.getJSONArray("columns");
            if (column < columns.length()) {
                JSONArray rows = columns.getJSONObject(column).getJSONArray("row");
                if (row < rows.length()) {
                    return rows.get(row);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getRows() {
        try {
            JSONObject value = new JSONObject(this.value);
            JSONArray columns = value.getJSONArray("columns");
            if (columns.length() == 0) return 0;
            JSONArray rows = columns.getJSONObject(0).getJSONArray("row");
            return rows.length();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getColumns() {
        try {
            JSONObject value = new JSONObject(this.value);
            JSONArray columns = value.getJSONArray("columns");
            return columns.length();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    static public TableLookupData getFromJSON(JSONObject json) {
        try {
            String _id = json.getString("_id");
            String name = json.getString("name");
            String type = json.getString("type");
            String value = json.getJSONObject("value").toString();
            return new TableLookupData(_id, name, type, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
