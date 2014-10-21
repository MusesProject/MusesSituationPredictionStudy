package eu.musesproject.client;
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
import eu.musesproject.client.db.DBManager;
import eu.musesproject.client.model.ClassifierSerializer;
import eu.musesproject.client.preferences.IsClassificationActivatedPreference;
import eu.musesproject.client.preferences.IsLabelingActivatedPreference;
import eu.musesproject.client.preferences.IsModelCreatedPreference;
import eu.musesproject.client.preferences.IsWaitingForModelBuildPreference;

public class Resetter {

	public static void resetAll(Context context) {
		IsClassificationActivatedPreference.getInstance().set(context, false);
		IsModelCreatedPreference.getInstance().set(context, false);
		IsLabelingActivatedPreference.getInstance().set(context, false);
		IsWaitingForModelBuildPreference.getInstance().set(context, false);

		DBManager dbManager = new DBManager(context);
		dbManager.openDB();
		dbManager.dropAllTables();
		dbManager.closeDB();

		File file = new File(ClassifierSerializer.getNaiveBayesSerializationPath());
		if(file.exists()){
			file.delete();
		}
		
		NotificationController.getInstance(context).removeNotification();
	}
}
