package io.silvestri.teapot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class ProgressActivity extends Activity {

	TextView status_text;
	TextView details_text;
	ImageButton details_button;
	Button button_stop;
	ImageView image_tea;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress);

		/* *** init views *** */
		status_text = findViewById(R.id.text_status);
		details_text = findViewById(R.id.details_text);
		details_button = findViewById(R.id.details_button);
		button_stop = findViewById(R.id.button_stop);
		image_tea = findViewById(R.id.image_tea);

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

		details_button.setOnTouchListener(detailsTouchListener);
	}

	private BroadcastReceiver teaBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String state = intent.getStringExtra("state");
			if (state.equals("ready")) {
				Intent newIntent = new Intent(ProgressActivity.this, MainActivity.class);
				newIntent.putExtras(intent);
				startActivity(newIntent);
				finish();
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

		// ask for the very latest state
		TeapotAPI.makeRequest("get_state");
	}

	@Override
	protected void onPause() {
		super.onPause();
		// unsubscribe to broadcasts
		LocalBroadcastManager.getInstance(this).unregisterReceiver(teaBroadcastReceiver);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(errorBroadcastReceiver);
	}

	void updateState(Bundle extras) {
		String state = extras.getString("state");

		if (state == null) {
			return;
		}

		button_stop.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorPrimary)));

		// update extended info
		StringBuilder info_details = new StringBuilder();
		for (String el : extras.keySet()) {
			info_details
					.append(el)
					.append(": ")
					.append(extras.getString(el, "null"))
					.append("\n");
		}
		details_text.setText(info_details.toString());

		if (state.equals("done")) {
			status_text.setText("Your tea is ready!");
			image_tea.setImageResource(R.drawable.tea);
			button_stop.setText("Start over");
			return;
		}

		button_stop.setText("Stop the tea!");

		Map<String, String> state_descriptions = new HashMap<>();
		Map<String, Integer> state_images = new HashMap<>();

		state_descriptions.put("boiling", "Boiling the water");
		state_images.put("boiling", R.drawable.progress_boiling);

		state_descriptions.put("pumping", "Filling the teacup");
		state_images.put("pumping", R.drawable.progress_pumping);

		state_descriptions.put("lowering", "Lowering the tea bag");
		state_images.put("lowering", R.drawable.progress_lowering);

		state_descriptions.put("brewing", "Brewing the tea");
		state_images.put("brewing", R.drawable.progress_brewing);

		state_descriptions.put("raising", "Raising the tea bag");
		state_images.put("raising", R.drawable.progress_raising);

		state_descriptions.put("cooling", "Waiting for the tea to cool down");
		state_images.put("cooling", R.drawable.progress_cooling);

		status_text.setText(state_descriptions.containsKey(state) ? state_descriptions.get(state) : state);
		image_tea.setImageResource(state_images.containsKey(state) ? state_images.get(state) : R.drawable.tea);
	}

	void setErrorState(String message) {
		status_text.setText(message);
		image_tea.setImageResource(R.drawable.tea_error);
		button_stop.setText("Start over");
		button_stop.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorError)));
	}

	void resetTeapot() {
		TeapotAPI.makeRequest("abort");
	}

	View.OnTouchListener detailsTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
				TeapotAPI.makeRequest("get_stats");
				details_text.setVisibility(View.VISIBLE);
				return true;
			} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
				details_text.setVisibility(View.GONE);
				return true;
			}
			return false;
		}
	};
}
