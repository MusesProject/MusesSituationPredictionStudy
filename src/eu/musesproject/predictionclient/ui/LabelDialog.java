package eu.musesproject.predictionclient.ui;
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
import android.app.AlertDialog;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.WindowManager;
import eu.musesproject.client.R;
import eu.musesproject.predictionclient.ui.listener.SessionEndDialogListener;
import eu.musesproject.predictionclient.ui.listener.SessionStartDialogListener;

/**
 * Dialog that shows up to ask the user to label a session.
 * 
 * @author D
 * 
 */
public class LabelDialog extends Activity {

	public static final String ACTION_SESSION_START = "session_start";
	public static final String ACTION_SESSION_END = "session_end";
	public static final String ACTION_FINISH_ACTIVITY = "finish_activity";
	
	public static final String USER_SELECTION_PRIVATE = "private";
	public static final String USER_SELECTION_PROFESSIONAL = "professional";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String action = getIntent().getAction();
		showLabelDialog(action);

	}

	private void showLabelDialog(String action) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);

		OnClickListener listener = null;
		if (action.equals(ACTION_SESSION_START)) {
			listener = new SessionStartDialogListener(this);

			builder.setTitle(R.string.session_start_label_dialog_text);
			builder.setPositiveButton(R.string.label_dialog_later_button_text,
					listener);

			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

		} else if (action.equals(ACTION_SESSION_END)) {
			listener = new SessionEndDialogListener(this);

			builder.setTitle(R.string.session_end_label_dialog_text);
			builder.setPositiveButton(
					R.string.label_dialog_dontknow_button_text, listener);

			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

		}

		builder.setNegativeButton(R.string.label_dialog_private_button_text,
				listener);
		builder.setNeutralButton(
				R.string.label_dialog_professional_button_text, listener);

		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(false);

		dialog.show();
	}
}
