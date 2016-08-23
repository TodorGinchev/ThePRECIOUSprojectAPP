package rules.processing;

import rules.entities.Rule;
import rules.entities.conditions.Condition;
import rules.entities.operation.Operation;
import rules.managers.data.DataManagerInterface;

/**
 * Created by christopher on 10.08.16.
 */

public class RuleProcessor {

    public static boolean processRule(Rule rule, DataManagerInterface manager) throws Exception {

        Condition[] conditions = rule.getConditions();
        boolean[] conditionResults = new boolean[conditions.length];

        int idx = 0;
        for (Condition c: conditions) {
            conditionResults[idx++] = ConditionProcessor.processCondition(rule, c, manager);
        }

        Operation operation = rule.getOperation();
        return OperationProcessor.evaluateOperation(operation, conditionResults);
    }

}
