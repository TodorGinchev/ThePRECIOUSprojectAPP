package aalto.comnet.thepreciousrecognition;

import aalto.comnet.thepreciousproject.R;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GoodmorningService extends Service{
		
		private NotificationCompat.Builder mBuilder;
		private NotificationManager mNotifyMgr;
		private int mNotificationId = 2113;		
		private Intent resultIntent;
		private PendingIntent resultPendingIntent;
		

		@Override
	    public void onCreate() {
			resultIntent = new Intent(this, aalto.comnet.thepreciousfacerecognition.MainActivity.class);
			resultPendingIntent =
					    PendingIntent.getActivity(
					    this,
					    0,
					    resultIntent,
					    PendingIntent.FLAG_UPDATE_CURRENT
					);	
			 mBuilder = new NotificationCompat.Builder(this);
			 mBuilder.setContentIntent(resultPendingIntent)
		     .setSmallIcon(R.drawable.face_white_small)
		     .setTicker(getString(R.string.good_morning_notif))
		     .setWhen(System.currentTimeMillis())
		     .setAutoCancel(true)
		     .setContentTitle(getString(R.string.good_morning_notif))
		     .setContentText(getString(R.string.good_morning_notif_content));//TODO change perdometer notification
			 // Gets an instance of the NotificationManager service
			 mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			 Log.i("NOTIF","START");
			 startForeground(mNotificationId, mBuilder.build());			 
		}
		
	    @SuppressLint("Wakelock") public int onStartCommand(Intent intenc, int flags, int idArranque) {
			return START_STICKY;
		}
		
		 public void onDestroy() {
			  mNotifyMgr.cancel(mNotificationId);
		 }
		 
		 @Override
	     public IBinder onBind(Intent intencion) {
	           return null;
	     }
}
