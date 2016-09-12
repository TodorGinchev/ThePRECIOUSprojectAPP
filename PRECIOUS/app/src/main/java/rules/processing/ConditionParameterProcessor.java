package rules.processing;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import rules.data.Data;
import rules.entities.Rule;
import rules.entities.conditions.ConditionDataOption;
import rules.entities.conditions.ConditionHistoricRequirement;
import rules.entities.conditions.ConditionLookup;
import rules.entities.conditions.ConditionParameter;
import rules.entities.conditions.lookup.ConditionLookupBounds;
import rules.entities.conditions.lookup.ConditionLookupTable;
import rules.helpers.Helpers;
import rules.helpers.Tuple;
import rules.managers.data.DataManagerInterface;
import rules.processing.processinghelpers.ProcessingHelpers;
import rules.types.RuleTypes;

/**
 * Created by christopher on 10.08.16.
 */

public class ConditionParameterProcessor {

    public static Object processConditionParameter(Rule rule, ConditionParameter parameter, DataManagerInterface manager) throws Exception {
        if (parameter.isUserDefined()) {
            return ConditionParameterProcessor.processUserDefinedValue(parameter, manager);
        } else if (parameter.isDataDefined()) {
            return ConditionParameterProcessor.processDataDefinedValue(rule, parameter, manager);
        } else if (parameter.isLookupDefined()) {
            return ConditionParameterProcessor.processLookupDefinedValue(rule, parameter, manager);
        }
        throw new Exception("Invalid definedBy Field detected");
    }

    public static Object processUserDefinedValue(ConditionParameter parameter, DataManagerInterface manager) throws Exception{
        return parameter.getUserDefinedValue();
    }

    public static Object processDataDefinedValue(Rule rule, ConditionParameter parameter, DataManagerInterface manager) throws Exception {

        // get properties
        RuleTypes.DataKeys key = parameter.getDataKey();
        ConditionDataOption dataOption = parameter.getDataOption();
        ConditionHistoricRequirement historicRequirements = parameter.getHistoricRequirements();
        RuleTypes.ArrayReduce arrayReduce = parameter.getArrayReduce();

        // gather historic requirements, default is null (i.e. static data)
        Date from = null;
        Date to = null;

        if (historicRequirements != null) {
            Tuple<Date,Date> dateTuple = historicRequirements.getDates();
            from = dateTuple.x;
            to = dateTuple.y;
        }

        // gather the data from the data manager
        Data[] _data = manager.getData(key, from, to, rule);

        // we don't really need the dates, get values only
        Object[] data = new Object[_data.length];
        int idx = 0;
        for(Data d : _data) { data[idx++]=d.value; }

        Object result = null;

        // check whether we need to reduce individual items themselves
        if (dataOption !=  null) {
            result = ProcessingHelpers.reduceWithReducerAndDataAndDataObject(data, arrayReduce, dataOption);
        } else {
            result = ProcessingHelpers.reduceWithReducer(data, arrayReduce);
        }

        return result;
    }

    public static Object processLookupDefinedValue(Rule rule, ConditionParameter parameter, DataManagerInterface manager) throws Exception {

        ConditionLookup lookup = parameter.getConditionLookup();
        ConditionLookup.LookupType type = lookup.getType();

        if (type == ConditionLookup.LookupType.COLUMN_BOUNDS || type == ConditionLookup.LookupType.ROW_BOUNDS) {
            return ConditionParameterProcessor.processLookupTableBounds(rule, (ConditionLookupBounds) lookup.getParameters(), manager);
        } else if (type == ConditionLookup.LookupType.TABLE) {
            return ConditionParameterProcessor.processLookupTable(rule, (ConditionLookupTable) lookup.getParameters(), manager);
        }

        throw new Exception("Unhandled Lookup Type detected");
    }

    public static Object processLookupTableBounds(Rule rule, ConditionLookupBounds bounds, DataManagerInterface manager) throws Exception {

        String tableId = bounds.getTableId();
        boolean isRow = bounds.getDimension().equals("row");
        boolean isColumn = bounds.getDimension().equals("column");

        if (isRow) {
            return manager.getLookupTableRowBounds(tableId);
        } else if (isColumn) {
            return manager.getLookupTableColumnBounds(tableId);
        }

        throw new Exception("Invalid Table-Bound dimension detected");

    }

    public static Object processLookupTable(Rule rule, ConditionLookupTable table, DataManagerInterface manager) throws Exception {

        // get parameters
        String tableId = table.getTableId();
        ConditionParameter row = table.getRow();
        ConditionParameter column = table.getColumn();

        // convert parameters
        Object orow = ConditionParameterProcessor.processConditionParameter(rule, row, manager);
        Object ocolumn = ConditionParameterProcessor.processConditionParameter(rule, column, manager);

        // convert to integers (have to be)
        Integer irow = Helpers.getInt(orow);
        Integer icolumn = Helpers.getInt(ocolumn);

        // check
        if (irow == null || icolumn == null) {
            throw new Exception("tablelookup failed: one of the parameters is not an integer");
        }

        Object result = manager.getLookupTableDataWithId(tableId, irow, icolumn);

        if (result == null) {
            throw new Exception("tablelookup failed: no value found in table");
        }

        // get the data
        return result;
    }


}
