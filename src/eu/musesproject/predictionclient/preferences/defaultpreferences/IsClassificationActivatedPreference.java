package eu.musesproject.predictionclient.preferences.defaultpreferences;

import android.content.Context;
import eu.musesproject.client.R;

public class IsClassificationActivatedPreference extends AbstractBooleanDefaultPreference {
	private static String KEY;
	private static IsClassificationActivatedPreference mInstance;

	private IsClassificationActivatedPreference(Context context) {
		mContext = context;
		KEY = context.getString(R.string.key_classification_switch);
	}

	public static IsClassificationActivatedPreference getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new IsClassificationActivatedPreference(context);
		}
		return mInstance;
	}

	@Override
	protected String getKey() {
		return KEY;
	}

}
