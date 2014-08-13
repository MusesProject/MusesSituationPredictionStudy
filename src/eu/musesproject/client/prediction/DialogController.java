package eu.musesproject.client.prediction;

import android.content.Context;
import android.content.Intent;
import eu.musesproject.client.prediction.dialog.LabelDialog;

public class DialogController {

	public static void showLabelDialog(Context context, String action) {
		Intent intent = new Intent(context, LabelDialog.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(action);
		context.startActivity(intent);
	}
}
