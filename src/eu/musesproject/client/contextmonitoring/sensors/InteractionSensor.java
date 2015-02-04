package eu.musesproject.client.contextmonitoring.sensors;

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

import android.accessibilityservice.AccessibilityService;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import eu.musesproject.client.contextmonitoring.ContextListener;
import eu.musesproject.client.db.entity.SensorConfiguration;
import eu.musesproject.contextmodel.ContextEvent;

import java.util.ArrayList;
import java.util.List;

public class InteractionSensor extends AccessibilityService implements ISensor {
    private static final String TAG = InteractionSensor.class.getSimpleName();

    // sensor identifier
    public static final String TYPE = "CONTEXT_SENSOR_INTERACTION";
    public static final String PROPERTY_KEY_APP_NAME 		= "appname";
    public static final String PROPERTY_KEY_VIEW_TYPE		= "packagename";
    public static final String PROPERTY_KEY_EVENT_TYPE		= "appversion";
    public static final String PROPERTY_KEY_EVENT_TEXT 	    = "backgroundprocess";

    private ContextListener listener;

    // stores all fired context events of this sensor
    private List<ContextEvent> contextEventHistory;

    // holds a value that indicates if the sensor is enabled or disabled
    private boolean sensorEnabled;


    private PackageManager pckManager;
    private PackageInfo pckInfo;


    public InteractionSensor() {
        init();
    }


    // initializes all necessary default values
    private void init() {
        sensorEnabled = false;
        contextEventHistory = new ArrayList<ContextEvent>(CONTEXT_EVENT_HISTORY_SIZE);
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
    public void enable() {
        if (!sensorEnabled) {
            sensorEnabled = true;
        }
    }

    @Override
    public void disable() {
        if (sensorEnabled) {
            sensorEnabled = false;
        }
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
    public void onAccessibilityEvent(AccessibilityEvent event) {
        long eventTime = System.currentTimeMillis();
        String appName = getAppName(event.getPackageName().toString());
        String viewType = event.getClassName().toString();
        String eventType = getEventType(event.getEventType());
        String eventText = getEventText(event);

        Log.d(TAG, "app:" + appName + " viewType:" + viewType + " viewText:" + eventText + " eventType:"+eventType + " eventTime:" + eventTime);
        createContextEvent(eventTime, appName, viewType, eventType, eventText);
    }


    @Override
    public void onInterrupt() {
        // ignore
    }

    private String getAppName(String pckName) {
        String appName = "";
        if(pckManager == null) {
            pckManager = getPackageManager();
        }
        try {
            pckInfo = pckManager.getPackageInfo(pckName, 0);
            appName = pckInfo.applicationInfo.loadLabel(pckManager).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return appName;
    }

    // returns the text of clicked view
    private String getEventText(AccessibilityEvent event) {
        if(event.getClassName().equals("android.widget.EditText")) {
            return ""; // do not store the written text
        }

        // if there is no privacy sensitive data. continue here
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : event.getText()) {
            sb.append(s);
        }
        return sb.toString();
    }

    private String getEventType(int eventTypeNumber) {
        String eventType = "";
        switch(eventTypeNumber) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                eventType = "clicked";
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                eventType = "scrolled";
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                eventType = "textChanged";
                break;
        }

        return eventType;
    }


    @Override
    public void configure(List<SensorConfiguration> config) {
        // ignore
    }

    @Override
    public String getSensorType() {
        return TYPE;
    }

    private void createContextEvent(long eventTime, String appName, String viewType, String eventType, String eventText) {
        ContextEvent contextEvent = new ContextEvent();
        contextEvent.setTimestamp(eventTime);
        contextEvent.setType(TYPE);
        contextEvent.addProperty(PROPERTY_KEY_APP_NAME, appName);
        contextEvent.addProperty(PROPERTY_KEY_VIEW_TYPE, viewType);
        contextEvent.addProperty(PROPERTY_KEY_EVENT_TYPE, eventType);
        contextEvent.addProperty(PROPERTY_KEY_EVENT_TEXT, eventText);

        // add context event to the context event history
        contextEventHistory.add(contextEvent);
        if(contextEventHistory.size() > CONTEXT_EVENT_HISTORY_SIZE) {
            contextEventHistory.remove(0);
        }

        if(listener != null) {
            listener.onEvent(contextEvent);
        }
    }
}