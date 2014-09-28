package eu.musesproject.client;

import java.io.File;

import android.content.Context;
import eu.musesproject.client.db.DBManager;
import eu.musesproject.client.model.ClassifierSerializer;
import eu.musesproject.client.preferences.IsClassificationActivatedPreference;
import eu.musesproject.client.preferences.IsLabelingActivatedPreference;
import eu.musesproject.client.preferences.IsModelCreatedPreference;
import eu.musesproject.client.preferences.IsWaitingForModelBuildPreference;

public class Resetter {

	public static void resetAll(Context context) {
		IsClassificationActivatedPreference.getInstance().set(context, false);
		IsModelCreatedPreference.getInstance().set(context, false);
		IsLabelingActivatedPreference.getInstance().set(context, false);
		IsWaitingForModelBuildPreference.getInstance().set(context, false);

		DBManager dbManager = new DBManager(context);
		dbManager.openDB();
		dbManager.dropAllTables();
		dbManager.closeDB();

		File file = new File(ClassifierSerializer.getNaiveBayesSerializationPath());
		if(file.exists()){
			file.delete();
		}
		
		NotificationController.getInstance(context).removeNotification();
	}
}
