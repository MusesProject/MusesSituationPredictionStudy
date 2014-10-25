package eu.musesproject.predictionclient.preferences.defaultpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public abstract class AbstractDefaultPreferences {

	protected Context mContext;
	
	protected SharedPreferences getDefaultSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	protected abstract String getKey();

	public static class DefaultValues {
		public static final int INT = -1;
		public static final boolean BOOLEAN = false;
		public static final String STRING = "default";
	}
}
