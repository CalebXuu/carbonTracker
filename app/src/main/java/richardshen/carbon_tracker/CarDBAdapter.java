// ------------------------------------ DBADapter.java ---------------------------------------------

// TODO: Change the package to match your project.
package richardshen.carbon_tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


// TO USE:
// Change the package (at top) to match your project.
public class CarDBAdapter {

	/////////////////////////////////////////////////////////////////////
	//	Constants & Data
	/////////////////////////////////////////////////////////////////////
	// For logging:
	private static final String TAG = "CarDBAdapter";
	
	// DB Fields
	public static final String KEY_ROWID = "_id";
	public static final int COL_ROWID = 0;
	/*
	 * CHANGE 1:
	 */
	public static final String KEY_ENGID = "engineId";
	public static final String KEY_BRAND = "brand";
	public static final String KEY_MODEL = "model";
	public static final String KEY_YR = "year";
	public static final String KEY_CYLINDERS = "cylinders";
	public static final String KEY_FUELTYPE = "fuelType";
	public static final String KEY_CTY08 = "city08";
	public static final String KEY_HWY08 = "hwy08";
	public static final String KEY_COMB08 = "comb08";
	public static final String KEY_CO2TAILPIPLINE = "co2Tailpipe08";
	public static final String KEY_BARRELS = "barrels";
	public static final String KEY_DISPL = "displ";
	public static final String KEY_DRIVE = "drive";
	public static final String KEY_TRANY = "trany";
	public static final String KEY_NAME = "name";
	public static final String KEY_ICON = "icon";
	public static final int COL_ENGID = 1;
	public static final int COL_BRNAD = 2;
	public static final int COL_MODEL = 3;
	public static final int COL_YR = 4;
	public static final int COL_CYLINDERS = 5;
	public static final int COL_FUELTYPE = 6;
	public static final int COL_CTY08 = 7;
	public static final int COL_HWY08= 8;
	public static final int COL_COMB08 = 9;
	public static final int COL_CO2TAILPIPLINE = 10;
	public static final int COL_BARRELS = 11;
	public static final int COL_DISPL = 12;
	public static final int COL_DRIVE = 13;
	public static final int COL_TRANY = 14;
	public static final int COL_NAME = 15;
	public static final int COL_ICON = 16;
	
	public static final String[] ALL_KEYS = new String[] {KEY_ROWID, KEY_ENGID, KEY_BRAND, KEY_MODEL, KEY_YR, KEY_CYLINDERS, KEY_FUELTYPE, KEY_CTY08, KEY_HWY08, KEY_COMB08, KEY_CO2TAILPIPLINE, KEY_BARRELS, KEY_DISPL, KEY_DRIVE, KEY_TRANY, KEY_NAME, KEY_ICON};
	
	// DB info: it's name, and the table we are using (just one).
	public static final String DATABASE_NAME = "CarDatabase";
	public static final String DATABASE_TABLE = "carTable";
	// Track DB version if a new version of your app changes the format.
	public static final int DATABASE_VERSION = 3;
	
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
			+ KEY_ENGID + " integer not null, "
			+ KEY_BRAND + " text not null, "
			+ KEY_MODEL + " text not null, "
			+ KEY_YR + " integer not null, "
			+ KEY_CYLINDERS + " integer not null, "
			+ KEY_FUELTYPE + " text not null, "
			+ KEY_CTY08 + " integer not null, "
			+ KEY_HWY08 + " integer not null, "
			+ KEY_COMB08 + " integer not null, "
			+ KEY_CO2TAILPIPLINE + " real not null, "
			+ KEY_BARRELS + " real not null, "
			+ KEY_DISPL + " real not null, "
			+ KEY_DRIVE + " text not null, "
			+ KEY_TRANY + " text not null, "
			+ KEY_NAME + " text not null, "
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
	
	public CarDBAdapter(Context ctx) {
		this.context = ctx;
		myDBHelper = new DatabaseHelper(context);
	}
	
	// Open the database connection.
	public CarDBAdapter open() {
		db = myDBHelper.getWritableDatabase();
		return this;
	}
	
	// Close the database connection.
	public void close() {
		myDBHelper.close();
	}
	
	// Add a new set of values to the database.
	public long insertRow(int engineId, String brand, String model, int year, int cylinders, String fuelType, int city08, int hwy08, int comb08, float co2Tailpipe08, double barrels, double displ, String drive, String trany, String name, int iconId, int index) {
		/*
		 * CHANGE 3:
		 */		
		// Create row's data:
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ROWID, index);
		initialValues.put(KEY_ENGID, engineId);
		initialValues.put(KEY_BRAND, brand);
		initialValues.put(KEY_MODEL, model);
		initialValues.put(KEY_YR, year);
		initialValues.put(KEY_CYLINDERS, cylinders);
		initialValues.put(KEY_FUELTYPE, fuelType);
		initialValues.put(KEY_CTY08, city08);
		initialValues.put(KEY_HWY08, hwy08);
		initialValues.put(KEY_COMB08, comb08);
		initialValues.put(KEY_CO2TAILPIPLINE, co2Tailpipe08);
		initialValues.put(KEY_BARRELS, barrels);
		initialValues.put(KEY_DISPL, displ);
		initialValues.put(KEY_DRIVE, drive);
		initialValues.put(KEY_TRANY, trany);
		initialValues.put(KEY_NAME, name);
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
	public boolean updateRow(long rowId, int engineId, String brand, String model, int year, int cylinders, String fuelType, int city08, int hwy08, int comb08, float co2Tailpipe08, double barrels, double displ, String drive, String trany, String name, int icon) {
		String where = KEY_ROWID + "=" + rowId;

		/*
		 * CHANGE 4:
		 */
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_ENGID, engineId);
		newValues.put(KEY_BRAND, brand);
		newValues.put(KEY_MODEL, model);
		newValues.put(KEY_YR, year);
		newValues.put(KEY_CYLINDERS, cylinders);
		newValues.put(KEY_FUELTYPE, fuelType);
		newValues.put(KEY_CTY08, city08);
		newValues.put(KEY_HWY08, hwy08);
		newValues.put(KEY_COMB08, comb08);
		newValues.put(KEY_CO2TAILPIPLINE, co2Tailpipe08);
		newValues.put(KEY_BARRELS, barrels);
		newValues.put(KEY_DISPL, displ);
		newValues.put(KEY_DRIVE, drive);
		newValues.put(KEY_TRANY, trany);
		newValues.put(KEY_NAME, name);
		newValues.put(KEY_ICON, icon);
		
		// Insert it into the database.
		return db.update(DATABASE_TABLE, newValues, where, null) != 0;
	}

	public boolean updateRowName(long rowId, String name) {
		String where = KEY_ROWID + "=" + rowId;

		/*
		 * CHANGE 4:
		 */
		// Create row's data:
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_NAME, name);

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
