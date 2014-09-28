package eu.musesproject.client.preferences;

public class IsWaitingForModelBuildPreference extends AbstractBooleanPreference {

	private static IsWaitingForModelBuildPreference mInstance;
	private static final String KEY = "is_waiting_for_model_build";

	public static IsWaitingForModelBuildPreference getInstance() {
		if (mInstance == null) {
			mInstance = new IsWaitingForModelBuildPreference();
		}
		return mInstance;
	}

	@Override
	protected String getKey() {
		return KEY;
	}

}
