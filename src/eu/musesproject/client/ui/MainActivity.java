package eu.musesproject.client.ui;

/*
 * #%L
 * MUSES Client
 * %%
 * Copyright (C) 2013 - 2014 Sweden Connectivity
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
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import eu.musesproject.MUSESBackgroundService;
import eu.musesproject.client.R;
import eu.musesproject.client.actuators.ActuatorController;
import eu.musesproject.client.connectionmanager.NetworkChecker;
import eu.musesproject.client.contextmonitoring.UserContextMonitoringController;
import eu.musesproject.client.model.contextmonitoring.UISource;
import eu.musesproject.client.model.decisiontable.Action;
import eu.musesproject.client.model.decisiontable.ActionType;
import eu.musesproject.client.prediction.dataexport.DataExport;
import eu.musesproject.client.prediction.preferences.IsLabelingActivatedPreference;
import eu.musesproject.client.prediction.session.SessionDataController;

/**
 * MainActivity class handles List buttons on the main GUI
 * 
 * @author Yasir Ali
 * @version Jan 27, 2014
 */

public class MainActivity extends Activity implements View.OnClickListener {

	public static final String DECISION_OK = "ok";
	public static final String DECISION_CANCEL = "cancel";
	public static final String DECISION_KEY = "decision";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String PREFERENCES_KEY = "eu.musesproject.client";
	private static String TAG = MainActivity.class.getSimpleName();
	private LinearLayout topLayout;
	private Button loginListBtn, securityInformationListbtn;
	private Context context;
	private LoginView loginView;
	private UserContextMonitoringController userContextMonitoringController;
	public static boolean isLoggedIn = false;
	private SharedPreferences prefs;

