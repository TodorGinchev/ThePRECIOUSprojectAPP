package rules.tests;


import junit.framework.TestCase;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import rules.data.Data;
import rules.entities.Rule;
import rules.entities.actions.ActionParameter;
import rules.entities.conditions.Condition;
import rules.entities.conditions.ConditionDataOption;
import rules.entities.conditions.ConditionHistoricRequirement;
import rules.entities.conditions.ConditionParameter;
import rules.helpers.Tuple;
import rules.managers.processing.Processor;
import rules.processing.ActionProcessor;
import rules.processing.ConditionParameterProcessor;
import rules.processing.processinghelpers.ProcessingHelpers;
import rules.tests.helpers.DataManagerDummy;
import rules.types.RuleTypes;

/**
 * Created by christopher on 10.06.16.
 */
public class ProcessorTest extends TestCase {

    Rule rule;
    Processor processor;

    protected void setUp(){
        System.out.println("[TESTING] Processor");
        rule = new Rule();
        processor = new Processor(rule, null, null);
    }

    /*
    Old Test, remove

    public void testGetDateRange(){

        Date now = new Date();
        Date start = new Date();
        start.setTime(0);

        Date from = new Date();
        from.setTime(0);

        Date to = new Date(now.getTime());
        Set<ConditionHistoricRequirement> list = new HashSet<>();

        long range = to.getTime()-from.getTime();

        for(int i=0; i<100; i++) {
            Random r = new Random();
            long number = (long)(r.nextDouble()*range);
            from.setTime(number);
            to.setTime(to.getTime()-number/2);
            list.add(new ConditionHistoricRequirement(from, to));
        }

        from.setTime(0);
        list.add(new ConditionHistoricRequirement(from, new Date(now.getTime())));

        Tuple<Date,Date> t = processor.getDateRange(list);

        assertEquals(t.x.getTime(), start.getTime());
        assertEquals(t.y.getTime(), now.getTime());

    }

    public void testGetKeysAndRestructuredHistoricalData() {

        ConditionHistoricRequirement h1 = new ConditionHistoricRequirement(new Date(0), new Date(25*60*60*1000*1));
        ConditionHistoricRequirement h2 = new ConditionHistoricRequirement(new Date(25*60*60*1000*1), new Date(25*60*60*1000*2));
        ConditionHistoricRequirement h3 = new ConditionHistoricRequirement(new Date(25*60*60*1000*3), new Date(25*60*60*1000*4));
        ConditionHistoricRequirement h4 = new ConditionHistoricRequirement(RuleTypes.HistoricRequirements.TODAY);
        ConditionHistoricRequirement h5 = new ConditionHistoricRequirement(RuleTypes.HistoricRequirements.YESTERDAY);
        ConditionHistoricRequirement h6 = new ConditionHistoricRequirement(RuleTypes.HistoricRequirements.THIS_MONTH);
        ConditionHistoricRequirement h7 = new ConditionHistoricRequirement(RuleTypes.HistoricRequirements.LAST_WEEK);

        ConditionParameter p1 = new ConditionParameter("muh");
        ConditionParameter p2 = new ConditionParameter(8);
        ConditionParameter p3 = new ConditionParameter(RuleTypes.DataKeys.USER_STEPS, null, h1, null);
        ConditionParameter p4 = new ConditionParameter(RuleTypes.DataKeys.USER_STEPS, null, h2, null);
        ConditionParameter p5 = new ConditionParameter(RuleTypes.DataKeys.USER_STEPS, null, h3, null);
        ConditionParameter p6 = new ConditionParameter(RuleTypes.DataKeys.USER_STEPS, null, h4, null);
        ConditionParameter p7 = new ConditionParameter(RuleTypes.DataKeys.USER_STEPS, null, h5, null);
        ConditionParameter p8 = new ConditionParameter(RuleTypes.DataKeys.USER_STEPS, null, h6, null);
        ConditionParameter p9 = new ConditionParameter(RuleTypes.DataKeys.USER_STEPS, null, h7, null);

        ConditionParameter p10 = new ConditionParameter(RuleTypes.DataKeys.GPS_COORDINATES, null, h1, null);
        ConditionParameter p11 = new ConditionParameter(RuleTypes.DataKeys.GPS_COORDINATES, null, h2, null);
        ConditionParameter p12 = new ConditionParameter(RuleTypes.DataKeys.GPS_COORDINATES, null, h3, null);
        ConditionParameter p13 = new ConditionParameter(RuleTypes.DataKeys.GPS_COORDINATES, null, h4, null);
        ConditionParameter p14 = new ConditionParameter(RuleTypes.DataKeys.GPS_COORDINATES, null, h5, null);
        ConditionParameter p15 = new ConditionParameter(RuleTypes.DataKeys.GPS_COORDINATES, null, h6, null);
        ConditionParameter p16 = new ConditionParameter(RuleTypes.DataKeys.GPS_COORDINATES, null, h7, null);

        ConditionParameter[] params1 = {p1,p2};
        ConditionParameter[] params2 = {p1,p3};
        ConditionParameter[] params3 = {p3,p3};
        ConditionParameter[] params4 = {p3,p10};
        ConditionParameter[] params5 = {p6,p9};
        ConditionParameter[] params6 = {p6,p13};
        ConditionParameter[] params7 = {p1,p2,p3,p4,p5,p6,p7,p8,p9,p10,p11,p12,p14,p15,p16};
        ConditionParameter[] params8 = {p3};
        ConditionParameter[] params9 = {p6};

        Condition c1 = new Condition("c1", params1, RuleTypes.Comparator.EQUAL);
        Condition c2 = new Condition("c2", params2, RuleTypes.Comparator.EQUAL);
        Condition c3 = new Condition("c3", params3, RuleTypes.Comparator.EQUAL);
        Condition c4 = new Condition("c4", params4, RuleTypes.Comparator.EQUAL);
        Condition c5 = new Condition("c5", params5, RuleTypes.Comparator.EQUAL);
        Condition c6 = new Condition("c6", params6, RuleTypes.Comparator.EQUAL);
        Condition c7 = new Condition("c7", params7, RuleTypes.Comparator.EQUAL);

        // All Conditions
        Rule r1 = new Rule();
        r1.addCondition(c1);
        r1.addCondition(c2);
        r1.addCondition(c3);
        r1.addCondition(c4);
        r1.addCondition(c5);
        r1.addCondition(c6);
        r1.addCondition(c7);

        Processor pr1 = new Processor(r1, null, null);
        Map<RuleTypes.DataKeys, Set<ConditionHistoricRequirement>> res1 = pr1.getKeys();
        assertEquals(res1.size(),2);
        assertEquals(res1.get(RuleTypes.DataKeys.USER_STEPS).size(), 7);
        assertEquals(res1.get(RuleTypes.DataKeys.GPS_COORDINATES).size(), 7);

        // Make raw date and placeholder date multiple times
        Rule r2 = new Rule();
        for(int i=0; i<100; i++) {
            String name = "c" + i;
            r2.addCondition(new Condition(name, params8, RuleTypes.Comparator.EQUAL));
        }

        for(int i=0; i<100; i++) {
            String name = "c" + i;
            r2.addCondition(new Condition(name, params9, RuleTypes.Comparator.EQUAL));
        }

        Processor pr2 = new Processor(r2, null, null);
        Map<RuleTypes.DataKeys, Set<ConditionHistoricRequirement>> res2 = pr2.getKeys();

        assertEquals(res2.size(),1);
        assertEquals(res2.get(RuleTypes.DataKeys.USER_STEPS).size(), 2);

        // Test getRestructedHistoricalData
        Map<RuleTypes.DataKeys, Tuple<Date,Date>> k1 = pr1.getRestructuredHistoricalData(res1);
        Map<RuleTypes.DataKeys, Tuple<Date,Date>> k2 = pr2.getRestructuredHistoricalData(res2);

        assertEquals(k1.size(), 2);
        assertEquals(k2.size(), 1);

        Tuple<Date, Date> dates = k2.get(RuleTypes.DataKeys.USER_STEPS);
        // From should be zero in our case
        assertEquals(dates.x.getTime(), new Date(0).getTime());
        // And the "to" value should be now actually (including a delta because of execution time)
        assertEquals(dates.y.getTime(), new Date().getTime(), 1000);

    }

    public void testDataStructuring() {

        ConditionHistoricRequirement h1 = new ConditionHistoricRequirement(new Date(0), new Date(24*60*60*1000*1));
        ConditionHistoricRequirement h2 = new ConditionHistoricRequirement(new Date(24*60*60*1000*1), new Date(24*60*60*1000*2));
        ConditionHistoricRequirement h3 = new ConditionHistoricRequirement(new Date(24*60*60*1000*2), new Date(24*60*60*1000*3));
        ConditionHistoricRequirement h4 = new ConditionHistoricRequirement(new Date(24*60*60*1000*3), new Date(24*60*60*1000*4));

        ConditionParameter p1 = new ConditionParameter(RuleTypes.DataKeys.USER_STEPS, null, h1, null);
        ConditionParameter p2 = new ConditionParameter(RuleTypes.DataKeys.USER_STEPS, null, h2, null);
        ConditionParameter p3 = new ConditionParameter(RuleTypes.DataKeys.USER_STEPS, null, h3, null);
        ConditionParameter p4 = new ConditionParameter(RuleTypes.DataKeys.USER_STEPS, null, h4, null);

        ConditionParameter[] params1 = {p1};
        ConditionParameter[] params2 = {p2};
        ConditionParameter[] params3 = {p3};
        ConditionParameter[] params4 = {p4};

        Condition c1 = new Condition("c1", params1, RuleTypes.Comparator.EQUAL);
        Condition c2 = new Condition("c2", params2, RuleTypes.Comparator.EQUAL);
        Condition c3 = new Condition("c3", params3, RuleTypes.Comparator.EQUAL);
        Condition c4 = new Condition("c4", params4, RuleTypes.Comparator.EQUAL);

        Rule r1 = new Rule();
        r1.addCondition(c1);
        r1.addCondition(c2);
        r1.addCondition(c3);
        r1.addCondition(c4);

        final int N = 4;

        DataManagerDummy.DataArrayFn fn = new DataManagerDummy.DataArrayFn() {
            public Data[] get(RuleTypes.DataKeys key, Date from, Date to) {
                Data[] rtn = new Data[N];
                long start = from.getTime();
                long end = to.getTime();
                long step = (end-start)/(N);
                for(int i=0; i<N; i++) {
                    rtn[i] = new Data(new Date(start), new Date(start+step), i);
                    start = start + step;
                }
                return rtn;
            }
        };

        DataManagerDummy dataDummy = new DataManagerDummy(fn,null);

        Processor pr1 = new Processor(r1, null, dataDummy);
        Map<RuleTypes.DataKeys, Set<ConditionHistoricRequirement>> res1 = pr1.getKeys();
        Map<RuleTypes.DataKeys, Tuple<Date,Date>> k1 = pr1.getRestructuredHistoricalData(res1);
        Map<RuleTypes.DataKeys, Data[]> dataObjects = pr1.gatherData(k1);

        Map<Integer, Map<Integer,List<Data>>> structuredData = pr1.structureData(dataObjects);

        assertEquals(structuredData.size(), 4);
        assertEquals(structuredData.get(0).get(0).size(), 1);
        assertEquals(structuredData.get(1).get(0).size(), 1);
        assertEquals(structuredData.get(2).get(0).size(), 1);
        assertEquals(structuredData.get(3).get(0).size(), 1);

    }*/

