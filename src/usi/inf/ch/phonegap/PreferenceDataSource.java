package usi.inf.ch.phonegap;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PreferenceDataSource {

	// Database fields
	private SQLiteDatabase database;
	private PreferenceOpenHelper dbHelper;
	
	private String[] allColumns = { "id", "appName", "isActive", "pref_values" };
	

	public PreferenceDataSource(Context context) {
		dbHelper = new PreferenceOpenHelper(context);
	}

	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	

	public void close() {
		dbHelper.close();
	}

	
	/**
	 * Creates a preference object.
	 * @param pref
	 * @return
	 */
	public Preference createPreference(String appName, int isActive, String values) {
		ContentValues val = new ContentValues();
		val.put("isActive", isActive);
		val.put("pref_values", values);
		val.put("appName", appName);
		long insertId = database.insert("preferences", null, val);
		
		Cursor cursor = database.query("preferences",
				allColumns, "id = " + insertId, null, null, null, null);
		
		cursor.moveToFirst();
		Preference newPreference = cursorToPref(cursor);
		cursor.close();
		return newPreference;
	}
	
	
	
	
	/**
	 * Update the given preference object.
	 * @param pref
	 */
	public void updatePreference(String appName, String prefValue) {
		
		Log.v("update string", prefValue);
		String strFilter = "appName = '"+ appName+"'";
		ContentValues args = new ContentValues();
		args.put("pref_value", prefValue.toString());
		database.update("preferences", args, strFilter, null);
	}
	
	
	/**
	 * Update the value of isActive to 1.
	 * @param pref
	 */
	public void activatePreference(String appName) {
		
		String strFilter = "appName = '"+ appName+"'";
		ContentValues args = new ContentValues();
		args.put("isActive", "1");
		database.update("preferences", args, strFilter, null);
	}
	
	
	
	/**
	 * Deletes the given preference object.
	 * @param pref
	 */
	public void deletePreference(Preference pref) {
		long id = pref.getId();
		database.delete("preferences", "id = " + id, null);
	}

	
	/**
	 * Deletes all entries
	 */
	public void deleteAll() {
		
		database.delete("preferences", null, null);
		
	}
	
	/**
	 * Lists all preference entries.
	 * @return
	 */
	public List<Preference> getAllPreferences() {
		List<Preference> prefs = new ArrayList<Preference>();

		Cursor cursor = database.query("preferences",
				allColumns, null, null, null, null, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Preference pref = cursorToPref(cursor);
			prefs.add(pref);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return prefs;
	}

	
	
	private Preference cursorToPref(Cursor cursor) {
		Preference pref = new Preference();
		pref.setId(cursor.getLong(0));
		pref.setAppName(cursor.getString(1));
		pref.setIsActive(cursor.getInt(2));
		pref.setValues(cursor.getString(3));
		return pref;
	}
	
	
}
