package eu.musesproject.predictionclient.builder;

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
import java.util.List;
import java.util.Map;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import android.database.Cursor;
import android.util.Log;
import eu.musesproject.client.model.contextmonitoring.BluetoothState;
import eu.musesproject.contextmodel.ContextEvent;
import eu.musesproject.contextmodel.PackageStatus;
import eu.musesproject.predictionclient.contextmonitoring.sensors.AppSensor;
import eu.musesproject.predictionclient.contextmonitoring.sensors.ConnectivitySensor;
import eu.musesproject.predictionclient.contextmonitoring.sensors.PackageSensor;
import eu.musesproject.predictionclient.contextmonitoring.sensors.RecursiveFileSensor;
import eu.musesproject.predictionclient.db.DBManager;
import eu.musesproject.predictionclient.model.ModelController.MODEL_DATA;

public class InstanceBuilder {

	private ArrayList<TrackingObject> mTrackingArray;

	private FastVector mAllAttributesVector;

	public InstanceBuilder(FastVector allAttributesVector) {
		mAllAttributesVector = allAttributesVector;
		mTrackingArray = new ArrayList<TrackingObject>();
		setTrackingArray(mAllAttributesVector);
	}

	public Instance getInstanceFromCursor(Cursor sessionData, Instances trainingSet) {

		Instance instance = null;

		if (sessionData.getCount() != 0) {
			if (sessionData.moveToFirst()) {
				instance = new Instance(mAllAttributesVector.size());
				instance.setDataset(trainingSet);
				// every record has the users selection, we take it here
				// from the first record
				// if (!isInstanceToClassify) {
				instance.setValue(getIndexOfAttribute(MODEL_DATA.USER_SELECTION_ATTRIBUTE_NAME),
						getUserSelection(sessionData));

				// }

				setInsertedStatus(MODEL_DATA.USER_SELECTION_ATTRIBUTE_NAME, true);
				do {
					String type = sessionData.getString(sessionData
							.getColumnIndex(DBManager.TYPE_CONTEXTEVENT_LABELING));

					String key = sessionData.getString(sessionData.getColumnIndex(DBManager.KEY_PROPERTY_LABELING));

					setValueToInstance(type, key, instance, sessionData, mAllAttributesVector);

				} while (sessionData.moveToNext());
				fillEmptyValues(instance, mAllAttributesVector);
			}
		}
		return instance;
	}

	public Instance getInstanceFromList(List<ContextEvent> sensorContextEvents, Instances trainingSet) {

		Instance instance = new Instance(mAllAttributesVector.size());
		instance.setDataset(trainingSet);

		for (ContextEvent ce : sensorContextEvents) {
			setValueToInstance(ce, instance, mAllAttributesVector);
		}

		fillEmptyValues(instance, mAllAttributesVector);

		return instance;
	}

	private String getFileEventValue(String value) {
		// String value = cursor.getString(cursor
		// .getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));
		if (value.equals(RecursiveFileSensor.MOVE_SELF) || value.equals(RecursiveFileSensor.MOVED_FROM)
				|| value.equals(RecursiveFileSensor.MOVED_TO)) {
			return MODEL_DATA.FILESENSOR_MOVED;
		} else if (value.equals(RecursiveFileSensor.CREATE)) {
			return MODEL_DATA.FILESENSOR_CREATE;
		} else if (value.equals(RecursiveFileSensor.DELETE)) {
			return MODEL_DATA.FILESENSOR_DELETE;
		} else if (value.equals(RecursiveFileSensor.MODIFY)) {
			return MODEL_DATA.FILESENSOR_MODIFY;
		} else if (value.equals(RecursiveFileSensor.OPEN)) {
			return MODEL_DATA.FILESENSOR_OPEN;
		} else {
			return MODEL_DATA.NONE_STRING;
		}
	}

	private String getPackageStatusValue(String value) {
		PackageStatus packageStatusValue = null;

		try {
			packageStatusValue = PackageStatus.valueOf(value);
		} catch (Exception e) {
			return MODEL_DATA.NONE_STRING;
		}

		if (packageStatusValue == PackageStatus.INSTALLED) {
			return MODEL_DATA.PACKAGE_STATUS_INSTALLED;
		} else if (packageStatusValue == PackageStatus.REMOVED) {
			return MODEL_DATA.PACKAGE_STATUS_REMOVED;
		} else if (packageStatusValue == PackageStatus.UPDATED) {
			return MODEL_DATA.PACKAGE_STATUS_UPDATED;
		} else {
			return MODEL_DATA.NONE_STRING;
		}
	}

