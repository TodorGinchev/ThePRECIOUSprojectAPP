package rules.types;

import java.lang.reflect.Array;
import java.util.ArrayList;

import rules.helpers.ArrayReduceOperations;
import rules.helpers.ConditionEvaluator;

/**
 * A simple container for all types of final variables and enums
 * declared as static member variables here
 * Created by christopher on 07.06.16.
 */
public class RuleTypes {

    /**
     * A general interface to Keys (i.e. Data and Triggerkeys), as
     * Triggerkeys generally extend Datakeys
     */
    public interface Key {
        public boolean equalsName(String otherName);
        public String toString();
    }

    /**
     * Datakeys, i.e. keys that are unique regarding a certain type
     * of stored data
     */
    public static enum DataKeys implements Key {

        /**
         * Physical Activity related Keys
         */

        // returns the gps coordinates of the user, i.e. a JSON Object { lat: latitude, lng: longitude }
        GPS_COORDINATES ("$gps_coordinates"),
        // returns the number of steps of the user, i.e. 3000, time-frame is specified by the respective query
        USER_STEPS ("$user_steps"),

        /**
         * UH-Specific Data Keys
         */

        // gets the current intervention state of the participant, i.e. ranging 1-5
        UH_INTERVENTION_STATE ("$uh_intervention_state"),
        // gets the user's participant id
        UH_PARTICIPANT_ID ("$uh_participant_id"),
        // returns the start date of the trial (timestamp ms)
        UH_TRIAL_START_DATE ("$uh_trial_start_date"),
        // returns the end date of the trial (timestamp ms)
        UH_TRIAL_END_DATE ("$uh_trial_end_date"),

        /**
         * General Things
         */

        REGISTRATION_DATE ("$registration_date"),
        // returns the time since the registration in hours, i.e. 3.24
        TIME_SINCE_REGISTRATION_HOURS ("$time_since_registration_hours"),
        // returns the time since the registration in days, i.e. 10.18
        TIME_SINCE_REGISTRATION_DAYS ("$time_since_registration_days"),
        // returns the current date (timestamp ms)
        CURRENT_DATE("$date"),
        // returns the id of the user
        USER_ID ("$user_id"),

        /**
         * Things set by the user
         */
        CURRENT_OUTCOME_GOAL ("$current_outcome_goal"),
        OUTCOME_GOAL_SET ("$outcome_goal_set"),
        GOAL_SET_FOR_TODAY ("$goal_set_for_today"),
        DAILY_GOAL_STEPS ("$daily_goal_steps"),


        // Derived
        DAILY_GOAL_PERCENTAGE ("$daily_goal_percentage"),
        DAILY_GOAL_COMPARISON_YESTERDAY ("$daily_goal_comparison_yesterday"),
        CURRENT_TRIAL_DAY ("$current_trial_day"),
        APPLICATION_NOT_OPENED_SINCE_HOURS ("$application_not_opened_since"),
        SUGGESTED_APP ("$suggested_app"),
        CONSECUTIVE_PA_GOALS_ACHIEVED ("$consecutive_pa_goals_achieved"),
        TOTAL_PA_GOALS_ACHIEVED ("$total_pa_goals_achieved"),
        STEPS_TODAY ("$steps_today"),
        STEPS_YESTERDAY ("$steps_yesterday"),

        //test keys
        TEST_PARTICIPANT_ID("$test_participant_id"),
        TEST_INTERVENTION_STATE("$test_intervention_state"),
        TEST_HOURS("$test_hours"),
        TEST_DAYS("$test_day"),

        // Rule-system
        RULE_LAST_RUN_HOURS ("$rule_last_run_hours"),
        RULE_LAST_RUN_SECONDS ("$rule_last_run_seconds");



        private final String name;

        private DataKeys(String s) {
            name = s;
        }

        public static DataKeys fromString(String text) {
            text = text.toLowerCase();
            if (text != null) {
                for (DataKeys b : DataKeys.values()) {
                    if (text.equalsIgnoreCase(b.name)) {
                        return b;
                    }
                }
            }
            return null;
        }
        public boolean equalsName(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }
        public String toString() {
            return this.name;
        }
    }

    /**
     * Triggerkeys, i.e. keys that can trigger a rule, based on
     * certain events
     */
    public static enum TriggerKeys implements Key {

        /**
         * App-related things
         */
        APP_OPENED ("$app_opened"),
        APP_CLOSED ("$app_closed"),
        APP_WAS_UPDATED ("$app_updated"),

        /**
         * PA-related things
         */
        GOAL_COMPLETED ("$goal_completed"),
        TRIAL_STARTED ("$trial_started"),

