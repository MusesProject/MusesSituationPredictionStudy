package eu.musesproject.client.session;
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
import eu.musesproject.client.preferences.AbstractPreference.DefaultValues;
import eu.musesproject.client.preferences.SessionIdPreference;

/**
 * Class to create a unique session id. This represents a unique number for
 * every usage of the smartphone.
 * 
 * @author D
 * 
 */
public class SessionIdGenerator {

	public static int getCurrentSessionId(Context context) {
		return SessionIdPreference.getInstance().get(context);
	}

	public static int setNewSessionId(Context context) {
		int oldSessionId = SessionIdPreference.getInstance().get(context);
		if(oldSessionId == DefaultValues.INT){
			oldSessionId = 0;
		}
		SessionIdPreference.getInstance().set(context, ++oldSessionId);
		return oldSessionId;
	}
	
	public static int getMaxSessionId(Context context) {
		return getCurrentSessionId(context);
	}
}
