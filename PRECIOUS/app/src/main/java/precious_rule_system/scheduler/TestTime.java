package precious_rule_system.scheduler;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.Date;

import ui.precious.comnet.aalto.precious.PRECIOUS_APP;

/**
 * Created by khatt on 8/25/2016.
 */
public class TestTime {

        private static String TAG = "TEST TIME";
        private static String testPrefString = "TestSharedPrefs";
        private static SharedPreferences sharedPreferencesTest = PRECIOUS_APP.getAppContext().getSharedPreferences(testPrefString,0);
        private static SharedPreferences.Editor editor = sharedPreferencesTest.edit();

        //how many actual minutes == testTimeHours;
        private static int minInTestHour = 5;

        private TestTime() {
            editor.putLong("start", System.currentTimeMillis());
            editor.apply();
            Log.i(TAG, "Resetting Test Time");
        }

        public static void Reset() {
                   }

        public static int getDay() {
            long totalMinutes = getTotalMinutes();
            int totalTestHour = (int) (totalMinutes / minInTestHour);
            int testDay = totalTestHour / 24;
            return testDay;
        }

        public static int getHour() {
            long totalMinutes = getTotalMinutes();
            int totalTestHour = (int) (totalMinutes / minInTestHour);
            int testHourNow = totalTestHour % 24;
            return testHourNow;
        }

        private static long getTotalMinutes() {
            long startTime = sharedPreferencesTest.getLong("start",0);
            long totalSeconds = (System.currentTimeMillis() - startTime) / 1000;
            return totalSeconds / 60;
          }
}