package eu.musesproject.client.prediction.session;

import android.content.Context;
import eu.musesproject.client.prediction.preferences.SessionIdPreference;

/**
 * Class to create a unique session id. This represents a unique number for
 * every usage of the smartphone.
 * 
 * @author D
 * 
 */
public class SessionIdGenerator {

	public static int getCurrentSessionId(Context context) {
		return SessionIdPreference.getInstance().get(context);
	}

	public static int setNewSessionId(Context context) {
		int oldSessionId = SessionIdPreference.getInstance().get(context);
		SessionIdPreference.getInstance().set(context, ++oldSessionId);
		return oldSessionId;
	}
}
