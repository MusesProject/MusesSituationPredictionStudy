package eu.musesproject.client.classification;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import android.content.Context;
import android.database.Cursor;
import eu.musesproject.client.classification.ClassificationController.MODEL_DATA;
import eu.musesproject.client.contextmonitoring.sensors.AppSensor;
import eu.musesproject.client.contextmonitoring.sensors.ConnectivitySensor;
import eu.musesproject.client.contextmonitoring.sensors.FileSensor;
import eu.musesproject.client.contextmonitoring.sensors.PackageSensor;
import eu.musesproject.client.db.handler.DBManager;
import eu.musesproject.client.model.contextmonitoring.BluetoothState;
import eu.musesproject.client.prediction.preferences.LastSessionIdForModelPreference;
import eu.musesproject.contextmodel.PackageStatus;

public class InstanceBuilder {

	private ArrayList<TrackingObject> mTrackingArray;

	public InstanceBuilder() {

	}

	public Instance getInstance(Cursor sessionData, Context context,
			FastVector allAttributesVector) {
		mTrackingArray = new ArrayList<TrackingObject>();

		setTrackingArray(allAttributesVector);

		Instance instance = null;

		if (sessionData.getCount() != 0) {
			if (sessionData.moveToFirst()) {
				instance = new Instance(allAttributesVector.size());
				// every record has the users selection, we take it here
				// from the first record
				instance.setValue(
						(Attribute) allAttributesVector
								.elementAt(getIndexOfAttribute(MODEL_DATA.USER_SELECTION_ATTRIBUTE_NAME)),
						getUserSelection(sessionData));

				setInsertedStatus(MODEL_DATA.USER_SELECTION_ATTRIBUTE_NAME,
						true);

				do {
					String type = sessionData
							.getString(sessionData
									.getColumnIndex(DBManager.TYPE_CONTEXTEVENT_LABELING));

					String key = sessionData.getString(sessionData
							.getColumnIndex(DBManager.KEY_PROPERTY_LABELING));

					setValueToInstance(type, key, instance, sessionData,
							allAttributesVector);

				} while (sessionData.moveToNext());
				fillEmptyValues(instance, allAttributesVector);
				// trainingSet.add(instance);
			}
		}
		return instance;
	}

