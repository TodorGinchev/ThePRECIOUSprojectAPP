package rules.tests.helpers;

import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

import rules.data.Data;
import rules.entities.Rule;
import rules.managers.data.DataManagerInterface;
import rules.types.RuleTypes;

/**
 * Created by christopher on 07.07.16.
 */

public class DataManagerDummy implements DataManagerInterface {

    public interface DataArrayFn { Data[] get(RuleTypes.DataKeys key, Date from, Date to); }
    public interface RulesArrayFn { Rule[] get(RuleTypes.Key key); }
    public interface LookupTableFn { Object get(String id, int row, int column); }

    private DataArrayFn dataFn;
    private RulesArrayFn rulesFn;
    public LookupTableFn lookupTableFn;

    public DataManagerDummy(DataArrayFn dataFn, RulesArrayFn rulesFn) {
        this.dataFn = dataFn;
        this.rulesFn = rulesFn;
    }

    public Data[] getData(RuleTypes.DataKeys key, Date from, Date to, Rule rule) {
        return this.dataFn.get(key, from, to);
    }
    public Rule[] getRulesWithTrigger(RuleTypes.Key key, Map<String, Object> parameters) {
        return this.rulesFn.get(key);
    }

    public void setRuleExecuted(Rule rule, Date date) {
        return;
    }

    public Object getLookupTableDataWithId(String id, int row, int column) {
        return this.lookupTableFn.get(id, row, column);
    }

    @Override
    public int getLookupTableRowBounds(String id) throws Exception {
        return 0;
    }

    @Override
    public int getLookupTableColumnBounds(String id) throws Exception {
        return 0;
    }
}