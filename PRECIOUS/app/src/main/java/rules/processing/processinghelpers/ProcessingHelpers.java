package rules.processing.processinghelpers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import rules.data.Data;
import rules.entities.conditions.ConditionDataOption;
import rules.helpers.Helpers;
import rules.processing.ConditionParameterProcessor;
import rules.types.RuleTypes;

/**
 * Created by christopher on 10.08.16.
 */

public class ProcessingHelpers {
    public static Object reduceWithPath(Object value, String path) throws Exception {

        // check for null
        if (value == null) {
            throw new Exception("dataOption was used with a path, but the data's value was null");
        }

        if (value instanceof String) {
            JSONObject json = new JSONObject((String) value);
            value = Helpers.getObjectFromPath(json, path);
        } else if (value instanceof JSONObject) {
            value = Helpers.getObjectFromPath((JSONObject) value, path);
        } else {
            throw new Exception("Object was used with a dataOption option, but is not a string");
        }

        return value;
    }

    public static Object reduceWithReducerAndDataAndDataObject(Object[] values, RuleTypes.ArrayReduce reducer, ConditionDataOption option) throws Exception {

        if (option.getPath() == null && option.getArrayReduce() == null) {
            throw new Exception("Either path or reducer must be set for a valid dataoption object");
        }

        if (option.getPath() != null) {
            values = ProcessingHelpers.reduceWithPath(values, option.getPath());
        }

        if (option.getArrayReduce() != null) {
            values = ProcessingHelpers.reduceEachElement(values, option.getArrayReduce());
        }

        // we use first as default array reduce
        if (reducer == null) {
            reducer = RuleTypes.ArrayReduce.FIRST;
        }

        return ProcessingHelpers.reduceWithReducer(values, reducer);
    }

    public static Object reduceWithReducerAndDataAndDataObject(ArrayList<Data> _values, RuleTypes.ArrayReduce reducer, ConditionDataOption option) throws Exception {
        Object[] values = new Object[_values.size()];
        int idx=0;
        for(Data d: _values) { values[idx++]=d.value; };
        return ProcessingHelpers.reduceWithReducerAndDataAndDataObject(values, reducer, option);
    }

    public static Object reduceWithReducerAndData(ArrayList<Data> _values, RuleTypes.ArrayReduce reducer) throws Exception {
        ArrayList<Object> values = new ArrayList<Object>();
        for(Data d: _values) { values.add(d.value); }
        return ProcessingHelpers.reduceWithReducer(values, reducer);
    }

    public static Object[] reduceWithPath(Object[] data, String path) throws Exception {
        Object[] newData = new Object[data.length];
        int i = 0;
        for (Object d: data) {
            newData[i++] = ProcessingHelpers.reduceWithPath(d, path);
        }
        return newData;
    }

    public static Object reduceWithReducer(ArrayList<Object> values, RuleTypes.ArrayReduce reducer) throws Exception {
        if (values.size() == 1) {
            return values.get(0);
        }
        if (reducer == null) {
            reducer = RuleTypes.ArrayReduce.FIRST;
        }
        return reducer.reduce(values);
    }

    public static Object reduceWithReducer(Object[] values, RuleTypes.ArrayReduce reducer) throws Exception {
        if (values.length == 1) {
            return values[0];
        }
        return  ProcessingHelpers.reduceWithReducer(new ArrayList<Object>(java.util.Arrays.asList(values)), reducer);
    }

    public static Object[] reduceEachElement(Object[] values, RuleTypes.ArrayReduce reducer) throws Exception {

        Object[] reduced = new Object[values.length];

        int idx=0;
        for (Object o : values) {
            if (o instanceof String) {
                JSONArray arr = new JSONArray(o);
                ArrayList<Object> objs = new ArrayList<>();
                for(int i=0; i<arr.length(); i++) {
                    objs.add(arr.get(i));
                }
                reduced[idx] = ProcessingHelpers.reduceWithReducer(objs, reducer);
            } else if (o instanceof JSONArray) {
                JSONArray arr = (JSONArray) o;
                ArrayList<Object> objs = new ArrayList<>();
                for(int i=0; i<arr.length(); i++) {
                    objs.add(arr.get(i));
                }
                reduced[idx] = ProcessingHelpers.reduceWithReducer(objs, reducer);
            } else {
                throw new Exception("Object cannot be reduced as it is not a string");
            }
            idx++;
        }

        return reduced;
    }
}
