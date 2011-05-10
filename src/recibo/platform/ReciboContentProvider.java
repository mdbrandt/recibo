package recibo.platform;

import java.util.Date;

import recibo.platform.model.AttributeHash;
import recibo.platform.model.Item;
import recibo.platform.model.Receipt;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

/**
 * Exposes the underlying receipt database for the recibo platform.
 * All interactions follow the normal content provider model, with 
 * the exception of the static <code>dummyQuery()</code> method
 * which is used to perform a dummy query (returns a
 * <code>Cursor</code> that points to a dummy receipt database).
 *
 * @author Lere Williams
 * @modified May 9, 2011
 *
 */
public class ReciboContentProvider extends ContentProvider {

  public static final Uri CONTENT_URI = Uri.parse("content://recibo.platform.ReciboContentProvider");
  
  /**
   * A <code>Cursor</code> that represents a dummy receipt database.
   * 
   * Note the special methods <code>getId()<code>, <code>getDate()<code>,
   * etc. that replace the usual get methods so that columns can be
   * accessed for the data types defined by <code>Receipt</code>. Also
   * note convenience constructor in <code>Receipt</code>.
   */
  public static class DummyCursor implements Cursor {
    
    private Receipt[] rcpts;
    private int pos;
    
    public DummyCursor() {
      Item[] items0 = {new Item(0, 0, "crab cakes", 11.00, 0, 1, Item.Category.FOOD, new AttributeHash()),
          new Item(1, 0, "ceviche", 11.50, 0, 1, Item.Category.FOOD, new AttributeHash()),
          new Item(2, 0, "ropa vieja", 18.00, 0, 1, Item.Category.FOOD, new AttributeHash()),
          new Item(3, 0, "paella de la bodeguita", 48.00, 0, 1, Item.Category.FOOD, new AttributeHash()),
          new Item(4, 0, "negro modelo", 3.50, 0, 2, Item.Category.DRINK, new AttributeHash())};
      Item[] items1 = {new Item(0, 1, "tomatoes", 4.55, 0, 1, Item.Category.FOOD, new AttributeHash()),
          new Item(1, 1, "pasta", 2.99, 0, 1, Item.Category.FOOD, new AttributeHash()),
          new Item(2, 1, "ice cream", 6.99, 0, 1, Item.Category.FOOD, new AttributeHash())};
      Item[] items2 = {new Item(0, 2, "suit cleaning", 20.00, 0, 1, Item.Category.DRY_CLEANING, new AttributeHash())};
      
      rcpts = new Receipt[3];
      rcpts[0] = new Receipt(0, new Date(), Receipt.Type.Proof, new Date(), "La Bodeguita Del Medio", Receipt.Category.Dining, 18.00, items0, new AttributeHash());
      rcpts[1] = new Receipt(1, new Date(), Receipt.Type.Proof, new Date(), "Trader Joe's", Receipt.Category.Grocery, 9.25, items1, new AttributeHash());
      rcpts[2] = new Receipt(2, new Date(), Receipt.Type.Token, new Date(), "Palo Alto Cleaners", Receipt.Category.Service, 9.25, items2, new AttributeHash());
      
      pos = 0;
    }
    
    public long getId() { return rcpts[pos]._id; }
    public Date getDate() { return rcpts[pos].date; }
    public Receipt.Type getType() { return rcpts[pos].type; }
    public Date getExpiration() { return rcpts[pos].expiration; }
    public String getStore() { return rcpts[pos].store; }
    public Receipt.Category getCategory() { return rcpts[pos].category; }
    public double getTaxRate() { return rcpts[pos].tax_rate; }
    public Item[] getItems() { return rcpts[pos].items; }
    public AttributeHash getAttributes() { return rcpts[pos].attributes; }
    
    @Override
    public void close() {
      rcpts = null;
      pos = -1;
    }

    @Override
    public void copyStringToBuffer(int arg0, CharArrayBuffer arg1) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void deactivate() {
      // TODO Auto-generated method stub
      
    }
    
    @Override
    public byte[] getBlob(int columnIndex) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public int getColumnCount() {
      return 8;
    }

    @Override
    public int getColumnIndex(String arg0) {
      if (arg0.equals(Receipt._ID)) return 0;
      else if (arg0.equals(Receipt.DATE)) return 1;
      else if (arg0.equals(Receipt.TYPE)) return 2;
      else if (arg0.equals(Receipt.EXPIRATION)) return 3;
      else if (arg0.equals(Receipt.STORE)) return 4;
      else if (arg0.equals(Receipt.CATEGORY)) return 5;
      else if (arg0.equals(Receipt.ITEMS)) return 6;
      else if (arg0.equals(Receipt.ATTRIBUTES)) return 7;
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
        case 4: return Receipt.STORE;
        case 5: return Receipt.CATEGORY;
        case 6: return Receipt.ITEMS;
        case 7: return Receipt.ATTRIBUTES;
        default: return null;
      }
    }

    @Override
    public String[] getColumnNames() {
      String[] names = {Receipt._ID, Receipt.DATE, Receipt.TYPE, Receipt.EXPIRATION,
          Receipt.STORE, Receipt.CATEGORY, Receipt.ITEMS, Receipt.ATTRIBUTES};
      return names;
    }

    @Override
    public int getCount() {
      return rcpts.length;
    }

    @Override
    public double getDouble(int columnIndex) {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public Bundle getExtras() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public float getFloat(int columnIndex) {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public int getInt(int columnIndex) {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public long getLong(int columnIndex) {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public int getPosition() {
      return pos;
    }

    @Override
    public short getShort(int columnIndex) {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public String getString(int columnIndex) {
      switch (columnIndex) {
        case 0: return "" + rcpts[pos]._id;
        case 1: return rcpts[pos].date.toString();
        case 2: return rcpts[pos].type.toString();
        case 3: return rcpts[pos].expiration.toString();
        case 4: return rcpts[pos].store.toString();
        case 5: return rcpts[pos].category.toString();
        case 6: return rcpts[pos].items.toString();
        case 7: return rcpts[pos].attributes.toString();
        default: return null;
      }
    }

    @Override
    public boolean getWantsAllOnMoveCalls() {
      // TODO Auto-generated method stub
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
        case 0: return rcpts[pos]._id == -1;
        case 1: return rcpts[pos].date == null;
        case 2: return rcpts[pos].type == null;
        case 3: return rcpts[pos].expiration == null;
        case 4: return rcpts[pos].store == null;
        case 5: return rcpts[pos].category == null;
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
    public void registerContentObserver(ContentObserver observer) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
      // TODO Auto-generated method stub
      
    }

    @Override
    @Deprecated
    public boolean requery() {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public Bundle respond(Bundle extras) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public void setNotificationUri(ContentResolver cr, Uri uri) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void unregisterContentObserver(ContentObserver observer) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
      // TODO Auto-generated method stub
      
    }
    
  }
  
  /**
   * 
   * @return <code>Cursor</code> pointing to a fake receipt database
   */
  public static Cursor dummyQuery() {
    return new DummyCursor();
  }
  
  @Override
  public int delete(Uri arg0, String arg1, String[] arg2) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public String getType(Uri arg0) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Uri insert(Uri arg0, ContentValues arg1) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean onCreate() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,
      String arg4) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
    // TODO Auto-generated method stub
    return 0;
  }

}
