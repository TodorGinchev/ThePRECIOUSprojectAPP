package precious_rule_system.scheduler;

import android.content.SharedPreferences;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

import ui.precious.comnet.aalto.precious.PRECIOUS_APP;

/**
 * Created by khatt on 8/25/2016.
 */
public class TestTime {

        private final String TAG = "TEST TIME";
        private final String testPrefString = "TestSharedPrefs";
        private final SharedPreferences sharedPreferencesTest = PRECIOUS_APP.getAppContext().getSharedPreferences(testPrefString,0);
        private final SharedPreferences.Editor editor = sharedPreferencesTest.edit();
        private static TestTime instance;
        //how many actual minutes == testTimeHours;
        private final int minInTestHour = 2;



        private TestTime() {
            Reset();
        }

        public static TestTime getInstance() {
            if (instance == null)
                instance = new TestTime();
            return instance;
        }

        public void Reset() {
            boolean reset = sharedPreferencesTest.getBoolean("TimeStarted",false);
            if (!reset) {
                editor.putBoolean("TimeStarted", true);
                editor.apply();
                editor.putLong("StartTime", System.currentTimeMillis());
                editor.apply();
                Log.i(TAG, "Resetting Test Time to " + DateFormat.getInstance().format(System.currentTimeMillis()).toString());
            }

            }

        public int getDay() {
            long totalMinutes = getTotalMinutes();
            int totalTestHour = (int) (totalMinutes / minInTestHour);
            int testDay = totalTestHour / 24;
            Log.i(TAG, "Current Day: " + testDay + " Current Hour: " + totalTestHour % 24);
            return testDay;
        }

        public int getHour() {
            long totalMinutes = getTotalMinutes();
            int totalTestHour = (int) (totalMinutes / minInTestHour);
            int testHourNow = totalTestHour % 24;
            return testHourNow;
        }

        private long getTotalMinutes() {
            long startTime = sharedPreferencesTest.getLong("StartTime",0);
            long totalSeconds = (System.currentTimeMillis() - startTime) / 1000;
            return totalSeconds / 60;
          }
}