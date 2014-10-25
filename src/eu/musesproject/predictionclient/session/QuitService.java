package eu.musesproject.predictionclient.session;
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
import android.os.IBinder;
import eu.musesproject.predictionclient.session.controller.LabelingSessionController;
import eu.musesproject.predictionclient.session.controller.SessionController;

/**
 * Service to send a broadcast to {@link LabelingSessionController} to stop current
 * session. This service is started by the PendingIntent, which is send if the
 * screen goes off without lock.
 * 
 * We need this workaround because sending the PendingIntent directly to the
 * {@link LabelingSessionController} will create a new instance.
 * 
 * @author D
 * 
 */
public class QuitService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		sendBroadcast();
		this.stopSelf();
		return super.onStartCommand(intent, flags, startId);
	}

	private void sendBroadcast() {
		Intent intent = new Intent();
		intent.setAction(SessionController.ACTION_QUIT_SESSION);
		this.sendBroadcast(intent);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
