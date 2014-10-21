package eu.musesproject.client.builder;
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
import eu.musesproject.client.db.DBManager;
import eu.musesproject.client.model.ModelController.MODEL_DATA;
import eu.musesproject.client.session.SessionIdGenerator;

public class TrainingSetBuilder {

	public TrainingSetBuilder() {
	}

	public Instances createTrainingSet(Context context, DBManager dbManager,
			FastVector allAttributesVector, int classIndex) {

		// first, get the maximum session id
		int maxSessionId = SessionIdGenerator.getMaxSessionId(context);

		// Create an empty training set
		Instances trainingSet = new Instances(MODEL_DATA.MODEL_IDENTIFIER_NB,
				allAttributesVector, maxSessionId);
		
		// Set class index	
		trainingSet.setClassIndex(classIndex);
		InstanceBuilder instanceBuilder = new InstanceBuilder(allAttributesVector);

		for (int i = 0; i <= maxSessionId; ++i) {
			Cursor sessionData = dbManager.getAllLabeledDataForSessionId(i);
			Instance instance = instanceBuilder.getInstance(sessionData, trainingSet);
			if (instance != null) {
				trainingSet.add(instance);
			}
			sessionData.close();
		}
		
		trainingSet.compactify();

		

		return trainingSet;
	}

}
