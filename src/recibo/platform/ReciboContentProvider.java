package recibo.platform;

import recibo.platform.model.AttributeHash;
import recibo.platform.model.Item;
import recibo.platform.model.Receipt;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * Exposes the underlying receipt database for the recibo platform.
 * All interactions follow the normal content provider model, with 
 * the exception of the static <code>dummyQuery()</code> method
 * which is used to perform a dummy query (returns a
 * <code>Cursor</code> that points to a dummy receipt database).
 *
 * @author Lere Williams
 * @modified May 17, 2011
 *
 */
public class ReciboContentProvider extends ContentProvider {

  //TODO Add transactions to all database operations
  //TODO Define write permissions
  //TODO Think up better exceptions to throw from get...() methods
  //TODO Improve default insert(): perhaps throw exception instead?

  public static final String TAG = "ReciboContentProvider";
  public static final String AUTHORITY = "recibo.platform.recibocontentprovider";
  public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/receipts");
  public static final Uri CONTENT_URI_ITEMS = Uri.parse("content://" + AUTHORITY + "/items");

  public static final String RECEIPT_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.recibo.receipt";
  public static final String RECEIPT_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.recibo.receipt"; 
  public static final String ITEM_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.recibo.item";
  public static final String ITEM_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.recibo.item";

  /**
   * A <code>Cursor</code> that represents a dummy receipt database.
   * Note convenience constructor in <code>Receipt</code> that takes
   * a Cursor object.
   */
  private static class DummyCursor implements Cursor {

    private Receipt[] rcpts;
    private int pos;

    public DummyCursor(int which) {
      switch (which) {
        case 0:
          Item[] bodeguita = {new Item(0, 0, 0, "crab cakes", 11.00, 0, 1, Item.CATEGORY_FOOD, new AttributeHash()),
              new Item(1, 0, 0, "ceviche", 11.50, 0, 1, Item.CATEGORY_FOOD, new AttributeHash()),
              new Item(2, 0, 0, "ropa vieja", 18.00, 0, 1, Item.CATEGORY_FOOD, new AttributeHash()),
              new Item(3, 0, 0, "paella de la bodeguita", 48.00, 0, 1, Item.CATEGORY_FOOD, new AttributeHash()),
              new Item(4, 0, 0, "negro modelo", 3.50, 0, 2, Item.CATEGORY_DRINK, new AttributeHash())};
          Item[] groceries = {new Item(0, 1, 0, "tomatoes", 4.55, 0, 1, Item.CATEGORY_FOOD, new AttributeHash()),
              new Item(1, 1, 0, "pasta", 2.99, 0, 1, Item.CATEGORY_FOOD, new AttributeHash()),
              new Item(2, 1, 0, "ice cream", 6.99, 0, 1, Item.CATEGORY_FOOD, new AttributeHash())};
          Item[] dryCleaning = {new Item(0, 2, 0, "suit cleaning", 20.00, 0, 1, Item.CATEGORY_DRYCLEANING, new AttributeHash())};

          rcpts = new Receipt[3];
          rcpts[0] = new Receipt(0, System.currentTimeMillis(), Receipt.TYPE_PROOF, System.currentTimeMillis(), "La Bodeguita Del Medio", Receipt.CATEGORY_DINING, 18.00, bodeguita, new AttributeHash());
          rcpts[1] = new Receipt(1, System.currentTimeMillis(), Receipt.TYPE_PROOF, System.currentTimeMillis(), "Trader Joe's", Receipt.CATEGORY_GROCERY, 9.25, groceries, new AttributeHash());
          rcpts[2] = new Receipt(2, System.currentTimeMillis(), Receipt.TYPE_TOKEN, System.currentTimeMillis(), "Palo Alto Cleaners", Receipt.CATEGORY_SERVICE, 9.25, dryCleaning, new AttributeHash());
          break;
        case 1:
        default:
          AttributeHash milk = new AttributeHash(); milk.addAttribute("origin", "bakersfield, CA");
          AttributeHash bread = new AttributeHash(); bread.addAttribute("origin", "Palo Alto, cA");
          AttributeHash eggs = new AttributeHash(); eggs.addAttribute("origin", "Merced, CA");
          Item[] essentials = {new Item(0, 0, 0, "milk", 3.50, 0, 1, Item.CATEGORY_DRINK, milk),
              new Item(1, 0, 0, "bread", 2.50, 0, 1, Item.CATEGORY_FOOD, bread),
              new Item(2, 0, 0, "eggs", 4.00, 0, 1, Item.CATEGORY_FOOD, eggs)};
          AttributeHash beer = new AttributeHash(); beer.addAttribute("origin", "Boston, MA");
          AttributeHash charcoal = new AttributeHash(); charcoal.addAttribute("type", "easy light");
          AttributeHash asparagus = new AttributeHash(); asparagus.addAttribute("suggestion", "grill these");
          AttributeHash skewers = new AttributeHash(); skewers.addAttribute("origin", "Flint, MI");
          AttributeHash beef = new AttributeHash(); beef.addAttribute("origin", "Topeka, Kansas");
          Item[] party = {new Item(0, 1, 0, "beer", 18.99, 0, 1, Item.CATEGORY_DRINK, beer),
              new Item(1, 1, 0, "charcoal", 15.00, 0, 1, Item.CATEGORY_OTHER, charcoal),
              new Item(2, 1, 0, "asparagus", 6.34, 0, 1, Item.CATEGORY_FOOD, asparagus),
              new Item(3, 1, 0, "beef", 11.99, 0, 1, Item.CATEGORY_FOOD, beef),
              new Item(4, 1, 0, "skewers", 7.99, 0, 1, Item.CATEGORY_OTHER, skewers)};
          rcpts = new Receipt[2];
          rcpts[0] = new Receipt(0, System.currentTimeMillis(), Receipt.TYPE_PROOF, System.currentTimeMillis(), "Whole Foods", Receipt.CATEGORY_GROCERY, 9.25, essentials, new AttributeHash());
          rcpts[1] = new Receipt(1, System.currentTimeMillis(), Receipt.TYPE_PROOF, System.currentTimeMillis(), "Safeway", Receipt.CATEGORY_GROCERY, 9.25, party, new AttributeHash());
          break;
      }

      pos = 0;
    }


