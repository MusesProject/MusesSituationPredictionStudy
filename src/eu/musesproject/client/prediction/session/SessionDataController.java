package eu.musesproject.client.prediction.session;

import java.util.List;

import android.content.Context;
import eu.musesproject.client.contextmonitoring.SensorController;
import eu.musesproject.client.db.handler.DBManager;
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

	public static SessionDataController getInstance(Context context){
		if (mInstance == null)
		{
			mInstance = new SessionDataController(context);
		}
		return mInstance;
	}
	
	public void startDataCollecting() {
		// reset the list with all contextevents
		SensorController.getInstance(mContext).resetAllContextEvents();
		
		// get timestamp of session start
		mSessionStartTimestamp = System.currentTimeMillis();

		// // get reference to sessioncontroller
		// SensorController sensorController =
		// SensorController.getInstance(mContext);

		// create a new entry with sessionid in userselection table
		mDBManager.openDB();
		mDBManager
				.insertSessionId(SessionIdGenerator.setNewSessionId(mContext));
		mDBManager.closeDB();
		
		mDataDeleted = false;
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
		mDBManager.deleteSessionData(SessionIdGenerator.getCurrentSessionId(mContext));
		mDBManager.closeDB();
	}
	
	public void storeUserSelection(String selection){
		mDBManager.openDB();
		mDBManager.storeUserSelection(SessionIdGenerator.getCurrentSessionId(mContext),selection);
		mDBManager.closeDB();	
	}

	public void deleteAllSessionData() {
		mDBManager.openDB();
		mDBManager.deleteAllSessionData();
		mDBManager.closeDB();
		
		mDataDeleted = true;
	}
	
	public boolean isDataDeleted(){
		return mDataDeleted;
	}
}
