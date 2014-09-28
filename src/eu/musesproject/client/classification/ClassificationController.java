package eu.musesproject.client.classification;

import weka.classifiers.Classifier;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import android.content.Context;
import android.util.Log;
import eu.musesproject.client.NotificationController;
import eu.musesproject.client.builder.InstanceBuilder;
import eu.musesproject.client.builder.TrainingSetBuilder;
import eu.musesproject.client.contextmonitoring.SensorController;
import eu.musesproject.client.db.DBManager;
import eu.musesproject.client.model.ModelController;
import eu.musesproject.client.preferences.IsModelCreatedPreference;

public class ClassificationController {

	private static ClassificationController mInstance;
	private Context mContext;
	private Classifier mClassifier;
	private TrainingSetBuilder mTrainingSetBuilder;
	private Instances mTrainingSet;
	private ModelController mModelController;

	private ClassificationController(Context context) {
		mContext = context;
		mModelController = new ModelController();
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
			Instance instance = instanceBuilder.getInstance(SensorController
					.getInstance(mContext).getAllContextEvents(), mTrainingSet);

			dbManager.closeDB();
			
			try {
				if (mClassifier == null) {
					mClassifier = mModelController.getClassifier(mContext);
				}

				if (mClassifier != null) {
					double result = mClassifier.classifyInstance(instance);
					NotificationController.getInstance(mContext).updateNotification(result +"");
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
