package precious_rule_system.precoiusinterface;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import activity_tracker.precious.comnet.aalto.atUtils;

/**
 * Created by khatt on 8/19/2016.
 */
public class PreciousApplicationData {

    private static PreciousApplicationData ourInstance = new PreciousApplicationData();
    private static PreciousApplicationData getInstance() {return ourInstance; }

    private PreciousApplicationData() {}


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



    public static int getStepsForToday(){
        int steps=0;

        return steps;
    }

    /**
     * Get the number of steps (including walking/running/cycling + manually added) within a time period
     * REMEMBER! Physical activity information is a daily basis information. The data for every day is stored at the current day date, at 0h0min0s0ms.
     * @param from start timestamp
     * @param to stop timestamp
     * @return array list of the day within the from and to timestamps period, starting from the oldest to the newest timestamp/day
     *      //Example: Let from by 3rd of July 2016 and to be 5th of July 2016, you can call:
     *      ArrayList<Integer> steps = getSteps ();
     *      if(steps.size()==3){
     *          int steps_3jul2016 = steps.get(0);
     *          int steps_4jul2016 = steps.get(1);
     *          int steps_5jul2016 = steps.get(2);
     *      }
     */
    public static ArrayList<Integer> getSteps(long from, long to){
        //Create Calendar instance to control timestamp format
        Calendar c = Calendar.getInstance();
        //Make sure that from timestamp has the correct format
        c.setTimeInMillis(from);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        from = c.getTimeInMillis();

        //Create array list of steps
        ArrayList<Integer> steps = new ArrayList<>();
        while(to>from){
            boolean timestampIsFromToday = checkIfTimestampIsFromToday(from);
            if(!timestampIsFromToday){
                steps.add(stepsTranspatorAndRetriever(from));
            }
            else{
                steps.add(getStepsForToday());
            }

            //Jump to the next day
            from += 24*3600*1000;
        }
        return  steps;
    }

    /**
     * Checks if a timestamp belong to the current day
     * @param timestamp the timestamp to be checked
     * @return true if timestamp belong to the current day, false if not
     */
    public static boolean checkIfTimestampIsFromToday(long timestamp){
        //Create calendar instance for current time
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        //Create calendar instance from timestamp
        Calendar c2 = Calendar.getInstance();
        c2.setTimeInMillis(timestamp);
        //Return is timestamp is from current day
        return ( c.get(Calendar.YEAR)==c2.get(Calendar.YEAR) && c.get(Calendar.MONTH)==c2.get(Calendar.MONTH) && c.get(Calendar.DAY_OF_MONTH)==c2.get(Calendar.DAY_OF_MONTH));
    }


    /**
     * Receives a timestamp of a specific day and return the total number of steps
     * REMEMBER THAT THE TIMESTAMP SHOULD BE SET TO 0h0min0s0ms OR OTHERWISE THE FUNCTION WILL NOT FOUND THE DAY
     * @param timestamp timestamp of the desired day
     * @return -1 if the day was not found, otherwise will return the number of steps
     */
    private static int stepsTranspatorAndRetriever(long timestamp) {
        //Get application context
        Context context = ui.precious.comnet.aalto.precious.preicousApp.getAppContext();
        //Instantiate steps accumulator
        int stepsAcumul = 0;
        //Get manually entered physical activities
        ArrayList<ArrayList<Long>> paManualData = sql_db.precious.comnet.aalto.DBHelper.getInstance(context).getManPA(timestamp-1, timestamp + 24 * 3600 * 1000);
        //Translate physical activities to steps and sum the result
        for (int i = 0; i < paManualData.size(); i++) {
            stepsAcumul += (paManualData.get(i).get(4));
        }
        //Process automatically detected physical activity
        atUtils.getLog(context);
        //Get Log vectors
        Vector<Long> LogVectorDayResult = atUtils.getLogVectorDayResult();
        Vector<Integer> LogVectorWalk = atUtils.getLogVectorWalk();
        Vector<Integer> LogVectorBicycle = atUtils.getLogVectorBicycle();
        Vector<Integer> LogVectorRun = atUtils.getLogVectorRun();
        //Find position of the timestamp within the log vectors
        int day_to_show = -1;
        for(int i=0; i<LogVectorDayResult.size(); i++){
            if(timestamp==LogVectorDayResult.elementAt(i)) {
                day_to_show = i;
                break;
            }
        }
        if(day_to_show==-1)
            return -1;

        //FOR WALK
        int walk_duration = LogVectorWalk.get(day_to_show);
        if (walk_duration > 0) {
            ArrayList<Long> aux = new ArrayList<>();
            aux.add(LogVectorDayResult.get(day_to_show));//timestamp
            aux.add((long) 27 - 1);//type
            aux.add((long) (1));//intensity
            aux.add((long) (walk_duration / 60));//duration
            aux.add((long) (walk_duration * 84 / 60));//steps
            paManualData.add(0, aux);
            stepsAcumul += walk_duration * 84 / 60;
        }
        //FOR RUN
        int run_duration = LogVectorRun.get(day_to_show);
        if (run_duration > 120) {
            ArrayList<Long> aux = new ArrayList<>();
            aux.add(LogVectorDayResult.get(day_to_show));//timestamp
            aux.add((long) 38 - 1);//type
            aux.add((long) (1));//intensity
            aux.add((long) (run_duration / 60));//duration
            aux.add((long) (run_duration * 222 / 60));//steps
            paManualData.add(0, aux);
            stepsAcumul += run_duration * 222 / 60;
        }
        //For CYCLING
        int bicycle_duration = LogVectorBicycle.get(day_to_show);
        if (bicycle_duration > 120) {
            ArrayList<Long> aux = new ArrayList<>();
            aux.add(LogVectorDayResult.get(day_to_show));//timestamp
            aux.add((long) 36 - 1);//type
            aux.add((long) (1));//intensity
            aux.add((long) (bicycle_duration / 60));//duration
            aux.add((long) (bicycle_duration * 170 / 60));//steps
            paManualData.add(0, aux);
            stepsAcumul += bicycle_duration * 170 / 60;
        }
        return stepsAcumul;
    }


}
