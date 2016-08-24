package rule_sys.precious.comnet.aalto;


import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import activity_tracker.precious.comnet.aalto.atUtils;

public class rs_data_retrieval {


//    //returns the current step count
//    public static int getSteps() {
//        return 0;
//    }
    //returns the userID
    public static int getUserID() {
        return 0;
    }
    //returns the date object set to registration date (could be same as installation date)
    public static long getRegistrationDate() {
        return 0;
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
    public static long getAppNotOpenedSince() {
        return 0;
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