	private static final int NOTIFICATION_EX = 1;
	private NotificationManager notificationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.muses_main);
		context = getApplicationContext();

		topLayout = (LinearLayout) findViewById(R.id.top_layout);
		loginListBtn = (Button) findViewById(R.id.login_list_button);
		securityInformationListbtn = (Button) findViewById(R.id.security_info_list_button);
		loginListBtn.setOnClickListener(this);
		securityInformationListbtn.setOnClickListener(this);

		userContextMonitoringController = UserContextMonitoringController
				.getInstance(context);

		registerCallbacks();
		prefs = context.getSharedPreferences(MainActivity.PREFERENCES_KEY,
				Context.MODE_PRIVATE);

		if (!sendDecisionIfComingFromShowFeedbackDialog(super.getIntent()
				.getExtras())) {
			// starts the background service of MUSES
			startService(new Intent(this, MUSESBackgroundService.class));
			Log.v(TAG, "muses service started ...");
		}

		loginView = new LoginView(context);
		topLayout.removeAllViews();
		topLayout.addView(loginView);
		// setAppIconOnStatusBar();
	}

	@SuppressLint("NewApi")
	private void setAppIconOnStatusBar() {
		Notification.Builder mBuilder = new Notification.Builder(this)
				.setSmallIcon(R.drawable.muses_main).setContentTitle("")
				.setContentText("");
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, MainActivity.class);

		// The stack builder object will contain an artificial back stack for
		// the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(1, mBuilder.build());
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	private boolean sendDecisionIfComingFromShowFeedbackDialog(Bundle bundle) {
		if (bundle != null) {
			moveTaskToBack(true); // Forcing activity to go in background
			String userDecision = bundle.getString(DECISION_KEY);
			if (userDecision != null) {
				if (userDecision.equals(DECISION_OK)) {
					Action okAction = new Action();
					okAction.setActionType(ActionType.OK);
					okAction.setTimestamp(System.currentTimeMillis());
					Log.e(TAG, "user pressed ok..");
					sendUserDecisionToMusDM(okAction);

				}
				if (userDecision.equals(DECISION_CANCEL)) {
					Action cancelAction = new Action();
					cancelAction.setActionType(ActionType.CANCEL);
					cancelAction.setTimestamp(System.currentTimeMillis());
					Log.e(TAG, "user pressed cancel..");
					sendUserDecisionToMusDM(cancelAction);
				}
				return true;
			} else
				return false;

		} else
			return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		menu.findItem(R.id.action_labeling).setChecked(
				IsLabelingActivatedPreference.getInstance().get(
						getApplicationContext()));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_labeling:
			if (item.isChecked()) {
				item.setChecked(false);
			} else {
				item.setChecked(true);
			}
			IsLabelingActivatedPreference.getInstance().set(
					getApplicationContext(), item.isChecked());

			return true;
 
		case R.id.action_export:
			new DataExport(getApplicationContext()).exportData();
			return true;
			
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_list_button:
			topLayout.removeAllViews();
			topLayout.addView(loginView);
			break;
		case R.id.security_info_list_button:
			if (isLoggedIn) {
				// topLayout.removeAllViews();
			}
			break;
		}
	}

	@Override
	public void onResume() {

		if (loginView != null)
			loginView = new LoginView(context);
		topLayout.removeAllViews();
		topLayout.addView(loginView);

		super.onResume();
	}

	private Handler callbackHandler = new Handler() {

		private String decisionName;
		private String riskTextualDecp;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MusesUICallbacksHandler.LOGIN_SUCCESSFUL:
				loginView.updateLoginView();
				isLoggedIn = true;
				toastMessage(getResources().getString(
						R.string.login_success_msg));
				break;
			case MusesUICallbacksHandler.LOGIN_UNSUCCESSFUL:
				toastMessage(getResources().getString(R.string.login_fail_msg));
				break;
			case MusesUICallbacksHandler.ACTION_RESPONSE_ACCEPTED:
				Log.d(TAG, "Action response accepted ..");
				// FIXME This action should not be sent here, if action is
				// granted then it should be sent directly from MusDM
				Action action = new Action();
				action.setActionType(ActionType.OK);
				action.setTimestamp(System.currentTimeMillis());
				Log.e(TAG, "user pressed ok..");
				sendUserDecisionToMusDM(action);
				break;
			case MusesUICallbacksHandler.ACTION_RESPONSE_DENIED:
				Log.d(TAG, "Action response denied ..");
				decisionName = msg.getData().getString("name");
				riskTextualDecp = msg.getData().getString("risk_textual_decp");
				showResultDialog(riskTextualDecp,
						MusesUICallbacksHandler.ACTION_RESPONSE_DENIED);
				break;
			case MusesUICallbacksHandler.ACTION_RESPONSE_MAY_BE:
				Log.d(TAG, "Action response maybe ..");
				decisionName = msg.getData().getString("name");
				riskTextualDecp = msg.getData().getString("risk_textual_decp");
				showResultDialog(riskTextualDecp,
						MusesUICallbacksHandler.ACTION_RESPONSE_MAY_BE);
				break;
			case MusesUICallbacksHandler.ACTION_RESPONSE_UP_TO_USER:
				Log.d(TAG, "Action response upToUser ..");
				decisionName = msg.getData().getString("name");
				riskTextualDecp = msg.getData().getString("risk_textual_decp");
				showResultDialog(riskTextualDecp,
						MusesUICallbacksHandler.ACTION_RESPONSE_UP_TO_USER);
				break;

			}
		}

	};

	/**
	 * Shows the result dialog to the user
	 * 
	 * @param message
	 */

	private void showResultDialog(String message, int type) {
		Intent showFeedbackIntent = new Intent(getApplicationContext(),
				FeedbackActivity.class);
		showFeedbackIntent.putExtra("message", message);
		showFeedbackIntent.putExtra("type", type);
		showFeedbackIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);

		Bundle extras = new Bundle();
		extras.putString(MainActivity.DECISION_KEY, MainActivity.DECISION_OK);
		showFeedbackIntent.putExtras(extras);
		startActivity(showFeedbackIntent);
	}

	/**
	 * Send user's decision back to MusDM which will either allow MusesAwareApp
	 * or not
	 * 
	 * @param action
	 */

	private void sendUserDecisionToMusDM(Action action) {
		userContextMonitoringController.sendUserAction(UISource.MUSES_UI,
				action, null);
	}

	/**
	 * Registers for callbacks using MusesUICallbacksHandler in
	 * UserContextMonitoringImplementation.
	 */
	private void registerCallbacks() {
		MusesUICallbacksHandler musesUICallbacksHandler = new MusesUICallbacksHandler(
				context, callbackHandler);
		ActuatorController.getInstance().registerCallback(
				musesUICallbacksHandler);
	}

	/**
	 * Toast messages to UI
	 * 
	 * @param message
	 */

	private void toastMessage(String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Check the login fields and Then tries login to the server
	 */

	public void doLogin(String userName, String password) {
		if (checkLoginInputFields(userName, password)) {
			userContextMonitoringController.login(userName, password);
			loginView.setUsernamePasswordIfSaved();
		} else {
			toastMessage(getResources().getString(
					R.string.empty_login_fields_msg));
		}
	}

	/**
	 * Check input fields are not empty before sending it for authentication
	 * 
	 * @param userName
	 * @param password
	 * @return
	 */

	private boolean checkLoginInputFields(String userName, String password) {
		if (userName != null || password != null) {
			if (userName.equals("") || password.equals(""))
				return false; // FIXME need some new checking in future
		} else
			return false;
		return true;
	}

	/**
	 * LoginView class handles Login GUI (Username, passwords etc ) on the main
	 * GUI
	 * 
	 * @author Yasir Ali
	 * @version Jan 27, 2014
	 */

	private class LoginView extends LinearLayout implements
			View.OnClickListener, OnCheckedChangeListener {

		private EditText userNameTxt, passwordTxt;
		private LinearLayout loginLayout1, loginLayout2;
		private Button loginBtn, logoutBtn;
		private TextView loginDetailTextView;
		private CheckBox rememberCheckBox, agreeTermsCheckBox;
		private String userName, password;
		boolean isPrivacyPolicyAgreementChecked = false;

		public LoginView(Context context) {
			super(context);
			inflate(context, R.layout.login_view, this);
			userNameTxt = (EditText) findViewById(R.id.username_text);
			passwordTxt = (EditText) findViewById(R.id.pass_text);

			userNameTxt.setText("muses");
			passwordTxt.setText("muses");
			userName = userNameTxt.getText().toString();
			password = passwordTxt.getText().toString();

			loginDetailTextView = (TextView) findViewById(R.id.login_detail_text_view);
			rememberCheckBox = (CheckBox) findViewById(R.id.remember_checkbox);
			rememberCheckBox.setOnCheckedChangeListener(this);
			agreeTermsCheckBox = (CheckBox) findViewById(R.id.agree_terms_checkbox);
			agreeTermsCheckBox.setOnCheckedChangeListener(this);
			// loginLayout1 = (LinearLayout) findViewById(R.id.login_layout_1);
			loginLayout2 = (LinearLayout) findViewById(R.id.login_layout_2);
			loginBtn = (Button) findViewById(R.id.login_button);
			loginBtn.setOnClickListener(this);
			logoutBtn = (Button) findViewById(R.id.logout_button);
			logoutBtn.setOnClickListener(this);
			setUsernamePasswordIfSaved();
			populateLoggedInView();
		}

		/**
		 * Populate logged in view if user is successfully logged in.
		 * 
		 */

		private void populateLoggedInView() {
			if (isLoggedIn) {
				loginLayout2.setVisibility(View.GONE);
				logoutBtn.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
			switch (arg0.getId()) {
			case R.id.remember_checkbox:
				userName = userNameTxt.getText().toString();
				password = passwordTxt.getText().toString();
				SharedPreferences.Editor prefEditor = prefs.edit();
				if (isChecked) {
					if (checkLoginInputFields(userName, password)) {
						prefEditor.putString(USERNAME, userName);
						prefEditor.putString(PASSWORD, password);
						prefEditor.commit();
					}
				} else {
					prefEditor.clear();
					prefEditor.commit();
				}
				break;
			case R.id.agree_terms_checkbox:
				if (isChecked) {
					isPrivacyPolicyAgreementChecked = true;
				} else
					isPrivacyPolicyAgreementChecked = false;
				break;
			}
		}

		/**
		 * Handles all the button on the screen, overridden method for
		 * onClickLitsener
		 * 
		 * @param View
		 * 
		 */

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.login_button:
				if (isPrivacyPolicyAgreementChecked) {
					userName = userNameTxt.getText().toString();
					password = passwordTxt.getText().toString();
					if (NetworkChecker.isInternetConnected) {
						doLogin(userName, password);
					} else
						toastMessage(getResources().getString(
								R.string.no_internet_connection_msg));

				} else
					toastMessage(getResources().getString(
							R.string.make_sure_privacy_policy_read_txt));
				break;
			case R.id.logout_button:
				logoutBtn.setVisibility(View.GONE);
				loginDetailTextView.setText(getResources().getString(
						R.string.login_detail_view_txt));
				loginLayout2.setVisibility(View.VISIBLE);
				toastMessage(getResources().getString(
						R.string.logout_successfully_msg));
				isLoggedIn = false;
				setUsernamePasswordIfSaved();
				break;
			}

		}

		public void updateLoginView() {
			loginLayout2.setVisibility(View.GONE);
			logoutBtn.setVisibility(View.VISIBLE);
			loginDetailTextView.setText(String.format("%s %s", getResources()
					.getString(R.string.logged_in_info_txt), userName));
			setUsernamePasswordIfSaved();
		}

		public void setUsernamePasswordIfSaved() {
			if (prefs.contains(USERNAME)) {
				userName = prefs.getString(USERNAME, "");
				password = prefs.getString(PASSWORD, "");
				userNameTxt.setText(userName);
				passwordTxt.setText(password);
			} else {
				Log.d(TAG, "No username-pass found in preferences");
			}
		}

	}

	/**
	 * PrivacyPolicyView class shows privacy details on the main GUI
	 * 
	 * @author Yasir Ali
	 * @version Jan 27, 2014
	 */

	private class PrivacyPolicyView extends LinearLayout {

		public PrivacyPolicyView(Context context) {
			super(context);
			// inflate(context, R.layout.privacy_policy_view, this);
		}

	}

	/**
	 * SecurityInformationView class shows security information on the main GUI
	 * 
	 * @author Yasir Ali
	 * @version Jan 27, 2014
	 */

	public class SecurityInformationView extends LinearLayout {

		public SecurityInformationView(Context context) {
			super(context);
			// inflate(context, R.layout.security_information_view, this);
			setSecurityInformationViewAttiributesHere();
		}

		private void setSecurityInformationViewAttiributesHere() {
			// TBD
		}

	}

}