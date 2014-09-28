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
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import eu.musesproject.client.MUSESBackgroundService;
import eu.musesproject.client.R;
import eu.musesproject.client.dataexport.DataExport;
import eu.musesproject.client.preferences.AbstractPreference.DefaultValues;
import eu.musesproject.client.preferences.IsClassificationActivatedPreference;
import eu.musesproject.client.preferences.IsLabelingActivatedPreference;
import eu.musesproject.client.preferences.IsModelCreatedPreference;
import eu.musesproject.client.preferences.IsWaitingForModelBuildPreference;
import eu.musesproject.client.preferences.ModelCountPreference;
import eu.musesproject.client.session.QuitService;

/**
 * MainActivity class handles List buttons on the main GUI
 * 
 * @author Yasir Ali
 * @version Jan 27, 2014
 */

public class MainActivity extends Activity implements View.OnClickListener,
		OnCheckedChangeListener {

	private static String TAG = MainActivity.class.getSimpleName();
	private Button mExportButton;
	private Switch mLabelingSwitch;
	private EditText mMaxSessionEditText;
	private Button mResetButton;
	private LinearLayout mClassificationSwitchContainer;
	private Switch mClassificationSwitch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.muses_main);

		// loginListBtn.setOnClickListener(this);
		// securityInformationListbtn.setOnClickListener(this);

		mExportButton = (Button) findViewById(R.id.export_button);
		mExportButton.setOnClickListener(this);

		mLabelingSwitch = (Switch) findViewById(R.id.labeling_switch);

		mMaxSessionEditText = (EditText) findViewById(R.id.session_count_editText);
		mMaxSessionEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				
				try{
					int sessionNumber = Integer.parseInt(s.toString());
					if(sessionNumber == 0){
						throw new Exception();
					}
					ModelCountPreference.getInstance().set(getApplicationContext(),
							Integer.parseInt(s.toString()));
				} catch (Exception e){
					ModelCountPreference.getInstance().set(getApplicationContext(),
							DefaultValues.INT);
				}
				
				
			}
		});

		mClassificationSwitchContainer = (LinearLayout) findViewById(R.id.classification_switch_container);

		mClassificationSwitch = (Switch) findViewById(R.id.classification_switch);

		mResetButton = (Button) findViewById(R.id.reset_button);
		mResetButton.setOnClickListener(this);

		if (IsLabelingActivatedPreference.getInstance().get(
				getApplicationContext())
				|| IsModelCreatedPreference.getInstance().get(
						getApplicationContext())
				|| IsWaitingForModelBuildPreference.getInstance().get(
						getApplicationContext())
				|| IsClassificationActivatedPreference.getInstance().get(
						getApplicationContext())) {
			// starts the background service of MUSES
			startService(new Intent(this, MUSESBackgroundService.class));
			Log.v(TAG, "muses service started ...");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.export_button:
			new DataExport(getApplicationContext()).exportData();
			break;

		case R.id.reset_button:
			// TODO reset all data
			break;

		case R.id.labeling_switch:
			Toast.makeText(
					getApplicationContext(),
					getResources().getString(
							R.string.labeling_deactivated_toast_text),
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		initLabelingSwitch();
		initClassificationSwitch();
	}

	private void initClassificationSwitch() {
		mClassificationSwitch.setOnCheckedChangeListener(null);
		if (IsModelCreatedPreference.getInstance().get(getApplicationContext())) {
			mClassificationSwitch.setVisibility(View.VISIBLE);
			if (IsClassificationActivatedPreference.getInstance().get(
					getApplicationContext())) {
				mClassificationSwitch.setChecked(true);
			}

			mClassificationSwitch.setOnCheckedChangeListener(this);
		}
	}

	private void initLabelingSwitch() {
		mLabelingSwitch.setOnCheckedChangeListener(null);
		if (IsModelCreatedPreference.getInstance().get(getApplicationContext())
				|| IsWaitingForModelBuildPreference.getInstance().get(
						getApplicationContext())
				|| IsClassificationActivatedPreference.getInstance().get(
						getApplicationContext())) {

			mLabelingSwitch.setChecked(false);
			mLabelingSwitch.setEnabled(false);
			mLabelingSwitch.setOnClickListener(this);
			// to be safe
			IsLabelingActivatedPreference.getInstance().set(
					getApplicationContext(), false);
		} else if (IsLabelingActivatedPreference.getInstance().get(
				getApplicationContext())) {

			mLabelingSwitch.setChecked(true);
			mLabelingSwitch.setOnClickListener(null);
			mLabelingSwitch.setOnCheckedChangeListener(this);
		}
		mLabelingSwitch.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.labeling_switch:
			onLabelingSwitchCheckedChanged(isChecked);
			break;

		case R.id.classification_switch:
			onClassificationSwitchCheckedChanged(isChecked);
			break;
		}

	}

	private void onClassificationSwitchCheckedChanged(boolean isChecked) {
		if (isChecked) {
			startService(new Intent(this, MUSESBackgroundService.class));
		} else {
			stopService(new Intent(this, MUSESBackgroundService.class));
		}

		IsClassificationActivatedPreference.getInstance().set(
				getApplicationContext(), isChecked);
	}

	private void onLabelingSwitchCheckedChanged(boolean isChecked) {
		if (isChecked) {
			startService(new Intent(this, MUSESBackgroundService.class));
		} else {
			startService(new Intent(this, QuitService.class));
			stopService(new Intent(this, MUSESBackgroundService.class));
		}

		IsLabelingActivatedPreference.getInstance().set(
				getApplicationContext(), isChecked);
	}

}