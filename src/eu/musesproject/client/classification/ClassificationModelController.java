package eu.musesproject.client.classification;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.FastVector;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.BatteryManager;
import eu.musesproject.MUSESBackgroundService;
import eu.musesproject.client.contextmonitoring.sensors.ConnectivitySensor;
import eu.musesproject.client.contextmonitoring.sensors.FileSensor;
import eu.musesproject.client.contextmonitoring.sensors.PackageSensor;
import eu.musesproject.client.db.handler.DBManager;
import eu.musesproject.client.model.contextmonitoring.BluetoothState;
import eu.musesproject.client.prediction.preferences.ModelCountPreference;
import eu.musesproject.contextmodel.PackageStatus;

public class ClassificationModelController {

	public static final String FILESENSOR_MOVED = "moved";

	public static final String NONE_STRING = "none";

	public static final String TRUE = "true";
	public static final String FALSE = "false";

	private static ClassificationModelController mInstance;
	private Context mContext;
	
	private ArrayList<Attribute> mAttributesList;

	private ClassificationModelController(Context context) {
		mContext = context;
		mAttributesList = new ArrayList<Attribute>();
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
			if (ModelCountPreference.getInstance().get(mContext) >= 100) {
				// disable background service
				Intent stopIntent = new Intent(mContext,
						MUSESBackgroundService.class);
				mContext.stopService(stopIntent);

				// create model to train

				// attribute for filesensor
				// TODO one path for every sensor (need to query db)
				FastVector fileSensorVector = new FastVector(6);
				fileSensorVector.addElement(FileSensor.OPEN);
				fileSensorVector.addElement(FileSensor.MODIFY);
				fileSensorVector.addElement(FileSensor.CREATE);
				fileSensorVector.addElement(FileSensor.DELETE);
				fileSensorVector.addElement(FILESENSOR_MOVED);
				fileSensorVector.addElement(NONE_STRING);
				Attribute fileSensorAttribute = new Attribute(
						FileSensor.PROPERTY_KEY_FILE_EVENT, fileSensorVector);
				
				mAttributesList.add(fileSensorAttribute);

				
				// connectivity sensor, one attribute for every property
				FastVector mobileConnectedVector = new FastVector(2);
				mobileConnectedVector.addElement(TRUE);
				mobileConnectedVector.addElement(FALSE);
				Attribute mobileConnectedAttribute = new Attribute(
						ConnectivitySensor.PROPERTY_KEY_MOBILE_CONNECTED,
						mobileConnectedVector);
				
				mAttributesList.add(mobileConnectedAttribute);

				FastVector wifiEnabledVector = new FastVector(2);
				wifiEnabledVector.addElement(TRUE);
				wifiEnabledVector.addElement(FALSE);
				Attribute wifiEnabledAttribute = new Attribute(
						ConnectivitySensor.PROPERTY_KEY_WIFI_ENABLED,
						wifiEnabledVector);
				
				mAttributesList.add(wifiEnabledAttribute);

				FastVector wifiConnectedVector = new FastVector(2);
				wifiConnectedVector.addElement(TRUE);
				wifiConnectedVector.addElement(FALSE);
				Attribute wifiConnectedAttribute = new Attribute(
						ConnectivitySensor.PROPERTY_KEY_WIFI_CONNECTED,
						wifiConnectedVector);

				mAttributesList.add(wifiConnectedAttribute);
				
				Attribute wifiNeighborsAttribute = new Attribute(
						ConnectivitySensor.PROPERTY_KEY_WIFI_NEIGHBORS);
				
				mAttributesList.add(wifiNeighborsAttribute);

				FastVector hiddenSSIDVector = new FastVector(2);
				hiddenSSIDVector.addElement(TRUE);
				hiddenSSIDVector.addElement(FALSE);
				Attribute hiddenSSIDAttribute = new Attribute(
						ConnectivitySensor.PROPERTY_KEY_HIDDEN_SSID,
						hiddenSSIDVector);

				mAttributesList.add(hiddenSSIDAttribute);
				
				FastVector bluetoothStatusVector = new FastVector(3);
				bluetoothStatusVector.addElement(BluetoothState.TRUE);
				bluetoothStatusVector.addElement(BluetoothState.FALSE);
				bluetoothStatusVector.addElement(BluetoothState.NOT_SUPPORTED);
				Attribute bluetoothStatusAttribute = new Attribute(
						ConnectivitySensor.PROPERTY_KEY_BLUETOOTH_CONNECTED,
						bluetoothStatusVector);
				
				mAttributesList.add(bluetoothStatusAttribute);

				FastVector airplaneModeVector = new FastVector(2);
				airplaneModeVector.addElement(TRUE);
				airplaneModeVector.addElement(FALSE);
				Attribute airplaneModeAttribtue = new Attribute(
						ConnectivitySensor.PROPERTY_KEY_AIRPLANE_MODE,
						airplaneModeVector);

				mAttributesList.add(airplaneModeAttribtue);
				
				// package sensor
				FastVector packageStatusVektor = new FastVector(3);
				packageStatusVektor.addElement(PackageStatus.INSTALLED);
				packageStatusVektor.addElement(PackageStatus.REMOVED);
				packageStatusVektor.addElement(PackageStatus.UPDATED);
				Attribute packageStatusAttribute = new Attribute(PackageSensor.PROPERTY_KEY_PACKAGE_STATUS, packageStatusVektor);
				
				mAttributesList.add(packageStatusAttribute);
				
				DBManager dbManager = new DBManager(mContext);
				dbManager.openDB();
				Cursor allUsedApps = dbManager.getAllUsedAppNames();
				
				
				// add all the attributes to one feature vector
				FastVector allAttributesVector = new FastVector(mAttributesList.size() + allUsedApps.getCount());
				
				for(Attribute a : mAttributesList){
					allAttributesVector.addElement(a);
				}
				
				
				// for app sensor, check every app that was used and build an
				// attribute for EVERY used app with values true and false
				
				if(allUsedApps.moveToFirst()){
					do{
						FastVector appNameVector = new FastVector(2);
						appNameVector.addElement(TRUE);
						appNameVector.addElement(FALSE);
						Attribute appNameAttribute = new Attribute(allUsedApps.getString(allUsedApps.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING)), appNameVector);
						allAttributesVector.addElement(appNameAttribute);						
					} while (allUsedApps.moveToNext());
				}
				
				// fill the model
				Cursor allDataCursor = dbManager.getAllLabeledData();
				if (allDataCursor.moveToFirst()) {
					do {
						String type = allDataCursor
								.getString(allDataCursor
										.getColumnIndex(DBManager.TYPE_CONTEXTEVENT_LABELING));
						if (type.equals(FileSensor.TYPE)) {

						}

					} while (allDataCursor.moveToNext());

				}

				// end work
				allDataCursor.close();
				dbManager.closeDB();

				Intent startIntent = new Intent(mContext,
						MUSESBackgroundService.class);
				mContext.startService(startIntent);
			}
		}
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
