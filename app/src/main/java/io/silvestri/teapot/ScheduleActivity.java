package io.silvestri.teapot;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleActivity extends Activity {

	SeekBar seek_temp;
	SeekBar seek_strength;
	TextView seek_temp_number;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);

		/* *** init views *** */
		seek_temp = findViewById(R.id.seek_temperature);
		seek_temp_number = findViewById(R.id.seek_temp_number);
		seek_strength = findViewById(R.id.seek_strength);

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
	}

	void onScheduleClicked() {
		int temp = seek_temp.getProgress();
		int strength = seek_strength.getProgress();
		scheduleTea(System.currentTimeMillis() + 3000, temp, strength);
	}

	void scheduleTea(long time, int temp, int strength) {
		// Retrieve a PendingIntent that will perform a broadcast
		Intent alarmIntent = new Intent(this, AlarmReceiver.class);
		alarmIntent.putExtra("temp", temp);
		alarmIntent.putExtra("strength", strength);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

		AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		manager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
		Toast.makeText(this, "Tea scheduled", Toast.LENGTH_SHORT).show();
	}
}