    @Override
    public void close() {
      rcpts = null;
      pos = -1;
    }

    @Override
    public void copyStringToBuffer(int arg0, CharArrayBuffer arg1) { }

    @Override
    public void deactivate() { }

    @Override
    public byte[] getBlob(int columnIndex) {
      switch (columnIndex) {
        case 7: return rcpts[pos].itemsToBytes();
        case 8: return rcpts[pos].attributesToBytes();
        default: throw new RuntimeException("called getBlob() on column that does not contain a byte[]");
      }
    }

    @Override
    public int getColumnCount() {
      return 10;
    }

    @Override
    public int getColumnIndex(String arg0) {
      if (arg0.equals(Receipt._ID)) return 0;
      else if (arg0.equals(Receipt.DATE)) return 1;
      else if (arg0.equals(Receipt.TYPE)) return 2;
      else if (arg0.equals(Receipt.EXPIRATION)) return 3;
      else if (arg0.equals(Receipt.VENDOR)) return 4;
      else if (arg0.equals(Receipt.CATEGORY)) return 5;
      else if (arg0.equals(Receipt.TAX_RATE)) return 6;
      else if (arg0.equals(Receipt.ITEMS)) return 7;
      else if (arg0.equals(Receipt.ATTRIBUTES)) return 8;
      else if (arg0.equals(Receipt.VALID)) return 9;
      else return -1;
    }

    @Override
    public int getColumnIndexOrThrow(String arg0)
    throws IllegalArgumentException {
      int result = getColumnIndex(arg0);
      if (result == -1) throw new IllegalArgumentException();
      else return result;
    }

    @Override
    public String getColumnName(int columnIndex) {
      switch (columnIndex) {
        case 0: return Receipt._ID;
        case 1: return Receipt.DATE;
        case 2: return Receipt.TYPE;
        case 3: return Receipt.EXPIRATION;
        case 4: return Receipt.VENDOR;
        case 5: return Receipt.CATEGORY;
        case 6: return Receipt.TAX_RATE;
        case 7: return Receipt.ITEMS;
        case 8: return Receipt.ATTRIBUTES;
        case 9: return Receipt.VALID;
        default: return null;
      }
    }

