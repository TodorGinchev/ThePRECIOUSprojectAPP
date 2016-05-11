package ui.precious.comnet.aalto.precious;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class uiUtils {

    /**
     * Startup configuration: get user personal data, create local files, get user ID
     */
    public static void firstStartConfig(Context context){

        Log.i("firstStartConfig", "Starting DetectionRequesterService");
        Intent i = new Intent(context, activity_tracker.precious.comnet.aalto.DetectionRequesterService.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(i);


        AlarmManager alarmMgr_at;
        PendingIntent alarmIntent_at;
        alarmMgr_at = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent_at = new Intent(context, activity_tracker.precious.comnet.aalto.DetectionRequesterService.class );
        alarmIntent_at = PendingIntent.getService(context, 0, intent_at, 0);
        alarmMgr_at.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                30 * 60 * 1000, alarmIntent_at);//30 min interval
//                20 * 1000, alarmIntent_at);//30 min interval



        // Send data every 1/2 hour
//        AlarmManager alarmMgr;
//        PendingIntent alarmIntent;
//        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, SendLog.class );
//        alarmIntent = PendingIntent.getService(context, 0, intent, 0);
//        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),
//                AlarmManager.INTERVAL_HOUR, alarmIntent);
        //21*1000, alarmIntent); //TODO

        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        SharedPreferences prefs =context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
//        SharedPreferences.Editor edit = prefs.edit();
//        edit.putString("previous_version", ui_MainActivity.AppVersion);
//        edit.commit();
        //TODO
        //TODO
        //TODO
//        try{
//            File file = new File(getFilesDir(), "server.txt");
//            file.createNewFile();
//        }catch (Exception e){
//            Log.e("upUtils", "Error creating new files", e);
//        }
//        //Create user ID
//        String model = Build.MODEL;
//        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        String imei = telephonyManager.getDeviceId();
//        imei = imei.substring(imei.length()-5);
//        String userID =model.concat(" ").concat(imei);
//        edit.putString("user_id", userID);
//        edit.commit();
//        //Ask to open user configuration
//        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
//        builder1.setMessage(getString(R.string.first_start));
//        builder1.setCancelable(true);
//        builder1.setPositiveButton(getString(R.string.ok),
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        openConfiguration();
//                        dialog.cancel();
//                    }
//                });
//        builder1.setNegativeButton(getString(R.string.later),
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog alert1 = builder1.create();
//        alert1.show();
    }
}

