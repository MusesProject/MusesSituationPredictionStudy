package eu.musesproject.predictionclient;

import com.crashlytics.android.Crashlytics;

import android.app.Application;


public class MussesSituationPredictionStudyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Crashlytics.start(this);

	}
}
