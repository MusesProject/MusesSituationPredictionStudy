package eu.musesproject.client.preferences;

public class IsClassificationActivatedPreference extends
		AbstractBooleanPreference {

	private static final String KEY = "is_classification_activated";
	private static IsClassificationActivatedPreference mInstance;

	public static IsClassificationActivatedPreference getInstance() {
		if (mInstance == null) {
			mInstance = new IsClassificationActivatedPreference();
		}
		return mInstance;
	}

	@Override
	protected String getKey() {

		return KEY;
	}

}
