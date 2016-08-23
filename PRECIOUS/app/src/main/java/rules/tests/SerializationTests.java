package rules.tests;

import junit.framework.TestCase;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import rules.entities.Rule;
import rules.entities.actions.Action;
import rules.entities.actions.ActionParameter;
import rules.entities.conditions.Condition;
import rules.entities.conditions.ConditionDataOption;
import rules.entities.conditions.ConditionHistoricRequirement;
import rules.entities.conditions.ConditionParameter;
import rules.entities.operation.Operation;
import rules.entities.trigger.Trigger;
import rules.helpers.Tuple;
import rules.tests.helpers.RuleTwo;
import rules.types.RuleTypes;

/**
 * Created by christopher on 04.07.16.
 */

public class SerializationTests extends TestCase {

    protected void setUp(){
        System.out.println("[TESTING] Serialization");
    }

    public void testActionParameterSerialization() {

        // Should get the values
        try {
            String jsonStr = "{ definedBy: 'user', key: 'param1', value: 7 }";
            JSONObject json = new JSONObject(jsonStr);
            ActionParameter p = ActionParameter.fromJSON(json);
            assertEquals(p.getKey(), "param1");
            assertEquals((int) p.getValue(), 7);
        } catch (Exception e) {
            System.out.println(e);
            assertNotNull(null);
        }

        // Should throw when wrong parameters are given
        try {
            String jsonStr = "{ definedBy: 'user', muh: 'param1', value: '7' }";
            JSONObject json = new JSONObject(jsonStr);
            ActionParameter p = ActionParameter.fromJSON(json);
            assertNotNull(null);
        } catch (Exception e) {
            assertTrue(true);
        }

    }

