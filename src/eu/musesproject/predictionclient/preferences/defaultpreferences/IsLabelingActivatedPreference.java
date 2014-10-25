package eu.musesproject.predictionclient.preferences.defaultpreferences;

import android.content.Context;
import eu.musesproject.client.R;

public class IsLabelingActivatedPreference extends AbstractBooleanDefaultPreference{
	public static String KEY;
	private static IsLabelingActivatedPreference mInstance;

	private IsLabelingActivatedPreference(Context context) {
		mContext = context;
		KEY = context.getString(R.string.key_labeling_switch);
	}

	public static IsLabelingActivatedPreference getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new IsLabelingActivatedPreference(context);
		}
		return mInstance;
	}

	@Override
	protected String getKey() {
		return KEY;
	}
}
