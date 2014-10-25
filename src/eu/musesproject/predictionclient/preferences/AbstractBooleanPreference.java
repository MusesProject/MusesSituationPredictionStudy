package eu.musesproject.predictionclient.preferences;
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
import android.content.SharedPreferences;

public abstract class AbstractBooleanPreference extends AbstractPreference {

	public void set(Context context, boolean newValue) {
		SharedPreferences.Editor spE = getSharedPreferences(context).edit();
		spE.putBoolean(getKey(), newValue).commit();
	}

	public boolean get(Context context) {
		SharedPreferences sp = getSharedPreferences(context);
		if (sp.contains(getKey())) {
			return sp.getBoolean(getKey(), DefaultValues.BOOLEAN);
		}

		return DefaultValues.BOOLEAN;
	}

}
