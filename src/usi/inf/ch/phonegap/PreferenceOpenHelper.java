package usi.inf.ch.phonegap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PreferenceOpenHelper extends SQLiteOpenHelper {

    private static final String PREF_TABLENAME = "preferences";
    
    
    private static final String DATABASE_NAME = "preferences.db";
	private static final int DATABASE_VERSION = 2;
    
    private static final String PREF_TABLE_CREATE = "DROP TABLE preferences;"+
	    											"CREATE TABLE preferences (" +
										                	"id integer primary key autoincrement, " +
										                	"appname text not null unique, " +
										                	"isActive integer not null, "+
										                	"pref_values text not null" +
										                ");";

    PreferenceOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PREF_TABLE_CREATE);
    }


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		Log.w(PreferenceOpenHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		
		db.execSQL("DROP TABLE IF EXISTS " + PREF_TABLENAME);
		onCreate(db);
	}
	
	
	
	
}