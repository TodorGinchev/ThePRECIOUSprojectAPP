package rules.processing;

import android.util.Log;

import java.util.ArrayList;

import rules.entities.Rule;
import rules.entities.conditions.Condition;
import rules.entities.conditions.ConditionParameter;
import rules.managers.data.DataManagerInterface;
import rules.types.RuleTypes;

/**
 * Created by christopher on 10.08.16.
 */

public class ConditionProcessor {

    private static final String TAG = "Rules.Conditions";

    public static boolean processCondition(Rule rule, Condition condition, DataManagerInterface manager) throws Exception {

        RuleTypes.Comparator comparator = condition.getComparator();
        ConditionParameter[] parameters = condition.getParameters();
        ArrayList<Object> results = new ArrayList<Object>();

        String resultString = "";
        for (ConditionParameter p : parameters) {
            Object result = ConditionParameterProcessor.processConditionParameter(rule, p,manager);
            results.add(result);
            resultString = resultString + String.valueOf(result) + ", ";
        }

        boolean result = comparator.evaluate(results);
        Log.d(TAG, comparator.toString() + ": " + resultString.substring(0,resultString.length()-2) + " -> " + result);

        return result;
    }
}
