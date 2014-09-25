package eu.musesproject.client.classification;

import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.BatteryManager;
import android.util.Log;
import eu.musesproject.client.contextmonitoring.SensorController;
import eu.musesproject.client.contextmonitoring.sensors.AppSensor;
import eu.musesproject.client.contextmonitoring.sensors.ConnectivitySensor;
import eu.musesproject.client.contextmonitoring.sensors.FileSensor;
import eu.musesproject.client.contextmonitoring.sensors.PackageSensor;
import eu.musesproject.client.db.handler.DBManager;
import eu.musesproject.client.model.contextmonitoring.BluetoothState;
import eu.musesproject.client.prediction.dialog.LabelDialog;
import eu.musesproject.client.prediction.preferences.AbstractPreference;
import eu.musesproject.client.prediction.preferences.LastSessionIdForModelPreference;
import eu.musesproject.client.prediction.preferences.ModelCountPreference;
import eu.musesproject.client.prediction.session.SessionDataController;
import eu.musesproject.client.prediction.session.SessionIdGenerator;
import eu.musesproject.contextmodel.PackageStatus;

public class ClassificationController {

	private static ClassificationController mInstance;
	private Context mContext;

	private Classifier mClassifier;

//	private ArrayList<String> mAllAppNames;
	private ModelBuilder mModelBuilder;
	private TrainingSetBuilder mTrainingSetBuilder;
//	private FastVector mAllAttributesVector;

	public static class MODEL_DATA {

		public static final String MODEL_IDENTIFIER_NB = "naive_bayes_classifier";

		public static final String NONE_STRING = "none";

		public static final String TRUE = "true";
		public static final String FALSE = "false";

		public static final String FILESENSOR_ATTRIBUTE_NAME = FileSensor.PROPERTY_KEY_FILE_EVENT;
		public static final String FILESENSOR_MOVED = "moved";
		public static final String FILESENSOR_OPEN = FileSensor.OPEN;
		public static final String FILESENSOR_MODIFY = FileSensor.MODIFY;
		public static final String FILESENSOR_CREATE = FileSensor.CREATE;
		public static final String FILESENSOR_DELETE = FileSensor.DELETE;

		public static final String MOBILE_CONNECTED_ATTRIBUTE_NAME = ConnectivitySensor.PROPERTY_KEY_MOBILE_CONNECTED;
		public static final String WIFI_ENABLED_ATTRIBUTE_NAME = ConnectivitySensor.PROPERTY_KEY_WIFI_ENABLED;
		public static final String WIFI_CONNECTED_ATTRIBUTE_NAME = ConnectivitySensor.PROPERTY_KEY_WIFI_CONNECTED;
		public static final String WIFI_NEIGHBORS_ATTRIBUTE_NAME = ConnectivitySensor.PROPERTY_KEY_WIFI_NEIGHBORS;

		public static final String BLUETOOTH_CONNECTED_ATTRIBUTE_NAME = ConnectivitySensor.PROPERTY_KEY_BLUETOOTH_CONNECTED;
		public static final String BLUETOOTH_TRUE = BluetoothState.TRUE
				.toString();
		public static final String BLUETOOTH_FALSE = BluetoothState.FALSE
				.toString();
		public static final String BLUETOOTH_NOT_SUPPORTED = BluetoothState.NOT_SUPPORTED
				.toString();

		public static final String HIDDEN_SSID_ATTRIBUTE_NAME = ConnectivitySensor.PROPERTY_KEY_HIDDEN_SSID;
		public static final String AIRPLANE_MODE_ATTRIBUTE_NAME = ConnectivitySensor.PROPERTY_KEY_AIRPLANE_MODE;

		public static final String PACKAGE_STATUS_ATTRIBUTE_NAME = PackageSensor.PROPERTY_KEY_PACKAGE_STATUS;
		public static final String PACKAGE_STATUS_INSTALLED = PackageStatus.INSTALLED
				.toString();
		public static final String PACKAGE_STATUS_REMOVED = PackageStatus.REMOVED
				.toString();
		public static final String PACKAGE_STATUS_UPDATED = PackageStatus.UPDATED
				.toString();

		public static final String USER_SELECTION_ATTRIBUTE_NAME = DBManager.VALUE_USERSELECTION_LABELING;
		public static final String USER_SELECTION_PRIVATE = LabelDialog.USER_SELECTION_PRIVATE;
		public static final String USER_SELECTION_PROFESSIONAL = LabelDialog.USER_SELECTION_PROFESSIONAL;
	}

	private ClassificationController(Context context) {
		mContext = context;
	}

