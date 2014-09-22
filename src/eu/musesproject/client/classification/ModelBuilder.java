package eu.musesproject.client.classification;

import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instances;
import eu.musesproject.client.classification.ClassificationController.MODEL_DATA;

public class ModelBuilder {

	private ArrayList<String> mAllAppNames;
	
	private int mClassIndex;

	private Classifier mClassifier;
	


	public ModelBuilder() {
	}

	public int getClassIndex(){
		return mClassIndex;
	}
	
	/**
	 * Creates feature vector and sets class index for classification
	 * 
	 * @param dbManager
	 * @return
	 */
	public FastVector createFeatureVector(ArrayList<String> allAppNames) {
		mAllAppNames = allAppNames;
		FastVector allAttributesVector = new FastVector();
		// create model to train

		// attribute for filesensor
		// TODO one path for every sensor (need to query db)

		// index 0
		FastVector fileSensorVector = new FastVector(6);
		fileSensorVector.addElement(MODEL_DATA.FILESENSOR_OPEN);
		fileSensorVector.addElement(MODEL_DATA.FILESENSOR_MODIFY);
		fileSensorVector.addElement(MODEL_DATA.FILESENSOR_CREATE);
		fileSensorVector.addElement(MODEL_DATA.FILESENSOR_DELETE);
		fileSensorVector.addElement(MODEL_DATA.FILESENSOR_MOVED);
		fileSensorVector.addElement(MODEL_DATA.NONE_STRING);
		Attribute fileSensorAttribute = new Attribute(
				MODEL_DATA.FILESENSOR_ATTRIBUTE_NAME, fileSensorVector);

		allAttributesVector.addElement(fileSensorAttribute);

		// index 1
		// connectivity sensor, one attribute for every property
		FastVector mobileConnectedVector = new FastVector(2);
		mobileConnectedVector.addElement(MODEL_DATA.TRUE);
		mobileConnectedVector.addElement(MODEL_DATA.FALSE);
		Attribute mobileConnectedAttribute = new Attribute(
				MODEL_DATA.MOBILE_CONNECTED_ATTRIBUTE_NAME,
				mobileConnectedVector);

		allAttributesVector.addElement(mobileConnectedAttribute);

		// index 2
		FastVector wifiEnabledVector = new FastVector(2);
		wifiEnabledVector.addElement(MODEL_DATA.TRUE);
		wifiEnabledVector.addElement(MODEL_DATA.FALSE);
		Attribute wifiEnabledAttribute = new Attribute(
				MODEL_DATA.WIFI_ENABLED_ATTRIBUTE_NAME, wifiEnabledVector);

		allAttributesVector.addElement(wifiEnabledAttribute);

		// index 3
		FastVector wifiConnectedVector = new FastVector(2);
		wifiConnectedVector.addElement(MODEL_DATA.TRUE);
		wifiConnectedVector.addElement(MODEL_DATA.FALSE);
		Attribute wifiConnectedAttribute = new Attribute(
				MODEL_DATA.WIFI_CONNECTED_ATTRIBUTE_NAME, wifiConnectedVector);

		allAttributesVector.addElement(wifiConnectedAttribute);

		// index 4
		Attribute wifiNeighborsAttribute = new Attribute(
				MODEL_DATA.WIFI_NEIGHBORS_ATTRIBUTE_NAME);

		allAttributesVector.addElement(wifiNeighborsAttribute);

		// index 5
		FastVector hiddenSSIDVector = new FastVector(2);
		hiddenSSIDVector.addElement(MODEL_DATA.TRUE);
		hiddenSSIDVector.addElement(MODEL_DATA.FALSE);
		Attribute hiddenSSIDAttribute = new Attribute(
				MODEL_DATA.HIDDEN_SSID_ATTRIBUTE_NAME, hiddenSSIDVector);

		allAttributesVector.addElement(hiddenSSIDAttribute);

		// index 6
		FastVector bluetoothStatusVector = new FastVector(3);
		bluetoothStatusVector.addElement(MODEL_DATA.BLUETOOTH_TRUE);
		bluetoothStatusVector.addElement(MODEL_DATA.BLUETOOTH_FALSE);
		bluetoothStatusVector.addElement(MODEL_DATA.BLUETOOTH_NOT_SUPPORTED);
		Attribute bluetoothStatusAttribute = new Attribute(
				MODEL_DATA.BLUETOOTH_CONNECTED_ATTRIBUTE_NAME,
				bluetoothStatusVector);

		allAttributesVector.addElement(bluetoothStatusAttribute);

		// index 7
		FastVector airplaneModeVector = new FastVector(2);
		airplaneModeVector.addElement(MODEL_DATA.TRUE);
		airplaneModeVector.addElement(MODEL_DATA.FALSE);
		Attribute airplaneModeAttribtue = new Attribute(
				MODEL_DATA.AIRPLANE_MODE_ATTRIBUTE_NAME, airplaneModeVector);

		allAttributesVector.addElement(airplaneModeAttribtue);

		// package sensor
		// index 8
		FastVector packageStatusVector = new FastVector(3);
		packageStatusVector.addElement(MODEL_DATA.PACKAGE_STATUS_INSTALLED);
		packageStatusVector.addElement(MODEL_DATA.PACKAGE_STATUS_REMOVED);
		packageStatusVector.addElement(MODEL_DATA.PACKAGE_STATUS_UPDATED);
		packageStatusVector.addElement(MODEL_DATA.NONE_STRING);
		Attribute packageStatusAttribute = new Attribute(
				MODEL_DATA.PACKAGE_STATUS_ATTRIBUTE_NAME, packageStatusVector);

		allAttributesVector.addElement(packageStatusAttribute);

		// user selection
		// index 9
		FastVector userSelectionVector = new FastVector(2);
		userSelectionVector.addElement(MODEL_DATA.USER_SELECTION_PRIVATE);
		userSelectionVector.addElement(MODEL_DATA.USER_SELECTION_PROFESSIONAL);
		Attribute userSelectionAttribute = new Attribute(
				MODEL_DATA.USER_SELECTION_ATTRIBUTE_NAME, userSelectionVector);

		allAttributesVector.addElement(userSelectionAttribute);

		// add all the attributes to one feature vector

		// for app sensor, check every app that was used and build an
		// attribute for EVERY used app with values true and false

		for (String appName : mAllAppNames) {
			FastVector appNameVector = new FastVector(2);
			appNameVector.addElement(MODEL_DATA.TRUE);
			appNameVector.addElement(MODEL_DATA.FALSE);
			Attribute appNameAttribute = new Attribute(appName, appNameVector);
			allAttributesVector.addElement(appNameAttribute);
		}

		mClassIndex = allAttributesVector.indexOf(userSelectionAttribute);

		return allAttributesVector;
	}
	
	
	public Classifier trainClassifier(Instances trainingSet){
		
		mClassifier = (Classifier) new NaiveBayes();
		try {
			mClassifier.buildClassifier(trainingSet);

			ClassifierSerializer.serializeClassifier(mClassifier, ClassifierSerializer.NB_MODEL_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mClassifier;
	}

}
