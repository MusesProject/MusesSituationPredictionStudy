package eu.musesproject.client.preferences;

/**
 * Class that holds the value indicating that the user has already labeled the
 * current session (need this to decide if we have to show another label dialog
 * at the end of a session)
 * 
 * @author D
 * 
 */
public class LabelingStatePreference extends AbstractBooleanPreference {

	private static final String KEY = "dialog_shown";
	private static LabelingStatePreference mInstance;

	public static LabelingStatePreference getInstance() {
		if (mInstance == null) {
			mInstance = new LabelingStatePreference();
		}
		return mInstance;
	}

	@Override
	protected String getKey() {
		return KEY;
	}
}
