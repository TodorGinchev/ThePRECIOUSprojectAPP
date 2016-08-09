package uploader.precious.comnet.aalto;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;

public class SendLog  extends Service {


    private static SharedPreferences prefs;
    private static SharedPreferences Default_prefs;
    public static final String TAG = "SendLog";
    public static final String UP_PREFS_NAME = "UploaderPreferences";

    public static Context mContext;

    @Override
    public void onCreate() {
        prefs =this.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        Default_prefs = PreferenceManager.getDefaultSharedPreferences(this);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.
                Builder().permitNetwork().build());
        mContext=this;
    }

    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {
//        activity_tracker.precious.comnet.aalto.atUtils.getLog(ui_MainActivity.mContext);
        Log.i("SendLog", "on startd command");
        SharedPreferences uploader_preferences = this.getSharedPreferences(UP_PREFS_NAME, 0);
        if ( ! (uploader_preferences.getBoolean("isUserLoggedIn",false) )) {
            Log.i(TAG,"USER NOT LOGGED IN YET");
            onDestroy();
            return START_NOT_STICKY;
        }

        Log.i(TAG,"USER HAS LOGGED IN");

//        //Send Automatic PA data
//        new AsyncTask<Void, Void, Void>(){
//            @Override
//            protected Void doInBackground(Void... params) {
//                try {
//                    activity_tracker.precious.comnet.aalto.atUtils.getLog(ui_MainActivity.mContext);
//
//                } catch (Exception e){
//                    Log.e("getLog"," ",e);
//                }
//                return null;
//            }
//        }.execute();


        //Send Automatic PA data
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    upUtils.setContext(mContext);
                    upUtils.sendAutomaticPADataToPreciousServer();

                } catch (Exception e){
                    Log.e("getLog"," ",e);
                }
                return null;
            }
        }.execute();

        //Send Manual PA data
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            upUtils.setContext(mContext);
                            upUtils.sendManualPADataToPreciousServer();

                        } catch (Exception e) {
                            Log.e("getLog", " ", e);
                        }
                        return null;
                    }
                }.execute();
            }
        }, 30000);

        //Send Food data
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            upUtils.setContext(mContext);
                            upUtils.sendFoodDataToPreciousServer();

                        } catch (Exception e) {
                            Log.e("getLog", " ", e);
                        }
                        return null;
                    }
                }.execute();
            }
        }, 60000);

        //Send Food Challenge data
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            upUtils.setContext(mContext);
                            upUtils.sendFoodChallengeDataToPreciousServer();

                        } catch (Exception e) {
                            Log.e("getLog", " ", e);
                        }
                        return null;
                    }
                }.execute();
            }
        }, 90000);

        //Send App usage data
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            upUtils.setContext(mContext);
                            upUtils.sendAppUsageDataToPreciousServer();

                        } catch (Exception e) {
                            Log.e("getLog", " ", e);
                        }
                        return null;
                    }
                }.execute();
            }
        }, 120000);

        onDestroy();
        return START_NOT_STICKY;
    }

    public void onDestroy() {
    }

    @Override
    public IBinder onBind(Intent intencion) {
        return null;
    }



}