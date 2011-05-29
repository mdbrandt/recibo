package recibo.platform.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.database.Cursor;

/**
 * A receipt received via a point-of-sale interaction.  This can
 * be one of two types: either Receipt.Proof for a receipt that
 * can be presented back to the vendor multiple times, or a
 * Receipt.Token for a receipt that can only be presented back
 * to the vendor once.
 *
 * @author Lere Williams
 * @modified May 17, 2011
 *
 */
public class Receipt {

  //TODO Override toString()
  
  public static final int TYPE_PROOF = 0;
  public static final int TYPE_TOKEN = 1;
  
  public static final int CATEGORY_GROCERY = 0;
  public static final int CATEGORY_FASHION = 1;
  public static final int CATEGORY_ELECTRONICS = 2;
  public static final int CATEGORY_DINING = 3;
  public static final int CATEGORY_EVENT = 4;
  public static final int CATEGORY_SERVICE = 5;
  public static final int CATEGORY_OTHER = 6;
  
  //database column headers
  
  /**
   * Unique identifier for this receipt.
   * <p>Type: integer</p>
   */
  public static final String _ID = "_ID";
  
  /**
   * Date of purchase.
   * <p>Type: long</p>
   */
  public static final String DATE = "DATE";
  
  /**
   * Type of receipt (from among predefined types in this class).
   * <p>Type: integer</p>
   */
  public static final String TYPE = "TYPE"; //receipt type
  
  /**
   * Date at which the receipt can no longer be presented back to the vendor (for redemption or refund).
   * <p>Type: long</p>
   */
  public static final String EXPIRATION = "EXPIRATION";
  
  /**
   * Name of vendor.
   * <p>Type: String</p>
   */
  public static final String VENDOR = "VENDOR";
  
  /**
   * Type of vendor (from among predefined categories in this class).
   * <p>Type: integer</p>
   */
  public static final String CATEGORY = "CATEGORY"; //store category
  
  /**
   * Tax rate on all items under this receipt (except those with their own special tax rates).
   * <p>Type: double</p>
   */
  public static final String TAX_RATE = "TAX_RATE";
  
  /**
   * Array of all the items purchased under this receipt.
   * To recover Item[] use <code>itemsFromBytes()</code>.
   * <p>Type: byte[]</p>
   */
  public static final String ITEMS = "ITEMS";
  
  /**
   * Extra characteristics of this item.
   * To recover AttributeHash use <code>attributesFromBytes()</code>.
   * <p>Type: byte[]</p>
   */
  public static final String ATTRIBUTES = "ATTRIBUTES";
  
  /**
   * Whether or not the receipt is still valid.  Read-only.
   * <p>Type: integer</p>
   */
  public static final String VALID = "VALID";
  
  public final int _id;
  public final long date;
  public final int type;
  public final long expiration;
  public final String vendor;
  public final int category;
  public final double tax_rate;
  public final Item[] items;
  public final AttributeHash attributes;
  private int valid;
  
  /**
   * Convenience constructor.
   * 
   * @param rc the return value of <code>query()</code> in <code>ReciboContentProvide</code>
   *        or <code>ReciboContentProvider.dummyQuery()</code>
   */
  public Receipt(Cursor c) {
    this._id = c.getInt(0);
    this.date = c.getLong(1);
    this.type = c.getInt(2);
    this.expiration = c.getLong(3);
    this.vendor = c.getString(4);
    this.category = c.getInt(5);
    this.tax_rate = c.getDouble(6);
    this.items = itemsFromBytes(c.getBlob(7));
    this.attributes = attributesFromBytes(c.getBlob(8));
    this.valid = c.getInt(9);
  }
  
  //TODO Is there a case where it is necessary to instantiate an invalid receipt?
  public Receipt(int _id, long date,
      int type, long expiration, 
      String vendor, int category,
      double tax_rate, Item[] items, AttributeHash attributes) {
    this._id = _id;
    this.date = date;
    this.type = type;
    this.expiration = expiration;
    this.vendor = vendor;
    this.category = category;
    this.tax_rate = tax_rate;
    this.items = items;
    this.attributes = attributes;
    this.valid = 1;
  }
  
  /**
   * @return whether or not the current receipt is valid
   */
  public int getValidity() { return valid; };
  
  //TODO Find a better exception to throw from pickling methods
  /**
   * Converts the items instance variable into a byte array, ready for
   * packaging into a database entry that can be fed to the <code>insert()</code>
   * in <code>ReciboContentProvider</code>.
   * 
   * @return a byte array representing the items instance variable
   */
  public byte[] itemsToBytes() {
    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    try {
      ObjectOutputStream oos = new ObjectOutputStream(bs);
      oos.writeObject(items);
    }
    catch (IOException e){
      throw new RuntimeException("Failed to serialize items instance variable.");
    }
    return bs.toByteArray();
  }
  
  /**
   * Recovers an array of items from the byte array received back from
   * calling <code>getBlob()</code> on the appropriate column of the database
   * entry returned (via <code>Cursor</code>) from a query on <code>ReciboContentProvider</code>.
   * 
   * @param bytes
   * @return an array of type <code>Item</code>
   */
  public static Item[] itemsFromBytes(byte[] bytes) {
    Item[] recoveredItems;
    ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
    try {
      ObjectInputStream ois = new ObjectInputStream(bs);
      recoveredItems = (Item[]) ois.readObject();
    }
    catch (IOException e) {
      throw new RuntimeException("Failed to recover items array from bytes.");
    }
    catch (ClassNotFoundException e) {
      throw new RuntimeException("Failed to recover items array from bytes.");
    }
    return recoveredItems;
  }
  
  /**
   * Converts the attributes instance variable into a byte array, ready for
   * packaging into a database entry that can be fed to the <code>insert()</code>
   * in <code>ReciboContentProvider</code>.
   * 
   * @return a byte array representing the attributes instance variable
   */
  public byte[] attributesToBytes() {
    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    try {
      ObjectOutputStream oos = new ObjectOutputStream(bs);
      oos.writeObject(attributes);
    }
    catch (IOException e){
      throw new RuntimeException("Failed to serialize attributes instance variable.");
    }
    return bs.toByteArray();
  }
  
  /**
   * Recovers an <code>AttributeHash<code> from the byte array received back from
   * calling <code>getBlob()</code> on the appropriate column of the database
   * entry returned (via <code>Cursor</code>) from a query on <code>ReciboContentProvider</code>.
   * 
   * @param bytes
   * @return an <code>AttributeHash</code>
   */
  public static AttributeHash attributesFromBytes(byte[] bytes) {
    AttributeHash recoveredAttributes;
    ByteArrayInputStream bs = new ByteArrayInputStream(bytes);
    try {
      ObjectInputStream ois = new ObjectInputStream(bs);
      recoveredAttributes = (AttributeHash) ois.readObject();
    }
    catch (IOException e) {
      throw new RuntimeException("Failed to recover items array from bytes.");
    }
    catch (ClassNotFoundException e) {
      throw new RuntimeException("Failed to recover items array from bytes.");
    }
    return recoveredAttributes;
  }
  

}