	public static ClassificationController getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new ClassificationController(context);
		}
		return mInstance;
	}

	public void buildModel() {
		// first, check if the device is charging or is
		if (checkChargingStatus()) {

			// check if we reached 100 new datarecords (min)
			if (ModelCountPreference.getInstance().get(mContext) >= 2) {
				// disable background service
			
				// Intent stopIntent = new Intent(mContext,
				// MUSESBackgroundService.class);
				// mContext.stopService(stopIntent);

				// open DB connection
				DBManager dbManager = new DBManager(mContext);
				dbManager.openDB();

				// get all app names to create an attribute for each of them in
				// feature vector
				ArrayList<String> allAppNames = getAllUsedAppNames(dbManager);

				// create instance of ModelBuilder
				mModelBuilder = new ModelBuilder();

				// get feature vector
				FastVector allAttributesVector = mModelBuilder
						.createFeatureVector(allAppNames);

				// create instance of TrainingSetBuilder
				mTrainingSetBuilder = new TrainingSetBuilder();

				// create training set
				Instances trainingSet = mTrainingSetBuilder.createTrainingSet(
						mContext, dbManager, allAttributesVector,
						mModelBuilder.getClassIndex());

				dbManager.closeDB();

				mClassifier = mModelBuilder.trainClassifier(trainingSet);

				if(mClassifier != null){
					// resets the model count preference (we do not need to build a model every time)
					ModelCountPreference.getInstance().set(mContext, 0);
				}
				// Intent startIntent = new Intent(mContext,
				// MUSESBackgroundService.class);
				// mContext.startService(startIntent);
			}
		}
	}

	public double classifyDataRecord(int sessionId) {
		// check the preference with the last uses sessionID (indicates, that there is a model) and check if the user is in a "new session" 
		int lastSessionId = LastSessionIdForModelPreference.getInstance().get(
				mContext);
		if (lastSessionId != AbstractPreference.DefaultValues.INT && lastSessionId < SessionIdGenerator.getCurrentSessionId(mContext)) {

			
			
//			if (!SessionDataController.getInstance(mContext)
//					.isDataDeleted()) {
//				SessionDataController.getInstance(mContext)
//						.storeSessionData();
//			} else {
//				return Instance.missingValue();
//			}
			
			DBManager dbManager = new DBManager(mContext);
			dbManager.openDB();
			
			ArrayList<String> allAppNames = getAllUsedAppNames(dbManager, lastSessionId);
			ModelBuilder modelBuilder = new ModelBuilder();
			
			FastVector featureVector = modelBuilder.createFeatureVector(allAppNames);
			
			
//			Cursor sessionData = dbManager
//					.getAllLabeledDataForSessionId(sessionId);

			InstanceBuilder instanceBuilder = new InstanceBuilder();
			Instance instance = instanceBuilder.getInstance(SensorController.getInstance(mContext).getAllContextEvents(),
					featureVector);

			try {
				if (mClassifier == null) {
					mClassifier = ClassifierSerializer.deserializeClassifier();
				}

				if (mClassifier != null) {
					return mClassifier.classifyInstance(instance);
				}
			} catch (Exception e) {
				Log.e("Classification result:", "Could not classify instance");
				return Instance.missingValue();
			}

			dbManager.closeDB();

		}
		return Instance.missingValue();
	}

	private ArrayList<String> getAllUsedAppNames(DBManager dbManager) {
		Cursor allUsedApps = dbManager.getAllUsedAppNames();
		ArrayList<String> array = new ArrayList<String>();
		if (allUsedApps.moveToFirst()) {
			do {
				String key = allUsedApps.getString(allUsedApps
						.getColumnIndex(DBManager.KEY_PROPERTY_LABELING));
				if (key.equals(AppSensor.PROPERTY_KEY_APP_NAME)) {

					String appName = allUsedApps.getString(allUsedApps
							.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));
					if (!array.contains(appName)) {
						array.add(appName);
					}
				}
			} while (allUsedApps.moveToNext());
		}

		allUsedApps.close();

		return array;
	}

	private ArrayList<String> getAllUsedAppNames(DBManager dbManager,
			int sessionId) {
		Cursor allUsedApps = dbManager.getAllUsedAppNames(sessionId);
		ArrayList<String> array = new ArrayList<String>();
		if (allUsedApps.moveToFirst()) {
			do {
				String key = allUsedApps.getString(allUsedApps
						.getColumnIndex(DBManager.KEY_PROPERTY_LABELING));
				if (key.equals(AppSensor.PROPERTY_KEY_APP_NAME)) {

					String appName = allUsedApps.getString(allUsedApps
							.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));
					if (!array.contains(appName)) {
						array.add(appName);
					}
				}
			} while (allUsedApps.moveToNext());
		}

		allUsedApps.close();

		return array;
	}

	private boolean checkChargingStatus() {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = mContext.registerReceiver(null, ifilter);
		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

		if (status == BatteryManager.BATTERY_STATUS_DISCHARGING
				|| status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
			return false;
		}

		return true;
	}
}
