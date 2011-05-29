package recibo.app.origins;

import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class OriginsItemizedOverlay<Item extends OverlayItem> extends ItemizedOverlay<Item> implements Iterable<Item> {

  private ArrayList<OverlayItem> overlays;
  private TextView detail;

  public OriginsItemizedOverlay(Drawable defaultMarker, TextView detailDisplay) {
    super(boundCenterBottom(defaultMarker));
    overlays = new ArrayList<OverlayItem>();
    detail = detailDisplay;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Item createItem(int i) {
    return (Item) overlays.get(i);
  }

  @Override
  public int size() {
    return overlays.size();
  }

  public void addOverlay(OverlayItem o) {
    overlays.add(o);
    populate();
  }

  public void clear() {
    overlays.clear();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Iterator<Item> iterator() {
    return (Iterator<Item>) overlays.iterator();
  }

  @Override
  protected boolean onTap(int index) {
    OverlayItem o = overlays.get(index);
    detail.setText("Item: " + o.getTitle() + ", Origin: " + o.getSnippet());
    return true;
  }

}
