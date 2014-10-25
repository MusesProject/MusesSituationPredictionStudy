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



/**
 * Class that holds the value indicating that the user has already labeled the
 * current session (need this to decide if we have to show another label dialog
 * at the end of a session)
 * 
 * @author D
 * 
 */
public class LabelingStatePreference extends AbstractBooleanPreference {

	private static final String KEY = "dialog_shown";
	private static LabelingStatePreference mInstance;

	public static LabelingStatePreference getInstance() {
		if (mInstance == null) {
			mInstance = new LabelingStatePreference();
		}
		return mInstance;
	}

	@Override
	protected String getKey() {
		return KEY;
	}
}
