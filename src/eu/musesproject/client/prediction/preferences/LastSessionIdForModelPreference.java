package eu.musesproject.client.prediction.preferences;

public class LastSessionIdForModelPreference extends AbstractIntPreference{

	private static final String KEY = "last_sessionId_for_model";
	private static LastSessionIdForModelPreference mInstance;
	
	public static LastSessionIdForModelPreference getInstance(){
		if(mInstance == null){
			mInstance = new LastSessionIdForModelPreference();
		}
		return mInstance;
	}
	
	@Override
	protected String getKey() {
		return KEY;
	}

}
