package eu.musesproject.predictionclient.preferences.defaultpreferences;

import android.content.Context;
import eu.musesproject.client.R;

public class SessionCountPreference extends AbstractIntDefaultPreference {
	private static String KEY;
	private static SessionCountPreference mInstance;

	private SessionCountPreference(Context context) {
		mContext = context;
		KEY = context.getString(R.string.key_session_count_preference);
	}

	public static SessionCountPreference getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new SessionCountPreference(context);
		}
		return mInstance;
	}

	@Override
	protected String getKey() {
		return KEY;
	}
}
