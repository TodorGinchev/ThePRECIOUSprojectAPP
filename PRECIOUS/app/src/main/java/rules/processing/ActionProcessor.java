package rules.processing;

import java.util.ArrayList;

import rules.entities.Rule;
import rules.entities.actions.Action;
import rules.entities.actions.ActionParameter;
import rules.entities.conditions.ConditionParameter;
import rules.managers.data.DataManagerInterface;
import rules.types.RuleTypes;

/**
 * Created by christopher on 10.08.16.
 */

public class ActionProcessor {

    /**
     * Transforms actions into actions definedBy user, i.e. gathers data for all actions
     * that have the definedBy field set to 'parameter'
     * @param actions - list of actions to transform
     * @param manager - datamanager object to retreive infos
     * @return
     * @throws Exception
     */
    public static Action[] transformActions(Rule rule, Action[] actions, DataManagerInterface manager) throws Exception {

        Action[] transFormedActions = new Action[actions.length];
        int idx = 0;
        for (Action a : actions) {
            Action newAction = new Action(a.getKey(), a.getPriority());
            for (ActionParameter p : a.getParameters()) {
                if (p.isUserDefined()) {
                    ActionParameter newParameter = new ActionParameter(p.getKey(), transformParameterValue(rule, p.getValue(), manager));
                    newAction.addParameter(newParameter);
                } else if (p.isParameterDefined()){
                    ConditionParameter conditionParameter = p.getValueAsConditionParameter();
                    Object value = ConditionParameterProcessor.processConditionParameter(rule, conditionParameter, manager);
                    ActionParameter newParameter = new ActionParameter(p.getKey(), transformParameterValue(rule, value, manager));
                    newAction.addParameter(newParameter);
                } else {
                    throw new Exception("Invalid Action parameter type detected");
                }
            }
            transFormedActions[idx++] = newAction;
        }
        return transFormedActions;
    }


    /**
     *
     * @param _value
     * @return
     * @throws Exception
     */
    public static Object transformParameterValue(Rule rule, Object _value, DataManagerInterface manager) throws Exception {

        if (_value instanceof String) {

            String value = (String) _value;
            if (!value.contains("$")) { return value; }

            ArrayList<RuleTypes.DataKeys> foundKeys = new ArrayList<>();

            final char[] chars = value.toCharArray();
            for(int i = 0; i<chars.length; i++) {
                if (chars[i] == '$') {

                    if (i>0) {
                        if (chars[i-1] == '\\') {
                            continue;
                        }
                    } else if (i==chars.length-1) {
                        continue;
                    }

                    String currentSubString = "$";

                    // we found a $ sign, iterate the remaining char array until we find either a dollar
                    // sign or a valid datakey
                    for(int j=i+1; j<chars.length; j++) {
                        if (chars[j] == '$') { break; }
                        currentSubString = currentSubString + chars[j];
                        RuleTypes.DataKeys key = RuleTypes.DataKeys.fromString(currentSubString);
                        if (key != null) {
                            if (!foundKeys.contains(key)) {
                                foundKeys.add(key);
                            }
                            i = j+1;
                            break;
                        }
                    }

                }
            }

            for (RuleTypes.DataKeys k : foundKeys) {

                ConditionParameter p = new ConditionParameter(k, null, null, null);
                Object transformedValue = ConditionParameterProcessor.processConditionParameter(rule, p, manager);

                if (transformedValue == null) {
                    throw new Exception("ActionParameter dataKey "+k.toString()+" returned invalid data");
                }

                value = value.replace(k.toString(), String.valueOf(transformedValue));
            }

            return value;
        } else {
            return _value;
        }

    }

}
