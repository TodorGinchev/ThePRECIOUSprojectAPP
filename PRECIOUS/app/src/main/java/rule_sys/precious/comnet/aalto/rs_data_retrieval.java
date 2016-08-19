package rule_sys.precious.comnet.aalto;


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


    public static int getStepsForToday(){
        int steps=0;

        return steps;
    }
//
//    /**
//     * @return
//     */
//    private void activityToStepsTranslator(long timestamp, Context context) {
//
//        int stepsAcumul = 0;
//
//        ArrayList<ArrayList<Long>> paManualData = sql_db.precious.comnet.aalto.DBHelper.getInstance(context).getManPA(
//            timestamp-1, timestamp + 24 * 3600 * 1000);
//
//            for (int i = 0; i < paManualData.size(); i++) {
//                stepsAcumul += (paManualData.get(i).get(4));
//            }
//
//        atUtils.getLog(ui.precious.comnet.aalto.precious.preicousApp.getAppContext());
//
//
//        //FOR WALK
//        int walk_duration = LogVectorWalk.get(day_to_show);
//        if (walk_duration > 0) {
//            ArrayList<Long> aux = new ArrayList<>();
//            aux.add(LogVectorDayResult.get(day_to_show));//timestamp
//            aux.add((long) 27 - 1);//type
//            aux.add((long) (1));//intensity
//            aux.add((long) (walk_duration / 60));//duration
//            aux.add((long) (walk_duration * 84 / 60));//steps
//            paManualData.add(0, aux);
//            stepsAcumul += walk_duration * 84 / 60;
//        }
//        //FOR RUN
//        int run_duration = LogVectorRun.get(day_to_show);
//        if (run_duration > 120) {
//            ArrayList<Long> aux = new ArrayList<>();
//            aux.add(LogVectorDayResult.get(day_to_show));//timestamp
//            aux.add((long) 38 - 1);//type
//            aux.add((long) (1));//intensity
//            aux.add((long) (run_duration / 60));//duration
//            aux.add((long) (run_duration * 222 / 60));//steps
//            paManualData.add(0, aux);
//            stepsAcumul += run_duration * 222 / 60;
//        }
//        //For CYCLING
//        int bicycle_duration = LogVectorBicycle.get(day_to_show);
//        if (bicycle_duration > 120) {
//            ArrayList<Long> aux = new ArrayList<>();
//            aux.add(LogVectorDayResult.get(day_to_show));//timestamp
//            aux.add((long) 36 - 1);//type
//            aux.add((long) (1));//intensity
//            aux.add((long) (bicycle_duration / 60));//duration
//            aux.add((long) (bicycle_duration * 170 / 60));//steps
//            paManualData.add(0, aux);
//            stepsAcumul += bicycle_duration * 170 / 60;
//        }
////        }
//    }





}
