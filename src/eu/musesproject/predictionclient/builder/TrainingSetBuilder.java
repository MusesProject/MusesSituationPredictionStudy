package eu.musesproject.predictionclient.builder;

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
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import eu.musesproject.predictionclient.db.DBManager;
import eu.musesproject.predictionclient.model.ModelController.MODEL_DATA;
import eu.musesproject.predictionclient.session.SessionIdGenerator;

public class TrainingSetBuilder {

	public TrainingSetBuilder() {
	}

	public Instances createTrainingSet(Context context, DBManager dbManager, FastVector allAttributesVector,
			int classIndex) {

		// first, get the maximum session id
		int maxSessionId = SessionIdGenerator.getMaxSessionId(context);

		// Create an empty training set
		Instances trainingSet = new Instances(MODEL_DATA.MODEL_IDENTIFIER_NB, allAttributesVector, maxSessionId);

		// Set class index
		trainingSet.setClassIndex(classIndex);
		InstanceBuilder instanceBuilder = new InstanceBuilder(allAttributesVector);
		System.out.println("Max Sessions: " + maxSessionId);

		for (int i = 1; i <= maxSessionId; ++i) {
			Cursor sessionData = dbManager.getAllLabeledDataForSessionId(i);
			sessionData.moveToFirst();
			if (sessionData.getCount() > 0) {
				try{
					Log.d("createTrainingSet", "SessionID user selection: " + sessionData.getString(sessionData.getColumnIndex(DBManager.VALUE_USERSELECTION_LABELING)));
				} catch (Exception e){
					e.printStackTrace();
					Log.d("createTrainingSet", "SessionID Exception: " + i);
				}
				Instance instance = instanceBuilder.getInstanceFromCursor(sessionData, trainingSet);
				if (instance != null) {
					trainingSet.add(instance);
				}
			}
			else {
				Log.d("createTrainingSet", "SessionID NOT used: " + i);

			}
			sessionData.close();
		}

		trainingSet.compactify();

		return trainingSet;
	}

}
