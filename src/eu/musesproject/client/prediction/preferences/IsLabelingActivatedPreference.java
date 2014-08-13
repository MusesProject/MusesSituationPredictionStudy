package eu.musesproject.client.prediction.preferences;

/**
 * Class to hold the current value of the "labeling"-setting.
 * 
 * @author D
 * 
 */
public class IsLabelingActivatedPreference extends AbstractBooleanPreference {

	private static final String KEY = "is_labeling_activated";
	private static IsLabelingActivatedPreference mInstance;

	public static IsLabelingActivatedPreference getInstance() {
		if (mInstance == null) {
			mInstance = new IsLabelingActivatedPreference();
		}
		return mInstance;
	}

	@Override
	protected String getKey() {
		return KEY;
	}

}