        /**
         * UH-Specific, "placeholders" for actions
         */
        UH_INTERVENTION_1 ("$uh_intervention_1"),
        UH_INTERVENTION_2 ("$uh_intervention_2"),
        UH_INTERVENTION_3 ("$uh_intervention_3"),
        UH_INTERVENTION_4 ("$uh_intervention_4"),
        UH_INTERVENTION_5 ("$uh_intervention_5"),

        /**
         * Test States for testing
         */
        TEST_INTERVENTION_1 ("$test_intervention_1"),
        TEST_INTERVENTION_2("$test_intervention_2"),
        TEST_INTERVENTION_3("$test_intervention_3"),
        TEST_INTERVENTION_4("$test_intervention_4"),
        TEST_INTERVENTION_5("$test_intervention_5"),
        /**
         * Time based keys
         */

        // a shortcut for all time-related events
        TIME_ALL ("$time_all"),

        // specific times
        TIME_00 ("$time_00"),
        TIME_01 ("$time_01"),
        TIME_02 ("$time_02"),
        TIME_03 ("$time_03"),
        TIME_04 ("$time_04"),
        TIME_05 ("$time_05"),
        TIME_06 ("$time_06"),
        TIME_07 ("$time_07"),
        TIME_08 ("$time_08"),
        TIME_09 ("$time_09"),
        TIME_10 ("$time_10"),
        TIME_11 ("$time_11"),
        TIME_12 ("$time_12"),
        TIME_13 ("$time_13"),
        TIME_14 ("$time_14"),
        TIME_15 ("$time_15"),
        TIME_16 ("$time_16"),
        TIME_17 ("$time_17"),
        TIME_18 ("$time_18"),
        TIME_19 ("$time_19"),
        TIME_20 ("$time_20"),
        TIME_21 ("$time_21"),
        TIME_22 ("$time_22"),
        TIME_23 ("$time_23"),
        TIME_24 ("$time_24"),


        //Test times for testing rules

        // specific times
        TEST_TIME_01 ("$time_01"),
        TEST_TIME_02 ("$time_02"),
        TEST_TIME_03 ("$time_03"),
        TEST_TIME_04 ("$time_04"),
        TEST_TIME_05 ("$time_05"),
        TEST_TIME_06 ("$time_06"),
        TEST_TIME_07 ("$time_07"),
        TEST_TIME_08 ("$time_08"),
        TEST_TIME_09 ("$time_09"),
        TEST_TIME_10 ("$time_10"),
        TEST_TIME_11 ("$time_11"),
        TEST_TIME_12 ("$time_12"),
        TEST_TIME_13 ("$time_13"),
        TEST_TIME_14 ("$time_14"),
        TEST_TIME_15 ("$time_15"),
        TEST_TIME_16 ("$time_16"),
        TEST_TIME_17 ("$time_17"),
        TEST_TIME_18 ("$time_18"),
        TEST_TIME_19 ("$time_19"),
        TEST_TIME_20 ("$time_20"),
        TEST_TIME_21 ("$time_21"),
        TEST_TIME_22 ("$time_22"),
        TEST_TIME_23 ("$time_23"),
        TEST_TIME_24 ("$time_24");



        private final String name;

        private TriggerKeys(String s) {
            name = s;
        }

        public static TriggerKeys fromString(String text) {
            text = text.toLowerCase();
            if (text != null) {
                for (TriggerKeys b : TriggerKeys.values()) {
                    if (text.equalsIgnoreCase(b.name)) {
                        return b;
                    }
                }
            }
            return null;
        }

