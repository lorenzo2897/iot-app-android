package io.silvestri.teapot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// make tea!
		int temp = intent.getIntExtra("temp", 40);
		int strength = intent.getIntExtra("strength", 2);
		Log.i("Debug", String.valueOf(temp));
		Log.i("Debug", String.valueOf(strength));
		try {
			TeapotAPI.makeRequest("make_tea", makeSettingsJson(temp, strength));
			Toast.makeText(context, "Making tea", Toast.LENGTH_SHORT).show();
		} catch (JSONException e) {
			Toast.makeText(context, "JSON error", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	static String makeSettingsJson(int temp, int strength) throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("temperature", temp + 30);
		obj.put("concentration", strength / 4.0f);
		return obj.toString();
	}

}
