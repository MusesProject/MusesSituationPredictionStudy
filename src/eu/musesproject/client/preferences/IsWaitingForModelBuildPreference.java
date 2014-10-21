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



public class IsWaitingForModelBuildPreference extends AbstractBooleanPreference {

	private static IsWaitingForModelBuildPreference mInstance;
	private static final String KEY = "is_waiting_for_model_build";

	public static IsWaitingForModelBuildPreference getInstance() {
		if (mInstance == null) {
			mInstance = new IsWaitingForModelBuildPreference();
		}
		return mInstance;
	}

	@Override
	protected String getKey() {
		return KEY;
	}

}
