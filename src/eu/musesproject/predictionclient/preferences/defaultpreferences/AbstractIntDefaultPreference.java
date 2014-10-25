package eu.musesproject.predictionclient.preferences.defaultpreferences;

import android.content.SharedPreferences;

public abstract class AbstractIntDefaultPreference extends AbstractDefaultPreferences {
	public void set(int newValue) {
        SharedPreferences.Editor spE = getDefaultSharedPreferences().edit();
        spE.putInt(getKey(), newValue).commit();
    }

    public int get() {
        SharedPreferences sp = getDefaultSharedPreferences();

        if (sp.contains(getKey())) {
            return sp.getInt(getKey(), DefaultValues.INT);
        }

        return DefaultValues.INT;
    }
}
