package io.silvestri.teapot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);

		Map<String, String> messageData = remoteMessage.getData();

		Log.i("MSG", "got push: " + messageData.size());

		/* check for error */
		if (messageData.containsKey("error")) {
			Log.e("MSG", "got an error notification: " + messageData.get("error"));
			if (messageData.containsKey("notify")) {
				sendNotification("Tea failed", messageData.get("error"), false);
			}
			// TODO better error detection
			return;
		}

		/* process the push */
		if (!messageData.containsKey("state")) {
			Log.e("MSG", "push does not contain any data, ignoring.");
			return;
		}

		// send a notification if tea is ready
		if (messageData.containsKey("notify")) {
			sendNotification("Tea is ready!", "", true);
		}

		// broadcast the data to the foreground app
		Intent broadcast = new Intent();
		broadcast.setAction("tea");
		for (Map.Entry<String, String> el : messageData.entrySet()) {
			broadcast.putExtra(el.getKey(), el.getValue());
		}
		LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
	}

	private void sendNotification(String title, String messageBody, boolean isDone) {

		NotificationManager notificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		Intent intent;
		if (isDone) {
			intent = new Intent(this, ProgressActivity.class);
			intent.putExtra("done", true);
		} else {
			intent = new Intent(this, SplashActivity.class);
		}

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
				PendingIntent.FLAG_ONE_SHOT);

		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		Notification.Builder notificationBuilder;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel("tea", "Teapot", NotificationManager.IMPORTANCE_DEFAULT);
			notificationManager.createNotificationChannel(channel);
			notificationBuilder = new Notification.Builder(this, "tea");
		} else {
			notificationBuilder = new Notification.Builder(this);
		}
		notificationBuilder
				.setSmallIcon(R.drawable.tea)
				.setColor(getColor(R.color.colorPrimary))
				.setContentTitle(title)
				.setContentText(messageBody)
				.setAutoCancel(true)
				.setSound(defaultSoundUri)
				.setContentIntent(pendingIntent);

		notificationManager.notify(0, notificationBuilder.build());


	}

}
