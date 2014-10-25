package eu.musesproject.predictionclient;
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


import java.io.File;

import android.content.Context;
import android.widget.Toast;
import eu.musesproject.client.R;
import eu.musesproject.predictionclient.db.DBManager;
import eu.musesproject.predictionclient.model.ClassifierSerializer;
import eu.musesproject.predictionclient.preferences.IsModelCreatedPreference;
import eu.musesproject.predictionclient.preferences.IsWaitingForModelBuildPreference;
import eu.musesproject.predictionclient.preferences.defaultpreferences.IsClassificationActivatedPreference;
import eu.musesproject.predictionclient.preferences.defaultpreferences.IsLabelingActivatedPreference;

public class Resetter {

	public static void resetAll(Context context) {
		IsClassificationActivatedPreference.getInstance(context).set(false);
		IsModelCreatedPreference.getInstance().set(context, false);
		IsLabelingActivatedPreference.getInstance(context).set(false);
		IsWaitingForModelBuildPreference.getInstance().set(context, false);

		context.deleteDatabase(DBManager.getDbName());

		File file = new File(ClassifierSerializer.getNaiveBayesSerializationPath());
		if(file.exists()){
			file.delete();
		}
		
		NotificationController.getInstance(context).removeNotification();
		
		Toast.makeText(context, R.string.toast_reset_model_text,
				Toast.LENGTH_LONG).show();
	}
}
