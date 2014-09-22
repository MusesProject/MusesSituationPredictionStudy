package eu.musesproject.client.prediction.dialog;

import eu.musesproject.client.classification.ClassificationController;
import eu.musesproject.client.prediction.session.SessionDataController;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * Class to listen for button press on the dialog which is (maybe) shown at the end of a session
 * @author D
 *
 */
public class SessionEndDialogListener implements OnClickListener {

	private Activity mActivity;

	public SessionEndDialogListener(Activity activity) {
		mActivity = activity;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_NEGATIVE:
			// professional, store
			SessionDataController.getInstance(mActivity.getApplicationContext()).storeUserSelection(LabelDialog.USER_SELECTION_PROFESSIONAL);
			
			// try to build the model
			// TODO activate
			ClassificationController.getInstance(mActivity.getApplicationContext()).buildModel();
			break;

		case DialogInterface.BUTTON_POSITIVE:
			// private, store
			SessionDataController.getInstance(mActivity.getApplicationContext()).storeUserSelection(LabelDialog.USER_SELECTION_PRIVATE);
			
			//try to build the model
			// TODO activate
			ClassificationController.getInstance(mActivity.getApplicationContext()).buildModel();
			break;

		case DialogInterface.BUTTON_NEUTRAL:
			// don't know, delete
			SessionDataController.getInstance(mActivity.getApplicationContext()).deleteSessionData();
			break;
		default:
			break;
		}
		mActivity.finish();
	}

}
