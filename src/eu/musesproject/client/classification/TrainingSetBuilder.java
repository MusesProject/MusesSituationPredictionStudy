package eu.musesproject.client.classification;

import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import android.content.Context;
import android.database.Cursor;
import eu.musesproject.client.classification.ClassificationController.MODEL_DATA;
import eu.musesproject.client.db.handler.DBManager;
import eu.musesproject.client.prediction.preferences.LastSessionIdForModelPreference;
import eu.musesproject.client.prediction.session.SessionIdGenerator;

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

		InstanceBuilder instanceBuilder = new InstanceBuilder();

		for (int i = 0; i <= maxSessionId; ++i) {
			Cursor sessionData = dbManager.getAllLabeledDataForSessionId(i);
			Instance instance = instanceBuilder.getInstance(sessionData,
					allAttributesVector);
			if (instance != null) {
				trainingSet.add(instance);

				// here, we track the last used sessionID (highest) for the
				// training set.
				LastSessionIdForModelPreference.getInstance().set(context, i);
			}
			sessionData.close();
		}

		// Set class index
		trainingSet.setClassIndex(classIndex);

		return trainingSet;
	}

}
