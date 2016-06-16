package food_diary.precious.comnet.aalto;

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
import ui.precious.comnet.aalto.precious.uiUtils;

public class fd_FoodInputReminder extends Service {

    private static final int FOOD_REMINDER_NOTIF_ID = 100034;
    public static final String TAG = "fd_FoodInputReminder";
    public static final String UP_PREFS_NAME = "UploaderPreferences";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG,"Service called");

        SharedPreferences preferences_up = this.getSharedPreferences(UP_PREFS_NAME, 0);
        int groupID=preferences_up.getInt("group_ID", -1);
        String nickname = preferences_up.getString("nickname", "-1");
        int nicknameID=-1;
        try{
            nicknameID = Integer.parseInt(nickname);
        }catch (Exception e){
            nicknameID=-1;
            Log.i(TAG," ",e);
        }
        if(groupID!=130 && groupID!=678 && nicknameID!=130 && nicknameID!=678) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis()); //just to be sure
            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            long from = calendar.getTimeInMillis();//for the DB
            long to = System.currentTimeMillis();

            boolean notifyBreakfast = true;
            boolean notifyLunch = true;
            boolean notifyDinner = true;

            //Check for breakfast
            if (hourOfDay < uiUtils.BREAKFAST_HOUR_REMINDER + 1) {
                notifyLunch = false;
                notifyDinner = false;
                ArrayList<ArrayList<Long>> foodData = sql_db.precious.comnet.aalto.DBHelper.getInstance(this).getFood(from, to);
                for (int i = 0; i < foodData.size(); i++) {
                    if (foodData.get(i).get(1) == 1) //type 1 is breakfast
                        notifyBreakfast = false;
                }
            } else if (hourOfDay < uiUtils.LUNCH_HOUR_REMINDER + 1) {
                //Check for lunch
                notifyDinner = false;
                ArrayList<ArrayList<Long>> foodData = sql_db.precious.comnet.aalto.DBHelper.getInstance(this).getFood(from, to);
                for (int i = 0; i < foodData.size(); i++) {
                    if (foodData.get(i).get(1) == 1) //type 1 is breakfast
                        notifyBreakfast = false;
                    if (foodData.get(i).get(1) == 3) //type 3 is lunch
                        notifyLunch = false;
                }
            } else {
                //Check for dinner
                ArrayList<ArrayList<Long>> foodData = sql_db.precious.comnet.aalto.DBHelper.getInstance(this).getFood(from, to);
                for (int i = 0; i < foodData.size(); i++) {
                    if (foodData.get(i).get(1) == 1) //type 1 is breakfast
                        notifyBreakfast = false;
                    if (foodData.get(i).get(1) == 3) //type 3 is lunch
                        notifyLunch = false;
                    if (foodData.get(i).get(1) == 5) //type 1 is dinner
                        notifyDinner = false;
                }
            }


            //Make notification
            if (notifyDinner) {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.precious_icon)
                                .setContentTitle(getString(R.string.meal_log_reminder))
                                .setContentText(getString(R.string.dinner_reminder));
                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(this, fd_MainActivity.class);

                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(fd_MainActivity.class);
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
                if((preferences_up.getBoolean("isUserLoggedIn",false)) )
                    mNotificationManager.notify(FOOD_REMINDER_NOTIF_ID, mBuilder.build());
            } else if (notifyLunch) {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.precious_icon)
                                .setContentTitle(getString(R.string.meal_log_reminder))
                                .setContentText(getString(R.string.lunch_reminder));
                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(this, fd_MainActivity.class);

                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(fd_MainActivity.class);
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
                if((preferences_up.getBoolean("isUserLoggedIn",false)) )
                    mNotificationManager.notify(FOOD_REMINDER_NOTIF_ID, mBuilder.build());
            } else if (notifyBreakfast) {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.precious_icon)
                                .setContentTitle(getString(R.string.meal_log_reminder))
                                .setContentText(getString(R.string.breakfast_reminder));
                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(this, fd_MainActivity.class);

                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(fd_MainActivity.class);
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
                if((preferences_up.getBoolean("isUserLoggedIn",false)) )
                    mNotificationManager.notify(FOOD_REMINDER_NOTIF_ID, mBuilder.build());
            }
        }

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startid)
    {
        return START_STICKY;
    }
}
