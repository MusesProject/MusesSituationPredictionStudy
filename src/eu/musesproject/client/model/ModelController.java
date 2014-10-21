package eu.musesproject.client.model;
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
import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.FastVector;
import weka.core.Instances;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import eu.musesproject.client.builder.FeatureVectorBuilder;
import eu.musesproject.client.builder.TrainingSetBuilder;
import eu.musesproject.client.contextmonitoring.sensors.ConnectivitySensor;
import eu.musesproject.client.contextmonitoring.sensors.PackageSensor;
import eu.musesproject.client.contextmonitoring.sensors.RecursiveFileSensor;
import eu.musesproject.client.contextmonitoring.sensors.RecursiveFileSensor.FileSensor;
import eu.musesproject.client.db.DBManager;
import eu.musesproject.client.model.contextmonitoring.BluetoothState;
import eu.musesproject.client.preferences.IsClassificationActivatedPreference;
import eu.musesproject.client.preferences.IsModelCreatedPreference;
import eu.musesproject.client.preferences.IsWaitingForModelBuildPreference;
import eu.musesproject.client.ui.LabelDialog;
import eu.musesproject.contextmodel.PackageStatus;

public class ModelController {

	private FeatureVectorBuilder mFeatureVectorBuilder;
	private TrainingSetBuilder mTrainingSetBuilder;
	private Classifier mClassifier;

	private FastVector mFeatureVector;
	private static ModelController mInstance;

	private ModelController() {
		mFeatureVectorBuilder = new FeatureVectorBuilder();
		mTrainingSetBuilder = new TrainingSetBuilder();
	}

	public static ModelController getInstance() {
		if (mInstance == null) {
			mInstance = new ModelController();
		}
		return mInstance;
	}

	public void buildModel(Context context) {
		// first, check if the device is charging or is
		if (checkChargingStatus(context)) {

			// open DB connection
			DBManager dbManager = new DBManager(context);
			dbManager.openDB();

			// get all app names to create an attribute for each of them in
			// feature vector
			ArrayList<String> allAppNames = dbManager
					.getAllUsedAppNamesAsArray();

			// get feature vector
			mFeatureVector = mFeatureVectorBuilder
					.createFeatureVector(allAppNames);

			// create training set
			Instances trainingSet = mTrainingSetBuilder.createTrainingSet(
					context, dbManager, mFeatureVector,
					mFeatureVectorBuilder.getClassIndex());

			dbManager.closeDB();

			mClassifier = trainClassifier(trainingSet);

			if (mClassifier != null) {
				try {
					ClassifierSerializer.serializeClassifier(mClassifier,
							ClassifierSerializer
									.getNaiveBayesSerializationPath());
					IsWaitingForModelBuildPreference.getInstance().set(context,
							false);
					IsModelCreatedPreference.getInstance().set(context, true);

					IsClassificationActivatedPreference.getInstance().set(
							context, true);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	public Classifier getClassifier(Context context) {
		if (mClassifier == null
				&& IsModelCreatedPreference.getInstance().get(context)) {
			mClassifier = ClassifierSerializer
					.deserializeClassifier(ClassifierSerializer
							.getNaiveBayesSerializationPath());
		}
		return mClassifier;
	}

	private boolean checkChargingStatus(Context context) {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = context.registerReceiver(null, ifilter);
		int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

		if (status == BatteryManager.BATTERY_STATUS_DISCHARGING
				|| status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
			return false;
		}

		return true;
	}

	public FastVector getFeatureVector(DBManager dbManager) {
		if (mFeatureVector == null) {
			ArrayList<String> allAppNames = dbManager
					.getAllUsedAppNamesAsArray();
			mFeatureVectorBuilder = new FeatureVectorBuilder();
			mFeatureVector = mFeatureVectorBuilder
					.createFeatureVector(allAppNames);
		}

		return mFeatureVector;
	}

	public int getClassIndex(DBManager dbManager) {
		if (mFeatureVectorBuilder == null) {
			ArrayList<String> allAppNames = dbManager
					.getAllUsedAppNamesAsArray();
			mFeatureVectorBuilder = new FeatureVectorBuilder();
			mFeatureVector = mFeatureVectorBuilder
					.createFeatureVector(allAppNames);
		}

		return mFeatureVectorBuilder.getClassIndex();
	}

	private Classifier trainClassifier(Instances trainingSet) {

		mClassifier = (Classifier) new NaiveBayes();
		try {
			mClassifier.buildClassifier(trainingSet);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return mClassifier;
	}

	public static class MODEL_DATA {

		public static final String MODEL_IDENTIFIER_NB = "naive_bayes_classifier";

		public static final String NONE_STRING = "none";

		public static final String TRUE = "true";
		public static final String FALSE = "false";

		public static final String FILESENSOR_ATTRIBUTE_NAME = RecursiveFileSensor.PROPERTY_KEY_FILE_EVENT;
		public static final String FILESENSOR_MOVED = "moved";
		public static final String FILESENSOR_OPEN = RecursiveFileSensor.OPEN;
		public static final String FILESENSOR_MODIFY = RecursiveFileSensor.MODIFY;
		public static final String FILESENSOR_CREATE = RecursiveFileSensor.CREATE;
		public static final String FILESENSOR_DELETE = RecursiveFileSensor.DELETE;

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

}
