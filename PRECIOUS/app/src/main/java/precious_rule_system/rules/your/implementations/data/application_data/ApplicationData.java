package precious_rule_system.rules.your.implementations.data.application_data;
import com.precious.christopher.precious_rule_system.precoiusinterface.PreciousApplicationData;

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
                return Helpers.wrapData(PreciousApplicationData.getSteps());
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
                return Helpers.wrapData(PreciousApplicationData.getSteps());
            case STEPS_YESTERDAY:
                return Helpers.wrapData(PreciousApplicationData.getStepsOld(1));
            default:
                break;
        }
        return null;
    }




}
