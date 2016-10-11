package precious_rule_system.rules.your.implementations.actions.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import aalto.comnet.thepreciousproject.R;
import rules.entities.actions.ActionParameter;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;
import ui.precious.comnet.aalto.precious.ui_MainActivity;

import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;

/**
 * Created by christopher on 12.08.16.
 */

public class NotificationAction {

    // TODO!
    public static void handleNotifications(ActionParameter[] parameters) {

        String text = "";

        for (ActionParameter p : parameters) {
            if (p.getKey().equals("eng")) {
                text = (String) p.getValue();
            }
        }
//        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.precious_icon);

        NotificationManager notificationManager = (NotificationManager) PRECIOUS_APP.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);

        //TODO: Remove extra text after testing
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(PRECIOUS_APP.getInstance().getApplicationContext())
                        .setSmallIcon(R.drawable.precious_icon)
                        .setContentTitle("PRECIOUS")
                        .setContentText("Trial Notification")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(text+" \nHello this is test \nThe big notification is working")
                        );
        PendingIntent contentIntent = PendingIntent.getActivity(PRECIOUS_APP.getContext(),0,
                new Intent(PRECIOUS_APP.getContext(), ui_MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        notificationManager.notify(10, mBuilder.build());
    }
}
