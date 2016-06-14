package diet_challenges.precious.comnet.aalto.fi;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import aalto.comnet.thepreciousproject.R;
import sql_db.precious.comnet.aalto.DBHelper;

public class dc_Reminder extends Service {

    private static final int DC_REMINDER_NOTIF_ID = 100036;
    public static final String TAG = "dc_Reminder";
    public static final String UP_PREFS_NAME = "UploaderPreferences";


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "Service called");
        Calendar c_aux = Calendar.getInstance();
        c_aux.setTimeInMillis(System.currentTimeMillis());
        c_aux.set(Calendar.HOUR_OF_DAY, 0);
        c_aux.set(Calendar.MINUTE, 0);
        c_aux.set(Calendar.SECOND, 0);
        c_aux.set(Calendar.MILLISECOND,0);
        boolean no_dc_input = true;

        ArrayList<ArrayList<Long>> dcData;
        try {
            dcData = ui.precious.comnet.aalto.precious.ui_MainActivity.dbhelp.getFoodChallenges(c_aux.getTimeInMillis() - 1, c_aux.getTimeInMillis() + 23 * 3600 * 1000);
        }
        catch (Exception e) {
            DBHelper dbhelp = new DBHelper(this);
            dcData = dbhelp.getFoodChallenges(c_aux.getTimeInMillis() - 1, c_aux.getTimeInMillis() + 23 * 3600 * 1000);
        }

        for(int i=0; i<dcData.size();i++){
            try {
                for(int j=1; j<dcData.get(i).size();j++)
                if( dcData.get(i).get(j).intValue()!=0)
                    no_dc_input=false;
            }catch (Exception e){
                Log.e(TAG," ",e);
            }
        }

        SharedPreferences preferences_up = this.getSharedPreferences(UP_PREFS_NAME, 0);

        long timeNow=System.currentTimeMillis();
        long registrationTime = timeNow;
        try {
            registrationTime = preferences_up.getLong("rd", timeNow);
            SharedPreferences.Editor editor = preferences_up.edit();
            editor.putLong("rd",(long)registrationTime);
            editor.commit();
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
        c_aux.setTimeInMillis(registrationTime);
        c_aux.set(Calendar.HOUR_OF_DAY, 0);
        c_aux.set(Calendar.MINUTE, 0);
        c_aux.set(Calendar.SECOND, 0);
        c_aux.set(Calendar.MILLISECOND, 0);

        int groupID=preferences_up.getInt("group_ID", -1);
        String nickname = preferences_up.getString("nickname", "-1");
        int nicknameID=-1;
        try{
            nicknameID = Integer.parseInt(nickname);
        }catch (Exception e){
            nicknameID=-1;
            Log.i(TAG," ",e);
        }

        boolean seven_days_passed=System.currentTimeMillis() > (c_aux.getTimeInMillis()+7*24*3600*1000);



        if(groupID==130 || nicknameID==130){
            no_dc_input=!seven_days_passed;
        }
        else if(groupID==678 || nicknameID==678){
            no_dc_input=seven_days_passed;
        }
        else if(groupID!=387 && nicknameID!=387 && nicknameID!=827 && nicknameID!=827) {
            no_dc_input=false;
        }

        Log.i(TAG,"groupID="+groupID);
        Log.i(TAG,"nicknameID="+nicknameID);
        Log.i(TAG,"seven_days_passed="+seven_days_passed);
        Log.i(TAG,"no_dc_input="+no_dc_input);

        if(no_dc_input){
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.precious_icon)
                            .setContentTitle(getString(R.string.dc_reminder_title))
                            .setContentText(" ");
            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(this, dc_MainActivity.class);

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(dc_MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // FOOD_REMINDER_NOTIF_ID allows you to update the notification later on.
            mNotificationManager.notify(DC_REMINDER_NOTIF_ID, mBuilder.build());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid)
    {
        return START_STICKY;
    }
}