	// private String getAirplaneModeValue(Cursor cursor) {
	// String value = cursor.getString(cursor
	// .getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));
	//
	// return value;
	// }

	private String getBluetoothConnectedValue(String value) {
		BluetoothState bluetoothStateValuevalue = BluetoothState.valueOf(value);

		if (bluetoothStateValuevalue == BluetoothState.FALSE) {
			return MODEL_DATA.BLUETOOTH_FALSE;
		} else if (bluetoothStateValuevalue == BluetoothState.TRUE) {
			return MODEL_DATA.BLUETOOTH_TRUE;
		} else {
			return MODEL_DATA.BLUETOOTH_NOT_SUPPORTED;
		}
	}

	// private String getHiddenSSIDValue(Cursor cursor) {
	// String value = cursor.getString(cursor
	// .getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));
	//
	// return value;
	// }

	private int getWifiNeighborsValue(String value) {
		// String value = cursor.getString(cursor
		// .getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));

		return Integer.parseInt(value);
	}

	// private String getWifiConnectedValue(Cursor cursor) {
	// String value = cursor.getString(cursor
	// .getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));
	// return value;
	// }

	// private String getWifiEnabledValue(Cursor cursor) {
	// String value = cursor.getString(cursor
	// .getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));
	// return value;
	// }

	// private String getMobileConnectedValue(Cursor cursor) {
	// String value = cursor.getString(cursor
	// .getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));
	// return value;
	// }

	// private String getAppValue(Cursor cursor) {
	// return cursor.getString(cursor
	// .getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));
	// }

	private String getUserSelection(Cursor allDataCursor) {
		return allDataCursor.getString(allDataCursor.getColumnIndex(DBManager.VALUE_USERSELECTION_LABELING));
	}

