package rules.processing;

import rules.entities.conditions.Condition;
import rules.entities.operation.Operation;
import rules.types.RuleTypes;

/**
 * Created by christopher on 10.08.16.
 */

public class OperationProcessor {

    public static boolean evaluateOperation(Operation operation, boolean[] conditions) throws Exception {

        RuleTypes.OperationType type = operation.getType();
        Object value = operation.getValue();

        if(type == RuleTypes.OperationType.OPERATION) {
            Operation _value = (Operation) value;
            return OperationProcessor.evaluateOperation(_value, conditions);
        } else if (type == RuleTypes.OperationType.REFERENCE) {
            boolean _value = conditions[(int) value];
            return _value;
        } else {
            if (type == RuleTypes.OperationType.LOGICAL_AND) {
                boolean rtn = true;
                for (Operation c: operation.getConditions()) {
                    rtn = rtn && OperationProcessor.evaluateOperation(c, conditions);
                }
                return rtn;
            } else if (type == RuleTypes.OperationType.LOGICAL_ANY || type == RuleTypes.OperationType.LOGICAL_OR) {
                boolean rtn = false;
                for (Operation c: operation.getConditions()) {
                    rtn = rtn || OperationProcessor.evaluateOperation(c,conditions);
                }
                return rtn;
            } else if (type == RuleTypes.OperationType.LOGICAL_NONE || type == RuleTypes.OperationType.LOGICAL_NOR) {
                boolean rtn = true;
                for (Operation c: operation.getConditions()) {
                    rtn = rtn && !OperationProcessor.evaluateOperation(c,conditions);
                }
                return rtn;
            } else if (type == RuleTypes.OperationType.LOGICAL_NAND) {
                if (operation.getConditions().length < 2) {
                    throw new Exception("Invalid number of conditions for LOGICAL_NAND");
                }
                boolean rtn = !OperationProcessor.evaluateOperation(operation.getConditions()[0],conditions);
                for (int i = 1; i < operation.getConditions().length - 1; i++) {
                    rtn = rtn && OperationProcessor.evaluateOperation(operation.getConditions()[i],conditions);
                }
                return rtn;
            }
        }

        throw new Exception("Invalid Operation Type");
    }

}
