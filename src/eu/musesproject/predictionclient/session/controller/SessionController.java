package eu.musesproject.predictionclient.session.controller;


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


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import eu.musesproject.predictionclient.NotificationController;
import eu.musesproject.predictionclient.model.ModelController;
import eu.musesproject.predictionclient.preferences.AbstractPreference;
import eu.musesproject.predictionclient.preferences.IsModelCreatedPreference;
import eu.musesproject.predictionclient.preferences.IsWaitingForModelBuildPreference;
import eu.musesproject.predictionclient.preferences.defaultpreferences.IsClassificationActivatedPreference;
import eu.musesproject.predictionclient.preferences.defaultpreferences.IsLabelingActivatedPreference;
import eu.musesproject.predictionclient.preferences.defaultpreferences.SessionCountPreference;
import eu.musesproject.predictionclient.session.ISession;
import eu.musesproject.predictionclient.session.SessionIdGenerator;

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

			if (IsLabelingActivatedPreference.getInstance(context).get()) {
				if (!checkForModelBuild(context)) {
					mLabelingSessionController.userPresent();
				}
			}

			if (IsModelCreatedPreference.getInstance().get(context)
					&& IsClassificationActivatedPreference.getInstance(context).get(
							)) {
				mClassificationSessionController.userPresent();
			}

		} else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
			initController(context);

			if (IsLabelingActivatedPreference.getInstance(context).get()) {
				mLabelingSessionController.screenOff();
			}

			if (IsModelCreatedPreference.getInstance().get(context)
					&& IsClassificationActivatedPreference.getInstance(context).get(
							)) {
				mClassificationSessionController.screenOff();
			}

			// if there are enough data for a model, we will build it here if
			// possible
			if (IsWaitingForModelBuildPreference.getInstance().get(context)) {

				if (mModelController == null) {
					mModelController = ModelController.getInstance();
				}
				mModelController.buildModel(context);
			}

		} else if (action.equals(Intent.ACTION_SCREEN_ON)) {
			initController(context);

			if (IsLabelingActivatedPreference.getInstance(context).get()) {
				mLabelingSessionController.screenOn();
			}

			if (IsModelCreatedPreference.getInstance().get(context)
					&& IsClassificationActivatedPreference.getInstance(context).get(
							)) {
				mClassificationSessionController.screenOn();
			}
		} else if (action.equals(ACTION_QUIT_SESSION)) {
			initController(context);

			if (IsLabelingActivatedPreference.getInstance(context).get()) {
				mLabelingSessionController.quit();
			}

			if (IsModelCreatedPreference.getInstance().get(context)
					&& IsClassificationActivatedPreference.getInstance(context).get(
							)) {
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

	}

	private boolean checkForModelBuild(Context context) {
		// TODO better get count from DB
		if (SessionCountPreference.getInstance(context).get() != AbstractPreference.DefaultValues.INT
				&& SessionIdGenerator.getCurrentSessionId(context) >= SessionCountPreference
						.getInstance(context).get()) {
			if (mLabelingSessionController != null) {
				mLabelingSessionController.quit();
			}
			IsLabelingActivatedPreference.getInstance(context).set(false);
			IsWaitingForModelBuildPreference.getInstance().set(context, true);
			return true;
		}
		return false;
	}

	public void quit(Context context) {
		if (mLabelingSessionController != null) {
			mLabelingSessionController.quit();
		}
		if (mClassificationSessionController != null) {
			mClassificationSessionController.quit();
		}
		
		NotificationController.getInstance(context).removeNotification();
	}
}
