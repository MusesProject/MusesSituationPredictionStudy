package eu.musesproject.client.session.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import eu.musesproject.client.model.ModelController;
import eu.musesproject.client.preferences.AbstractPreference;
import eu.musesproject.client.preferences.IsClassificationActivatedPreference;
import eu.musesproject.client.preferences.IsLabelingActivatedPreference;
import eu.musesproject.client.preferences.IsModelCreatedPreference;
import eu.musesproject.client.preferences.IsWaitingForModelBuildPreference;
import eu.musesproject.client.preferences.ModelCountPreference;
import eu.musesproject.client.session.ISession;
import eu.musesproject.client.session.SessionIdGenerator;

public class SessionController extends BroadcastReceiver {
	public static final String ACTION_QUIT_SESSION = "quit_session";

	private ISession mClassificationSessionController;
	private ISession mLabelingSessionController;
	private ModelController mModelController;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent == null) {
			return;
		}

		String action = intent.getAction();

		if (action.equals(Intent.ACTION_USER_PRESENT)) {
			initController(context);

			if (IsLabelingActivatedPreference.getInstance().get(context)) {
				if (!checkForModelBuild(context)) {
					mLabelingSessionController.userPresent();
				}
			}

			if (IsModelCreatedPreference.getInstance().get(context)
					&& IsClassificationActivatedPreference.getInstance().get(
							context)) {
				mClassificationSessionController.userPresent();
			}

		} else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
			initController(context);

			if (IsLabelingActivatedPreference.getInstance().get(context)) {
				mLabelingSessionController.screenOff();
			}

			if (IsModelCreatedPreference.getInstance().get(context)
					&& IsClassificationActivatedPreference.getInstance().get(
							context)) {
				mClassificationSessionController.screenOff();
			}

			// if there are enough data for a model, we will build it here if
			// possible
			if (IsWaitingForModelBuildPreference.getInstance().get(context)) {
				mModelController.buildModel(context);
			}

		} else if (action.equals(Intent.ACTION_SCREEN_ON)) {
			initController(context);

			if (IsLabelingActivatedPreference.getInstance().get(context)) {
				mLabelingSessionController.screenOn();
			}

			if (IsModelCreatedPreference.getInstance().get(context)
					&& IsClassificationActivatedPreference.getInstance().get(
							context)) {
				mClassificationSessionController.screenOn();
			}
		} else if (action.equals(ACTION_QUIT_SESSION)) {
			initController(context);

			if (IsLabelingActivatedPreference.getInstance().get(context)) {
				mLabelingSessionController.quit();
			}

			if (IsModelCreatedPreference.getInstance().get(context)
					&& IsClassificationActivatedPreference.getInstance().get(
							context)) {
				mClassificationSessionController.quit();
			}
		}
	}

	private void initController(Context context) {
		if (mClassificationSessionController == null) {
			mClassificationSessionController = new ClassificationSessionController(
					context);
		}

		if (mLabelingSessionController == null) {
			mLabelingSessionController = new LabelingSessionController(context);
		}

		if (mModelController == null) {
			mModelController = new ModelController();
		}
	}

	private boolean checkForModelBuild(Context context) {
		// TODO better get count from DB
		if (ModelCountPreference.getInstance().get(context) != AbstractPreference.DefaultValues.INT
				&& SessionIdGenerator.getCurrentSessionId(context) >= ModelCountPreference
						.getInstance().get(context)) {
			if (mLabelingSessionController != null) {
				mLabelingSessionController.quit();
			}
			IsLabelingActivatedPreference.getInstance().set(context, false);
			IsWaitingForModelBuildPreference.getInstance().set(context, true);
			return true;
		}
		return false;
	}
}
