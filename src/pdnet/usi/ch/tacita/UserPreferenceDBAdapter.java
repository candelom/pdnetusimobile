package pdnet.usi.ch.tacita;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class UserPreferenceDBAdapter extends AbstractDBAdapter {

	private static UserPreferenceDBAdapter instance = null;
    private static final String[] columns = new String[]{"id", "appName", "isActive", "pref_values"};
    private static final String DATABASE_TABLE = "preferences";
    
    
    
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    private UserPreferenceDBAdapter(Context ctx) {
    	super(ctx);
    }
    
    
    public static UserPreferenceDBAdapter getInstance(Context context) {
    	if(null == instance) {
            instance = new UserPreferenceDBAdapter(context);
        }
 
        return instance;
    }
    
    
    
    
    /**
	 * Creates a preference object.
	 * @param pref
	 * @return
	 */
	public UserPreference createPreference(String appName, int isActive, String prefValue) {
		
		ContentValues val = new ContentValues();
		val.put("isActive", isActive);
		val.put("pref_values", prefValue);
		val.put("appName", appName);
		long insertId = mDb.insert(DATABASE_TABLE, null, val);
		
		Cursor cursor = mDb.query(DATABASE_TABLE,
				columns, "id = " + insertId, null, null, null, null);
		
		cursor.moveToFirst();
		UserPreference newPreference = cursorToPref(cursor);
		cursor.close();
		return newPreference;
	}
	
	
	
	
	
	/**
	 * Selects the preference given the appName (unique)
	 * @param appName
	 * @return
	 */
	public UserPreference getPreference(String appName) {
		
		UserPreference selPref = null;
		Cursor cursor = mDb.query(DATABASE_TABLE, columns, "appName = '" + appName+"'", null, null, null, null);
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
		mDb.update(DATABASE_TABLE, args, strFilter, null);
	}
	
	
	
	/**
	 * Update the value of isActive to 1.
	 * @param pref
	 */
	public void activatePreference(String appName) {
		
		String strFilter = "appName = '"+ appName+"'";
		ContentValues args = new ContentValues();
		args.put("isActive", "1");
		mDb.update(DATABASE_TABLE, args, strFilter, null);
	}
	
	
	/**
	 * Update the value of isActive to 0.
	 * @param pref
	 */
	public void deactivatePreference(String appName) {
		
		String strFilter = "appName = '"+ appName+"'";
		ContentValues args = new ContentValues();
		args.put("isActive", "0");
		mDb.update("preferences", args, strFilter, null);
		
		//update Location service activeSockets
		Log.v("DB interface", "deactivating");
		LocationService.deactivateSocket(appName);
	}
	
	
	
	
	
	/**
	 * Deletes the given preference object.
	 * @param pref
	 */
	public void deletePreference(UserPreference pref) {
		long id = pref.getId();
		mDb.delete(DATABASE_TABLE, "id = " + id, null);
	}
	
	

	
	/**
	 * Deletes all entries
	 */
	public void deleteAll() {
		
		mDb.delete(DATABASE_TABLE, null, null);
		
	}
	
	/**
	 * Lists all preference entries.
	 * @return
	 */
	public List<UserPreference> getAllPreferences() {
		List<UserPreference> prefs = new ArrayList<UserPreference>();

		Cursor cursor = mDb.query(DATABASE_TABLE,
				columns, null, null, null, null, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			UserPreference pref = cursorToPref(cursor);
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
	public List<UserPreference> getActivePreferences() {
		List<UserPreference> prefs = new ArrayList<UserPreference>();

		Cursor cursor = mDb.query(DATABASE_TABLE,
				columns, "isActive = 1", null, null, null, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			UserPreference pref = cursorToPref(cursor);
			prefs.add(pref);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return prefs;
	}
	
	

	
	
	private UserPreference cursorToPref(Cursor cursor) {
		UserPreference pref = new UserPreference();
		pref.setId(cursor.getLong(0));
		pref.setAppName(cursor.getString(1));
		pref.setIsActive(cursor.getInt(2));
		pref.setPrefValue(cursor.getString(3));
		return pref;
	}
	
	
		

	
	public boolean checkPrefStatus(String appName) {
		Cursor cursor = mDb.query(DATABASE_TABLE, columns, "appName = '"+appName+"'", null, null, null, null);
		cursor.moveToFirst();
		UserPreference selPref = cursorToPref(cursor);
		if(selPref.isPrefActive() == 1){
			cursor.close();
			return true;
		}else  {
			cursor.close();
			return false;
		}
	}
}