package precious_rule_system.rules.your.implementations.actions.applications;

import android.util.Log;


import precious_rule_system.precoiusinterface.PreciousApplicationActions;
import rules.entities.actions.ActionParameter;

/**
 * Created by christopher on 12.08.16.
 */

public class ApplicationAction {

    private static String TAG = "ApplicationAction";

    public static void handleApplicationEnabling(ActionParameter[] parameters) {
        PreciousApplicationActions.SubAppKeys subApp;
        for (ActionParameter parameter: parameters) {
             subApp =  PreciousApplicationActions.SubAppKeys.fromString(parameter.getValue().toString());
             if (subApp != null) {
                    if (PreciousApplicationActions.enableDisableSubapp(true, subApp))
                        Log.d(TAG, subApp.toString()+ " SubApp enabled successfully");
                    else
                        Log.d(TAG, "Could not enable " + subApp.toString() + " SubApp");
             }
        }
    }

    public static void handleApplicationDisabling(ActionParameter[] parameters) {
        PreciousApplicationActions.SubAppKeys subApp;
        for (ActionParameter parameter: parameters) {
            subApp =  PreciousApplicationActions.SubAppKeys.fromString((String)parameter.getValue());
            if (subApp != null) {
                if (PreciousApplicationActions.enableDisableSubapp(false, subApp))
                    Log.d(TAG, subApp.toString()+ " SubApp disabled successfully");
                else
                    Log.d(TAG, "Could not disable " + subApp.toString() + " SubApp");
            }
        }
    }

    public static void handleApplicationOpen(ActionParameter[] parameters) {

    }

    public static void handleApplicationClose(ActionParameter[] parameters) {

    }

    public static void handleOpenAppStream(ActionParameter[] parameters) {

    }

    public static void handleSetSuggestedApp(ActionParameter[] parameters) {

    }
}