	private void fillEmptyValues(Instance instance, FastVector mAllAttributesVector) {
		for (TrackingObject to : mTrackingArray) {
			if (!to.mInserted) {
				if (to.mAttributeName.equals(RecursiveFileSensor.PROPERTY_KEY_FILE_EVENT)
						|| to.mAttributeName.equals(PackageSensor.PROPERTY_KEY_PACKAGE_STATUS)) {

					// no true/false value
					instance.setValue(getIndexOfAttribute(to.mAttributeName), MODEL_DATA.NONE_STRING);
				} else if (to.mAttributeName.equals(ConnectivitySensor.PROPERTY_KEY_WIFI_NEIGHBORS)) {
					instance.setValue(getIndexOfAttribute(to.mAttributeName), 0);
				} else if (to.mAttributeName.equals(ConnectivitySensor.PROPERTY_KEY_BLUETOOTH_CONNECTED)) {
					instance.setValue(getIndexOfAttribute(to.mAttributeName), MODEL_DATA.BLUETOOTH_FALSE);
				} else if (to.mAttributeName.equals(MODEL_DATA.USER_SELECTION_ATTRIBUTE_NAME))

				{
				} else {

					try {
						instance.setValue(getIndexOfAttribute(to.mAttributeName), MODEL_DATA.FALSE);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}
	}

	private void setValueToInstance(ContextEvent ce, Instance instance, FastVector mAllAttributesVector) {
		Map<String, String> ceProperties = ce.getProperties();

		if (ce.getType().equals(RecursiveFileSensor.TYPE)) {
			if (ceProperties.containsKey(RecursiveFileSensor.PROPERTY_KEY_FILE_EVENT)) {
				String value = getFileEventValue(ceProperties.get(RecursiveFileSensor.PROPERTY_KEY_FILE_EVENT));

				insertValue(instance, mAllAttributesVector, MODEL_DATA.FILESENSOR_ATTRIBUTE_NAME, value);

				// instance.setValue(
				// (Attribute) mAllAttributesVector
				// .elementAt(getIndexOfAttribute(MODEL_DATA.FILESENSOR_ATTRIBUTE_NAME)),
				// getFileEventValue(value));
				//
				// setInsertedStatus(MODEL_DATA.FILESENSOR_ATTRIBUTE_NAME,
				// true);
			}
		} else if (ce.getType().equals(ConnectivitySensor.TYPE)) {
			if (ceProperties.containsKey(ConnectivitySensor.PROPERTY_KEY_MOBILE_CONNECTED)) {
				String value = ceProperties.get(ConnectivitySensor.PROPERTY_KEY_MOBILE_CONNECTED);

				insertValue(instance, mAllAttributesVector, MODEL_DATA.MOBILE_CONNECTED_ATTRIBUTE_NAME, value);
			}

			if (ceProperties.containsKey(ConnectivitySensor.PROPERTY_KEY_WIFI_ENABLED)) {
				String value = ceProperties.get(ConnectivitySensor.PROPERTY_KEY_WIFI_ENABLED);

				insertValue(instance, mAllAttributesVector, MODEL_DATA.WIFI_ENABLED_ATTRIBUTE_NAME, value);
			}

			if (ceProperties.containsKey(ConnectivitySensor.PROPERTY_KEY_WIFI_CONNECTED)) {
				String value = ceProperties.get(ConnectivitySensor.PROPERTY_KEY_WIFI_CONNECTED);

				insertValue(instance, mAllAttributesVector, MODEL_DATA.WIFI_CONNECTED_ATTRIBUTE_NAME, value);
			}

			if (ceProperties.containsKey(ConnectivitySensor.PROPERTY_KEY_WIFI_NEIGHBORS)) {
				int value = getWifiNeighborsValue(ceProperties.get(ConnectivitySensor.PROPERTY_KEY_WIFI_NEIGHBORS));

				insertValue(instance, mAllAttributesVector, MODEL_DATA.WIFI_NEIGHBORS_ATTRIBUTE_NAME, value);
			}

			if (ceProperties.containsKey(ConnectivitySensor.PROPERTY_KEY_HIDDEN_SSID)) {
				String value = ceProperties.get(ConnectivitySensor.PROPERTY_KEY_HIDDEN_SSID);

				insertValue(instance, mAllAttributesVector, MODEL_DATA.HIDDEN_SSID_ATTRIBUTE_NAME, value);
			}

			if (ceProperties.containsKey(ConnectivitySensor.PROPERTY_KEY_BLUETOOTH_CONNECTED)) {
				String value = getBluetoothConnectedValue(ceProperties
						.get(ConnectivitySensor.PROPERTY_KEY_BLUETOOTH_CONNECTED));

				insertValue(instance, mAllAttributesVector, MODEL_DATA.BLUETOOTH_CONNECTED_ATTRIBUTE_NAME, value);
			}

			if (ceProperties.containsKey(ConnectivitySensor.PROPERTY_KEY_AIRPLANE_MODE)) {
				String value = ceProperties.get(ConnectivitySensor.PROPERTY_KEY_AIRPLANE_MODE);

				insertValue(instance, mAllAttributesVector, MODEL_DATA.AIRPLANE_MODE_ATTRIBUTE_NAME, value);
			}
		} else if (ce.getType().equals(PackageSensor.TYPE)) {
			if (ceProperties.containsKey(PackageSensor.PROPERTY_KEY_PACKAGE_STATUS)) {
				String value = getPackageStatusValue(ceProperties.get(PackageSensor.PROPERTY_KEY_PACKAGE_STATUS));

				insertValue(instance, mAllAttributesVector, MODEL_DATA.PACKAGE_STATUS_ATTRIBUTE_NAME, value);
			}
		} else if (ce.getType().equals(AppSensor.TYPE)) {
			if (ceProperties.containsKey(AppSensor.PROPERTY_KEY_APP_NAME)) {
				String appName = ceProperties.get(AppSensor.PROPERTY_KEY_APP_NAME);
				if (getIndexOfAttribute(appName) != -1) {
					insertValue(instance, mAllAttributesVector, appName, MODEL_DATA.TRUE);
				}
			}
		}
	}

	private void setValueToInstance(String type, String key, Instance instance, Cursor cursor,
			FastVector mAllAttributesVector) {
		if (type != null && key != null) {
			if (type.equals(RecursiveFileSensor.TYPE) && key.equals(RecursiveFileSensor.PROPERTY_KEY_FILE_EVENT)) {

				String value = cursor.getString(cursor.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));

				instance.setValue(getIndexOfAttribute(MODEL_DATA.FILESENSOR_ATTRIBUTE_NAME), getFileEventValue(value));

				setInsertedStatus(MODEL_DATA.FILESENSOR_ATTRIBUTE_NAME, true);

			} else if (type.equals(ConnectivitySensor.TYPE)
					&& key.equals(ConnectivitySensor.PROPERTY_KEY_MOBILE_CONNECTED)) {

				String value = cursor.getString(cursor.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));

				instance.setValue(getIndexOfAttribute(MODEL_DATA.MOBILE_CONNECTED_ATTRIBUTE_NAME), value);

				setInsertedStatus(MODEL_DATA.MOBILE_CONNECTED_ATTRIBUTE_NAME, true);
			} else if (type.equals(ConnectivitySensor.TYPE) && key.equals(ConnectivitySensor.PROPERTY_KEY_WIFI_ENABLED)) {

				String value = cursor.getString(cursor.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));

				instance.setValue(getIndexOfAttribute(MODEL_DATA.WIFI_ENABLED_ATTRIBUTE_NAME), value);

				setInsertedStatus(MODEL_DATA.WIFI_ENABLED_ATTRIBUTE_NAME, true);

			} else if (type.equals(ConnectivitySensor.TYPE)
					&& key.equals(ConnectivitySensor.PROPERTY_KEY_WIFI_CONNECTED)) {

				String value = cursor.getString(cursor.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));

				instance.setValue(getIndexOfAttribute(MODEL_DATA.WIFI_CONNECTED_ATTRIBUTE_NAME), value);
				setInsertedStatus(MODEL_DATA.WIFI_CONNECTED_ATTRIBUTE_NAME, true);

			} else if (type.equals(ConnectivitySensor.TYPE)
					&& key.equals(ConnectivitySensor.PROPERTY_KEY_WIFI_NEIGHBORS)) {

				int value = getWifiNeighborsValue(cursor.getString(cursor
						.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING)));

				instance.setValue(getIndexOfAttribute(MODEL_DATA.WIFI_NEIGHBORS_ATTRIBUTE_NAME), value);

				setInsertedStatus(MODEL_DATA.WIFI_NEIGHBORS_ATTRIBUTE_NAME, true);

			} else if (type.equals(ConnectivitySensor.TYPE) && key.equals(ConnectivitySensor.PROPERTY_KEY_HIDDEN_SSID)) {

				String value = cursor.getString(cursor.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));

