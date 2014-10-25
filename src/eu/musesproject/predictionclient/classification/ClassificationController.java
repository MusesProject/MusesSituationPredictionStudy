package eu.musesproject.predictionclient.classification;
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
import weka.classifiers.Classifier;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import android.content.Context;
import android.util.Log;
import eu.musesproject.predictionclient.NotificationController;
import eu.musesproject.predictionclient.builder.InstanceBuilder;
import eu.musesproject.predictionclient.builder.TrainingSetBuilder;
import eu.musesproject.predictionclient.contextmonitoring.SensorController;
import eu.musesproject.predictionclient.db.DBManager;
import eu.musesproject.predictionclient.model.ModelController;
import eu.musesproject.predictionclient.preferences.IsModelCreatedPreference;

public class ClassificationController {

	private static ClassificationController mInstance;
	private Context mContext;
	private Classifier mClassifier;
	private TrainingSetBuilder mTrainingSetBuilder;
	private Instances mTrainingSet;
	private ModelController mModelController;

	private ClassificationController(Context context) {
		mContext = context;
		mModelController = ModelController.getInstance();
		mTrainingSetBuilder = new TrainingSetBuilder();

	}

	public static ClassificationController getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new ClassificationController(context);
		}
		return mInstance;
	}

	public double classifyDataRecord() {
		// check the preference with the last uses sessionID (indicates, that
		// there is a model) and check if the user is in a "new session"
		if (IsModelCreatedPreference.getInstance().get(mContext)) {
			
			DBManager dbManager = new DBManager(mContext);
			dbManager.openDB();

			FastVector featureVector = mModelController
					.getFeatureVector(dbManager);

			// create training set
			if (mTrainingSet == null) {
				mTrainingSet = mTrainingSetBuilder.createTrainingSet(mContext,
						dbManager, featureVector,
						mModelController.getClassIndex(dbManager));
			}

			InstanceBuilder instanceBuilder = new InstanceBuilder(featureVector);
			Instance instance = instanceBuilder.getInstanceFromList(SensorController
					.getInstance(mContext).getAllContextEvents(), mTrainingSet);

			dbManager.closeDB();

			try {
				if (mClassifier == null) {
					mClassifier = mModelController.getClassifier(mContext);
				}

				if (mClassifier != null) {
					double result = mClassifier.classifyInstance(instance);
					NotificationController.getInstance(mContext)
							.updateNotification(
									instance.classAttribute().value(
											(int) result));
					return result;
				}
			} catch (Exception e) {
				Log.e("Classification result:", "Could not classify instance ");
				return Instance.missingValue();
			}

		}
		return Instance.missingValue();
	}
}
