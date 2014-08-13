package eu.musesproject.client.prediction.preferences;

/**
 * Class that holds the current session id.
 * 
 * @author D
 * 
 */
public class SessionIdPreference extends AbstractIntPreference {

	private static final String KEY = "session_id";
	private static SessionIdPreference mInstance;

	public static SessionIdPreference getInstance() {
		if (mInstance == null) {
			mInstance = new SessionIdPreference();
		}
		return mInstance;
	}

	@Override
	protected String getKey() {
		return KEY;
	}

}
