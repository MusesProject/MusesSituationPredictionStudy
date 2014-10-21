package eu.musesproject.client.session.controller;

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

import android.content.Context;
import eu.musesproject.client.contextmonitoring.UserContextMonitoringController;
import eu.musesproject.client.session.ISession;

public class ClassificationSessionController implements ISession {

	private Context mContext;

	public ClassificationSessionController(Context context) {
		mContext = context;
	}

	@Override
	public void userPresent() {
	}

	@Override
	public void screenOn() {
		UserContextMonitoringController.getInstance(mContext)
				.startContextObservation();
	}

	@Override
	public void screenOff() {
		UserContextMonitoringController.getInstance(mContext)
				.stopContextObservation();
	}

	@Override
	public void quit() {
		UserContextMonitoringController.getInstance(mContext)
				.stopContextObservation();
	}

}
