package recibo.platform.model;

import java.io.Serializable;

/**
 * An item on an itemized receipt.
 *
 * @author Lere Williams
 * @modified May 17, 2011
 *
 */
public class Item implements Serializable {

  private static final long serialVersionUID = -2764051288391158536L;

  public static final int CATEGORY_FOOD = 0;
  public static final int CATEGORY_DRINK = 1;
  public static final int CATEGORY_CLOTHES = 2;
  public static final int CATEGORY_SHOES = 3;
  public static final int CATEGORY_DRYCLEANING = 4;
  public static final int CATEGORY_VALETPARKING = 5;
  public static final int CATEGORY_OTHER = 6;

  //database column headers

  /**
   * Unique identifier for this item.
   * <p>Type: integer</p>
   */
  public static final String _ID = "_ID";

  /**
   * Unique identifier of parent receipt for this item.
   * <p>Type: integer</p>
   */
  public static final String RECEIPT_ID = "RECEIPT_ID";

  /**
   * Date at which the item can no longer be returned or redeemed, if different from global
   * expiration of receipt.
   * <p>Type: long</p>
   */
  public static final String EXPIRATION = "EXPIRATION";
  
  /**
   * Name of the item or service purchased.
   * <p>Type: String</p>
   */
  public static final String NAME = "NAME";

  /**
   * Price of the item.
   * <p>Type: double</p>
   */
  public static final String PRICE = "PRICE";

  /**
   * Tax rate of this specific item, if different from global tax rate on receipt.
   * <p>Type: double</p>
   */
  public static final String TAX_RATE = "TAX_RATE";

  /**
   * Number of units of this item that were purchased.
   * <p>Type: integer</p>
   */
  public static final String UNITS = "UNITS";

  /**
   * Type of item (from among predefined categories in the class).
   * <p>Type: Item.Category</p>
   */
  public static final String CATEGORY = "CATEGORY";

  /**
   * Extra characteristics of this item.
   * To recover call <code>getAttributeHash()</code> directly from <code>ReciboContentProvider</code>
   * class or call <code>attributesFromBytes()</code> in <code>AttributeHash</code> class after a call
   * to <code>getBlob()</code> in <code>ReciboContentProvider</code>.
   * <p>Type: byte[]</p>
   */
  public static final String ATTRIBUTES = "ATTRIBUTES";
  
  /**
   * Whether or not the receipt is still valid.  Read-only.
   * <p>Type: integer</p>
   */
  public static final String VALID = "VALID";

  public final int _id;
  public final long receipt_id;
  public final long expiration;
  public final String name;
  public final double price;
  public final double tax_rate;
  public final int units;
  public final int category;
  public final AttributeHash attributes;
  private int valid;

  /*public Item() {
    _id = 0;
    receipt_id = 0;
    name = "";
    price = 0.0;
    tax_rate = 0.0;
    units = 0;
    category = Category.Other;
    attributes = null;
  }*/

  //TODO Is there a case where it is necessary to instantiate an invalid item?
  public Item(int _id, long receipt_id, 
      long expiration, String name,
      double price, double tax_rate,
      int units, int category,
      AttributeHash attributes) {
    this._id = _id;
    this.receipt_id = receipt_id;
    this.expiration = expiration;
    this.name = name;
    this.price = price;
    this.tax_rate = tax_rate;
    this.units = units;
    this.category = category;
    this.attributes = attributes;
    this.valid = 1;
  }
  
  public int getValidity() { return valid; };

}
