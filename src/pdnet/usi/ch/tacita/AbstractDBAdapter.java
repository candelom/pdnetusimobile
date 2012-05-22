package pdnet.usi.ch.tacita;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public abstract class AbstractDBAdapter {

    protected static final String TAG = "DbAdapter";
    protected DatabaseHelper mDbHelper;
    protected SQLiteDatabase mDb;

    private static final String PREF_TABLE_CREATE = "CREATE TABLE preferences (" +
														"id integer primary key autoincrement, " +
														"appname text not null unique, " +
														"isActive integer not null, "+
														"pref_values text not null" +
													");";


    private static final String APP_TABLE_CREATE = "CREATE TABLE apps (" +
														"id integer primary key autoincrement, " +
														"name text not null unique, " +
														"namespace text not null, "+
														"view text not null," +
														"description text not null,"+
														"socket_address text not null,"+
														"icon text not null"+
													");";


    protected static final String DATABASE_NAME = "tacita.db";
    protected static final int DATABASE_VERSION = 2;
    protected final Context mCtx;
    protected static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(PREF_TABLE_CREATE);
            db.execSQL(APP_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS routes");
            onCreate(db);
        }
    }

    
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public AbstractDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    
    /**
     * Open or create the routes database.
     * 
     * @return this
     * @throws SQLException if the database could be neither opened or created
     */
    public AbstractDBAdapter open() throws SQLException {
		Log.v("DB", "open");
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    

    
    public void close() {
        mDbHelper.close();
    }

}