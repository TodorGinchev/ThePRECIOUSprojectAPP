package precious_rule_system.rules.your.implementations.data.application_data;
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
        switch (dataType) {
            case USER_STEPS:
                //TODO NOT CLEAR AT ALL WHAT THIS KEY MAKES REFERENCE TO
                return Helpers.wrapData(PreciousApplicationData.getSteps(System.currentTimeMillis(),System.currentTimeMillis()));
            case UH_PARTICIPANT_ID:
                return Helpers.wrapData(PreciousApplicationData.getUserID());
            case USER_ID:
                 return Helpers.wrapData(PreciousApplicationData.getUserID());
            case REGISTRATION_DATE:
                return Helpers.wrapData(PreciousApplicationData.getRegistrationDate());
            case CURRENT_OUTCOME_GOAL:
                return Helpers.wrapData(PreciousApplicationData.getOutcomeGoal());
            case OUTCOME_GOAL_SET:
                return Helpers.wrapData(PreciousApplicationData.IsOutcomeGoalSet());
            case GOAL_SET_FOR_TODAY:
                return Helpers.wrapData(PreciousApplicationData.IsDailyGoalSet());
            case DAILY_GOAL_STEPS:
                return Helpers.wrapData(PreciousApplicationData.getDailyGoalSteps());
            case APPLICATION_NOT_OPENED_SINCE_HOURS:
                return Helpers.wrapData(PreciousApplicationData.getAppNotOpenedSince());
            case SUGGESTED_APP:
                return Helpers.wrapData(PreciousApplicationData.getSuggestedApps());
            case CONSECUTIVE_PA_GOALS_ACHIEVED:
                return Helpers.wrapData(PreciousApplicationData.getConsecutivePAGoalsAchieved());
            case TOTAL_PA_GOALS_ACHIEVED:
                return Helpers.wrapData(PreciousApplicationData.getTotalPAGoalsAchieved());
            case STEPS_TODAY:
                //If you want the steps for today, just leave from=to=time now
                return Helpers.wrapData(PreciousApplicationData.getSteps(System.currentTimeMillis(),System.currentTimeMillis()));
            case STEPS_YESTERDAY:
                //Get timestamp for yesterday
                long from = System.currentTimeMillis()-24*3600*1000;
                //Get timestamp for the end of day of yesterday
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(from);
                c.set(Calendar.HOUR_OF_DAY,23);
                c.set(Calendar.MINUTE,59);
                c.set(Calendar.SECOND,59);
                long to = c.getTimeInMillis();
                return Helpers.wrapData(PreciousApplicationData.getSteps(from,to));
            default:
                break;
        }
        return null;
    }
}
