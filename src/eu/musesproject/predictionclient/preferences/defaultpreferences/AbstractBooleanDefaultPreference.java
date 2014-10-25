package eu.musesproject.predictionclient.preferences.defaultpreferences;

import android.content.SharedPreferences;

public abstract class AbstractBooleanDefaultPreference extends AbstractDefaultPreferences {
	
	public void set(boolean newValue) {
		SharedPreferences.Editor spE = getDefaultSharedPreferences().edit();
		spE.putBoolean(getKey(), newValue).commit();
	}

	public boolean get() {
		SharedPreferences sp = getDefaultSharedPreferences();
		if (sp.contains(getKey())) {
			return sp.getBoolean(getKey(), DefaultValues.BOOLEAN);
		}

		return DefaultValues.BOOLEAN;
	}

}
