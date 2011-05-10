package recibo.platform.model;

import java.util.Date;

import recibo.platform.ReciboContentProvider;

/**
 * A receipt received via a point-of-sale interaction.  This can
 * be one of two types: either Receipt.Proof for a receipt that
 * can be presented back to the vendor multiple times, or a
 * Receipt.Token for a receipt that can only be presented back
 * to the vendor once.
 *
 * @author Lere Williams
 * @modified May 9, 2011
 *
 */
public class Receipt {

  public static enum Type { Proof, Token }
  public static enum Category { Grocery, Fashion, Electronics, Dining, Event, Service, Other }
  
  //column headers
  public static final String _ID = "_ID";
  public static final String DATE = "DATE";
  public static final String TYPE = "TYPE"; //receipt type
  public static final String EXPIRATION = "EXPIRATION";
  public static final String STORE = "STORE";
  public static final String CATEGORY = "CATEGORY"; //store category
  public static final String TAX_RATE = "TAX_RATE";
  public static final String ITEMS = "ITEMS";
  public static final String ATTRIBUTES = "ATTRIBUTES";
  
  public final long _id;
  public final Date date;
  public final Type type;
  public final Date expiration;
  public final String store;
  public final Category category;
  public final double tax_rate;
  public final Item[] items;
  public final AttributeHash attributes;
  
  /**
   * Convenience constructor for rebuilding receipt from Cursor returned
   * by dummy query.
   * 
   * @param rc the return value of ReciboContentProvider.dummyQuery()
   */
  public Receipt(ReciboContentProvider.DummyCursor rc) {
    this._id = rc.getId();
    this.date = rc.getDate();
    this.type = rc.getType();
    this.expiration = rc.getExpiration();
    this.store = rc.getStore();
    this.category = rc.getCategory();
    this.tax_rate = rc.getTaxRate();
    this.items = rc.getItems();
    this.attributes = rc.getAttributes();
  }
  
  public Receipt(long _id, Date date,
      Type type, Date expiration, 
      String store, Category category,
      double tax_rate, Item[] items, AttributeHash attributes) {
    this._id = _id;
    this.date = date;
    this.type = type;
    this.expiration = expiration;
    this.store = store;
    this.category = category;
    this.tax_rate = tax_rate;
    this.items = items;
    this.attributes = attributes;
  }
  

}
