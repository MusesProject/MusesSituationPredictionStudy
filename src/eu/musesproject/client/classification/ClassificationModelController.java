package eu.musesproject.client.classification;

import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.core.FastVector;
import weka.core.Instances;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.BatteryManager;
import eu.musesproject.MUSESBackgroundService;
import eu.musesproject.client.contextmonitoring.sensors.AppSensor;
import eu.musesproject.client.contextmonitoring.sensors.ConnectivitySensor;
import eu.musesproject.client.contextmonitoring.sensors.FileSensor;
import eu.musesproject.client.contextmonitoring.sensors.PackageSensor;
import eu.musesproject.client.db.handler.DBManager;
import eu.musesproject.client.model.contextmonitoring.BluetoothState;
import eu.musesproject.client.prediction.dialog.LabelDialog;
import eu.musesproject.client.prediction.preferences.ModelCountPreference;
import eu.musesproject.contextmodel.PackageStatus;

public class ClassificationModelController {

	

	private static ClassificationModelController mInstance;
	private Context mContext;

	
	private Classifier mClassifier;

	private ArrayList<String> mAllAppNames;
	private ModelBuilder mModelBuilder;
	private TrainingSetBuilder mTrainingSetBuilder;
	


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

	private ClassificationModelController(Context context) {
		mContext = context;
	}

	public static ClassificationModelController getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new ClassificationModelController(context);
		}
		return mInstance;
	}

	public void buildModel() {
		// first, check if the device is charging or is
		if (checkChargingStatus()) {

			// check if we reached 100 new datarecords (min)
			if (ModelCountPreference.getInstance().get(mContext) >= 2) {
				// disable background service
//				Intent stopIntent = new Intent(mContext,
//						MUSESBackgroundService.class);
//				mContext.stopService(stopIntent);

				// open DB connection
				DBManager dbManager = new DBManager(mContext);
				dbManager.openDB();

				// get all app names to create an attribute for each of them in feature vector
				mAllAppNames = getAllUsedAppNames(dbManager);
				
				// create instance of ModelBuilder
				mModelBuilder = new ModelBuilder();
				
				// get feature vector
				FastVector allAttributesVector = mModelBuilder.createFeatureVector(mAllAppNames);

				
				// create instance of TrainingSetBuilder
				mTrainingSetBuilder = new TrainingSetBuilder();
				
				// create training set
				Instances trainingSet = mTrainingSetBuilder.createTrainingSet(mContext, dbManager,
						allAttributesVector, mModelBuilder.getClassIndex());

				dbManager.closeDB();

				mClassifier = mModelBuilder.trainClassifier(trainingSet);
				
//				Intent startIntent = new Intent(mContext,
//						MUSESBackgroundService.class);
//				mContext.startService(startIntent);
			}
		}
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
					if (!array.contains(appName))
						array.add(appName);
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
