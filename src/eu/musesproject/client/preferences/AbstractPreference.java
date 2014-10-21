package eu.musesproject.client.preferences;
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

public abstract class AbstractPreference {

	private static final String SHARED_PREF = "shared_pref";

	protected SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
	}

	protected abstract String getKey();

	public static class DefaultValues {
		public static final int INT = -1;
		public static final boolean BOOLEAN = false;
		public static final String STRING = "default";
	}
}
