package eu.musesproject.client.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class AbstractBooleanPreference extends AbstractPreference {

	public void set(Context context, boolean newValue) {
		SharedPreferences.Editor spE = getSharedPreferences(context).edit();
		spE.putBoolean(getKey(), newValue).commit();
	}

	public boolean get(Context context) {
		SharedPreferences sp = getSharedPreferences(context);
		if (sp.contains(getKey())) {
			return sp.getBoolean(getKey(), DefaultValues.BOOLEAN);
		}

		return DefaultValues.BOOLEAN;
	}

}
