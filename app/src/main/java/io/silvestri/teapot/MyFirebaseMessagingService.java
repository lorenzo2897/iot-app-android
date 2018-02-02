package io.silvestri.teapot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);
		StringBuilder keys = new StringBuilder();
		for (String i : remoteMessage.getData().keySet()) {
			keys.append(" ").append(i);
		}
		Log.e("MSG", "got push: " + remoteMessage.getData().size());
		sendNotification("Got push", String.valueOf(remoteMessage.getData().size()));
	}

	private void sendNotification(String title, String messageBody) {

		NotificationManager notificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		Intent intent = new Intent(this, ProgressActivity.class);
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
				.setContentTitle(title)
				.setContentText(messageBody)
				.setAutoCancel(true)
				.setSound(defaultSoundUri)
				.setContentIntent(pendingIntent);

		notificationManager.notify(0, notificationBuilder.build());


	}

}
