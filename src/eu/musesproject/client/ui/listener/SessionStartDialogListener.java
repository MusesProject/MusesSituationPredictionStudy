package eu.musesproject.client.ui.listener;
/*
 * #%L
 * musesclient
 * %%
 * Copyright (C) 2013 - 2014 HITEC
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import eu.musesproject.client.preferences.LabelingStatePreference;
import eu.musesproject.client.session.SessionDataController;
import eu.musesproject.client.ui.LabelDialog;

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
		case DialogInterface.BUTTON_NEUTRAL:
			//professional
			LabelingStatePreference.getInstance().set(mActivity.getApplicationContext(), true);
			SessionDataController.getInstance(mActivity.getApplicationContext()).storeUserSelection(LabelDialog.USER_SELECTION_PROFESSIONAL);
			break;

		case DialogInterface.BUTTON_NEGATIVE:
			// private
			LabelingStatePreference.getInstance().set(mActivity.getApplicationContext(), true);
			SessionDataController.getInstance(mActivity.getApplicationContext()).storeUserSelection(LabelDialog.USER_SELECTION_PRIVATE);
			break;
			
		case DialogInterface.BUTTON_POSITIVE:
			// ask later
			LabelingStatePreference.getInstance().set(mActivity.getApplicationContext(), false);
			break;
		default:
			break;
		}
		mActivity.finish();
	}

}
