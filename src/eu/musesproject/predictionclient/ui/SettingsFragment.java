package eu.musesproject.predictionclient.ui;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;
import eu.musesproject.client.R;
import eu.musesproject.predictionclient.Resetter;
import eu.musesproject.predictionclient.dataexport.DataExport;
import eu.musesproject.predictionclient.preferences.IsModelCreatedPreference;
import eu.musesproject.predictionclient.preferences.IsWaitingForModelBuildPreference;

public class SettingsFragment extends PreferenceFragment implements OnPreferenceClickListener,
		OnSharedPreferenceChangeListener, OnClickListener {

	private PreferenceScreen mPreferenceScreen;

	PreferenceCategory mClassificationPreferenceCategory;
	PreferenceCategory mLabelingPreferenceCategory;

	SwitchPreference mLabelingSwitchPreference;
	Preference mSessionCountPreference;

	SwitchPreference mClassificationSwitchPreference;

	Preference mExportDataPreference;

	Preference mResetModelPreference;

	private Dialog mDialog;

	// private NumberPicker np1;

	private NumberPicker np2;

	private NumberPicker np3;

	private NumberPicker np4;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);
		mPreferenceScreen = (PreferenceScreen) findPreference(getString(R.string.key_preference_screen));

		mClassificationPreferenceCategory = (PreferenceCategory) mPreferenceScreen
				.findPreference(getString(R.string.key_classification_category));
		mLabelingPreferenceCategory = (PreferenceCategory) mPreferenceScreen
				.findPreference(getString(R.string.key_labeling_category));

		mLabelingSwitchPreference = (SwitchPreference) mLabelingPreferenceCategory
				.findPreference(getString(R.string.key_labeling_switch));
		mSessionCountPreference = (Preference) mLabelingPreferenceCategory
				.findPreference(getString(R.string.key_session_count_preference));
		mSessionCountPreference.setOnPreferenceClickListener(this);

		mClassificationSwitchPreference = (SwitchPreference) mClassificationPreferenceCategory
				.findPreference(getString(R.string.key_classification_switch));

		mExportDataPreference = (Preference) mPreferenceScreen
				.findPreference(getString(R.string.key_export_data_preference));
		mExportDataPreference.setOnPreferenceClickListener(this);

		mResetModelPreference = (Preference) mPreferenceScreen
				.findPreference(getString(R.string.key_reset_model_preference));
		mResetModelPreference.setOnPreferenceClickListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!IsModelCreatedPreference.getInstance().get(getActivity().getApplicationContext())
				&& !IsWaitingForModelBuildPreference.getInstance().get(getActivity().getApplicationContext())) {
			mPreferenceScreen.removePreference(mClassificationPreferenceCategory);

			mLabelingPreferenceCategory.setEnabled(true);
		} else {
			mPreferenceScreen.addPreference(mClassificationPreferenceCategory);

			mLabelingPreferenceCategory.setEnabled(false);
		}

		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		String key = preference.getKey();
		if (key != null) {
			if (key.equals(mSessionCountPreference.getKey())) {
				showSessionCountDialog();
				return true;
			} else if (key.equals(mExportDataPreference.getKey())) {
				Toast.makeText(getActivity().getApplicationContext(), R.string.toast_export_data_text,
						Toast.LENGTH_LONG).show();
				new DataExport(getActivity().getApplicationContext()).exportData();
				return true;
			} else if (key.equals(mResetModelPreference.getKey())) {
				Resetter.resetAll(getActivity().getApplicationContext());
				return true;
			}
		}
		return false;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key != null) {
			if (key.equals(mLabelingSwitchPreference.getKey())) {
				if (sharedPreferences.getBoolean(key, false)) {
					Toast.makeText(getActivity().getApplicationContext(), R.string.toast_labeling_enabled_text,
							Toast.LENGTH_LONG).show();
				}
			} else if (key.equals(mClassificationSwitchPreference.getKey())) {
				int test = 1;
				int test2 = test;

			} else {
				int test = 1;
				int test2 = test;
			}
		}
	}

	private void showSessionCountDialog() {
		mDialog = new Dialog(getActivity());
		mDialog.setContentView(R.layout.session_count_dialog);

		mDialog.setTitle(getString(R.string.dialog_session_count_title));

		// np1 = (NumberPicker) mDialog.findViewById(R.id.digit1);
		// np1.setMaxValue(9);
		// np1.setMinValue(0);
		int currentValue = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getInt(
				mSessionCountPreference.getKey(), 0);

		np2 = (NumberPicker) mDialog.findViewById(R.id.digit2);
		np2.setMaxValue(9);
		np2.setMinValue(0);
		np2.setValue(currentValue / 100);
		currentValue -= ((currentValue / 100) * 100);

		np3 = (NumberPicker) mDialog.findViewById(R.id.digit3);
		np3.setMaxValue(9);
		np3.setMinValue(0);
		np3.setValue(currentValue / 10);
		currentValue -= ((currentValue / 10) * 10);

		np4 = (NumberPicker) mDialog.findViewById(R.id.digit4);
		np4.setMaxValue(9);
		np4.setMinValue(0);
		np4.setValue(currentValue);

		Button okButton = (Button) mDialog.findViewById(R.id.ok_button);
		okButton.setOnClickListener(this);

		Button cancelButton = (Button) mDialog.findViewById(R.id.cancel_button);
		cancelButton.setOnClickListener(this);

		mDialog.show();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		case R.id.ok_button:
			int sessionCount = Integer.parseInt("" +
			// np1.getValue() +
					np2.getValue() + np3.getValue() + np4.getValue());
			if (sessionCount != 0) {
				PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit()
						.putInt(mSessionCountPreference.getKey(), sessionCount).commit();
			}
			mDialog.dismiss();
			break;

		case R.id.cancel_button:
			mDialog.dismiss();
			break;

		}
	}
}
