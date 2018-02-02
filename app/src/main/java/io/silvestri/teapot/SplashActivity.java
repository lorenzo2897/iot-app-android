package io.silvestri.teapot;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.messaging.FirebaseMessaging;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		FirebaseMessaging.getInstance().subscribeToTopic("tea");

		findViewById(R.id.image_tea).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startMainActivity();
			}
		});
	}

	void startMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		ActivityOptions options = ActivityOptions.
				makeSceneTransitionAnimation(this, findViewById(R.id.image_tea), "teapot");
		startActivity(intent, options.toBundle());
	}
}
