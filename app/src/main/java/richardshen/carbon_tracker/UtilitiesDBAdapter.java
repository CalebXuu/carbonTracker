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
public class UtilitiesDBAdapter {

	/////////////////////////////////////////////////////////////////////
	//	Constants & Data
	/////////////////////////////////////////////////////////////////////
	// For logging:
	private static final String TAG = "UtilitiesDBAdapter";

	// DB Fields
	public static final String KEY_ROWID = "_id";
	public static final int COL_ROWID = 0;
	/*
	 * CHANGE 1:
	 */

	public static final String KEY_STARTDATE = "billStartDate";
	public static final String KEY_ENDDATE = "billEndDate";
	public static final String KEY_ELECBILL = "electricBill";
	public static final String KEY_KILOWATT = "kW";
	public static final String KEY_NATURALBILL = "naturalBill";
	public static final String KEY_GIGAJOULE = "gJ";
	public static final String KEY_HOUSEHOLDS = "NumberOfHouseholds";

	public static final int COL_STARTDATE = 1;
	public static final int COL_ENDDATE = 2;
	public static final int COL_ELECBILL = 3;
	public static final int COL_KILOWATT = 4;
	public static final int COL_NATURALBILL = 5;
	public static final int COL_GIGAJOULE = 6;
	public static final int COL_HOUSEHOLDS = 7;

	public static final String[] ALL_KEYS = new String[] {KEY_ROWID, KEY_STARTDATE, KEY_ENDDATE, KEY_ELECBILL, KEY_KILOWATT, KEY_NATURALBILL, KEY_GIGAJOULE, KEY_HOUSEHOLDS};

	// DB info: it's name, and the table we are using (just one).
	public static final String DATABASE_NAME = "UtilitiesDatabase";
	public static final String DATABASE_TABLE = "utilitiesTable";
	// Track DB version if a new version of your app changes the format.
	public static final int DATABASE_VERSION = 1;

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
			+ KEY_STARTDATE + " text not null, "
			+ KEY_ENDDATE + " text not null, "
			+ KEY_ELECBILL + " real not null, "
			+ KEY_KILOWATT + " real not null, "
			+ KEY_NATURALBILL + " real not null, "
                    + KEY_GIGAJOULE + " real not null, "
                    + KEY_HOUSEHOLDS + " real not null"

			// Rest  of creation:
			+ ");";


	// Context of application who uses us.
	private final Context context;

	private DatabaseHelper myDBHelper;
	private SQLiteDatabase db;

	/////////////////////////////////////////////////////////////////////
	//	Public methods:
	/////////////////////////////////////////////////////////////////////

	public UtilitiesDBAdapter(Context ctx) {
		this.context = ctx;
		myDBHelper = new DatabaseHelper(context);
	}
	
	// Open the database connection.
	public UtilitiesDBAdapter open() {
		db = myDBHelper.getWritableDatabase();
		return this;
	}
	
	// Close the database connection.
	public void close() {
		myDBHelper.close();
	}
	
	// Add a new set of values to the database.
	public long insertRow(Date billStartDate, Date billEndDate, float electricBill, float gW, float naturalBill, float gJ, float people, long rowId) {
		/*
		 * CHANGE 3:
		 */		
		// Create row's data:
		SimpleDateFormat toString = new SimpleDateFormat("dd/MM/yyyy");
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ROWID, rowId);
		initialValues.put(KEY_STARTDATE, toString.format(billStartDate));
		initialValues.put(KEY_ENDDATE, toString.format(billEndDate));
		initialValues.put(KEY_ELECBILL, electricBill);
		initialValues.put(KEY_KILOWATT, gW);
		initialValues.put(KEY_NATURALBILL, naturalBill);
		initialValues.put(KEY_GIGAJOULE, gJ);
		initialValues.put(KEY_HOUSEHOLDS, people);
		
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
	public boolean updateRow(int rowId, Date billStartDate, Date billEndDate, float electricBill, float gW, float naturalBill, float gJ, float people) {
		String where = KEY_ROWID + "=" + rowId;

		/*
		 * CHANGE 4:
		 */
		// Create row's data:
		SimpleDateFormat toString = new SimpleDateFormat("dd/MM/yyyy");
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_ROWID, rowId);
		newValues.put(KEY_STARTDATE, toString.format(billStartDate));
		newValues.put(KEY_ENDDATE, toString.format(billEndDate));
		newValues.put(KEY_ELECBILL, electricBill);
		newValues.put(KEY_KILOWATT, gW);
		newValues.put(KEY_NATURALBILL, naturalBill);
		newValues.put(KEY_GIGAJOULE, gJ);
		newValues.put(KEY_HOUSEHOLDS, people);
		
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
