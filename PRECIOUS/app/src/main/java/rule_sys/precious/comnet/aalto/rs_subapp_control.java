package rule_sys.precious.comnet.aalto;


import android.content.SharedPreferences;


import android.content.Context;

import java.util.ArrayList;

import ui.precious.comnet.aalto.precious.PRECIOUS_APP;

/*
This class is used to control which subapps appear at the main screen of the app and which are not visible.
 */
public class rs_subapp_control {

    public static final String UI_PREFS_NAME = "UIPreferences";
    public static final boolean DEFAULT_STATE = false; //if true, the subapps will be visible by default, until enableDisableSubapp is called to change it. If false, the subapp will be hidder by default.

    /**
     * @param subappEnabled true if subapp to be shown, false to be hidden for the user
     * @param subappID represents the name of the subapp:
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
    public static boolean enableDisableSubapp (boolean subappEnabled, String subappID){
        SharedPreferences preferences = PRECIOUS_APP.getAppContext().getSharedPreferences(UI_PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        switch (subappID){
            case "OG":
                //Outcome
                editor.putBoolean("showOG",subappEnabled);
                editor.apply();
                return  true;
            case "DC":
                editor.putBoolean("showDC",subappEnabled);
                editor.apply();
                return  true;
            case "IR":
                editor.putBoolean("showIR",subappEnabled);
                editor.apply();
                return  true;
            case "SM":
                editor.putBoolean("showSM",subappEnabled);
                editor.apply();
                return  true;
            case "WR":
                editor.putBoolean("showWR",subappEnabled);
                editor.apply();
                return  true;
            case "FA":
                editor.putBoolean("showFA",subappEnabled);
                editor.apply();
                return  true;
            case "MD":
                editor.putBoolean("showMD",subappEnabled);
                editor.apply();
                return  true;
            case "DB":
                editor.putBoolean("showDB",subappEnabled);
                editor.apply();
                return  true;
            case "PA_SOC":
                editor.putBoolean("showPA_SOC",subappEnabled);
                editor.apply();
                return  true;
            case "TM":
                editor.putBoolean("showTM",subappEnabled);
                editor.apply();
                return  true;
            case "CR":
                editor.putBoolean("showCR",subappEnabled);
                editor.apply();
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
        if(preferences.getBoolean("showSM",DEFAULT_STATE))
            stringArrayList.add("SM");
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
