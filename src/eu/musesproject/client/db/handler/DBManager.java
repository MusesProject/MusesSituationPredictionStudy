package eu.musesproject.client.db.handler;

import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager {

	private static final String TAG = DBManager.class.getSimpleName();
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "muses_client_db";

	// table names and column names for prediction/labeling
	private static final String TABLE_CONTEXTEVENT_LABELING = "table_contextevent_labeling";
	private static final String TABLE_PROPERTY_LABELING = "table_property_labeling";
	private static final String TABLE_USERSELECTION_LABELING = "table_userselection_labeling";

	// contextevent table columns
	public static final String ID_CONTEXTEVENT_LABELING = "_id";
	public static final String TYPE_CONTEXTEVENT_LABELING = "type_contextevent_labeling";
	public static final String TIMESTAMP_CONTEXTEVENT_LABELING = "timestamp_contextevent_labeling";
	public static final String SESSION_ID_CONTEXTEVENT_LABELING = "session_id_contextevent_labeling";

	// property table columns
	public static final String ID_PROPERTY_LABELING = "_id";
	public static final String CE_ID_PROPERTY_LABELING = "ce_id_property_labeling";
	public static final String KEY_PROPERTY_LABELING = "key_property_labeling";
	public static final String VALUE_PROPERTY_LABERLING = "value_property_labeling";

	// userselection table columns
	public static final String ID_USERSELECTION_LABELING = "_id";
	public static final String SESSION_ID_USERSELECTION_LABELING = "session_id";
	public static final String VALUE_USERSELECTION_LABELING = "value_userselection_labeling";

	

	private static final String CREATE_LABELING_CONTEXTEVENT_TABLE_QUERY = String
			.format("CREATE TABLE %s "
					+ "( %s INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "%s VARCHAR(45) NOT NULL, " + "%s TIMESTAMP NOT NULL, "
					+ "%s INTEGER NOT NULL, "
					+ "FOREIGN KEY(%s) REFERENCES %s(%s) "
					+ "ON DELETE CASCADE " 
					+ ");", TABLE_CONTEXTEVENT_LABELING,
					ID_CONTEXTEVENT_LABELING, TYPE_CONTEXTEVENT_LABELING,
					TIMESTAMP_CONTEXTEVENT_LABELING,
					SESSION_ID_CONTEXTEVENT_LABELING,
					SESSION_ID_CONTEXTEVENT_LABELING,
					TABLE_USERSELECTION_LABELING,
					SESSION_ID_USERSELECTION_LABELING);

	private static final String CREATE_LABELING_PROPERTY_TABLE_QUERY = String
			.format("CREATE TABLE %s "
					+ "( %s INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "%s INTEGER NOT NULL, " + "%s VARCHAR(45) NOT NULL, "
					+ "%s VARCHAR(45) NOT NULL, "
					+ "FOREIGN KEY(%s) REFERENCES %s(%s) "
					+ "ON DELETE CASCADE " + ");", TABLE_PROPERTY_LABELING,
					ID_PROPERTY_LABELING, CE_ID_PROPERTY_LABELING,
					KEY_PROPERTY_LABELING, VALUE_PROPERTY_LABERLING,
					CE_ID_PROPERTY_LABELING, TABLE_CONTEXTEVENT_LABELING,
					ID_CONTEXTEVENT_LABELING);

	private static final String CREATE_LABELING_USERSELECTION_TABLE_QUERY = String
			.format("CREATE TABLE %s " + "( %s INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "%s INTEGER UNIQUE, "
					+ "%s VARCHAR(20) " + ");", TABLE_USERSELECTION_LABELING, ID_USERSELECTION_LABELING,
					SESSION_ID_USERSELECTION_LABELING,
					VALUE_USERSELECTION_LABELING);

	private Context context;
	private DatabaseHelper databaseHelper;
	private SQLiteDatabase sqLiteDatabase;

	public DBManager(Context context) {
		this.context = context;
		databaseHelper = new DatabaseHelper(context);
	}

	public SQLiteDatabase openDB() { // always returns writableDB
		Log.d(TAG, "opening database..");
		sqLiteDatabase = databaseHelper.getWritableDatabase();
		sqLiteDatabase.execSQL("PRAGMA foreign_keys = ON;");
		return sqLiteDatabase;
	}

	public void closeDB() {
		if (sqLiteDatabase != null) {
			databaseHelper.close();
			sqLiteDatabase = null;
		}
	}

	public void encryptDB() {
		// TBD
	}

	public void decryptDB() {
		// TBD
	}

	/**
	 * This is a private class which creates the database when the application
	 * starts or upgrades it if it already exist by removing the last version of
	 * the databases Create database .. and tables
	 * 
	 */
	public static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG, "Creating the DB");
			db.execSQL(CREATE_LABELING_CONTEXTEVENT_TABLE_QUERY);
			db.execSQL(CREATE_LABELING_PROPERTY_TABLE_QUERY);
			db.execSQL(CREATE_LABELING_USERSELECTION_TABLE_QUERY);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Updating DB from previous version " + oldVersion
					+ " to " + newVersion);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTEXTEVENT_LABELING);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPERTY_LABELING);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERSELECTION_LABELING);

			onCreate(db);
		}

	}

	/*
	 * 
	 * Methods for prediction/label
	 */

	public boolean isSessionIdAvailable(int sessionId){
		Cursor cursor = sqLiteDatabase.query(TABLE_USERSELECTION_LABELING,new String[]{SESSION_ID_USERSELECTION_LABELING }, String.format("%s=%d", SESSION_ID_USERSELECTION_LABELING, sessionId), null, null, null, null);
		if(cursor.getCount() != 0){
			return true;
		}
		
		return false;
	}
	
	public void insertSessionId(int sessionId) {
		ContentValues cv = new ContentValues();
		cv.put(SESSION_ID_USERSELECTION_LABELING, sessionId);
		sqLiteDatabase.insert(TABLE_USERSELECTION_LABELING, null, cv);
	}

	public void insertLabelContextEvent(
			eu.musesproject.contextmodel.ContextEvent ce, int sessionId) {
		ContentValues cv = new ContentValues();
		cv.put(TYPE_CONTEXTEVENT_LABELING, ce.getType());
		cv.put(TIMESTAMP_CONTEXTEVENT_LABELING, ce.getTimestamp());
		cv.put(SESSION_ID_CONTEXTEVENT_LABELING, sessionId);
		long rowId = sqLiteDatabase.insert(TABLE_CONTEXTEVENT_LABELING, null,
				cv);
		if (rowId != -1) {
			Map<String, String> properties = ce.getProperties();
			for (Map.Entry<String, String> entry : properties.entrySet()) {
				ContentValues propCv = new ContentValues();
				propCv.put(CE_ID_PROPERTY_LABELING, rowId);
				propCv.put(KEY_PROPERTY_LABELING, entry.getKey());
				propCv.put(VALUE_PROPERTY_LABERLING, entry.getValue());
				sqLiteDatabase.insert(TABLE_PROPERTY_LABELING, null, propCv);
			}
		}
	}

	public void deleteSessionData(int sessionId) {
		sqLiteDatabase.delete(TABLE_USERSELECTION_LABELING, String.format(
				"%s=%s", SESSION_ID_USERSELECTION_LABELING, sessionId), null);
	}
	
	public void deleteAllSessionData(){
		sqLiteDatabase.delete(TABLE_USERSELECTION_LABELING, null,null);
	}

	public void storeUserSelection(int sessionId, String selection) {
		ContentValues cv = new ContentValues();
		cv.put(VALUE_USERSELECTION_LABELING, selection);
		sqLiteDatabase.update(TABLE_USERSELECTION_LABELING, cv, String.format("%s.%s=%s", TABLE_USERSELECTION_LABELING, SESSION_ID_USERSELECTION_LABELING, sessionId), null);
	}

	public Cursor getAllLabeledData() {
		final String query = String.format(
				"SELECT %s.%s, %s.%s, %s.%s, %s.%s, %s.%s, %s.%s, %s.%s ",
				TABLE_USERSELECTION_LABELING,
				SESSION_ID_USERSELECTION_LABELING,
				TABLE_USERSELECTION_LABELING, VALUE_USERSELECTION_LABELING,
				TABLE_CONTEXTEVENT_LABELING, ID_CONTEXTEVENT_LABELING,
				TABLE_CONTEXTEVENT_LABELING, TYPE_CONTEXTEVENT_LABELING,
				TABLE_CONTEXTEVENT_LABELING, TIMESTAMP_CONTEXTEVENT_LABELING,
				TABLE_PROPERTY_LABELING, KEY_PROPERTY_LABELING,
				TABLE_PROPERTY_LABELING, VALUE_PROPERTY_LABERLING)
				+ String.format("FROM %s, %s, %s ",
						TABLE_USERSELECTION_LABELING,
						TABLE_CONTEXTEVENT_LABELING, TABLE_PROPERTY_LABELING)
				+ String.format("WHERE %s.%s=%s.%s AND %s.%s=%s.%s",
						TABLE_USERSELECTION_LABELING,
						SESSION_ID_USERSELECTION_LABELING,
						TABLE_CONTEXTEVENT_LABELING,
						SESSION_ID_CONTEXTEVENT_LABELING,
						TABLE_CONTEXTEVENT_LABELING, ID_CONTEXTEVENT_LABELING,
						TABLE_PROPERTY_LABELING, CE_ID_PROPERTY_LABELING);

		return sqLiteDatabase.rawQuery(query, null);
	}
}