    @Override
    public String[] getColumnNames() {
      String[] names = {Receipt._ID, Receipt.DATE, Receipt.TYPE, Receipt.EXPIRATION, Receipt.VENDOR,
          Receipt.CATEGORY, Receipt.TAX_RATE, Receipt.ITEMS, Receipt.ATTRIBUTES, Receipt.VALID};
      return names;
    }

    @Override
    public int getCount() {
      return rcpts.length;
    }

    @Override
    public double getDouble(int columnIndex) {
      switch (columnIndex) {
        case 6: return rcpts[pos].tax_rate;
        default: throw new RuntimeException("called getDouble() on column that does not contain a double");
      }
    }

    @Override
    public Bundle getExtras() {
      return null;
    }

    @Override
    public float getFloat(int columnIndex) {
      return 0;
    }

    @Override
    public int getInt(int columnIndex) {
      switch (columnIndex) {
        case 0: return rcpts[pos]._id;
        case 2: return rcpts[pos].type;
        case 5: return rcpts[pos].category;
        case 9: return rcpts[pos].getValidity();
        default: throw new RuntimeException("called getInt() on column that does not contain an integer");
      }
    }

    @Override
    public long getLong(int columnIndex) {
      switch (columnIndex) {
        case 1: return rcpts[pos].date;
        case 3: return rcpts[pos].expiration;
        default: throw new RuntimeException("called getLong() on column that does not contain a long");
      }
    }

    @Override
    public int getPosition() {
      return pos;
    }

    @Override
    public short getShort(int columnIndex) {
      throw new RuntimeException("called getShort() on column that does not contain a short");
    }

    @Override
    public String getString(int columnIndex) {
      switch (columnIndex) {
        case 4: return rcpts[pos].vendor.toString();
        default: throw new RuntimeException("called getString() on column that does not contain a String");
      }
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
      return false;
    }

    @Override
    public boolean isAfterLast() {
      return pos >= rcpts.length;
    }

    @Override
    public boolean isBeforeFirst() {
      return pos < 0;
    }

    @Override
    public boolean isClosed() {
      return (rcpts == null);
    }

    @Override
    public boolean isFirst() {
      return pos == 0;
    }

    @Override
    public boolean isLast() {
      return pos == rcpts.length - 1;
    }

    @Override
    public boolean isNull(int columnIndex) {
      switch (columnIndex) {
        case 0: return rcpts[pos]._id == 0;
        case 1: return rcpts[pos].date == -1;
        case 2: return rcpts[pos].type == -1;
        case 3: return rcpts[pos].expiration == -1;
        case 4: return rcpts[pos].vendor == null;
        case 5: return rcpts[pos].category == -1;
        case 6: return rcpts[pos].items == null;
        case 7: return rcpts[pos].attributes == null;
        default: return true;
      }
    }

    @Override
    public boolean move(int offset) {
      pos += offset;
      if (pos < 0) pos = -1;
      else if (pos > rcpts.length) pos = rcpts.length;
      return (pos >= 0 && pos < rcpts.length);
    }

    @Override
    public boolean moveToFirst() {
      pos = 0;
      return rcpts.length > 0;
    }

    @Override
    public boolean moveToLast() {
      pos = rcpts.length - 1;
      return rcpts.length > 0;
    }

    @Override
    public boolean moveToNext() {
      pos += 1;
      if (pos >= rcpts.length) pos = rcpts.length;
      return pos < rcpts.length ;
    }

    @Override
    public boolean moveToPosition(int position) {
      pos = position;
      if (pos < 0) pos = -1;
      if (pos > rcpts.length) pos = rcpts.length;
      return (pos >= 0 && pos < rcpts.length);
    }

    @Override
    public boolean moveToPrevious() {
      pos -= 1;
      if (pos < 0) pos = -1;
      return pos >= 0;
    }

