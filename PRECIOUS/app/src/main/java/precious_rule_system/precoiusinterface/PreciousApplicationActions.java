package precious_rule_system.precoiusinterface;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

import ui.precious.comnet.aalto.precious.PRECIOUS_APP;


/**
 * Created by khatt on 8/19/2016.
 */

public class PreciousApplicationActions {
    public static final String PREFS_NAME = "SubAppPreferences";
    public static final String UI_PREFS_NAME = "UIPreferences";
    public static String TAG = "PreciousAppActions";
    public static final boolean DEFAULT_STATE = false; //if true, the subapps will be visible by default, until enableDisableSubapp is called to change it. If false, the subapp will be hidder by default.
    private static PreciousApplicationActions ourInstance = new PreciousApplicationActions();
    public static PreciousApplicationActions getInstance() {
        return ourInstance;
    }

    private PreciousApplicationActions() {}

    /**
     *                 OG - Outcome Goal Setting
     *                 DC - Dietary Challenge
     *                 IR - Importance Ruler
     *                 SM - Self monitoring / mountain view
     *                 WR - PRECIOUS wearable / Xiaomi band pairing
     *                 FA - Favourite Activity
     *                 MD - My Food Diary
     *                 UP - Firstbeat uploader, BodyGuard
     *                 PA_SOC -Physical Activity State of Change
     *                 TM - Time machine / Mental rehearsal... something
     *                 CR - Confidence ruler
     * @return true if subapp found, false means that subappID is not recognized
     */

    public static enum SubAppKeys {

        MOUNTAIN_CLIMBER ("mountain_climber"),
        FOOD_DIARY ("food_diary"),
        FIRSTBEAT ("firstbeat"),
        IMPORTANCE_RULER("importance_ruler"),
        OUTCOME_GOAL("outcome_goal"),
        DIETARY_CHALLENGE("dietary_challenge"),
        WEARABLE("wearable"),
        CONFIDENCE_RULER("confidence_ruler"),
        TIME_MACHINE("time_machine"),
        PHYSICAL_ACTIVITY_CHANGE("physical_activity_change"),
        FAVOURITE_ACTIVITY("favourite_activity");

        private final String name;

        private SubAppKeys(String s) {
            name = s;
        }

        public static SubAppKeys fromString(String text) {
            text = text.toLowerCase();
            if (text != null) {
                for (SubAppKeys b : SubAppKeys.values()) {
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
     *                 OG - Outcome Goal Setting - (What do I want)
     *                 DC - Dietary Challenge
     *                 IR - Importance Ruler
     *                 SM - Self monitoring / mountain view
     *                 WR - PRECIOUS wearable / Xiaomi band pairing
     *                 FA - Favourite Activity
     *                 MD - My Food Diary
     *                 UP - Firstbeat uploader, BodyGuard
     *                 PA_SOC -Physical Activity State of Change
     *                 TM - Time machine / Mental rehearsal... something
     *                 CR - Confidence ruler
     * @return true if subapp found, false means that subappID is not recognized
     */


    public static boolean enableDisableSubapp (boolean subappEnabled, SubAppKeys subapp){
        SharedPreferences preferences = PRECIOUS_APP.getAppContext().getSharedPreferences(UI_PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();

        switch (subapp){
            case OUTCOME_GOAL: //outcome_goal-- what do I want
                editor.putBoolean("showOG",subappEnabled);
                editor.apply();
                Log.i(TAG, "Outcome Goal Set to : " +subappEnabled);
                return  true;
            case IMPORTANCE_RULER:
                editor.putBoolean("showIR",subappEnabled);
                editor.apply();
                Log.i(TAG, "IR Set to : " +subappEnabled);
                return  true;
            case MOUNTAIN_CLIMBER:
                //Outcome
                editor.putBoolean("showSM",subappEnabled);
                editor.apply();
                Log.i(TAG, "Mountain Climber Set to : " +subappEnabled);
                return  true;
            case WEARABLE:
                editor.putBoolean("showWR",subappEnabled);
                editor.apply();
                Log.i(TAG, "Wearable Set to : " +subappEnabled);
                return  true;
            case FAVOURITE_ACTIVITY: //Choose favourite activity
                editor.putBoolean("showFA",subappEnabled);
                editor.apply();
                Log.i(TAG, "Favourite Activity Set to : " +subappEnabled);
                return  true;
            case FOOD_DIARY:
                editor.putBoolean("showMD",subappEnabled);
                editor.apply();
                Log.i(TAG, "Food Diary Set to : " +subappEnabled);
                return  true;
            case FIRSTBEAT:
                editor.putBoolean("showUP",subappEnabled);
                editor.apply();
                Log.i(TAG, "Firstbeat Set to : " +subappEnabled);
                return true;
            case PHYSICAL_ACTIVITY_CHANGE: //whats next
                editor.putBoolean("showPA_SOC",subappEnabled);
                editor.apply();
                Log.i(TAG, "PhysicalActivity Set to : " +subappEnabled);
                return true;
            case TIME_MACHINE:
                editor.putBoolean("showTM",subappEnabled);
                editor.apply();
                Log.i(TAG, "Time Machine Set to : " +subappEnabled);
                return true;
            case CONFIDENCE_RULER:
                editor.putBoolean("showCR",subappEnabled);
                editor.apply();
                Log.i(TAG, "Confidence Ruler Set to : " +subappEnabled);
                return  true;
            default: return false;
        }
    }

    /**
     * Reads the state of the apps (show/hide) that the rule system has stored into SharedPreferences
     * @return a String array with the subapps to be shown in the main screen
     */
    public static String [] getBoxOrganizer(){
        ArrayList<String> stringArrayList = new ArrayList<String>();
        SharedPreferences preferences = PRECIOUS_APP.getAppContext().getSharedPreferences(UI_PREFS_NAME, 0);
        if(preferences.getBoolean("showOG",DEFAULT_STATE))
            stringArrayList.add("OG");
        if(preferences.getBoolean("showDC",DEFAULT_STATE))
            stringArrayList.add("DC");
        if(preferences.getBoolean("showIR",DEFAULT_STATE))
            stringArrayList.add("IR");
        if(preferences.getBoolean("showSM",DEFAULT_STATE)) {
            stringArrayList.add("SM");
            stringArrayList.add("WR");
        }
        if(preferences.getBoolean("showWR",DEFAULT_STATE))
            stringArrayList.add("WR");
        if(preferences.getBoolean("showFA",DEFAULT_STATE))
            stringArrayList.add("FA");
        if(preferences.getBoolean("showMD",DEFAULT_STATE))
            stringArrayList.add("MD");
        if(preferences.getBoolean("showUP",DEFAULT_STATE))
            stringArrayList.add("UP");
        if(preferences.getBoolean("showPA_SOC",DEFAULT_STATE))
            stringArrayList.add("PA_SOC");
        if(preferences.getBoolean("showTM",DEFAULT_STATE))
            stringArrayList.add("TM");
        if(preferences.getBoolean("showCR",DEFAULT_STATE))
            stringArrayList.add("CR");

        return  stringArrayList.toArray(new String[stringArrayList.size()]);
    }

    public static void sendNotification (int notificationID, String text){
        Context appContext = PRECIOUS_APP.getAppContext();
//TODO long subtitle notification (different lines or scrollable)
    }
}
