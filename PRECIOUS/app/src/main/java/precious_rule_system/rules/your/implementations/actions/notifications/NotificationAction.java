package precious_rule_system.rules.your.implementations.actions.notifications;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import rules.entities.actions.ActionParameter;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;

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

        NotificationManager notificationManager = (NotificationManager) PRECIOUS_APP.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(PRECIOUS_APP.getInstance().getApplicationContext())
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setContentTitle("PRECIOUS")
                        .setContentText(text)
                        .setPriority(PRIORITY_MAX);

        notificationManager.notify(10, mBuilder.build());
    }


}
