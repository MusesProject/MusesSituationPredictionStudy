package eu.musesproject.client.preferences;

public class IsModelCreatedPreference extends AbstractBooleanPreference {
	
	private static final String KEY = "is_model_created";
	private static IsModelCreatedPreference mInstance;

	public static IsModelCreatedPreference getInstance(){
		if(mInstance == null){
			mInstance = new IsModelCreatedPreference();
		}
		return mInstance;
	}
	@Override
	protected String getKey() {
		return KEY;
	}
	
	
}
