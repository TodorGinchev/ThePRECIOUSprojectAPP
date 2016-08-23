package rules.tests;

import junit.framework.TestCase;

import org.json.JSONObject;

import rules.entities.operation.Operation;
import rules.processing.OperationProcessor;
import rules.types.RuleTypes;

/**
 * OPERATION ("$operation"),
 REFERENCE ("$reference"),
 LOGICAL_AND ("$and"),
 LOGICAL_ANY ("$any"),
 LOGICAL_NAND ("$nand"),
 LOGICAL_OR ("$or"),
 LOGICAL_NOR ("$nor"),
 LOGICAL_NONE  ("$none");
 * Created by christopher on 05.07.16.
 */

public class OperationTest extends TestCase {

    protected void setUp(){
        System.out.println("[TESTING] Operations");
    }

    public void testComplexOperation() {

        String c1 = "{ type: '$reference', value: 0 }";
        String c2 = "{ type: '$reference', value: 1 }";
        String c3 = "{ type: '$reference', value: 2 }";
        String c4 = "{ type: '$reference', value: 3 }";

        String simpleAny = "{ type: '$any', conditions: ["+c1+","+c2+","+c3+","+c4+"]} ";
        String simpleAnd = "{ type: '$and', conditions: ["+c1+","+c2+","+c3+","+c4+"]} ";
        String simpleNand = "{ type: '$nand', conditions: ["+c1+","+c2+","+c3+","+c4+"]} ";
        String simpleOr = "{ type: '$or', conditions: ["+c1+","+c2+","+c3+","+c4+"]} ";
        String simpleNor = "{ type: '$nor', conditions: ["+c1+","+c2+","+c3+","+c4+"]} ";
        String simpleNone = "{ type: '$none', conditions: ["+c1+","+c2+","+c3+","+c4+"]} ";

        String operation =
                "{ type: '$nand', conditions: [" +
                        "{ type: '$operation', value: "+ simpleAnd +" }," +
                        "{ type: '$operation', value: "+ simpleOr +" }," +
                        simpleAny +
                "]}";

        try {
            boolean[] conditions = {false, true, false, false};
            Operation o = Operation.fromJSON(new JSONObject(simpleAny));
            assertEquals(OperationProcessor.evaluateOperation(o, conditions), true);
        } catch (Exception e) {
            assertNotNull(null);
        }

    }

    public void testSimpleOperations() {

        String c1 = "{ type: '$reference', value: 0 }";
        String c2 = "{ type: '$reference', value: 1 }";
        String c3 = "{ type: '$reference', value: 2 }";
        String c4 = "{ type: '$reference', value: 3 }";

        String simpleAny = "{ type: '$any', conditions: ["+c1+","+c2+","+c3+","+c4+"]} ";
        String simpleAnd = "{ type: '$and', conditions: ["+c1+","+c2+","+c3+","+c4+"]} ";
        String simpleNand = "{ type: '$nand', conditions: ["+c1+","+c2+","+c3+","+c4+"]} ";
        String simpleOr = "{ type: '$or', conditions: ["+c1+","+c2+","+c3+","+c4+"]} ";
        String simpleNor = "{ type: '$nor', conditions: ["+c1+","+c2+","+c3+","+c4+"]} ";
        String simpleNone = "{ type: '$none', conditions: ["+c1+","+c2+","+c3+","+c4+"]} ";

        try {
            boolean[] conditions = {false, true, false, false};
            Operation o = Operation.fromJSON(new JSONObject(simpleAny));
            assertEquals(OperationProcessor.evaluateOperation(o, conditions), true);
        } catch (Exception e) {
            assertNotNull(null);
        }

        try {
            boolean[] conditions = {false, false, false, false};
            Operation o = Operation.fromJSON(new JSONObject(simpleAny));
            assertEquals(OperationProcessor.evaluateOperation(o, conditions), false);
        } catch (Exception e) {
            assertNotNull(null);
        }

        try {
            boolean[] conditions = {true, true, false, true};
            Operation o = Operation.fromJSON(new JSONObject(simpleAnd));
            assertEquals(OperationProcessor.evaluateOperation(o, conditions), false);
        } catch (Exception e) {
            assertNotNull(null);
        }

        try {
            boolean[] conditions = {true, true, true, true};
            Operation o = Operation.fromJSON(new JSONObject(simpleAnd));
            assertEquals(OperationProcessor.evaluateOperation(o, conditions), true);
        } catch (Exception e) {
            assertNotNull(null);
        }

        try {
            boolean[] conditions = {false, true, true, true};
            Operation o = Operation.fromJSON(new JSONObject(simpleNand));
            assertEquals(OperationProcessor.evaluateOperation(o, conditions), true);
        } catch (Exception e) {
            assertNotNull(null);
        }

        try {
            boolean[] conditions = {true, true, true, true};
            Operation o = Operation.fromJSON(new JSONObject(simpleNand));
            assertEquals(OperationProcessor.evaluateOperation(o, conditions), false);
        } catch (Exception e) {
            assertNotNull(null);
        }

        try {
            boolean[] conditions = {false, false, false, false};
            Operation o = Operation.fromJSON(new JSONObject(simpleOr));
            assertEquals(OperationProcessor.evaluateOperation(o, conditions), false);
        } catch (Exception e) {
            assertNotNull(null);
        }

        try {
            boolean[] conditions = {false, true, false, false};
            Operation o = Operation.fromJSON(new JSONObject(simpleOr));
            assertEquals(OperationProcessor.evaluateOperation(o, conditions), true);
        } catch (Exception e) {
            assertNotNull(null);
        }

        try {
            boolean[] conditions = {false, false, false, false};
            Operation o = Operation.fromJSON(new JSONObject(simpleNor));
            assertEquals(OperationProcessor.evaluateOperation(o, conditions), true);
        } catch (Exception e) {
            assertNotNull(null);
        }

        try {
            boolean[] conditions = {false, true, true, false};
            Operation o = Operation.fromJSON(new JSONObject(simpleNor));
            assertEquals(OperationProcessor.evaluateOperation(o, conditions), false);
        } catch (Exception e) {
            assertNotNull(null);
        }

        try {
            boolean[] conditions = {false, true, true, false};
            Operation o = Operation.fromJSON(new JSONObject(simpleNone));
            assertEquals(OperationProcessor.evaluateOperation(o, conditions), false);
        } catch (Exception e) {
            assertNotNull(null);
        }

        try {
            boolean[] conditions = {false, false, false, false};
            Operation o = Operation.fromJSON(new JSONObject(simpleNone));
            assertEquals(OperationProcessor.evaluateOperation(o, conditions), true);
        } catch (Exception e) {
            assertNotNull(null);
        }

    }


}
