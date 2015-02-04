package eu.musesproject.client.contextmonitoring.sensors;

import java.util.ArrayList;
import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import eu.musesproject.client.contextmonitoring.ContextListener;
import eu.musesproject.contextmodel.ContextEvent;

/**
 * 
 * @author danielgleim, christophstanik
 * 
 */
public class InteractionSensor extends AccessibilityService implements ISensor {
	private final static String TAG = InteractionSensor.class.getSimpleName();

	// sensor identifier
	public static final String TYPE = "CONTEXT_SENSOR_INTERACTION";

	// context property keys
	public static final String PROPERTY_KEY_ID = "id";
	public static final String PROPERTY_FOREGROUND_APP_PACKAGENAME = "apppackage";
	public static final String PROPERTY_FOREGROUND_APP_NAME = "appname";
	public static final String PROPERTY_CONTAINS_PASSWORDFIELDS = "passwordfields";

	private ContextListener listener;

	// history of fired context events
	List<ContextEvent> contextEventHistory;

	// holds a value that indicates if the sensor is enabled or disabled
	private boolean sensorEnabled;

	public InteractionSensor() {
		contextEventHistory = new ArrayList<ContextEvent>(CONTEXT_EVENT_HISTORY_SIZE);

		init();
	}

	private void init() {
		sensorEnabled = false;
	}

	@Override
	public void enable() {
		if (!sensorEnabled) {
			sensorEnabled = true;
		}
	}

	private void createContextEvent(String actionType, String containsPasswordField, String foregroundAppPackageName, String appName) {
		// create context event
		ContextEvent contextEvent = new ContextEvent();
		contextEvent.setType(TYPE);
		contextEvent.setTimestamp(System.currentTimeMillis());
		contextEvent.addProperty(PROPERTY_KEY_ID, String.valueOf(contextEventHistory != null ? (contextEventHistory.size() + 1) : -1));
		contextEvent.addProperty(PROPERTY_FOREGROUND_APP_PACKAGENAME, foregroundAppPackageName);
		contextEvent.addProperty(PROPERTY_FOREGROUND_APP_NAME, appName);
		contextEvent.addProperty(PROPERTY_CONTAINS_PASSWORDFIELDS, containsPasswordField);
		
		if (listener != null) {
			listener.onEvent(contextEvent);
		}
	}


	@Override
	public void disable() {
		if (sensorEnabled) {
			sensorEnabled = false;
		}
	}

	@Override
	public void addContextListener(ContextListener listener) {
		this.listener = listener;
	}

	@Override
	public void removeContextListener(ContextListener listener) {
		this.listener = listener;
	}

	@Override
	public ContextEvent getLastFiredContextEvent() {
		if (contextEventHistory.size() > 0) {
			return contextEventHistory.get(contextEventHistory.size() - 1);
		} else {
			return null;
		}
	}

	@Override
	protected void onServiceConnected() {
		Log.d(TAG, "DetectPasswordFieldsAccessibilityService:onServiceConnected");
		super.onServiceConnected();
	}

	public void onDestroy() {
		Log.d(TAG, "DetectPasswordFieldsAccessibilityService:onDestroy");
		super.onDestroy();
	}


	@Override
	public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
		PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	}

	@Override
	public void onInterrupt() {
        Log.d(TAG, "DetectPasswordFieldsAccessibilityService:onInterrupt");

    }
}