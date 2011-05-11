package recibo.platform.model;

/**
 * An item on an itemized receipt.
 *
 * @author Lere Williams
 * @modified May 9, 2011
 *
 */
public class Item {

  public static enum Category { FOOD, DRINK, CLOTHES, SHOES, DRY_CLEANING, VALET_PARKING, OTHER }

  //column headers
  public static final String _ID = "_ID";
  public static final String RECEIPT_ID = "RECEIPT_ID";
  public static final String NAME = "NAME";
  public static final String PRICE = "PRICE";
  public static final String TAX = "TAX";
  public static final String QUANTITY = "QUANTITY";
  public static final String CATEGORY = "CATEGORY"; //item category
  public static final String ATTRIBUTES = "ATTRIBUTES";
  
  public final long _id;
  public final long receipt_id;
  public final String name;
  public final double price;
  public final double tax;
  public final int quantity;
  public final Category category;
  public final AttributeHash attributes;
  
  public Item(long _id, long receipt_id, 
      String name, double price,
      double tax, int quantity,
      Category category, AttributeHash attributes) {
    this._id = _id;
    this.receipt_id = receipt_id;
    this.name = name;
    this.price = price;
    this.tax = tax;
    this.quantity = quantity;
    this.category = category;
    this.attributes = attributes;
  }

}
