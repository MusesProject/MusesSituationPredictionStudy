package eu.musesproject.client.prediction.session;

import eu.musesproject.client.contextmonitoring.SensorController;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service to send a broadcast to {@link SessionController} to stop current
 * session. This service is started by the PendingIntent, which is send if the
 * screen goes off without lock.
 * 
 * We need this workaround because sending the PendingIntent directly to the
 * {@link SessionController} will create a new instance.
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
