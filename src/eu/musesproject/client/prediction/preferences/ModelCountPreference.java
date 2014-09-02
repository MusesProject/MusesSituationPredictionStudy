package eu.musesproject.client.prediction.preferences;

import android.content.Context;

public class ModelCountPreference extends AbstractIntPreference {

	private static final String KEY = "model_count_preference";
	private static ModelCountPreference mInstance;
	
	public static ModelCountPreference getInstance(){
		if(mInstance == null){
			mInstance = new ModelCountPreference();
		}
		return mInstance;
	}
	
	@Override
	protected String getKey() {
		return KEY;
	}

	public void increment(Context context){
		int currentVale = get(context);
		if(currentVale == DefaultValues.INT){
			currentVale = 0;
		}
		++currentVale;
		set(context, currentVale);
	}
	
	public void decrement(Context context){
		int currentVale = get(context);
		if(currentVale == DefaultValues.INT){
			currentVale = 1;
		}
		--currentVale;
		set(context, currentVale);
	}
}
