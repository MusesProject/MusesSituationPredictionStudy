package eu.musesproject.client.builder;

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
