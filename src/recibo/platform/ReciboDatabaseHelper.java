package recibo.platform;

import recibo.platform.model.Item;
import recibo.platform.model.Receipt;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLite database helper.
 *
 * @author Lere Williams
 * @modified May 17, 2011
 *
 */
public class ReciboDatabaseHelper extends SQLiteOpenHelper {

  //TODO Add transactions to all database operations

  public static final String RECEIPT_TABLE_NAME = "receipts";
  public static final String ITEM_TABLE_NAME = "items";

  private static final String TAG = "ReciboDatabaseHelper";
  private static final String DB_NAME = "recibo_database";
  private static final int DB_VERSION = 1;

  public ReciboDatabaseHelper(Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  /**
   * Creates the item and receipt tables that back the recibo platform
   * if those tables do not already exist.
   */
  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + RECEIPT_TABLE_NAME + " ("
        + Receipt._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + Receipt.DATE + " INTEGER,"
        + Receipt.TYPE + " TEXT,"
        + Receipt.EXPIRATION + " INTEGER,"
        + Receipt.VENDOR + " TEXT,"
        + Receipt.CATEGORY + " TEXT,"
        + Receipt.TAX_RATE + " REAL,"
        + Receipt.ITEMS + " BLOB,"
        + Receipt.ATTRIBUTES + " BLOB,"
        + Receipt.VALID + " INTEGER"
        + ");");

    db.execSQL("CREATE TABLE " + ITEM_TABLE_NAME + " ("
        + Item._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + Item.RECEIPT_ID + " INTEGER,"
        + Item.NAME + " TEXT,"
        + Item.PRICE + " REAL,"
        + Item.TAX_RATE + " REAL,"
        + Item.UNITS + " INTEGER,"
        + Item.CATEGORY + " TEXT,"
        + Item.ATTRIBUTES + " BLOB,"
        + Item.VALID + " INTEGER"
        + ");");
  }

  @Override
  public void onOpen(SQLiteDatabase db) {
    db.setLockingEnabled(true);
  }

  //TODO Update tables in place rather than blasting old tables
  /**
   * Upgrades the database schema by blasting all old data and create
   * a new, empty database that conforms to the new schema.
   */
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
        + newVersion + " -- destroying all old data.");
    db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE_NAME);
    db.execSQL("DROP TABLE IF EXISTS " + RECEIPT_TABLE_NAME);
    onCreate(db);
  }



}
