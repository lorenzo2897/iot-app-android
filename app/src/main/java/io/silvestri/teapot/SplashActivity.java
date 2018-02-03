package io.silvestri.teapot;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		FirebaseMessaging.getInstance().subscribeToTopic("tea");

		findViewById(R.id.text_status).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startMainActivity(new Intent());
			}
		});
	}

	private BroadcastReceiver teaBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent
			String state = intent.getStringExtra("state");
			if (state.equals("ready")) {
				startMainActivity(intent);
			} else {
				startProgressActivity(state);
			}
		}
	};

	private BroadcastReceiver errorBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent
			String message = intent.getStringExtra("error");
			((TextView)findViewById(R.id.text_status)).setText(message);
			// enable the reset button
			findViewById(R.id.image_tea).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					resetTeapot();
				}
			});
		}
	};

	@Override
	protected void onResume() {
		super.onResume();

		// subscribe to broadcasts
		LocalBroadcastManager.getInstance(this).registerReceiver(teaBroadcastReceiver, new IntentFilter("tea"));
		LocalBroadcastManager.getInstance(this).registerReceiver(errorBroadcastReceiver, new IntentFilter("error"));

		// fire get_stats command
		TeapotAPI.makeRequest("get_stats");
	}

	@Override
	protected void onPause() {
		super.onPause();

		// unsubscribe to broadcasts
		LocalBroadcastManager.getInstance(this).unregisterReceiver(teaBroadcastReceiver);
	}

	void startMainActivity(Intent deviceStats) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtras(deviceStats);
		ActivityOptions options = ActivityOptions.
				makeSceneTransitionAnimation(this, findViewById(R.id.image_tea), "teapot");
		startActivity(intent, options.toBundle());
	}

	void startProgressActivity(String state) {
		Intent intent = new Intent(this, ProgressActivity.class);
		intent.putExtra("state", state);
		startActivity(intent);
	}

	void resetTeapot() {
		TeapotAPI.makeRequest("abort");
	}
}