				instance.setValue(getIndexOfAttribute(MODEL_DATA.HIDDEN_SSID_ATTRIBUTE_NAME), value);
				setInsertedStatus(MODEL_DATA.HIDDEN_SSID_ATTRIBUTE_NAME, true);

			} else if (type.equals(ConnectivitySensor.TYPE)
					&& key.equals(ConnectivitySensor.PROPERTY_KEY_BLUETOOTH_CONNECTED)) {

				String value = getBluetoothConnectedValue(cursor.getString(cursor
						.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING)));

				instance.setValue(getIndexOfAttribute(MODEL_DATA.BLUETOOTH_CONNECTED_ATTRIBUTE_NAME), value);
				setInsertedStatus(MODEL_DATA.BLUETOOTH_CONNECTED_ATTRIBUTE_NAME, true);

			} else if (type.equals(ConnectivitySensor.TYPE)
					&& key.equals(ConnectivitySensor.PROPERTY_KEY_AIRPLANE_MODE)) {

				String value = cursor.getString(cursor.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));

				instance.setValue(getIndexOfAttribute(MODEL_DATA.AIRPLANE_MODE_ATTRIBUTE_NAME), value);

				setInsertedStatus(MODEL_DATA.AIRPLANE_MODE_ATTRIBUTE_NAME, true);

			} else if (type.equals(PackageSensor.TYPE) && key.equals(PackageSensor.PROPERTY_KEY_PACKAGE_STATUS)) {

				String value = getPackageStatusValue(cursor.getString(cursor
						.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING)));

				instance.setValue(getIndexOfAttribute(MODEL_DATA.PACKAGE_STATUS_ATTRIBUTE_NAME), value);

				setInsertedStatus(MODEL_DATA.PACKAGE_STATUS_ATTRIBUTE_NAME, true);

			} else if (type.equals(AppSensor.TYPE) && key.equals(AppSensor.PROPERTY_KEY_APP_NAME)) {
				String value = cursor.getString(cursor.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING));

				// special: value (appname) is attribute name
				instance.setValue(getIndexOfAttribute(value), MODEL_DATA.TRUE);

				setInsertedStatus(value, true);
			}
		}
	}

	private void insertValue(Instance instance, FastVector mAllAttributesVector, String key, String value) {
		int index = getIndexOfAttribute(key);
		Log.e("INDEX IN TRACKING ARRAY: " + key, index + "");
		instance.setValue(index, value);
		setInsertedStatus(key, true);
	}

	private void insertValue(Instance instance, FastVector mAllAttributesVector, String key, int value) {
		instance.setValue(getIndexOfAttribute(key), value);

		setInsertedStatus(key, true);
	}

	private void setTrackingArray(FastVector mAllAttributesVector) {

		for (int i = 0; i < mAllAttributesVector.size(); ++i) {
			TrackingObject trackingObject = new TrackingObject();
			// trackingObject.mAttribute = (Attribute) mAllAttributesVector
			// .elementAt(i);
			trackingObject.mAttributeName = ((Attribute) mAllAttributesVector.elementAt(i)).name();
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
