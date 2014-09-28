package eu.musesproject.client;

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

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import eu.musesproject.client.session.controller.SessionController;
import eu.musesproject.client.ui.MainActivity;

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

	private boolean isAppInitialized;
	
	private SessionController mSessionController;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
        isAppInitialized = false;
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "on startComment called");
		if (!isAppInitialized) {
			Toast.makeText(this, "MUSES started", Toast.LENGTH_LONG).show();
			isAppInitialized = true;

			
			if(mSessionController == null){
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

		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		isAppInitialized = false;
		unregisterReceiver(mSessionController);
		mSessionController = null;
		super.onDestroy();
	}
	
	@SuppressLint("NewApi")
	private void setAppIconOnStatusBar() {
		Notification.Builder mBuilder = new Notification.Builder(this)
				.setSmallIcon(R.drawable.muses_main).setContentTitle("")
				.setContentText("");
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, MainActivity.class);

		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(1, mBuilder.build());
	}
}