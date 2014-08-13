package eu.musesproject.client.prediction.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class AbstractIntPreference extends AbstractPreference{

	public void set(Context context, int newValue) {
        SharedPreferences.Editor spE = getSharedPreferences(context).edit();
        spE.putInt(getKey(), newValue).commit();
    }

    public int get(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        int return_value = DefaultValues.INT;
        if (sp.contains(getKey())) {
            return_value = sp.getInt(getKey(), DefaultValues.INT);
        }

        return return_value;
    }
}
