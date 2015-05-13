package aalto.comnet.thepreciousrecognition;

import aalto.comnet.thepreciousproject.R;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GoodmorningService extends Service{
		
		private NotificationCompat.Builder mBuilder;
		private NotificationManager mNotifyMgr;
		private int mNotificationId = 2125;		
		private Intent resultIntent;
		private PendingIntent resultPendingIntent;
		

		@Override
	    public void onCreate() {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			Log.i("VIBRATOR",pref.getBoolean("useVibrator", false)+"");
			if(pref.getBoolean("useVibrator", false)){
				Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
				//Pattern:delay 0ms, vibrate 300ms, delay 3000ms, vibrate 300ms...
				long[] pattern = {0, 300, 300,300,300, 500};
				v.vibrate(pattern, -1); //-1 is important
			}
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
			 Notification n = mBuilder.build();
			 mNotifyMgr.notify(mNotificationId, n);
		}
		
	    @SuppressLint("Wakelock") public int onStartCommand(Intent intenc, int flags, int idArranque) {
			return START_NOT_STICKY;
		}
		
		 public void onDestroy() {
			  mNotifyMgr.cancel(mNotificationId);
		 }
		 
		 @Override
	     public IBinder onBind(Intent intencion) {
	           return null;
	     }
}
