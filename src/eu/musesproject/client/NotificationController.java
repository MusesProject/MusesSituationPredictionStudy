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


import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import eu.musesproject.client.ui.MainActivity;

public class NotificationController {

	private static NotificationController mInstance;
	private Context mContext;
	private NotificationManager mNotificationManager;
	private Builder mBuilder;

	private static final int NOTIFICATION_ID = 1;

	private NotificationController(Context context) {
		mContext = context;
	}

	public static NotificationController getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new NotificationController(context);
		}
		return mInstance;
	}

	public void updateNotification(String newValue) {
		if (mBuilder == null) {
			mBuilder = new Notification.Builder(mContext);
			mBuilder.setContentTitle(mContext.getResources().getString(R.string.notification_title));
		}

		mBuilder.setSmallIcon(getIconResId(newValue));
		mBuilder.setContentText(mContext.getResources().getString(R.string.notification_text) + " " +newValue);

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(mContext, MainActivity.class);

		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);

		mBuilder.setOngoing(true);
		if (mNotificationManager == null) {
			mNotificationManager = (NotificationManager) mContext
					.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		// mId allows you to update the notification later on.
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	}

	public void removeNotification() {
		if (mNotificationManager == null) {
			mNotificationManager = (NotificationManager) mContext
					.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		mNotificationManager.cancel(NOTIFICATION_ID);
	}
	
	private int getIconResId(String result){
		if(result.equals("private"))
		{
			return R.drawable.ic_stat_priv;
		} else if(result.equals("professional")){
			return R.drawable.ic_stat_prof;
		}
		
		return -1;
	}
}
