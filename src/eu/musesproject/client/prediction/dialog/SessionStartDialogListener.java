package eu.musesproject.client.prediction.dialog;

import eu.musesproject.client.prediction.preferences.LabelingStatePreference;
import eu.musesproject.client.prediction.session.SessionDataController;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * Class to listen for button press on the dialog which is shown at the beginning of a session
 * @author D
 *
 */
public class SessionStartDialogListener implements OnClickListener{

	private Activity mActivity;
	
	public SessionStartDialogListener(Activity activity)
	{
		mActivity = activity;
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_NEGATIVE:
			//professional
			LabelingStatePreference.getInstance().set(mActivity.getApplicationContext(), true);
			SessionDataController.getInstance(mActivity.getApplicationContext()).storeUserSelection(LabelDialog.USER_SELECTION_PROFESSIONAL);
			break;

		case DialogInterface.BUTTON_POSITIVE:
			// private
			LabelingStatePreference.getInstance().set(mActivity.getApplicationContext(), true);
			SessionDataController.getInstance(mActivity.getApplicationContext()).storeUserSelection(LabelDialog.USER_SELECTION_PRIVATE);
			break;
			
		case DialogInterface.BUTTON_NEUTRAL:
			// ask later
			LabelingStatePreference.getInstance().set(mActivity.getApplicationContext(), false);
			break;
		default:
			break;
		}
		mActivity.finish();
	}

}
