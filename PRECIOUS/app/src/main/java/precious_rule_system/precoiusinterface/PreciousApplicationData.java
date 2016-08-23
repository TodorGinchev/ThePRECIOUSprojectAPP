package precious_rule_system.precoiusinterface;

import java.util.Date;

/**
 * Created by khatt on 8/19/2016.
 */
public class PreciousApplicationData {

    private static PreciousApplicationData ourInstance = new PreciousApplicationData();
    private static PreciousApplicationData getInstance() {return ourInstance; }

    private PreciousApplicationData() {}

    //returns the current step count
    public static int getSteps() {
        return 0;
    }
    //returns the userID
    public static int getUserID() {
        return 0;
    }
    //returns the date object set to registration date (could be same as installation date)
    public static Date getRegistrationDate() {
        return null;
    }

    //returns a string containing name of outcome goal that has been set
    public static String getOutcomeGoal() {
        return null;
    }

    // returns true or false
    public static boolean IsOutcomeGoalSet() {
        return false;
    }

    // returns true or false
    public static boolean IsDailyGoalSet() {
        return false;
    }

    //returns today's goal if set
    public static int getDailyGoalSteps() {
        return 0;
    }

    //returns the datetime since app hasn't been opened
    public static Date getAppNotOpenedSince() {
        return null;
    }

    //returns a string of suggested app name
    public static String getSuggestedApps() {
        return null;
    }

    //returns consecutive PA goals achieved
    public static int getConsecutivePAGoalsAchieved() {
        return 0;
    }

    //returns total Physical Activity goals acheived
    public static int getTotalPAGoalsAchieved() {
        return 0;
    }

    //returns steps for "daysAgo" .. 1 being yesterday
    public static int getStepsOld(int daysAgo) {
        return 0;
    }
}
