package rules.tests;

import junit.framework.TestCase;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import rules.data.Data;
import rules.entities.Rule;
import rules.managers.processing.Processor;
import rules.tests.helpers.DataManagerDummy;
import rules.tests.helpers.RuleOne;
import rules.tests.helpers.RuleTwo;
import rules.types.RuleTypes;

/**
 * Created by christopher on 07.07.16.
 */

public class RuleTest extends TestCase {

    protected void setUp(){
        System.out.println("[TESTING] Rule Processing from JSON");
    }

    public void testRule1True() {

        String r = RuleOne.r;

        try {
            JSONObject json = new JSONObject(r);
            Rule rule = Rule.fromJSON(json);
            Calendar date = new GregorianCalendar();
            date.set(Calendar.HOUR_OF_DAY, 0);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);
            long midnight = date.getTimeInMillis();
            final long yesterdayMidnight = midnight - 24*60*60*1000;
            final int N = 100;
            final long timeStep = (midnight - yesterdayMidnight-24*60*60)/N;
            int maxSteps = 2999;
            final int stepStep = maxSteps/N;

            DataManagerDummy.DataArrayFn fn = new DataManagerDummy.DataArrayFn() {
                public Data[] get(RuleTypes.DataKeys key, Date from, Date to) {
                    Data[] rtn = new Data[N];
                    long start = yesterdayMidnight;
                    for(int i=0; i<N; i++) {
                        rtn[i] = new Data(new Date(start), new Date(start+timeStep), "{ steps: "+ stepStep +"}");
                        start = start + timeStep;
                    }
                    return rtn;
                }
            };

            DataManagerDummy dataDummy = new DataManagerDummy(fn,null);
            Processor p = new Processor(rule, null, dataDummy);
            boolean result = p.processRule();
            assertEquals(result, true);

        } catch (Exception e) {
            assertNotNull(null);
        }

    }

    public void testRule1False() {
        String r = RuleOne.r;

        try {
            JSONObject json = new JSONObject(r);
            Rule rule = Rule.fromJSON(json);
            Calendar date = new GregorianCalendar();
            date.set(Calendar.HOUR_OF_DAY, 0);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);
            long midnight = date.getTimeInMillis();
            final long yesterdayMidnight = midnight - 24*60*60*1000;
            final int N = 100;
            final long timeStep = (midnight - yesterdayMidnight-24*60*60)/N;
            int maxSteps = 4000;
            final int stepStep = maxSteps/N;

            DataManagerDummy.DataArrayFn fn = new DataManagerDummy.DataArrayFn() {
                public Data[] get(RuleTypes.DataKeys key, Date from, Date to) {
                    Data[] rtn = new Data[N];
                    long start = yesterdayMidnight;
                    for(int i=0; i<N; i++) {
                        rtn[i] = new Data(new Date(start), new Date(start+timeStep), "{ steps: "+ stepStep +"}");
                        start = start + timeStep;
                    }
                    return rtn;
                }
            };

            DataManagerDummy dataDummy = new DataManagerDummy(fn,null);
            Processor p = new Processor(rule, null, dataDummy);
            boolean result = p.processRule();
            assertEquals(result, false);

        } catch (Exception e) {
            assertNotNull(null);
        }

    }

    public void testRuleTwo() {

        try {
            Rule r = Rule.fromJSON(new JSONObject(RuleTwo.r));

            DataManagerDummy.DataArrayFn fn = new DataManagerDummy.DataArrayFn() {
                public Data[] get(RuleTypes.DataKeys key, Date from, Date to) {
                    Data[] rtn = new Data[1];
                    rtn[0] = new Data(new Date(), new Date(), 1);
                    return rtn;
                }
            };

            DataManagerDummy.LookupTableFn ltfn = new DataManagerDummy.LookupTableFn() {
                public Object get(String id, int row, int column) {
                    return String.valueOf(row) + ":" + String.valueOf(column);
                }
            };

            DataManagerDummy dataDummy = new DataManagerDummy(fn,null);
            dataDummy.lookupTableFn = ltfn;

            Processor p = new Processor(r, null, dataDummy);
            boolean result = p.processRule();
            assertEquals(result, true);

        } catch (Exception e) {
            assertNotNull(null);
        }


    }






}

