package io.silvestri.teapot;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TeapotAPI {

	static void makeRequest(String page) {
		makeRequest(page, "");
	}

	static void makeRequest(final String page, final String data) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					URL url = new URL("http://silvestri.io:50500/api/" + page);
					HttpURLConnection client = (HttpURLConnection) url.openConnection();
					client.setRequestMethod("POST");
					client.setDoOutput(true);

					String urlData = "data=" + URLEncoder.encode(data, "UTF-8");

					OutputStream os = client.getOutputStream();
					BufferedWriter writer = new BufferedWriter(
							new OutputStreamWriter(os, "UTF-8"));
					writer.write(urlData);

					writer.flush();
					writer.close();
					os.close();
					client.getResponseCode();
					client.disconnect();

				} catch (Exception e) {
					Log.e("TeapotAPI", e.getMessage());
				}
			}
		}).start();
	}
}
