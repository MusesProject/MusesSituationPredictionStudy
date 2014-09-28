package eu.musesproject.client.dataexport;

import java.io.File;
import java.io.FileWriter;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;
import eu.musesproject.client.db.DBManager;
import eu.musesproject.client.preferences.LabelingStatePreference;
import eu.musesproject.client.session.SessionDataController;

public class DataExport {

	private Context mContext;
	private String mSubDir = "Muses";
	private String mFileName = "labeled_data";
	private String mPrefix = ".csv";

	// private DBManager mDBManager;

	public DataExport(Context context) {
		mContext = context;
		// mDBManager = new DBManager(context);
	}

	public void exportData() {
		// first, store data for current session or delete them
		if (LabelingStatePreference.getInstance().get(mContext)) {
			SessionDataController.getInstance(mContext).storeSessionData();
		} else {
			// TODO first, show dialog to label data
			SessionDataController.getInstance(mContext).deleteSessionData();
		}

		// export data to file
		new ExportTask().execute();
	}

	private class ExportTask extends AsyncTask<Void, Void, Void> {

		DBManager mDBManager;
		String mFilePath = null;

		public ExportTask() {
			mDBManager = new DBManager(mContext);
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (createFileFromDb()) {
				if(!sendFile()){
					this.cancel(true);
				}
			} else {
				this.cancel(true);
			}
			new File(mFilePath).delete();
			return null;
		}

		private boolean sendFile() {
			MailSender mailSender = new MailSender("MUSES - labeled_user_data");

			if (mFilePath == null) {
				return false;
			}

			mailSender.setAttachment(mFilePath);

			if (!mailSender.sendMail()) {
				return false;
			}

			return true;
		}

		private boolean createFileFromDb() {
			mDBManager.openDB();
			Cursor cursor = mDBManager.getAllLabeledData();

			// get path of Downloads-Folder
			File dir = new File(Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DOWNLOADS).getPath()
					+ File.separatorChar + mSubDir);

			if (!dir.exists()) {
				dir.mkdir();
			}

			String fileName = dir.getPath() + File.separatorChar + mFileName;

			int i = 1;
			while (new File(fileName + mPrefix).exists()) {
				fileName = fileName + i;
			}

			try {
				FileWriter csvFile = new FileWriter(fileName + mPrefix);
				mFilePath = fileName + mPrefix;
				csvFile.append("session_id; user_selection; context_event_id; context_event_type; timestamp; property_key; property_value"
						+ "\n");
				if (cursor.moveToFirst()) {
					do {
						csvFile.append(cursor.getInt(cursor
								.getColumnIndex(DBManager.SESSION_ID_USERSELECTION_LABELING))
								+ ";");
						csvFile.append(cursor.getString(cursor
								.getColumnIndex(DBManager.VALUE_USERSELECTION_LABELING))
								+ ";");
						csvFile.append(cursor.getInt(cursor
								.getColumnIndex(DBManager.ID_CONTEXTEVENT_LABELING))
								+ ";");
						csvFile.append(cursor.getString(cursor
								.getColumnIndex(DBManager.TYPE_CONTEXTEVENT_LABELING))
								+ ";");

						csvFile.append(cursor.getLong(cursor
								.getColumnIndex(DBManager.TIMESTAMP_CONTEXTEVENT_LABELING))
								+ ";");
						csvFile.append(cursor.getString(cursor
								.getColumnIndex(DBManager.KEY_PROPERTY_LABELING))
								+ ";");
						csvFile.append(cursor.getString(cursor
								.getColumnIndex(DBManager.VALUE_PROPERTY_LABELING))
								+ ";");
						csvFile.append("\n");

					} while (cursor.moveToNext());
				}
				csvFile.close();
			} catch (Exception e) {
				return false;
			}

			cursor.close();
			mDBManager.closeDB();
			return true;
		}

		@Override
		protected void onCancelled() {
			Toast.makeText(mContext, "File could not be exported",
					Toast.LENGTH_LONG).show();
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			Toast.makeText(
					mContext,
					"File with labeled data was exported to the downloads folder of your smartphone",
					Toast.LENGTH_LONG).show();
		}

	}
}
