package parkgyu7.mytourtime.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import parkgyu7.mytourtime.BasicInfo;


/**
 *  PLAN 데이터베이스
 */
public class PlanDatabase {

	public static final String TAG = " ** PLAN DB";

	/**
	 * 싱글톤 인스턴스
	 */
	private static PlanDatabase database;

	/**
	 * table name for TOUR
	 */
	public static String TABLE_TOUR = "TOUR";

	/**
	 * table name for PLAN
	 */
	public static String TABLE_PLAN = "PLAN";

	/**
     * version
     */
	public static int DATABASE_VERSION = 1;


    /**
     * Helper class defined
     */
    private DatabaseHelper dbHelper;

    /**
     * SQLiteDatabase 인스턴스
     */
    private SQLiteDatabase db;

    /**
     * 컨텍스트 객체
     */
    private Context context;

    /**
     * 생성자
     */
	private PlanDatabase(Context context) {
		this.context = context;
	}

	/**
	 * 인스턴스 가져오기
	 */
	public static PlanDatabase getInstance(Context context) {
		if (database == null) {
			database = new PlanDatabase(context);
		}

		return database;
	}

	/**
	 * 데이터베이스 열기
	 */
    public boolean open() {
    	println("opening database [" + BasicInfo.DATABASE_NAME + "].");

    	dbHelper = new DatabaseHelper(context);
    	db = dbHelper.getWritableDatabase();

    	return true;
    }

    /**
     * 데이터베이스 닫기
     */
    public void close() {
    	println("closing database [" + BasicInfo.DATABASE_NAME + "].");
    	db.close();

    	database = null;
    }

    /**
     * execute raw query using the input SQL
     * close the cursor after fetching any result
     *
     * @param SQL
     * @return
     */
    public Cursor rawQuery(String SQL) {
		println("\n executeQuery called.\n");

		Cursor c1 = null;
		try {
			c1 = db.rawQuery(SQL, null);
			println("cursor count : " + c1.getCount());
		} catch(Exception ex) {
    		Log.e(TAG, "Exception in executeQuery", ex);
    	}

		return c1;
	}

    public boolean execSQL(String SQL) {
		println("\nexecute called.\n");

		try {
			Log.d(TAG, "SQL : " + SQL);
			db.execSQL(SQL);
	    } catch(Exception ex) {
			Log.e(TAG, "Exception in executeQuery", ex);
			return false;
		}

		return true;
	}



	/**
	 * Database Helper inner class
	 *
	 * 	CREATE TABLE of TOUR & PLAN
	 *
	 */
    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, BasicInfo.DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
        	println("creating database [" + BasicInfo.DATABASE_NAME + "].");

        	// TABLE_TOUR
        	println("creating table [" + TABLE_TOUR + "].");
        	// drop existing table
        	String DROP_SQL = "drop table if exists " + TABLE_TOUR;
        	try {
        		db.execSQL(DROP_SQL);
        	} catch(Exception ex) {
        		Log.e(TAG, "Exception in DROP_SQL", ex);
        	}

        	// create table
        	String CREATE_SQL = "create table " + TABLE_TOUR + "("
		        			+ "  _id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "	// TOUR ID
		        			+ "  TOUR_TITLE TEXT, "									// TOUR TITLE
		        			+ "  TOUR_firstDATE DATE, "								// TOUR FIRST DATE
		        			+ "  TOUR_lastDATE DATE, "								// TOUR LAST DATE
		        			+ "  CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP "	// TOUR 작성 날짜
		        			+ ")";
            try {
            	db.execSQL(CREATE_SQL);
            } catch(Exception ex) {
        		Log.e(TAG, "Exception in CREATE_SQL", ex);
        	}


			// TABLE_PLAN
			println("creating table [" + TABLE_PLAN + "].");

			// drop existing table
			DROP_SQL = "drop table if exists " + TABLE_PLAN;
			try {
				db.execSQL(DROP_SQL);
			} catch(Exception ex) {
				Log.e(TAG, "Exception in DROP_SQL", ex);
			}

			// create table
			CREATE_SQL = "create table " + TABLE_PLAN + "("
					+ "  _id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "    	// PLAN ID
					+ "  TOUR_ID INTEGER, "                            			// TOUR ID -> JOIN
					+ "  PLAN_DATETIME TIMESTAMP, "                    		 	// PLAN DATETIME
					+ "  PLAN_TITLE TEXT, "                           			// PLAN TITLE
					+ "  PLAN_BODY TEXT, "                       			// PLAN DETAIL
					+ "  CREATE_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP " 		// PLAN 작성 날짜
					+ ")";
			try {
				db.execSQL(CREATE_SQL);
			} catch(Exception ex) {
				Log.e(TAG, "Exception in CREATE_SQL", ex);
			}



        }

        public void onOpen(SQLiteDatabase db)
        {
        	println("opened database [" + BasicInfo.DATABASE_NAME + "].");

        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
        	println("Upgrading database from version " + oldVersion + " to " + newVersion + ".");



        }
    }

    private void println(String msg) {
    	Log.d(TAG, msg);
    }


}