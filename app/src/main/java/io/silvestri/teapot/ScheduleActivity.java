package io.silvestri.teapot;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class ScheduleActivity extends Activity {

	SeekBar seek_temp;
	SeekBar seek_strength;
	TextView seek_temp_number;
	TimePicker timePicker;
	Button cancel_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);

		/* *** init views *** */
		seek_temp = findViewById(R.id.seek_temperature);
		seek_temp_number = findViewById(R.id.seek_temp_number);
		seek_strength = findViewById(R.id.seek_strength);
		timePicker = findViewById(R.id.timePicker);
		cancel_button = findViewById(R.id.cancel_button);

		/* *** event listeners *** */
		findViewById(R.id.buttonStart).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onScheduleClicked();
			}
		});

		seek_temp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				seek_temp_number.setText(String.valueOf(i + 30) + "°C");
				seek_temp_number.setX(seek_temp.getX()
						+ seek_temp.getPaddingStart()
						+ i / 60.0f
						* (seek_temp.getWidth() - seek_temp.getPaddingStart() - seek_temp.getPaddingEnd()));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				seek_temp_number.setVisibility(View.VISIBLE);
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				seek_temp_number.setVisibility(View.GONE);
			}
		});

		cancel_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				cancelSchedule();
			}
		});

		/* *** init UI *** */
		updateInfoBar();

	}

	void onScheduleClicked() {
		int temp = seek_temp.getProgress();
		int strength = seek_strength.getProgress();

		Calendar time = Calendar.getInstance();
		time.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
		time.set(Calendar.MINUTE, timePicker.getMinute());
		time.set(Calendar.SECOND, 0);
		if (time.before(Calendar.getInstance())) time.add(Calendar.DATE, 1);

		scheduleTea(time.getTimeInMillis(), temp, strength);
	}

	void scheduleTea(long time, int temp, int strength) {
		// Retrieve a PendingIntent that will perform a broadcast
		Intent alarmIntent = new Intent(this, AlarmReceiver.class);
		alarmIntent.putExtra("temp", temp);
		alarmIntent.putExtra("strength", strength);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		manager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
		getSharedPreferences("schedule", MODE_PRIVATE).edit().putLong("time", time).apply();
		updateInfoBar();
	}

	void cancelSchedule() {
		Intent alarmIntent = new Intent(this, AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		manager.cancel(pendingIntent);
		getSharedPreferences("schedule", MODE_PRIVATE).edit().putLong("time", 0).apply();
		updateInfoBar();
	}

	void updateInfoBar() {
		long time = getSharedPreferences("schedule", MODE_PRIVATE).getLong("time", 0);
		if (time == 0) {
			findViewById(R.id.future_bar).setVisibility(View.GONE);
		} else {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(time);
			String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
			String minute = String.valueOf(c.get(Calendar.MINUTE));
			if (minute.length() == 1) minute = "0" + minute;
			((TextView) findViewById(R.id.future_text)).setText("Tea scheduled for " + hour + ":" + minute);
			findViewById(R.id.future_bar).setVisibility(View.VISIBLE);
		}
	}
}
