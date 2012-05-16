package usi.inf.ch.phonegap;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

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
	
	private String[] prefColumns = { "id", "appName", "isActive", "pref_values"};
	private String[] appColumns = {"id", "name", "namespace", "view", "socket_address", "description", "icon"};

	
	public PreferenceDataSource(Context context) {
		dbHelper = new PreferenceOpenHelper(context);
	}

	
	public void open() throws SQLException {
		Log.v("DB", "open");
		database = dbHelper.getWritableDatabase();
	}
	

	public void close() {
		if(dbHelper != null) {
			Log.v("DB Interface", "closing DB");
			dbHelper.close();
		}
	}
	
	/**
	 * Creates a preference object.
	 * @param pref
	 * @return
	 */
	public Preference createPreference(String appName, int isActive, String prefValue) {
		
		ContentValues val = new ContentValues();
		val.put("isActive", isActive);
		val.put("pref_values", prefValue);
		val.put("appName", appName);
		long insertId = database.insert("preferences", null, val);
		
		Cursor cursor = database.query("preferences",
				prefColumns, "id = " + insertId, null, null, null, null);
		
		cursor.moveToFirst();
		Preference newPreference = cursorToPref(cursor);
		cursor.close();
		return newPreference;
	}
	
	
	
	
	
	/**
	 * Selects the preference given the appName (unique)
	 * @param appName
	 * @return
	 */
	public Preference getPreference(String appName) {
		
		Preference selPref = null;
		Cursor cursor = database.query("preferences", prefColumns, "appName = '" + appName+"'", null, null, null, null);
		Log.v("DB Interface", "row numbers => "+cursor.getCount());
		if(cursor.getCount() > 0) {
			Log.v("DB Interface", "entry already exists");
			cursor.moveToFirst();
			selPref = cursorToPref(cursor);
		}
		cursor.close();
		return selPref;
	}
	

	
	/**
	 * Update the given preference object.
	 * @param pref
	 */
	public void updatePreference(String appName, String prefValue) {
		
		String strFilter = "appName = '"+ appName+"'";
		ContentValues args = new ContentValues();
		args.put("pref_values", prefValue);
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
	 * Update the value of isActive to 0.
	 * @param pref
	 */
	public void deactivatePreference(String appName) {
		
		String strFilter = "appName = '"+ appName+"'";
		ContentValues args = new ContentValues();
		args.put("isActive", "0");
		database.update("preferences", args, strFilter, null);
		
		//update Location service activeSockets
		Log.v("DB interface", "deactivating");
		LocationService.deactivateSocket(appName);
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
				prefColumns, null, null, null, null, null);
		
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
	
	/**
	 * Lists all active preferences.
	 * @return
	 */
	public List<Preference> getActivePreferences() {
		List<Preference> prefs = new ArrayList<Preference>();

		Cursor cursor = database.query("preferences",
				prefColumns, "isActive = 1", null, null, null, null);
		
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
		pref.setPrefValue(cursor.getString(3));
		return pref;
	}
	
	
	/**
	 * Creates a preference object.
	 * @param pref
	 * @return
	 */
	public App createApp(String name, String namespace, String view, String socket_address, String description, String icon) {
		Log.v("DB Interface", "craeting app entry");
		ContentValues val = new ContentValues();
		val.put("name", name);
		val.put("namespace", namespace);
		val.put("view", view);
		val.put("socket_address", socket_address);
		val.put("description", description);
		val.put("icon", icon);
		
		long insertId = database.insert("apps", null, val);
		
		Cursor cursor = database.query("apps",
				appColumns, "id = " + insertId, null, null, null, null);
		
		cursor.moveToFirst();
		App newApp = cursorToApp(cursor);
		cursor.close();
		return newApp;
	}
	
	
	
	
	
	/**
	 * Selects the preference given the appName (unique)
	 * @param appName
	 * @return
	 */
	public App getApp(long id) {
		
		App selApp = null;
		Cursor cursor = database.query("apps", appColumns, "id = '" + id+"'", null, null, null, null);
		
		Log.v("rows number", cursor.getCount()+"");
		if(cursor.getCount() > 0) {
			Log.v("already", "exists");
			cursor.moveToFirst();
			selApp = cursorToApp(cursor);
		}
		cursor.close();
		return selApp;
	}
	


	
	
	/**
	 * Deletes the given preference object.
	 * @param pref
	 */
	public void deleteApp(String namespace) {
		database.delete("apps", "namespace = '" + namespace+"'", null);
		database.delete("preferences", "appName = '"+ namespace+"'", null);
		LocationService.deactivateSocket(namespace);
	}
	

	
	/**
	 * Deletes all entries
	 */
	public void deleteAllApps() {
		
		database.delete("apps", null, null);
		
	}
	
	/**
	 * Lists all preference entries.
	 * @return
	 */
	public List<App> getAllApps() {
		List<App> apps = new ArrayList<App>();

		Cursor cursor = database.query("apps",
				appColumns, null, null, null, null, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			App app = cursorToApp(cursor);
			apps.add(app);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return apps;
	}

	
	
	private App cursorToApp(Cursor cursor) {
		App app = new App();
		app.setId(cursor.getLong(0));
		app.setName(cursor.getString(1));
		app.setNamespace(cursor.getString(2));
		app.setView(cursor.getString(3));
		app.setSocketAddress(cursor.getString(4));
		app.setDescription(cursor.getString(5));
		app.setIcon(cursor.getString(6));
		return app;
	}


	public boolean checkPrefStatus(String appName) {

		
		Cursor cursor = database.query("preferences", prefColumns, "appName = '"+appName+"'", null, null, null, null);
		cursor.moveToFirst();
		Preference selPref = cursorToPref(cursor);
		if(selPref.isPrefActive() == 1){
			cursor.close();
			return true;
		}else  {
			cursor.close();
			return false;
		}
		
	}


	public boolean checkAppStatus(String appName) {

		Cursor cursor = database.query("apps", appColumns, "namespace = '"+appName+"'", null, null, null, null);
		if(cursor.getCount() > 0) {
			
			return true;
		} 
		cursor.close();
		return false;
	}
		
	
}
