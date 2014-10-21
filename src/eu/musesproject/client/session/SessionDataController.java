package eu.musesproject.client.session;
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


import java.util.List;

import android.content.Context;
import eu.musesproject.client.contextmonitoring.SensorController;
import eu.musesproject.client.db.DBManager;
import eu.musesproject.contextmodel.ContextEvent;

public class SessionDataController {

	private static SessionDataController mInstance;
	private DBManager mDBManager;
	private Context mContext;
	private long mSessionStartTimestamp;

	// workaround flag to indicate that all data has been deleted
	private boolean mDataDeleted = false;

	private SessionDataController(Context context) {
		mContext = context;
		mDBManager = new DBManager(context);
	}

	public static SessionDataController getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new SessionDataController(context);
		}
		return mInstance;
	}

	public void startDataCollecting() {
		// reset the list with all contextevents
		SensorController.getInstance(mContext).resetAllContextEvents();

		// get timestamp of session start
		mSessionStartTimestamp = System.currentTimeMillis();

		// create a new entry with sessionid in userselection table
		insertNewSessionId();
		mDataDeleted = false;

	}

	private void insertNewSessionId() {
		mDBManager.openDB();
		mDBManager
				.insertSessionId(SessionIdGenerator.setNewSessionId(mContext));

		// if (result != -1) {
		// ModelCountPreference.getInstance().increment(mContext);
		// }
		mDBManager.closeDB();
	}

	public void storeSessionData() {
		// store to db
		SensorController sensorController = SensorController
				.getInstance(mContext);
		List<ContextEvent> sensorContextEvents = sensorController
				.getAllContextEvents();
		int sessionId = SessionIdGenerator.getCurrentSessionId(mContext);
		mDBManager.openDB();
		for (ContextEvent ce : sensorContextEvents) {
			if (ce.getTimestamp() > mSessionStartTimestamp) {
				mDBManager.insertLabelContextEvent(ce, sessionId);
			}
		}
		mDBManager.closeDB();
		mSessionStartTimestamp = 0L;
	}

	public void deleteSessionData() {
		// delete from db (ref sessionid)
		mDBManager.openDB();

		int result = mDBManager.deleteSessionData(SessionIdGenerator
				.getCurrentSessionId(mContext));
		// if (result != 0) {
		// ModelCountPreference.getInstance().decrement(mContext);
		// }
		mDBManager.closeDB();
	}

	public void storeUserSelection(String selection) {
		mDBManager.openDB();
		mDBManager.storeUserSelection(
				SessionIdGenerator.getCurrentSessionId(mContext), selection);
		mDBManager.closeDB();
	}

	public void deleteAllSessionData() {
		mDBManager.openDB();
		mDBManager.deleteAllSessionData();
		mDBManager.closeDB();

		mDataDeleted = true;
	}

	public boolean isDataDeleted() {
		return mDataDeleted;
	}
}
