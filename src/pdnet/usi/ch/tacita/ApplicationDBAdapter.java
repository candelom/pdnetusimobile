package pdnet.usi.ch.tacita;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class ApplicationDBAdapter extends AbstractDBAdapter {

	private static ApplicationDBAdapter instance = null;
    private static final String[] columns = new String[]{"id", "name", "namespace", "view", "socket_address", "description", "icon"};
    private static final String DATABASE_TABLE = "apps";

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created.
     * 
     * @param ctx the Context within which to work
     */
    private ApplicationDBAdapter(Context ctx) {
    	super(ctx);
    }
 
    
    public static ApplicationDBAdapter getInstance(Context context) {
    	if(null == instance) {
            instance = new ApplicationDBAdapter(context);
        }
        return instance;
    }
    
    
    
    
    /**
	 * Creates a preference object.
	 * @param pref
	 * @return
	 */
	public Application createApp(String name, String namespace, String view, String socket_address, String description, String icon) {
		Log.v("DB Interface", "craeting app entry");
		ContentValues val = new ContentValues();
		val.put("name", name);
		val.put("namespace", namespace);
		val.put("view", view);
		val.put("socket_address", socket_address);
		val.put("description", description);
		val.put("icon", icon);
		
		long insertId = mDb.insert(DATABASE_TABLE, null, val);
		Cursor cursor = mDb.query(DATABASE_TABLE,
				columns, "id = " + insertId, null, null, null, null);
		
		cursor.moveToFirst();
		Application newApp = cursorToApp(cursor);
		cursor.close();
		return newApp;
	}
	
	
	
	/**
	 * Selects the preference given the appName (unique)
	 * @param appName
	 * @return
	 */
	public Application getApp(long id) {
		
		Application selApp = null;
		Cursor cursor = mDb.query(DATABASE_TABLE, columns, "id = '" + id+"'", null, null, null, null);
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
		mDb.delete(DATABASE_TABLE, "namespace = '" + namespace+"'", null);
		LocationService.deactivateSocket(namespace);
	}
	

	
	/**
	 * Deletes all entries
	 */
	public void deleteAllApps() {
		
		mDb.delete("apps", null, null);
		
	}
	
	/**
	 * Lists all preference entries.
	 * @return
	 */
	public List<Application> getAllApps() {
		List<Application> apps = new ArrayList<Application>();

		Cursor cursor = mDb.query(DATABASE_TABLE,
				columns, null, null, null, null, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Application app = cursorToApp(cursor);
			apps.add(app);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return apps;
	}

	
	
	private Application cursorToApp(Cursor cursor) {
		Application app = new Application();
		app.setId(cursor.getLong(0));
		app.setName(cursor.getString(1));
		app.setNamespace(cursor.getString(2));
		app.setView(cursor.getString(3));
		app.setSocketAddress(cursor.getString(4));
		app.setDescription(cursor.getString(5));
		app.setIcon(cursor.getString(6));
		return app;
	}
	

	public boolean checkAppStatus(String appName) {

		Cursor cursor = mDb.query(DATABASE_TABLE, columns, "namespace = '"+appName+"'", null, null, null, null);
		if(cursor.getCount() > 0) {
			
			return true;
		} 
		cursor.close();
		return false;
	}
    
    
    
}