package activity_tracker.precious.comnet.aalto;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

import aalto.comnet.thepreciousproject.R;
import sql_db.precious.comnet.aalto.DBHelper;
import ui.precious.comnet.aalto.precious.ui_MainActivity;

public class atGoalSettingReminder extends Service {

    private static final int PA_GOAL_REMINDER_NOTIF_ID = 100035;
    public static final String TAG = "atGoalSettingReminder";


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
        int goalData=-1;
        try{
            goalData = ui_MainActivity.dbhelp.getGoalData (c_aux.getTimeInMillis());
        }
        catch (Exception e) {
            DBHelper dbhelp = new DBHelper(this);
            goalData = dbhelp.getGoalData(c_aux.getTimeInMillis());
        }

        Log.i(TAG,"Goal data="+goalData);
        if(goalData==-1){
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.precious_icon)
                                .setContentTitle(getString(R.string.pa_goal_reminder_title))
                                .setContentText(getString(R.string.pa_goal_reminder_description));
                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(this, MountainViewActivity.class);

                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(MountainViewActivity.class);
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
                mNotificationManager.notify(PA_GOAL_REMINDER_NOTIF_ID, mBuilder.build());
            }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid)
    {
        return START_STICKY;
    }
}