    @Override
    public void registerContentObserver(ContentObserver observer) { }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) { }

    @Override
    @Deprecated
    public boolean requery() {
      return false;
    }

    @Override
    public Bundle respond(Bundle extras) {
      return null;
    }

    @Override
    public void setNotificationUri(ContentResolver cr, Uri uri) { }

    @Override
    public void unregisterContentObserver(ContentObserver observer) { }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) { }

  }

  /**
   * 
   * @param which the fake receipt to return: 0 for Billshare, 1 for Origins
   * @return <code>Cursor</code> pointing to a fake receipt database
   */
  public static Cursor dummyQuery(int which) {
    return new DummyCursor(which);
  }

  private static final int RECEIPTS = 1;
  private static final int RECEIPTS_ID = 2;
  private static final int ITEMS = 3;
  private static final int ITEMS_ID = 4;
  private static final String DEFAULT_RECEIPT_ORDERING = Receipt._ID + " ASC";
  private static final String DEFAULT_ITEM_ORDERING = Item._ID + " ASC";

  private ReciboDatabaseHelper dbHelper;
  private static final UriMatcher uriMatcher;
  private static final ContentValues defaultReceiptValues;
  private static final ContentValues defaultItemValues;
  private static final ContentValues invalidationUpdate;

  static {
    uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    uriMatcher.addURI(AUTHORITY, "receipts", RECEIPTS);
    uriMatcher.addURI(AUTHORITY, "receipts/#", RECEIPTS_ID);
    uriMatcher.addURI(AUTHORITY, "items", ITEMS);
    uriMatcher.addURI(AUTHORITY, "items/#", ITEMS_ID);

    defaultReceiptValues = new ContentValues();
    defaultReceiptValues.put(Receipt.DATE, System.currentTimeMillis());
    defaultReceiptValues.put(Receipt.TYPE, Receipt.TYPE_PROOF);
    defaultReceiptValues.put(Receipt.EXPIRATION, System.currentTimeMillis());
    defaultReceiptValues.put(Receipt.VENDOR, "(none)");
    defaultReceiptValues.put(Receipt.CATEGORY, Receipt.CATEGORY_OTHER);
    defaultReceiptValues.put(Receipt.TAX_RATE, 0.0);
    defaultReceiptValues.put(Receipt.ITEMS, (byte[])null);
    defaultReceiptValues.put(Receipt.ATTRIBUTES, (byte[])null);

    defaultItemValues = new ContentValues();
    defaultItemValues.put(Item.RECEIPT_ID, 0);
    defaultItemValues.put(Item.NAME, "(none)");
    defaultItemValues.put(Item.PRICE, 0.0);
    defaultItemValues.put(Item.TAX_RATE, 0.0);
    defaultItemValues.put(Item.UNITS, 0);
    defaultItemValues.put(Item.CATEGORY, Item.CATEGORY_OTHER);
    defaultItemValues.put(Item.ATTRIBUTES, (byte[])null);

    invalidationUpdate = new ContentValues();
    invalidationUpdate.put(Receipt.VALID, 0);
  }

  @Override
  public boolean onCreate() {
    dbHelper = new ReciboDatabaseHelper(getContext());
    return true; //assumes all errors occur via exceptions
  }

  //executes the query; sensitive to invalidation vs. true deletion
  private Cursor doQuery(Uri uri, String[] projection, String selection, String[] selectionArgs,
      String sortOrder, boolean onlyReturnValidEntries)
  {
    SQLiteQueryBuilder qBldr = new SQLiteQueryBuilder();
    String table;
    String ordering;

    switch (uriMatcher.match(uri)) {
      case RECEIPTS:
        table = ReciboDatabaseHelper.RECEIPT_TABLE_NAME;
        ordering = TextUtils.isEmpty(sortOrder) ? DEFAULT_RECEIPT_ORDERING : sortOrder;
        if (onlyReturnValidEntries) qBldr.appendWhere(Receipt.VALID + " = 1");
        break;

      case RECEIPTS_ID:
        table = ReciboDatabaseHelper.RECEIPT_TABLE_NAME;
        ordering = TextUtils.isEmpty(sortOrder) ? DEFAULT_RECEIPT_ORDERING : sortOrder;
        qBldr.appendWhere(Receipt._ID + "=" + uri.getPathSegments().get(1));
        if (onlyReturnValidEntries) qBldr.appendWhere(Receipt.VALID + " = 1");
        break;

      case ITEMS:
        table = ReciboDatabaseHelper.ITEM_TABLE_NAME;
        ordering = TextUtils.isEmpty(sortOrder) ? DEFAULT_ITEM_ORDERING : sortOrder;
        if (onlyReturnValidEntries) qBldr.appendWhere(Item.VALID + " = 1");
        break;

      case ITEMS_ID:
        table = ReciboDatabaseHelper.ITEM_TABLE_NAME;
        ordering = TextUtils.isEmpty(sortOrder) ? DEFAULT_ITEM_ORDERING : sortOrder;
        qBldr.appendWhere(Item._ID + "=" + uri.getPathSegments().get(1));
        if (onlyReturnValidEntries) qBldr.appendWhere(Item.VALID + " = 1");
        break;

      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }


    qBldr.setTables(table);
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    Cursor c = qBldr.query(db, projection, selection, selectionArgs, null, null, ordering);

    // Tell the cursor what uri to watch, so it knows when its source data changes
    c.setNotificationUri(getContext().getContentResolver(), uri);
    return c;
  }

  /**
   * Performs a query on the database, returning only those records that are still valid.
   */
  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) 
  {
    return doQuery(uri, projection, selection, selectionArgs, sortOrder, true);
  }

  /**
   * Performs a query on the database, returning all records (including those that are invalid).
   */
  public Cursor queryAll(Uri uri, String[] projection, String selection, String[] selectionArgs,
      String sortOrder) {
    return doQuery(uri, projection, selection, selectionArgs, sortOrder, false);
  }

  @Override
  public String getType(Uri uri) {
    switch (uriMatcher.match(uri)) {
      case RECEIPTS: return RECEIPT_CONTENT_TYPE;
      case RECEIPTS_ID: return RECEIPT_CONTENT_ITEM_TYPE;
      case ITEMS: return ITEM_CONTENT_TYPE;
      case ITEMS_ID: return ITEM_CONTENT_ITEM_TYPE;
      default: throw new IllegalArgumentException("Unknown URI: " + uri);
    }
  }

  /**
   * Inserts a new receipt or a new item into the database, depending on the
   * given URI.  If no value is specified for any single field then the entire
   * record is replaced by a default (null) record for the relevant type.  
   * 
   * @return the URI of the newly inserted record
   */
  @Override
  public Uri insert(Uri uri, ContentValues values) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    String table;
    Uri contentUri;

    switch (uriMatcher.match(uri)) {
      case RECEIPTS:
        if (!(values.containsKey(Receipt.DATE) 
            && values.containsKey(Receipt.TYPE)
            && values.containsKey(Receipt.EXPIRATION)
            && values.containsKey(Receipt.VENDOR)
            && values.containsKey(Receipt.CATEGORY)
            && values.containsKey(Receipt.TAX_RATE)
            && values.containsKey(Receipt.ITEMS)
            && values.containsKey(Receipt.ATTRIBUTES)))
          values = defaultReceiptValues;
        table = ReciboDatabaseHelper.RECEIPT_TABLE_NAME;
        contentUri = CONTENT_URI;
        break;

      case ITEMS:
        if (!(values.containsKey(Item.RECEIPT_ID)
            && values.containsKey(Item.NAME)
            && values.containsKey(Item.CATEGORY)
            && values.containsKey(Item.PRICE)
            && values.containsKey(Item.TAX_RATE)
            && values.containsKey(Item.UNITS)
            && values.containsKey(Item.ATTRIBUTES)))
          values = defaultItemValues;
        table = ReciboDatabaseHelper.ITEM_TABLE_NAME;
        contentUri = CONTENT_URI_ITEMS;
        break;

      default: throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    long rowId = db.insert(table, null, values);
    if (rowId > 0) {
      Uri newRecordUri = ContentUris.withAppendedId(contentUri, rowId);
      getContext().getContentResolver().notifyChange(newRecordUri, null);
      return newRecordUri;
    }
    else throw new SQLException("Failed to insert row into " + uri);
  }

  /**
   * Invalidates (without permanently deleting) receipts or an items depending
   * on the given URI.  The deleted records are still accessible via <code>queryAll</code>
   * 
   * @return the number of records invalidated
   */
  @Override
  public int delete(Uri uri, String where, String[] whereArgs) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    int count;

    switch (uriMatcher.match(uri)) {
      case RECEIPTS:
        count = db.update(ReciboDatabaseHelper.RECEIPT_TABLE_NAME, invalidationUpdate, where, whereArgs);
        break;

      case RECEIPTS_ID:
        String receiptId = uri.getPathSegments().get(1);
        count = db.update(ReciboDatabaseHelper.RECEIPT_TABLE_NAME, invalidationUpdate, Receipt._ID + "=" + receiptId
            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
        break;

      case ITEMS:
        count = db.update(ReciboDatabaseHelper.ITEM_TABLE_NAME, invalidationUpdate, where, whereArgs);
        break;

      case ITEMS_ID:
        String itemId = uri.getPathSegments().get(1);
        count = db.update(ReciboDatabaseHelper.RECEIPT_TABLE_NAME, invalidationUpdate, Item._ID + "=" + itemId
            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
        break; 

      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    getContext().getContentResolver().notifyChange(uri, null);
    return count;
  }

  /**
   * Updates receipts or items depending on the given URI.
   * 
   * @return the number of records updated
   */
  @Override
  public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    int count;

    switch (uriMatcher.match(uri)) {
      case RECEIPTS:
        count = db.update(ReciboDatabaseHelper.RECEIPT_TABLE_NAME, values, where, whereArgs);
        break;

      case RECEIPTS_ID:
        String receiptId = uri.getPathSegments().get(1);
        count = db.update(ReciboDatabaseHelper.RECEIPT_TABLE_NAME, values, Receipt._ID + "=" + receiptId
            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
        break;

      case ITEMS:
        count = db.update(ReciboDatabaseHelper.ITEM_TABLE_NAME, values, where, whereArgs);
        break;

      case ITEMS_ID:
        String itemId = uri.getPathSegments().get(1);
        count = db.update(ReciboDatabaseHelper.RECEIPT_TABLE_NAME, values, Item._ID + "=" + itemId
            + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
        break; 

      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    getContext().getContentResolver().notifyChange(uri, null);
    return count;
  }

  //TODO Delete all items a receipt owns if the receipt is deleted?
  //TODO If last item for a receipt is deleted, delete receipt?
  /**
   * Permanently deletes invalidated entries in table identified by
   * the given URI (remember that <code>delete()</code> only invalidates
   * entries in the database, but keeps the entries around so that apps
   * can take advantage of the additional data).
   * 
   * @param uri table to clean
   * @return the number of entries that were permanently deleted
   */
  public int clean(Uri uri) {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    int count;

    switch (uriMatcher.match(uri)) {
      case RECEIPTS:
        count = db.delete(ReciboDatabaseHelper.RECEIPT_TABLE_NAME, Receipt.VALID + " = 0", null);
        break;

      case RECEIPTS_ID:
        String receiptId = uri.getPathSegments().get(1);
        count = db.delete(ReciboDatabaseHelper.RECEIPT_TABLE_NAME, Receipt._ID + "=" + receiptId
            + " AND (" + Receipt.VALID + " = 0)", null);
        break;

      case ITEMS:
        count = db.delete(ReciboDatabaseHelper.ITEM_TABLE_NAME, Item.VALID + " = 0", null);
        break;

      case ITEMS_ID:
        String itemId = uri.getPathSegments().get(1);
        count = db.delete(ReciboDatabaseHelper.RECEIPT_TABLE_NAME, Item._ID + "=" + itemId
            + " AND (" + Item.VALID + " = 0)", null);
        break; 

      default:
        throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    getContext().getContentResolver().notifyChange(uri, null);
    return count;
  }

}
