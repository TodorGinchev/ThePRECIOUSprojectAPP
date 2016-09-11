package precious_rule_system.rules.your.implementations.data.application_data;
import android.provider.CalendarContract;

import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.Calendar;

import precious_rule_system.precoiusinterface.PreciousApplicationData;

import rules.data.Data;
import rules.helpers.Helpers;
import rules.types.RuleTypes;

/**
 * Created by khatt on 8/19/2016.
 */
public class ApplicationData {

    private static String TAG = "Application Data";

    public static Data[] getApplicationData(RuleTypes.DataKeys dataType) {
        ArrayList<Integer> steps_array;
        ArrayList<Integer> goals_array;
        long from, to;
        Calendar c;

        switch (dataType) {
            //return the step count for today
            case STEPS_TODAY:
                 steps_array = PreciousApplicationData.getSteps(System.currentTimeMillis(),System.currentTimeMillis());
                return Helpers.wrapData(steps_array.get(0));
            //return 0 if goal not set for today, 1 otherwise
            case DAILY_GOAL_TODAY_SET:
                goals_array = PreciousApplicationData.getGoals(System.currentTimeMillis(),System.currentTimeMillis());
                if (goals_array.get(0) < 0)
                    return Helpers.wrapData(0);
                return Helpers.wrapData(1);

            //return the daily goal value if set, return 0 otherwise
            case DAILY_GOAL_TODAY:
                goals_array = PreciousApplicationData.getGoals(System.currentTimeMillis(),System.currentTimeMillis());
                if (goals_array.get(0) < 0)
                    return Helpers.wrapData(0);
                return Helpers.wrapData(goals_array.get(0));

            case DAILY_GOAL_TODAY_PERCENTAGE:
                steps_array = PreciousApplicationData.getSteps(System.currentTimeMillis(),System.currentTimeMillis());
                goals_array = PreciousApplicationData.getGoals(System.currentTimeMillis(),System.currentTimeMillis());
                if (goals_array.get(0) > 0 ) {
                    int goal_today_percentage = (int) Math.floor(steps_array.get(0) / goals_array.get(0) * 100);
                    return Helpers.wrapData(goal_today_percentage);
                }
                return Helpers.wrapData(0);

                                // ! These keys are not used for UH-Trial 1

            //return the step count for yesterday
            //Would it return step count from the start of yesterday since the from field is now - 24 hours ?
            case STEPS_YESTERDAY:
                //Get timestamp for yesterday
                from = System.currentTimeMillis()-24*3600*1000;
                //Get timestamp for the end of day of yesterday
                c = Calendar.getInstance();
                c.setTimeInMillis(from);
                c.set(Calendar.HOUR_OF_DAY,23);
                c.set(Calendar.MINUTE,59);
                c.set(Calendar.SECOND,59);
                to = c.getTimeInMillis();
                steps_array = PreciousApplicationData.getSteps(from,to);
                return Helpers.wrapData(steps_array.get(1));


            case DAILY_GOAL_YESTERDAY_SET:
                from = System.currentTimeMillis()-24*3600*1000;
                //Get timestamp for the end of day of yesterday
                c = Calendar.getInstance();
                c.setTimeInMillis(from);
                c.set(Calendar.HOUR_OF_DAY,23);
                c.set(Calendar.MINUTE,59);
                c.set(Calendar.SECOND,59);
                to = c.getTimeInMillis();
                steps_array = PreciousApplicationData.getSteps(from, to);
                goals_array = PreciousApplicationData.getGoals(from, to);
                if (goals_array.get(1) > 0 ) {
                    int goal_yesterday_percentage = (int) Math.floor(steps_array.get(1) / goals_array.get(1) * 100);
                    return Helpers.wrapData(goal_yesterday_percentage);
                }
                return Helpers.wrapData(0);

            case TIME_SINCE_REGISTRATION_DAYS:
                return Helpers.wrapData(PreciousApplicationData.getDaysSinceRegistation());

            case APPLICATION_NOT_OPENED_SINCE_HOURS:
                return Helpers.wrapData(PreciousApplicationData.getAppNotOpenedSince());
            case SUGGESTED_APP:
                return Helpers.wrapData(PreciousApplicationData.getSuggestedApps());
            case CONSECUTIVE_PA_GOALS_ACHIEVED:
                return Helpers.wrapData(PreciousApplicationData.getConsecutivePAGoalsAchieved());
            case TOTAL_PA_GOALS_ACHIEVED:
                return Helpers.wrapData(PreciousApplicationData.getTotalPAGoalsAchieved());
            case CURRENT_OUTCOME_GOAL:
                return Helpers.wrapData(PreciousApplicationData.getOutcomeGoal());
            case OUTCOME_GOAL_SET:
                return Helpers.wrapData(PreciousApplicationData.IsOutcomeGoalSet());
            default:
                break;
        }
        return null;
    }
}