        public boolean equalsName(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    /**
     * Actionkeys, i.e. keys that trigger a certain action within the app
     */
    public static enum ActionKeys {

        SHOW_NOTIFICATION ("$show_notification"),
        ENABLE_APPLICATION ("$enable_application"),
        DISABLE_APPLICATION ("$disable_application"),
        OPEN_APPLICATION ("$open_application"),
        CLOSE_APPLICATION ("$close_application"),
        SET_UH_INTERVENTION_STATE ("$set_uh_intervention_state"),
        CALL_TRIGGER ("$call_trigger"),
        SET_SUGGESTED_APP ("$set_suggested_app"),
        OPEN_APP_STREAM ("$open_app_stream"),
        REWARD_POINTS ("$reward_points"),
        SET_TEST_INTERVENTION("$set_test_intervention_state");

        private final String name;

        private ActionKeys(String s) {
            name = s;
        }

        public static ActionKeys fromString(String text) {
            text = text.toLowerCase();
            if (text != null) {
                for (ActionKeys b : ActionKeys.values()) {
                    if (text.equalsIgnoreCase(b.name)) {
                        return b;
                    }
                }
            }
            return null;
        }

        public boolean equalsName(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    /**
     * Arrayreduce keys, i.e. ways to reduce an array of values
     */
    public static enum ArrayReduce {

        SUM ("$sum"),
        AVERAGE ("$average"),
        MEDIAN  ("$median"),
        MIN ("$min"),
        MAX ("$max"),
        LAST ("$last"),
        FIRST ("$first");

        public Object reduce(ArrayList<Object> data) {

            if(data.size() == 0) return null;

            if(this.equalsName(ArrayReduce.FIRST.toString())) {
                return data.get(0);
            } else if (this.equalsName(ArrayReduce.LAST.toString())) {
                return data.get(data.size()-1);
            } else {
                if (this.equalsName(ArrayReduce.SUM.toString())) {
                    return ArrayReduceOperations.sum(data);
                } else if (this.equalsName(ArrayReduce.AVERAGE.toString())) {
                    return ArrayReduceOperations.average(data);
                } else if (this.equalsName(ArrayReduce.MEDIAN.toString())) {
                    return ArrayReduceOperations.median(data);
                } else if (this.equalsName(ArrayReduce.MAX.toString())) {
                    return ArrayReduceOperations.max(data);
                } else if (this.equalsName(ArrayReduce.MIN.toString())) {
                    return ArrayReduceOperations.min(data);
                } else {
                    return null;
                }
            }
        }

        // Enum Methods for serialization etc.
        private final String name;
        private ArrayReduce(String s) {
            name = s;
        }
        public static ArrayReduce fromString(String text) {
            text = text.toLowerCase();
            if (text != null) {
                for (ArrayReduce b : ArrayReduce.values()) {
                    if (text.equalsIgnoreCase(b.name)) {
                        return b;
                    }
                }
            }
            return null;
        }
        public boolean equalsName(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }
        public String toString() {
            return this.name;
        }
    }

    /**
     * Comparators which are applied upon certain conditions
     */
    public static enum Comparator {

        EQUAL("$eq"),
        UNEQUAL ("$neq"),
        NOT ("$not"),
        GREATER_THAN ("$gt"),
        LOWER_THAN ("$lt"),
        GREATER_THAN_EQUAL ("$gte"),
        LOWER_THAN_EQUAL ("$lte");

        private final String name;
        private Comparator(String s) {
            name = s;
        }

        public boolean evaluate(ArrayList<Object> parameters) throws Exception {
            return ConditionEvaluator.evaluateCondition(this, parameters);
        }

        public static Comparator fromString(String text) {
            text = text.toLowerCase();
            if (text != null) {
                for (Comparator b : Comparator.values()) {
                    if (text.equalsIgnoreCase(b.name)) {
                        return b;
                    }
                }
            }
            return null;
        }

        public boolean equalsName(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    /**
     * Operations, i.e. either references to conditions, deeper nested operations
     * or logical operations
     */
    public static enum OperationType {

        OPERATION ("$operation"),
        REFERENCE ("$reference"),
        LOGICAL_AND ("$and"),
        LOGICAL_ANY ("$any"),
        LOGICAL_NAND ("$nand"),
        LOGICAL_OR ("$or"),
        LOGICAL_NOR ("$nor"),
        LOGICAL_NONE  ("$none");

        private final String name;

        private OperationType(String s) {
            name = s;
        }

        public static OperationType fromString(String text) {
            if (text != null) {
                for (OperationType b : OperationType.values()) {
                    if (text.equalsIgnoreCase(b.name)) {
                        return b;
                    }
                }
            }
            return null;
        }

        public boolean equalsName(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    /**
     * Shortcuts in forms of Dates that are later converted to "to" and "from" Dates
     * for data search upon execution of a rule
     */
    public static enum HistoricRequirements {

        TODAY ("$today"),
        YESTERDAY ("$yesterday"),
        THIS_WEEK ("$this_week"),
        LAST_WEEK ("$last_week"),
        THIS_MONTH ("$this_month"),
        LAST_MONTH ("$last_month"),
        T24_HRS ("$24_hrs"),
        T12_HRS ("$12_hrs"),
        T2_DAYS ("$2_days"),
        T3_DAYS ("$3_days"),
        T4_DAYS ("$4_days"),
        T5_DAYS ("$5_days"),
        T6_DAYS ("$6_days"),
        T7_DAYS ("$7_days");

        private final String name;

        private HistoricRequirements(String s) {
            name = s;
        }

        public static HistoricRequirements fromString(String text) {
            if (text != null) {
                for (HistoricRequirements b : HistoricRequirements.values()) {
                    if (text.equalsIgnoreCase(b.name)) {
                        return b;
                    }
                }
            }
            return null;
        }

        public boolean equalsName(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }
}
