package eu.musesproject.client.prediction.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class AbstractPreference {

	private static final String SHARED_PREF = "shared_pref";

	protected SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
	}

	protected boolean clear(Context context, String key) {
		return getSharedPreferences(context).edit().remove(key).commit();
	}

	protected abstract String getKey();

	public static class DefaultValues {
		public static final int INT = -1;
		public static final boolean BOOLEAN = false;
		public static final String STRING = "default";
	}
}
