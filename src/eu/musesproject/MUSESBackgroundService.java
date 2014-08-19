package eu.musesproject;

/*
 * #%L
 * musesclient
 * %%
 * Copyright (C) 2013 - 2014 HITEC
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import eu.musesproject.client.contextmonitoring.UserContextMonitoringController;
import eu.musesproject.client.prediction.session.SessionController;

/**
 * This class is responsible to start the background service which enables the
 * application to run properly. This service initializes the necessary code.
 * 
 * @author christophstanik
 * 
 */
public class MUSESBackgroundService extends Service {
	private static final String TAG = MUSESBackgroundService.class
			.getSimpleName();

	private SessionController mSessionController;

	private boolean isAppInitialized;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		isAppInitialized = false;
		UserContextMonitoringController.getInstance(this);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "on startComment called");
		if (!isAppInitialized) {
			Toast.makeText(this, "MUSES started", Toast.LENGTH_LONG).show();
			isAppInitialized = true;
			UserContextMonitoringController.getInstance(this)
					.startContextObservation();

			if (mSessionController == null) {
				mSessionController = new SessionController();
			}
			registerReceiver(mSessionController, new IntentFilter(
					Intent.ACTION_SCREEN_ON));
			registerReceiver(mSessionController, new IntentFilter(
					Intent.ACTION_SCREEN_OFF));
			registerReceiver(mSessionController, new IntentFilter(
					Intent.ACTION_USER_PRESENT));
			registerReceiver(mSessionController, new IntentFilter(SessionController.ACTION_QUIT_SESSION));
		}
//		startService(new Intent(this, MusesServiceProvider.class));

		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		isAppInitialized = false;
		unregisterReceiver(mSessionController);
		mSessionController = null;
		super.onDestroy();
	}
}