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
import android.content.res.Resources;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import eu.musesproject.client.R;
import eu.musesproject.client.contextmonitoring.ContextListener;
import eu.musesproject.client.contextmonitoring.UserContextMonitoringController;
import eu.musesproject.client.db.entity.SensorConfiguration;
import eu.musesproject.client.model.contextmonitoring.MailAttachment;
import eu.musesproject.client.model.contextmonitoring.MailContent;
import eu.musesproject.client.model.contextmonitoring.MailProperties;
import eu.musesproject.client.model.contextmonitoring.UISource;
import eu.musesproject.client.model.decisiontable.Action;
import eu.musesproject.client.model.decisiontable.ActionType;
import eu.musesproject.contextmodel.ContextEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteractionSensor extends AccessibilityService implements ISensor {
	private static final String TAG = InteractionSensor.class.getSimpleName();
	
	// sensor identifier
	public static final String TYPE = "CONTEXT_SENSOR_INTERACTION";

	private ContextListener listener;

	// stores all fired context events of this sensor
	private List<ContextEvent> contextEventHistory;

	// hold this value, because just specific apps shall be observed
	private String appName;

	// holds a value that indicates if the sensor is enabled or disabled
	private boolean sensorEnabled;

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
//		Log.d(TAG, "onAccessibilityEvent(AccessibilityEvent event) ||| package name: " + event.getPackageName());

	}


	@Override
	public void onInterrupt() {
		// ignore
	}

    private String getEventText(AccessibilityEvent event) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : event.getText()) {
            sb.append(s);
        }
        return sb.toString();
    }



	@Override
	public void configure(List<SensorConfiguration> config) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSensorType() {
		return TYPE;
	}
}