    public void testActionTransform() {

        String k1 = RuleTypes.DataKeys.USER_ID.toString();
        String k2 = RuleTypes.DataKeys.CURRENT_DATE.toString();

        String v = "$Your $$$\\"+k1+" is " + k1 + ".";

        DataManagerDummy.DataArrayFn fn = new DataManagerDummy.DataArrayFn() {
            public Data[] get(RuleTypes.DataKeys key, Date from, Date to) {
                Data[] rtn = {new Data(null, null, 1)};
                return rtn;
            }
        };

        DataManagerDummy dataDummy = new DataManagerDummy(fn,null);

        try {
            String _v = (String) ActionProcessor.transformParameterValue(null, v, dataDummy);
            assertEquals(_v, "$Your $$$\\1 is 1.");
        } catch (Exception ex) {
            ex.printStackTrace();
            assertNotNull(null);
        }
    }

    public void testArrayReduceAndDataOption() {

        ArrayList<Data> d1 = new ArrayList<>();

        d1.add(new Data(null, null, 1));
        d1.add(new Data(null, null, 2));
        d1.add(new Data(null, null, 3));
        d1.add(new Data(null, null, 4));

        Processor p = new Processor(null, null, null);

        try {
            assertEquals(ProcessingHelpers.reduceWithReducerAndData(d1, RuleTypes.ArrayReduce.SUM), 10);
        } catch (Exception e) {
            assertNotNull(null);
        }

        try {
            assertEquals(ProcessingHelpers.reduceWithReducerAndData(d1, RuleTypes.ArrayReduce.AVERAGE), 2.5);
        } catch (Exception e) {
            assertNotNull(null);
        }

        d1.clear();
        d1.add(new Data(null, null, "{ a: { b: { c: 1} } }"));
        d1.add(new Data(null, null, "{ a: { b: { c: 2} } }"));
        d1.add(new Data(null, null, "{ a: { b: { c: 3} } }"));
        d1.add(new Data(null, null, "{ a: { b: { c: 4} } }"));

        ConditionDataOption dataOption = new ConditionDataOption("a.b.c", null);

        try {
            assertEquals(ProcessingHelpers.reduceWithReducerAndDataAndDataObject(d1, RuleTypes.ArrayReduce.SUM, dataOption), 10);
        } catch (Exception e) {
            assertNotNull(null);
        }

        try {
            assertEquals(ProcessingHelpers.reduceWithReducerAndDataAndDataObject(d1, RuleTypes.ArrayReduce.AVERAGE, dataOption), 2.5);
        } catch (Exception e) {
            assertNotNull(null);
        }

        d1.clear();
        d1.add(new Data(null, null, "{ a: { b: { c: [1,2,3,4]} } }")); // 10
        d1.add(new Data(null, null, "{ a: { b: { c: [4,5,6,7]} } }")); // 22
        d1.add(new Data(null, null, "{ a: { b: { c: [8,9,10,11]} } }")); // 38
        d1.add(new Data(null, null, "{ a: { b: { c: [12,13,14,15]} } }")); // 54

        dataOption = new ConditionDataOption("a.b.c", RuleTypes.ArrayReduce.SUM);

        try {
            assertEquals(ProcessingHelpers.reduceWithReducerAndDataAndDataObject(d1, RuleTypes.ArrayReduce.SUM, dataOption), 124);
        } catch (Exception e) {
            assertNotNull(null);
        }

        try {
            assertEquals(ProcessingHelpers.reduceWithReducerAndDataAndDataObject(d1, RuleTypes.ArrayReduce.AVERAGE, dataOption), 31.0);
        } catch (Exception e) {
            assertNotNull(null);
        }
    }

