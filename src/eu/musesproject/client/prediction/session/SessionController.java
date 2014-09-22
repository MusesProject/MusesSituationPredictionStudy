package eu.musesproject.client.prediction.session;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import eu.musesproject.client.classification.ClassificationController;
import eu.musesproject.client.contextmonitoring.UserContextMonitoringController;
import eu.musesproject.client.prediction.DialogController;
import eu.musesproject.client.prediction.dialog.LabelDialog;
import eu.musesproject.client.prediction.preferences.IsLabelingActivatedPreference;
import eu.musesproject.client.prediction.preferences.LabelingStatePreference;

/**
 * Class that implements the "time period" of a session. It shows the needed
 * dialogs and starts and stops collection data for a session.
 * 
 * @author D
 * 
 */
public class SessionController extends BroadcastReceiver {

	public static final String ACTION_QUIT_SESSION = "quit_session";
	private boolean mIsSessionRunning = false;

	public SessionController() {

	}

	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = null;

		if (intent != null) {
			action = intent.getAction();
		}

		if (action.equals(ACTION_QUIT_SESSION)) {
			mIsSessionRunning = false;
			// TODO delete data for session
			SessionDataController.getInstance(context).deleteSessionData();
			UserContextMonitoringController.getInstance(context)
					.stopContextObservation();
		}

		if (IsLabelingActivatedPreference.getInstance().get(context)) {

			if (action.equals(Intent.ACTION_USER_PRESENT)) {
				// show dialog to let user label before session starts

				// show dialog only if no session is running
				if (!mIsSessionRunning) {
					startSession(context);
					// TODO test
//					ClassificationController.getInstance(context).buildModel();
				}
			} else if (action.equals(Intent.ACTION_SCREEN_OFF)) {

				if (mIsSessionRunning) {
					// session is running (collecting data)
					// know, check if user makes a phone call (sensor
					// deactivates screen, but don't stop session)
					TelephonyManager telephonyManager = (TelephonyManager) context
							.getSystemService(Context.TELEPHONY_SERVICE);
					if (telephonyManager.getCallState() == TelephonyManager.CALL_STATE_IDLE) {

						// no call, now check if the device is locked (user
						// turned screen off, so stop session)
						// or not (screen timeout turned screen off, don't
						KeyguardManager keyguardManager = (KeyguardManager) context
								.getSystemService(Activity.KEYGUARD_SERVICE);
						if (keyguardManager.isKeyguardLocked()) {
							// no call and screen is locked (user turned
							// screen off), so stop session (don't detect
							// screen
							// timeout, this will end session)
							if (LabelingStatePreference.getInstance().get(
									context)) {
								// user has already labeled the session,
								// just
								// end
								// session

								// TODO end session, save data
								if (!SessionDataController.getInstance(context)
										.isDataDeleted()) {
									SessionDataController.getInstance(context)
											.storeSessionData();
								}
								// TODO activate
								ClassificationController.getInstance(context).buildModel();
							} else {
								// user hasn't labeled the session, ask him
								// again
								// and end session
								if (!SessionDataController.getInstance(context)
										.isDataDeleted()) {
									DialogController.showLabelDialog(context,
											LabelDialog.ACTION_SESSION_END);

									// TODO save data and save with label from
									// dialog
									SessionDataController.getInstance(context)
											.storeSessionData();
								}
							}
							mIsSessionRunning = false;
							UserContextMonitoringController
									.getInstance(context)
									.stopContextObservation();
						} else {
							// no call, but screen is locked by the display
							// timeout
							// so start timer to delete the session data if
							// screen doesn't turned on again by the user
							// before the device gets locked (we won't ask
							// the user to label that session, because the
							// smartphone seems to be "out of focus")

							startDeleteSessionDataTimer(context);
						}
					}
				}
			} else if (action.equals(Intent.ACTION_SCREEN_ON)) {
				if (mIsSessionRunning) {
					quitDeleteSessionDataTimer(context);
					// mIsSessionRunning = false;
				}
			}

		} else {

			if (mIsSessionRunning) {
				// the current session has to be quit here
				mIsSessionRunning = false;
				// TODO delete data
				SessionDataController.getInstance(context).deleteSessionData();
				UserContextMonitoringController.getInstance(context)
						.stopContextObservation();
			}
		}
	}

	private void startSession(Context context) {
		UserContextMonitoringController.getInstance(context)
				.startContextObservation();

		SessionDataController.getInstance(context)
		.startDataCollecting();
		
		DialogController.showLabelDialog(context,
				LabelDialog.ACTION_SESSION_START);
		// TODO start collecting data for session (check
		// sessionid)
		mIsSessionRunning = true;
	}

	private void quitDeleteSessionDataTimer(Context context) {
		final Intent intent = new Intent(context, QuitService.class);
		intent.setAction(ACTION_QUIT_SESSION);
		final PendingIntent pi = PendingIntent.getService(context, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		final AlarmManager mgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		mgr.cancel(pi);
	}

	@SuppressLint("NewApi")
	private void startDeleteSessionDataTimer(Context context) {
		final Intent intent = new Intent(context, QuitService.class);
		intent.setAction(ACTION_QUIT_SESSION);
		final AlarmManager mgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		Calendar c = Calendar.getInstance();
		final PendingIntent pi = PendingIntent.getService(context, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			mgr.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 10000,
					pi);
		} else {
			mgr.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 10000, pi);
		}
	}
}
