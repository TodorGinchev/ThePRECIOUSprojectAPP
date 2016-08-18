package ui.precious.comnet.aalto.precious;

import android.app.Application;
import android.content.Context;


public class preicousApp extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        preicousApp.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return preicousApp.context;
    }
}
