package io.silvestri.teapot;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ProgressActivity extends Activity {

	TextView status_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress);

		/* *** init views *** */
		status_text = findViewById(R.id.text_status);

		/* *** intent extras *** */
		if (getIntent().getExtras() != null) {
			updateState(getIntent().getExtras());
		}

		/* *** event listeners *** */
		findViewById(R.id.button_stop).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				resetTeapot();
			}
		});
	}

	private BroadcastReceiver teaBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String state = intent.getStringExtra("state");
			if (state.equals("ready")) {
				Intent newIntent = new Intent(ProgressActivity.this, MainActivity.class);
				newIntent.putExtras(intent);
				startActivity(newIntent);
			} else {
				updateState(intent.getExtras());
			}
		}
	};

	private BroadcastReceiver errorBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent
			String message = intent.getStringExtra("error");
			setErrorState(message);
		}
	};

	@Override
	protected void onResume() {
		super.onResume();

		// subscribe to broadcasts
		LocalBroadcastManager.getInstance(this).registerReceiver(teaBroadcastReceiver, new IntentFilter("tea"));
		LocalBroadcastManager.getInstance(this).registerReceiver(errorBroadcastReceiver, new IntentFilter("error"));
	}

	void updateState(Bundle extras) {
		status_text.setText(extras.getString("state"));
		// TODO better display
	}

	void setErrorState(String message) {
		status_text.setText(message);
		// TODO error image
	}

	void resetTeapot() {
		TeapotAPI.makeRequest("abort");
	}

	// TODO info button with detailed state
}