    /*public void testApplyFinalArrayReduceAndDataOptions() {

        ConditionHistoricRequirement h1 = new ConditionHistoricRequirement(new Date(0), new Date(24*60*60*1000*1));
        ConditionHistoricRequirement h2 = new ConditionHistoricRequirement(new Date(24*60*60*1000*1), new Date(24*60*60*1000*2));
        ConditionHistoricRequirement h3 = new ConditionHistoricRequirement(new Date(24*60*60*1000*2), new Date(24*60*60*1000*3));
        ConditionHistoricRequirement h4 = new ConditionHistoricRequirement(new Date(24*60*60*1000*3), new Date(24*60*60*1000*4));

        ConditionDataOption dataOption = new ConditionDataOption("a.b.c", RuleTypes.ArrayReduce.AVERAGE);

        ConditionParameter p1 = new ConditionParameter(RuleTypes.DataKeys.USER_STEPS, dataOption, h1, RuleTypes.ArrayReduce.SUM);
        ConditionParameter p2 = new ConditionParameter(RuleTypes.DataKeys.USER_STEPS, dataOption, h2, RuleTypes.ArrayReduce.SUM);
        ConditionParameter p3 = new ConditionParameter(RuleTypes.DataKeys.USER_STEPS, dataOption, h3, RuleTypes.ArrayReduce.SUM);
        ConditionParameter p4 = new ConditionParameter(RuleTypes.DataKeys.USER_STEPS, dataOption, h4, RuleTypes.ArrayReduce.SUM);

        ConditionParameter[] params1 = {p1};
        ConditionParameter[] params2 = {p2};
        ConditionParameter[] params3 = {p3};
        ConditionParameter[] params4 = {p4};

        Condition c1 = new Condition("c1", params1, RuleTypes.Comparator.EQUAL);
        Condition c2 = new Condition("c2", params2, RuleTypes.Comparator.EQUAL);
        Condition c3 = new Condition("c3", params3, RuleTypes.Comparator.EQUAL);
        Condition c4 = new Condition("c4", params4, RuleTypes.Comparator.EQUAL);

        Rule r1 = new Rule();
        r1.addCondition(c1);
        r1.addCondition(c2);
        r1.addCondition(c3);
        r1.addCondition(c4);

        final int N = 8;

        DataManagerDummy.DataArrayFn fn = new DataManagerDummy.DataArrayFn() {
            public Data[] get(RuleTypes.DataKeys key, Date from, Date to) {
                Data[] rtn = new Data[N];
                long start = from.getTime();
                long end = to.getTime();
                long step = (end-start)/(N);
                for(int i=0; i<N; i++) {
                    rtn[i] = new Data(new Date(start), new Date(start+step), "{ a: { b: { c: [1,2,3,4]} } }");
                    start = start + step;
                }
                return rtn;
            }
        };

        DataManagerDummy dataDummy = new DataManagerDummy(fn,null);
        Processor pr1 = new Processor(r1, null, dataDummy);

        Map<RuleTypes.DataKeys, Set<ConditionHistoricRequirement>> keys = pr1.getKeys();
        Map<RuleTypes.DataKeys, Tuple<Date,Date>> keysWithAggregatedDates = pr1.getRestructuredHistoricalData(keys);
        Map<RuleTypes.DataKeys, Data[]> dataObjects = pr1.gatherData(keysWithAggregatedDates);
        Map<Integer, Map<Integer,List<Data>>> structuredData = pr1.structureData(dataObjects);
        Map<Integer, ArrayList<Object>> finalConditions = pr1.getFinalConditionObjects();

        try {
            finalConditions = pr1.applyFinalArrayReduceAndDataOptions(structuredData, finalConditions);
        } catch (Exception e) {
            assertNotNull(null);
        }

        assertEquals(finalConditions.size(), 4);
        assertEquals(finalConditions.get(0).get(0), 5.0);
        assertEquals(finalConditions.get(1).get(0), 5.0);
        assertEquals(finalConditions.get(2).get(0), 5.0);
        assertEquals(finalConditions.get(3).get(0), 5.0);

    }*/

}