    public void testActionSerialization() {

        // Should do correct serialization
        try {
            String jsonStr =
                    "{" +
                            " priority: 7," +
                            " parameters: [" +
                            "   { definedBy: 'user', key: 'param1', value: 7 }," +
                            "   { definedBy: 'user', key: 'param2', value: 'v' }," +
                            "],"+
                            "key: '$open_app_stream'" +
                            "}";
            JSONObject json = new JSONObject(jsonStr);
            Action a = Action.fromJSON(json);
            assertEquals(a.getKey().toString(), "$open_app_stream");
            assertEquals(a.getParameters().length, 2);

            ActionParameter p1 = a.getParameters()[0];
            ActionParameter p2 = a.getParameters()[1];

            assertEquals(p1.getValue(), 7);
            assertEquals(p2.getValue(), "v");
            assertEquals(p1.getKey(), "param1");
            assertEquals(p2.getKey(), "param2");

            assertEquals(a.getPriority(), 7);

        } catch (Exception e) {
            System.out.println(e);
            assertNotNull(null);
        }

        // Should throw error when unknown key is detected
        try {
            String jsonStr =
                    "{" +
                            "key: '$bullshit_key'" +
                            "}";
            JSONObject json = new JSONObject(jsonStr);
            Action a = Action.fromJSON(json);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        // Should throw error no key is detected
        try {
            String jsonStr =
                    "{}";
            JSONObject json = new JSONObject(jsonStr);
            Action a = Action.fromJSON(json);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        // Should have the default priority set if not specified
        try {
            String jsonStr =
                    "{" +
                            "key: '$open_app_stream'" +
                            "}";
            JSONObject json = new JSONObject(jsonStr);
            Action a = Action.fromJSON(json);
            assertEquals(Action.defaultPriority, a.getPriority());
        } catch (Exception e) {
            assertTrue(false);
        }



    }

    public void testConditionHistoricRequirement() {

        // Test valid date string
        try {
            String jsonStr =
                    "{" +
                            "from: '05.12.1987 08:00:00'," +
                            "to: '01.01.2016 09:00:00'" +
                     "}";
            JSONObject json = new JSONObject(jsonStr);
            ConditionHistoricRequirement a = ConditionHistoricRequirement.fromJSON(json);
            Tuple<Date,Date> dates = a.getDates();

            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();

            from.setTime(dates.x);
            to.setTime(dates.y);

            assertEquals(from.get(Calendar.DAY_OF_MONTH), 5);
            assertEquals(from.get(Calendar.MONTH), 11);
            assertEquals(from.get(Calendar.YEAR), 1987);

            assertEquals(to.get(Calendar.DAY_OF_MONTH), 1);
            assertEquals(to.get(Calendar.MONTH), 0);
            assertEquals(to.get(Calendar.YEAR), 2016);


        } catch (Exception e) {
            assertTrue(false);
        }

        // Test invalid date string
        try {
            String jsonStr =
                    "{" +
                            "from: '1987'," +
                            "to: '1987'" +
                            "}";
            JSONObject json = new JSONObject(jsonStr);
            ConditionHistoricRequirement a = ConditionHistoricRequirement.fromJSON(json);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        // Store now
        Calendar now = Calendar.getInstance();

        // Test TODAY
        try {
            ConditionHistoricRequirement a = ConditionHistoricRequirement.fromJSON(RuleTypes.HistoricRequirements.TODAY.toString());
            Tuple<Date,Date> dates = a.getDates();
            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            from.setTime(dates.x);
            to.setTime(dates.y);

            assertEquals(from.get(Calendar.DAY_OF_MONTH), now.get(Calendar.DAY_OF_MONTH));
            assertEquals(to.get(Calendar.DAY_OF_MONTH), now.get(Calendar.DAY_OF_MONTH));
            assertEquals(from.get(Calendar.HOUR_OF_DAY), 0);
            assertEquals(to.get(Calendar.HOUR_OF_DAY), now.get(Calendar.HOUR_OF_DAY));

        } catch (Exception e) {
            assertTrue(false);
        }

        // Test YESTERDAY
        try {
            ConditionHistoricRequirement a = ConditionHistoricRequirement.fromJSON(RuleTypes.HistoricRequirements.YESTERDAY.toString());
            Tuple<Date,Date> dates = a.getDates();
            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            from.setTime(dates.x);
            to.setTime(dates.y);

            assertEquals(from.get(Calendar.DAY_OF_MONTH), now.get(Calendar.DAY_OF_MONTH)-1);
            assertEquals(to.get(Calendar.DAY_OF_MONTH), now.get(Calendar.DAY_OF_MONTH));
            assertEquals(from.get(Calendar.HOUR_OF_DAY), 0);
            assertEquals(to.get(Calendar.HOUR_OF_DAY), 0);

        } catch (Exception e) {
            assertTrue(false);
        }

        // TODO: Test other date shortcuts

    }

    public void testConditionDataOption() {

        // Should return correct values
        try {
            String jsonStr =
                    "{" +
                            "path: 'a.b.c'," +
                            "arrayReduce: '$sum'" +
                     "}";
            JSONObject json = new JSONObject(jsonStr);
            ConditionDataOption a = ConditionDataOption.fromJSON(json);
            assertEquals(a.getPath(), "a.b.c");
            assertEquals(a.getArrayReduce().toString(), RuleTypes.ArrayReduce.SUM.toString());
        } catch (Exception e) {
            assertTrue(false);
        }

        // Should return null
        try {
            String jsonStr =
                    "{}";
            JSONObject json = new JSONObject(jsonStr);
            ConditionDataOption a = ConditionDataOption.fromJSON(json);
            assertEquals(a, null);
        } catch (Exception e) {
            assertTrue(false);
        }

        // Should return path only
        try {
            String jsonStr =
                    "{" +
                            "path: 'a.b.c'" +
                            "}";
            JSONObject json = new JSONObject(jsonStr);
            ConditionDataOption a = ConditionDataOption.fromJSON(json);
            assertEquals(a.getPath(), "a.b.c");
            assertEquals(a.getArrayReduce(), null);
        } catch (Exception e) {
            assertTrue(false);
        }

        // Should return arrayreduce only
        try {
            String jsonStr =
                    "{" +
                            "arrayReduce: '"+RuleTypes.ArrayReduce.AVERAGE+"'" +
                            "}";
            JSONObject json = new JSONObject(jsonStr);
            ConditionDataOption a = ConditionDataOption.fromJSON(json);
            assertEquals(a.getPath(), null);
            assertEquals(a.getArrayReduce().toString(), RuleTypes.ArrayReduce.AVERAGE.toString());
        } catch (Exception e) {
            assertTrue(false);
        }

    }

    public void testConditionParameterSerialization() {

        // Store now
        Calendar now = Calendar.getInstance();

        // Should return string userdefined value
        try {
            String jsonStr =
                    "{" +
                            "definedBy: 'user'," +
                            "userDefinedValue: 'abc'"  +
                     "}";
            JSONObject json = new JSONObject(jsonStr);
            ConditionParameter a = ConditionParameter.fromJSON(json);
            assertEquals(a.isUserDefined(), (Boolean) true);
            assertEquals(a.getUserDefinedValue() instanceof String, true);
            assertEquals((String) a.getUserDefinedValue(), "abc");
        } catch (Exception e) {
            assertTrue(false);
        }

        // Should return int userdefined value
        try {
            String jsonStr =
                    "{" +
                            "definedBy: 'user'," +
                            "userDefinedValue: 7"  +
                            "}";
            JSONObject json = new JSONObject(jsonStr);
            ConditionParameter a = ConditionParameter.fromJSON(json);
            assertEquals(a.isUserDefined(), (Boolean) true);
            assertEquals(a.getUserDefinedValue() instanceof Integer, true);
            assertEquals((int) a.getUserDefinedValue(), 7);
        } catch (Exception e) {
            assertTrue(false);
        }

        // Should return double userdefined value
        try {
            String jsonStr =
                    "{" +
                            "definedBy: 'user'," +
                            "userDefinedValue: 7.567"  +
                            "}";
            JSONObject json = new JSONObject(jsonStr);
            ConditionParameter a = ConditionParameter.fromJSON(json);
            assertEquals(a.isUserDefined(), (Boolean) true);
            assertEquals(a.getUserDefinedValue() instanceof Double, true);
            assertEquals((Double) a.getUserDefinedValue(), 7.567);
        } catch (Exception e) {
            assertTrue(false);
        }

        // Should return float userdefined value
        try {
            String jsonStr =
                    "{" +
                            "definedBy: 'user'," +
                            "userDefinedValue: 7.567f"  +
                            "}";
            JSONObject json = new JSONObject(jsonStr);
            ConditionParameter a = ConditionParameter.fromJSON(json);
            assertEquals(a.isUserDefined(), (Boolean) true);
            assertEquals(a.getUserDefinedValue() instanceof Double, true);
            assertEquals((Double) a.getUserDefinedValue(), 7.567);
        } catch (Exception e) {
            assertTrue(false);
        }

        // Should return correct parameter object
        try {
            String jsonStr =
                    "{" +
                            "definedBy: 'data'," +
                            "key: '$user_steps'," +
                            "historicRequirements: $today," +
                            "dataOption: { path: 'a.b.c', arrayReduce: '$sum' }," +
                            "arrayReduce: '$sum'" +
                            "}";
            JSONObject json = new JSONObject(jsonStr);
            ConditionParameter a = ConditionParameter.fromJSON(json);
            assertEquals(a.isUserDefined(), (Boolean) false);
            assertEquals(a.getUserDefinedValue(), null);
            assertEquals(a.getDataKey().toString(), RuleTypes.DataKeys.USER_STEPS.toString());

            ConditionHistoricRequirement req = a.getHistoricRequirements();
            assertNotNull(req);

            Tuple<Date,Date> dates = req.getDates();
            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            from.setTime(dates.x);
            to.setTime(dates.y);

            assertEquals(from.get(Calendar.DAY_OF_MONTH), now.get(Calendar.DAY_OF_MONTH));
            assertEquals(to.get(Calendar.DAY_OF_MONTH), now.get(Calendar.DAY_OF_MONTH));
            assertEquals(from.get(Calendar.HOUR_OF_DAY), 0);
            assertEquals(to.get(Calendar.HOUR_OF_DAY), now.get(Calendar.HOUR_OF_DAY));

            ConditionDataOption opt = a.getDataOption();
            assertNotNull(opt);

            assertEquals(opt.getPath(), "a.b.c");
            assertEquals(opt.getArrayReduce().toString(), RuleTypes.ArrayReduce.SUM.toString());

        } catch (Exception e) {
            assertTrue(false);
        }

    }

    public void testConditionSerialization() {

        // Should an error due to empty parameters
        try {
            String jsonStr =
                    "{" +
                            "name: 'a name'," +
                            "comparator: '"+RuleTypes.Comparator.EQUAL.toString()+"',"  +
                            "parameters: []"  +
                     "}";
            JSONObject json = new JSONObject(jsonStr);
            Condition a = Condition.fromJSON(json);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        // Should an error due to invalid comparator
        try {
            String jsonStr =
                    "{" +
                            "name: 'a name'," +
                            "comparator: 'muhmuh',"  +
                            "parameters: []"  +
                            "}";
            JSONObject json = new JSONObject(jsonStr);
            Condition a = Condition.fromJSON(json);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        // Should return a valid condition object
        try {

            String parameter =
                    "{" +
                            "definedBy: 'data'," +
                            "key: '$user_steps'," +
                            "historicRequirements: $today," +
                            "dataOption: { path: 'a.b.c', arrayReduce: '$sum' }," +
                            "arrayReduce: '$sum'" +
                     "}";

            String jsonStr =
                    "{" +
                            "name: 'a name'," +
                            "comparator: '$eq',"  +
                            "parameters: ["+parameter+"]"  +
                     "}";
            JSONObject json = new JSONObject(jsonStr);
            Condition a = Condition.fromJSON(json);

            assertEquals(a.getName(), "a name");
            assertEquals(a.getComparator().toString(), RuleTypes.Comparator.EQUAL.toString());
            assertEquals(a.getParameters().length, 1);
            assertEquals(a.getParameters()[0].isUserDefined(), (Boolean) false);

        } catch (Exception e) {
            assertTrue(false);
        }


    }


    public void testOperationSerialization() {

            String operation = "{ operation: { " +
                    "    type: '$any', " +
                    "    conditions: [ " +
                    "      { " +
                    "        value: { " +
                    "          conditions: [ " +
                    "            { " +
                    "              value: 0, " +
                    "              type: '$reference' " +
                    "            }, " +
                    "            { " +
                    "              value: { " +
                    "                conditions: [ " +
                    "                  { " +
                    "                    value: 0, " +
                    "                    type: '$reference' " +
                    "                  }, " +
                    "                  { " +
                    "                    value: { " +
                    "                      conditions: [ " +
                    "                        { " +
                    "                          value: 0, " +
                    "                          type: '$reference' " +
                    "                        } " +
                    "                      ], " +
                    "                      type: '$none' " +
                    "                    }, " +
                    "                    type: '$operation' " +
                    "                  } " +
                    "                ], " +
                    "                type: '$none' " +
                    "              }, " +
                    "              type: '$operation' " +
                    "            } " +
                    "          ], " +
                    "          type: '$any' " +
                    "        }, " +
                    "        type: '$operation' " +
                    "      } " +
                    "    ] " +
                    "} }";

        // return correct operation
        try {

            JSONObject json = new JSONObject(operation);
            Operation a = Operation.fromJSON(json.getJSONObject("operation"));
            assertEquals(a.getType().toString(), RuleTypes.OperationType.LOGICAL_ANY.toString());
            assertNull(a.getValue());
            assertEquals(a.getConditions().length, 1);

            Operation b = a.getConditions()[0];
            assertEquals(b.getType().toString(), RuleTypes.OperationType.OPERATION.toString());
            assertNull(b.getConditions());
            assertNotNull(b.getValue());
            assertEquals(b.getValue() instanceof Operation, true);

            Operation c = (Operation) b.getValue();
            assertEquals(c.getType().toString(), RuleTypes.OperationType.LOGICAL_ANY.toString());
            assertNull(c.getValue());
            assertNotNull(c.getConditions());
            assertEquals(c.getConditions().length, 2);

            Operation d = c.getConditions()[0];

            assertEquals(d.getType().toString(), RuleTypes.OperationType.REFERENCE.toString());
            assertEquals(d.getValue() instanceof Integer, true);
            assertEquals((int) d.getValue(), 0);

            Operation e = c.getConditions()[1];
            assertEquals(e.getType().toString(), RuleTypes.OperationType.OPERATION.toString());
            assertNotNull(e.getValue());
            assertEquals(e.getValue() instanceof Operation, true);

            Operation f = (Operation) e.getValue();
            assertEquals(f.getType().toString(), RuleTypes.OperationType.LOGICAL_NONE.toString());
            assertNotNull(f.getConditions());
            assertEquals(f.getConditions().length, 2);

            // Should be okay after this point, even though its further nested :)

        } catch (Exception e) {
            assertTrue(false);
        }



    }

    public void testTriggerSerialization() {

        // Should return error if wrong key
        try {
            String jsonStr = "{ key: 'asdadakjsd' }";
            JSONObject json = new JSONObject(jsonStr);
            Trigger p = Trigger.fromJSON(json);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        // Should return right datakey
        try {
            String jsonStr = "{ key: '"+RuleTypes.DataKeys.USER_STEPS.toString()+"' }";
            JSONObject json = new JSONObject(jsonStr);
            Trigger p = Trigger.fromJSON(json);
            assertNotNull(p);
            assertEquals(p.getKey().toString(), RuleTypes.DataKeys.USER_STEPS.toString());
        } catch (Exception e) {
            assertTrue(false);
        }

        // Should return right triggerkey
        try {
            String jsonStr = "{ key: '"+RuleTypes.TriggerKeys.APP_OPENED.toString()+"' }";
            JSONObject json = new JSONObject(jsonStr);
            Trigger p = Trigger.fromJSON(json);
            assertNotNull(p);
            assertEquals(p.getKey().toString(), RuleTypes.TriggerKeys.APP_OPENED.toString());
        } catch (Exception e) {
            assertTrue(false);
        }

    }

    public void testRuleSerialization1() {

        String parameter =
                "{" +
                        "definedBy: 'data'," +
                        "key: '$user_steps'," +
                        "historicRequirements: $today," +
                        "dataOption: { path: 'a.b.c', arrayReduce: '$sum' }," +
                        "arrayReduce: '$sum'" +
                        "}";

        String condition =
                "{" +
                        "name: 'a name'," +
                        "comparator: '$eq',"  +
                        "parameters: ["+parameter+"]"  +
                        "}";

        try {
            String jsonStr =
                    "{" +
                            "_id: '56f3cf298ab1a2ec0ec388f2'," +
                            "name: 'RULE'," +
                            "description: 'This is a description'," +
                            "created: '05.12.1987 08:00:00'," +
                            "triggers: [{ key: '$user_steps'}, {key: '$gps_coordinates'}]," +
                            "conditions: ["+condition+"]," +
                            "actions: [{ key: '$open_app_stream'}]," +
                            "operation: { type: '$any', conditions: [{ type: '$reference', value: 0 }]}," +
                            "priority: 7" +
                     "}";
            JSONObject json = new JSONObject(jsonStr);
            Rule a = Rule.fromJSON(json);
            assertNotNull(a);
            assertEquals(a.getRuleId(), "56f3cf298ab1a2ec0ec388f2");
            assertEquals(a.getName(), "RULE");
            assertEquals(a.getDescription(), "This is a description");
            assertEquals(a.getCreated().toString(), "Sat Dec 05 08:00:00 CET 1987");
            assertEquals(a.getTriggers().length, 2);
            assertEquals(a.getConditions().length, 1);
            assertEquals(a.getActions().length, 1);
            assertEquals(a.getOperation().getConditions().length, 1);
        } catch (Exception e) {
            assertTrue(false);
        }


    }

    public void testRuleSerialization2() {
        try {

            JSONObject json = new JSONObject(RuleTwo.r);
            Rule a = Rule.fromJSON(json);
            assertEquals(a.toJSON().toString(), json.toString());
            //assertTrue(a.toJSON().toString().equals(json.toString()));

        } catch (Exception e) {
            assertTrue(false);
        }
    }


}
