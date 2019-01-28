// ------------------------------------ DBADapter.java ---------------------------------------------

// TODO: Change the package to match your project.
package richardshen.carbon_tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;


// TO USE:
// Change the package (at top) to match your project.
public class JourneyDBAdapter {

	/////////////////////////////////////////////////////////////////////
	//	Constants & Data
	/////////////////////////////////////////////////////////////////////
	// For logging:
	private static final String TAG = "JourneyDBAdapter";

	// DB Fields
	public static final String KEY_ROWID = "_id";
	public static final int COL_ROWID = 0;
	/*
	 * CHANGE 1:
	 */

	public static final String KEY_ROUTENAME = "route";
	public static final String KEY_CARNAME = "car";
	public static final String KEY_CARBRAND = "brand";
	public static final String KEY_ENGID = "engineId";
	public static final String KEY_DATE = "date";
	public static final String KEY_ICON = "iconId";

	public static final int COL_ROUTENAME = 1;
	public static final int COL_CARNAME = 2;
	public static final int COL_CARBRAND = 3;
	public static final int COL_ENGID = 4;
	public static final int COL_DATE = 5;
	public static final int COL_ICON = 6;

	public static final String[] ALL_KEYS = new String[] {KEY_ROWID, KEY_ROUTENAME, KEY_CARNAME, KEY_CARBRAND, KEY_ENGID, KEY_DATE, KEY_ICON};

	// DB info: it's name, and the table we are using (just one).
	public static final String DATABASE_NAME = "JourneyDatabase";
	public static final String DATABASE_TABLE = "journeyTable";
	// Track DB version if a new version of your app changes the format.
	public static final int DATABASE_VERSION = 2;

	private static final String DATABASE_CREATE_SQL =
			"create table " + DATABASE_TABLE
			+ " (" + KEY_ROWID + " integer primary key autoincrement, "

			/*
			 * CHANGE 2:
			 */
			// + KEY_{...} + " {type} not null"
			//	- Key is the column name you created above.
			//	- {type} is one of: text, integer, real, blob
			//		(http://www.sqlite.org/datatype3.html)
			//  - "not null" means it is a required field (must be given a value).
			// NOTE: All must be comma separated (end of line!) Last one must have NO comma!!
			+ KEY_ROUTENAME + " text not null, "
			+ KEY_CARNAME + " text not null, "
			+ KEY_CARBRAND + " text not null, "
			+ KEY_ENGID + " integer not null, "
			+ KEY_DATE + " text not null, "
			+ KEY_ICON + " integer not null"

			// Rest  of creation:
			+ ");";

	// Context of application who uses us.
	private final Context context;

	private DatabaseHelper myDBHelper;
	private SQLiteDatabase db;

	/////////////////////////////////////////////////////////////////////
	//	Public methods:
	/////////////////////////////////////////////////////////////////////

	public JourneyDBAdapter(Context ctx) {
		this.context = ctx;
		myDBHelper = new DatabaseHelper(context);
	}
	
	// Open the database connection.
	public JourneyDBAdapter open() {
		db = myDBHelper.getWritableDatabase();
		return this;
	}
	
	// Close the database connection.
	public void close() {
		myDBHelper.close();
	}
	
	// Add a new set of values to the database.
	public long insertRow(String routeName, String carName, String carBrand, int engId, Date date, int iconId, long rowId) {
		/*
		 * CHANGE 3:
		 */		
		// Create row's data:
		SimpleDateFormat toString = new SimpleDateFormat("dd/MM/yyyy");
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ROWID, rowId);
		initialValues.put(KEY_ROUTENAME, routeName);
		initialValues.put(KEY_CARNAME, carName);
		initialValues.put(KEY_CARBRAND, carBrand);
		initialValues.put(KEY_ENGID, engId);
		initialValues.put(KEY_DATE, toString.format(date));
		initialValues.put(KEY_ICON, iconId);
		
		// Insert it into the database.
		return db.insert(DATABASE_TABLE, null, initialValues);
	}
	
	// Delete a row from the database, by rowId (primary key)
	public boolean deleteRow(long rowId) {
		String where = KEY_ROWID + "=" + rowId;
		return db.delete(DATABASE_TABLE, where, null) != 0;
	}
	
	public void deleteAll() {
		Cursor c = getAllRows();
		long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
		if (c.moveToFirst()) {
			do {
				deleteRow(c.getLong((int) rowId));				
			} while (c.moveToNext());
		}
		c.close();
	}
	
	// Return all data in the database.
	public Cursor getAllRows() {
		String where = null;
		Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
				where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	// Get a specific row (by rowId)
	public Cursor getRow(long rowId) {
		String where = KEY_ROWID + "=" + rowId;
		Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS, 
						where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}
	
	// Change an existing row to be equal to new data.
	public boolean updateRow(int rowId, String routeName, String carName, String carBrand, int engId, Date date, int iconId) {
		String where = KEY_ROWID + "=" + rowId;

		/*
		 * CHANGE 4:
		 */
		// Create row's data:
		SimpleDateFormat toString = new SimpleDateFormat("dd/MM/yyyy");
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_ROUTENAME, routeName);
		newValues.put(KEY_CARNAME, carName);
		newValues.put(KEY_CARBRAND, carBrand);
		newValues.put(KEY_ENGID, engId);
		newValues.put(KEY_DATE, toString.format(date));
		newValues.put(KEY_ICON, iconId);
		
		// Insert it into the database.
		return db.update(DATABASE_TABLE, newValues, where, null) != 0;
	}
	
	
	
	/////////////////////////////////////////////////////////////////////
	//	Private Helper Classes:
	/////////////////////////////////////////////////////////////////////
	
	/**
	 * Private class which handles database creation and upgrading.
	 * Used to handle low-level database access.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE_SQL);			
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading application's database from version " + oldVersion
					+ " to " + newVersion + ", which will destroy all old data!");
			
			// Destroy old database:
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			
			// Recreate new database:
			onCreate(_db);
		}
	}
}
