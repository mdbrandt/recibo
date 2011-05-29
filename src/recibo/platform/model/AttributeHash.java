package recibo.platform.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

/**
 * A hash of extra attributes defined by the vendor on a <code>Receipt</code> or an <code>Item</code>.
 * A wrapper around a HashMap<String, String>.
 *
 * @author Lere Williams
 * @modified May 17, 2011
 *
 */
public class AttributeHash implements Serializable {

  private static final long serialVersionUID = -6699845704453769548L;

  private HashMap<String, String> hash;
  
  public AttributeHash() {
    hash = new HashMap<String, String>();
  }
  
  public void addAttribute(String name, String value) {
    hash.put(name, value);
  }
  
  public String getAttribute(String name) {
    return hash.get(name);
  }
  
  public boolean containsAttribute(String name) {
    return hash.containsKey(name);
  }
  
  public int size() { 
    return hash.size();
  }
  
  public Set<String> attributeNameSet() {
    return hash.keySet();
  }
  
  public String toString() {
    return hash.toString();
  }
  
}
