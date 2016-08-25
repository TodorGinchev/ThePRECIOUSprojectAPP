package precious_rule_system.scheduler;

import android.content.SharedPreferences;

import java.util.Date;

import ui.precious.comnet.aalto.precious.PRECIOUS_APP;

/**
 * Created by khatt on 8/25/2016.
 */
public class TestTime {

        private static String testPrefString = "TestSharedPrefs";
        private static SharedPreferences sharedPreferencesTest = PRECIOUS_APP.getAppContext().getSharedPreferences(testPrefString,0);
        private static SharedPreferences.Editor editor = sharedPreferencesTest.edit();



        public static void Reset() {
            editor.putLong("start", System.currentTimeMillis());
        }

        public static int getDay() {
            return getHour() / 24;
        }

        public static int getHour() {
            long startTime = sharedPreferencesTest.getLong("start",0);
            long now = System.currentTimeMillis();
            long diffInMilli = now -startTime;
            long diffInMinutes = diffInMilli/(1000*60);

            int testHour = (int) (diffInMinutes / 5) % 24;
            return testHour;
        }
}