	private String getFileEventValue(Cursor cursor) {
		String value = cursor.getString(cursor
				.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));
		if (value.equals(FileSensor.MOVE_SELF)
				|| value.equals(FileSensor.MOVED_FROM)
				|| value.equals(FileSensor.MOVED_TO)) {
			return MODEL_DATA.FILESENSOR_MOVED;
		} else if (value.equals(FileSensor.CREATE)) {
			return MODEL_DATA.FILESENSOR_CREATE;
		} else if (value.equals(FileSensor.DELETE)) {
			return MODEL_DATA.FILESENSOR_DELETE;
		} else if (value.equals(FileSensor.MODIFY)) {
			return MODEL_DATA.FILESENSOR_MODIFY;
		} else if (value.equals(FileSensor.OPEN)) {
			return MODEL_DATA.FILESENSOR_OPEN;
		} else {
			return MODEL_DATA.NONE_STRING;
		}
	}

	private String getPackageStatusValue(Cursor cursor) {
		PackageStatus value = null;

		try {
			String cursorValue = cursor.getString(cursor
					.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));
			value = PackageStatus.valueOf(cursorValue);
		} catch (Exception e) {
			return MODEL_DATA.NONE_STRING;
		}

		if (value == PackageStatus.INSTALLED) {
			return MODEL_DATA.PACKAGE_STATUS_INSTALLED;
		} else if (value == PackageStatus.REMOVED) {
			return MODEL_DATA.PACKAGE_STATUS_REMOVED;
		} else if (value == PackageStatus.UPDATED) {
			return MODEL_DATA.PACKAGE_STATUS_UPDATED;
		} else {
			return MODEL_DATA.NONE_STRING;
		}
	}

	private String getAirplaneModeValue(Cursor cursor) {
		String value = cursor.getString(cursor
				.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));

		return value;
	}

	private String getBluetoothConnectedValue(Cursor cursor) {
		BluetoothState value = BluetoothState.valueOf(cursor.getString(cursor
				.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING)));

		if (value == BluetoothState.FALSE) {
			return MODEL_DATA.BLUETOOTH_FALSE;
		} else if (value == BluetoothState.TRUE) {
			return MODEL_DATA.BLUETOOTH_TRUE;
		} else {
			return MODEL_DATA.BLUETOOTH_NOT_SUPPORTED;
		}
	}

	private String getHiddenSSIDValue(Cursor cursor) {
		String value = cursor.getString(cursor
				.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));

		return value;
	}

	private int getWifiNeighborsValue(Cursor cursor) {
		String value = cursor.getString(cursor
				.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));

		return Integer.parseInt(value);
	}

	private String getWifiConnectedValue(Cursor cursor) {
		String value = cursor.getString(cursor
				.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));
		return value;
	}

	private String getWifiEnabledValue(Cursor cursor) {
		String value = cursor.getString(cursor
				.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));
		return value;
	}

	private String getMobileConnectedValue(Cursor cursor) {
		String value = cursor.getString(cursor
				.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));
		return value;
	}

	private String getAppValue(Cursor cursor) {
		return cursor.getString(cursor
				.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));
	}

	private String getUserSelection(Cursor allDataCursor) {
		return allDataCursor.getString(allDataCursor
				.getColumnIndex(DBManager.VALUE_USERSELECTION_LABELING));
	}

	private void fillEmptyValues(Instance instance,
			FastVector allAttributesVector) {
		for (TrackingObject to : mTrackingArray) {
			if (!to.mInserted) {
				if (to.mAttributeName
						.equals(FileSensor.PROPERTY_KEY_FILE_EVENT)) {

					// no true/false value
					instance.setValue((Attribute) allAttributesVector
							.elementAt(getIndexOfAttribute(to.mAttributeName)),
							MODEL_DATA.NONE_STRING);
				} else {
					instance.setValue((Attribute) allAttributesVector
							.elementAt(getIndexOfAttribute(to.mAttributeName)),
							MODEL_DATA.FALSE);
				}

			}
		}
	}

	private void setValueToInstance(String type, String key, Instance instance,
			Cursor cursor, FastVector allAttributesVector) {
		if (type != null && key != null) {
			if (type.equals(FileSensor.TYPE)
					&& key.equals(FileSensor.PROPERTY_KEY_FILE_EVENT)) {

				String value = getFileEventValue(cursor);

				instance.setValue(
						(Attribute) allAttributesVector
								.elementAt(getIndexOfAttribute(MODEL_DATA.FILESENSOR_ATTRIBUTE_NAME)),
						value);

				setInsertedStatus(MODEL_DATA.FILESENSOR_ATTRIBUTE_NAME, true);

			} else if (type.equals(ConnectivitySensor.TYPE)
					&& key.equals(ConnectivitySensor.PROPERTY_KEY_MOBILE_CONNECTED)) {

				String value = getMobileConnectedValue(cursor);

				instance.setValue(
						(Attribute) allAttributesVector
								.elementAt(getIndexOfAttribute(MODEL_DATA.MOBILE_CONNECTED_ATTRIBUTE_NAME)),
						value);

				setInsertedStatus(MODEL_DATA.MOBILE_CONNECTED_ATTRIBUTE_NAME,
						true);
			} else if (type.equals(ConnectivitySensor.TYPE)
					&& key.equals(ConnectivitySensor.PROPERTY_KEY_WIFI_ENABLED)) {

				String value = getWifiEnabledValue(cursor);

				instance.setValue(
						(Attribute) allAttributesVector
								.elementAt(getIndexOfAttribute(MODEL_DATA.WIFI_ENABLED_ATTRIBUTE_NAME)),
						value);

				setInsertedStatus(MODEL_DATA.WIFI_ENABLED_ATTRIBUTE_NAME, true);

			} else if (type.equals(ConnectivitySensor.TYPE)
					&& key.equals(ConnectivitySensor.PROPERTY_KEY_WIFI_CONNECTED)) {

				String value = getWifiConnectedValue(cursor);

				instance.setValue(
						(Attribute) allAttributesVector
								.elementAt(getIndexOfAttribute(MODEL_DATA.WIFI_CONNECTED_ATTRIBUTE_NAME)),
						value);
				setInsertedStatus(MODEL_DATA.WIFI_CONNECTED_ATTRIBUTE_NAME,
						true);

			} else if (type.equals(ConnectivitySensor.TYPE)
					&& key.equals(ConnectivitySensor.PROPERTY_KEY_WIFI_NEIGHBORS)) {

				int value = getWifiNeighborsValue(cursor);

				instance.setValue(
						(Attribute) allAttributesVector
								.elementAt(getIndexOfAttribute(MODEL_DATA.WIFI_NEIGHBORS_ATTRIBUTE_NAME)),
						value);
				setInsertedStatus(MODEL_DATA.WIFI_NEIGHBORS_ATTRIBUTE_NAME,
						true);

			} else if (type.equals(ConnectivitySensor.TYPE)
					&& key.equals(ConnectivitySensor.PROPERTY_KEY_HIDDEN_SSID)) {

				String value = getHiddenSSIDValue(cursor);

				instance.setValue(
						(Attribute) allAttributesVector
								.elementAt(getIndexOfAttribute(MODEL_DATA.HIDDEN_SSID_ATTRIBUTE_NAME)),
						value);
				setInsertedStatus(MODEL_DATA.HIDDEN_SSID_ATTRIBUTE_NAME, true);

			} else if (type.equals(ConnectivitySensor.TYPE)
					&& key.equals(ConnectivitySensor.PROPERTY_KEY_BLUETOOTH_CONNECTED)) {

				String value = getBluetoothConnectedValue(cursor);

				instance.setValue(
						(Attribute) allAttributesVector
								.elementAt(getIndexOfAttribute(MODEL_DATA.BLUETOOTH_CONNECTED_ATTRIBUTE_NAME)),
						value);
				setInsertedStatus(
						MODEL_DATA.BLUETOOTH_CONNECTED_ATTRIBUTE_NAME, true);

			} else if (type.equals(ConnectivitySensor.TYPE)
					&& key.equals(ConnectivitySensor.PROPERTY_KEY_AIRPLANE_MODE)) {

				String value = getAirplaneModeValue(cursor);

				instance.setValue(
						(Attribute) allAttributesVector
								.elementAt(getIndexOfAttribute(MODEL_DATA.AIRPLANE_MODE_ATTRIBUTE_NAME)),
						value);

				setInsertedStatus(MODEL_DATA.AIRPLANE_MODE_ATTRIBUTE_NAME, true);

			} else if (type.equals(PackageSensor.TYPE)
					&& key.equals(PackageSensor.PROPERTY_KEY_PACKAGE_STATUS)) {

				String value = getPackageStatusValue(cursor);

				instance.setValue(
						(Attribute) allAttributesVector
								.elementAt(getIndexOfAttribute(MODEL_DATA.PACKAGE_STATUS_ATTRIBUTE_NAME)),
						value);

				setInsertedStatus(MODEL_DATA.PACKAGE_STATUS_ATTRIBUTE_NAME,
						true);

			} else if (type.equals(AppSensor.TYPE)
					&& key.equals(AppSensor.PROPERTY_KEY_APP_NAME)) {
				String value = getAppValue(cursor);

				// special: value (appname) is attribute name
				instance.setValue((Attribute) allAttributesVector
						.elementAt(getIndexOfAttribute(value)), MODEL_DATA.TRUE);

				setInsertedStatus(value, true);
			}
		}
	}

	private void insertValue(Instance instance, FastVector allAttributesVector,
			String key, String value) {
		instance.setValue((Attribute) allAttributesVector
				.elementAt(getIndexOfAttribute(key)), value);

		setInsertedStatus(value, true);
	}

	private void setTrackingArray(FastVector allAttributesVector) {

		for (int i = 0; i < allAttributesVector.size(); ++i) {
			TrackingObject trackingObject = new TrackingObject();
			// trackingObject.mAttribute = (Attribute) allAttributesVector
			// .elementAt(i);
			trackingObject.mAttributeName = ((Attribute) allAttributesVector
					.elementAt(i)).name();
			trackingObject.mInserted = false;
			mTrackingArray.add(trackingObject);
		}
	}

	private int getIndexOfAttribute(String name) {
		for (int i = 0; i < mTrackingArray.size(); ++i) {
			if (mTrackingArray.get(i).mAttributeName.equals(name)) {
				return i;
			}
		}

		return -1;
	}

	private void setInsertedStatus(String attributeName, boolean status) {
		mTrackingArray.get(getIndexOfAttribute(attributeName)).mInserted = status;
	}

	private static class TrackingObject {
		public String mAttributeName;
		// Attribute mAttribute;
		boolean mInserted;

		@Override
		public boolean equals(Object o) {
			String name = (String) o;
			if (mAttributeName.equals(name)) {
				return true;
			} else {
				return false;
			}

		}
	}
}
