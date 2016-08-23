package rules.tests;

import junit.framework.TestCase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rules.data.Data;
import rules.helpers.ConditionEvaluator;
import rules.helpers.Helpers;
import rules.types.RuleTypes;

/**
 * Created by christopher on 04.07.16.
 */

public class GeneralTests extends TestCase {

    protected void setUp(){
        System.out.println("[TESTING] General Methods");
    }

    public void testObjectFromPath() {

        // Should return the correct value
        try {
            String jsonString = "{ a: { b: { c: { d: 5 } } } }";
            JSONObject json = new JSONObject(jsonString);
            Object res = Helpers.getObjectFromPath(json, "a.b.c.d");
            assertNotNull(res);
            assertEquals((int) res, 5);
        } catch (Exception e) {
            System.out.print(e);
            assertEquals(true, false);
        }

        // Should throw an exception
        try {
            String jsonString = "{ a: { b: { c: { d: 5 } } } }";
            JSONObject json = new JSONObject(jsonString);
            Object res = Helpers.getObjectFromPath(json, "a.d");
            assertEquals(true, false);
        } catch (Exception e) {
            assertEquals(true, true);
        }

        // Should return the correct value
        try {
            String jsonString = "{ a: 'muh' }";
            JSONObject json = new JSONObject(jsonString);
            Object res = Helpers.getObjectFromPath(json, "a");
            assertNotNull(res);
            assertEquals((String) res, "muh");
        } catch (Exception e) {
            System.out.print(e);
            assertEquals(true, false);
        }

    }

    public void testArrayReduceOperations() {

        ArrayList<Object> strArr = new ArrayList<>();
        ArrayList<Object> intArr = new ArrayList<>();
        ArrayList<Object> doubleArr = new ArrayList<>();

        strArr.add("a");
        strArr.add("b");
        strArr.add("c");
        strArr.add("d");

        intArr.add(1);
        intArr.add(2);
        intArr.add(3);
        intArr.add(4);

        doubleArr.add(0.1);
        doubleArr.add(0.2);
        doubleArr.add(0.3);
        doubleArr.add(0.4);

        assertEquals((String) RuleTypes.ArrayReduce.FIRST.reduce(strArr), "a");
        assertEquals((String) RuleTypes.ArrayReduce.LAST.reduce(strArr), "d");
        assertEquals(RuleTypes.ArrayReduce.MAX.reduce(strArr), null);
        assertEquals(RuleTypes.ArrayReduce.MAX.reduce(intArr), 4);
        assertEquals(RuleTypes.ArrayReduce.MIN.reduce(intArr), 1);
        assertEquals(RuleTypes.ArrayReduce.AVERAGE.reduce(intArr), 2.5);
        assertEquals(RuleTypes.ArrayReduce.SUM.reduce(intArr), 10);
        assertEquals(RuleTypes.ArrayReduce.MEDIAN.reduce(intArr), 2.5);

        assertEquals(RuleTypes.ArrayReduce.MAX.reduce(doubleArr), 0.4);
        assertEquals(RuleTypes.ArrayReduce.MIN.reduce(doubleArr), 0.1);
        assertEquals(RuleTypes.ArrayReduce.AVERAGE.reduce(doubleArr), 0.25);
        assertEquals(RuleTypes.ArrayReduce.SUM.reduce(doubleArr), 1.0);
        assertEquals(RuleTypes.ArrayReduce.MEDIAN.reduce(doubleArr), 0.25);

    }


    public void testPredicate() {

        ArrayList<Data> dataObjects = new ArrayList<>();
        dataObjects.add(new Data(new Date(0), new Date(100) , 1));
        dataObjects.add(new Data(new Date(100), new Date(200) , 2));
        dataObjects.add(new Data(new Date(300), new Date(400) , 3));
        dataObjects.add(new Data(new Date(500), new Date(600) , 4));
        Helpers.Predicate<Data> validData;

        final Date from1 = new Date(0);
        final Date to1 = new Date(600);

        validData = new Helpers.Predicate<Data>() {
            public boolean apply(Data data) {
                return data.from.after(from1) && data.to.before(to1);
            }
        };

        assertEquals(Helpers.filter(dataObjects, validData).size(), 2);

        final Date from2 = new Date(0);
        final Date to2 = new Date(800);

        validData = new Helpers.Predicate<Data>() {
            public boolean apply(Data data) {
                return data.from.after(from2) && data.to.before(to2);
            }
        };

        assertEquals(Helpers.filter(dataObjects, validData).size(), 3);

    }

    public void testConditionEvaluator() {

        ArrayList<Object> values = new ArrayList<>();
        values.add(1);
        values.add(1);

        try {
            assertEquals(RuleTypes.Comparator.EQUAL.evaluate(values), true);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.UNEQUAL.evaluate(values), false);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.GREATER_THAN.evaluate(values), false);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.GREATER_THAN_EQUAL.evaluate(values), true);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.LOWER_THAN.evaluate(values), false);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.LOWER_THAN_EQUAL.evaluate(values), true);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.NOT.evaluate(values), false);
        } catch (Exception e) { assertNotNull(null); }

        values.clear();
        values.add(1.0);
        values.add(1.0);

        try {
            assertEquals(RuleTypes.Comparator.EQUAL.evaluate(values), true);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.UNEQUAL.evaluate(values), false);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.GREATER_THAN.evaluate(values), false);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.GREATER_THAN_EQUAL.evaluate(values), true);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.LOWER_THAN.evaluate(values), false);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.LOWER_THAN_EQUAL.evaluate(values), true);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.NOT.evaluate(values), false);
        } catch (Exception e) { assertNotNull(null); }

        values.clear();
        values.add(1);
        values.add(1.0);

        try {
            assertEquals(RuleTypes.Comparator.EQUAL.evaluate(values), true);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.UNEQUAL.evaluate(values), false);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.GREATER_THAN.evaluate(values), false);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.GREATER_THAN_EQUAL.evaluate(values), true);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.LOWER_THAN.evaluate(values), false);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.LOWER_THAN_EQUAL.evaluate(values), true);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.NOT.evaluate(values), false);
        } catch (Exception e) { assertNotNull(null); }

        values.clear();
        values.add(7);
        values.add(1.0);

        try {
            assertEquals(RuleTypes.Comparator.EQUAL.evaluate(values), false);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.UNEQUAL.evaluate(values), true);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.GREATER_THAN.evaluate(values), true);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.GREATER_THAN_EQUAL.evaluate(values), true);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.LOWER_THAN.evaluate(values), false);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.LOWER_THAN_EQUAL.evaluate(values), false);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.NOT.evaluate(values), false);
        } catch (Exception e) { assertNotNull(null); }

        values.clear();
        values.add("a");
        values.add("a");

        try {
            assertEquals(RuleTypes.Comparator.EQUAL.evaluate(values), true);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.UNEQUAL.evaluate(values), false);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertNull(RuleTypes.Comparator.GREATER_THAN.evaluate(values));
        } catch (Exception e) { assertNull(null); }

        values.clear();
        values.add("a");
        values.add("b");

        try {
            assertEquals(RuleTypes.Comparator.EQUAL.evaluate(values), false);
        } catch (Exception e) { assertNotNull(null); }

        try {
            assertEquals(RuleTypes.Comparator.UNEQUAL.evaluate(values), true);
        } catch (Exception e) { assertNotNull(null); }
    }
}
