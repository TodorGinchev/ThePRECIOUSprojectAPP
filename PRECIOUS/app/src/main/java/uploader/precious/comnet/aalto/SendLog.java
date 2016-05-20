package uploader.precious.comnet.aalto;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

import ui.precious.comnet.aalto.precious.ui_MainActivity;

public class SendLog  extends Service {


    private static SharedPreferences prefs;
    private static SharedPreferences Default_prefs;
    public static final String TAG = "SendLog";

    @Override
    public void onCreate() {
        prefs =this.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        Default_prefs = PreferenceManager.getDefaultSharedPreferences(this);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.
                Builder().permitNetwork().build());
    }

    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {
        Log.i("SendLog", "on startd command");

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
//                    DBHelper dbhelp;
//                    dbhelp = new DBHelper(ui_MainActivity.mContext);
                    long sendFrom=System.currentTimeMillis()-2*24*3600*1000;
                    long sendTo=System.currentTimeMillis();

                    ui_MainActivity.dbhelp.getAllPA();//TODO this might be wrong
                    ArrayList<ArrayList<Long>> paData = ui_MainActivity.dbhelp.getPAdata(sendFrom, sendTo);
//                    ArrayList<ArrayList<Long>> manPaData = ui_MainActivity.dbhelp.getManPA(from, to);
//                    ArrayList<ArrayList<Long>> foodData = ui_MainActivity.dbhelp.getFood(from, to);
//                    ArrayList<ArrayList<String>> foodNames = ui_MainActivity.dbhelp.getFoodNames(from, to);
//
//                    upUtils.sendDataToPreciousServer(paData);


                    for (int i=0; i<paData.size();i++) {
                Log.i(TAG, ("Walk data:"+paData.get(i).get(1)) + "");
                        long from = (paData.get(i).get(0));
                        long to = from + 24*3600*1000-1;
                        int still_duration_s = (paData.get(i).get(1)).intValue();
                        int walk_duration_s = (paData.get(i).get(2)).intValue();
                        int bike_duration_s = (paData.get(i).get(3)).intValue();
                        int vehicle_duration_s = (paData.get(i).get(4)).intValue();
                        int run_duration_s = (paData.get(i).get(5)).intValue();
                        int tilt_duration_s = (paData.get(i).get(6)).intValue();
                        int goal_steps = (paData.get(i).get(7)).intValue();
                        upUtils.setContext(ui_MainActivity.mContext);//TODO this might be wrong
                        upUtils.sendDataToPreciousServer(from, to, still_duration_s, walk_duration_s, bike_duration_s, vehicle_duration_s, run_duration_s, tilt_duration_s, goal_steps);
                    }

                } catch (Exception e){
                    Log.e("getLog"," ",e);
                }
                return null;
            }
        }.execute();

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