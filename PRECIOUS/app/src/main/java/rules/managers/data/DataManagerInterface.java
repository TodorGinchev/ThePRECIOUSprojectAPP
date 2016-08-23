package rules.managers.data;

import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

import rules.data.Data;
import rules.entities.Rule;
import rules.types.RuleTypes;

/**
 * Created by christopher on 07.07.16.
 */

public interface DataManagerInterface {
     public Data[] getData(RuleTypes.DataKeys key, Date from, Date to, Rule rule) throws Exception;
     public Object getLookupTableDataWithId(String id, int row, int column) throws Exception;
}
