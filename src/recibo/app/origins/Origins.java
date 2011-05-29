package recibo.app.origins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONTokener;

import recibo.platform.R;
import recibo.platform.ReciboContentProvider;
import recibo.platform.model.Item;
import recibo.platform.model.Receipt;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class Origins extends MapActivity {

  //TODO Switch detail_display back to generic message when click doesn't hit a marker
  //TODO Implement filters
  //TODO Add permissions for tier
  //TODO Get pretty with UI
  
  private static final String TAG = "Origins";

  private MapView mapview;
  private OriginsItemizedOverlay<OverlayItem> itemizedOverlay;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.origins);

    mapview = (MapView) findViewById(R.id.mapview);
    mapview.setBuiltInZoomControls(true);

    //set up default marker for itemized overlay
    itemizedOverlay = new OriginsItemizedOverlay<OverlayItem>(getResources().getDrawable(R.drawable.fastfood),
        ((TextView) findViewById(R.id.detail_display)));

    //load filter controls and default filters

    //fetch food receipt data
    Cursor receipts = ReciboContentProvider.dummyQuery(1);
    ArrayList<Item> foodItems = new ArrayList<Item>();
    receipts.moveToFirst();
    while (!receipts.isAfterLast())
    {
      Item[] items = Receipt.itemsFromBytes(receipts.getBlob(receipts.getColumnIndex(Receipt.ITEMS)));
      for (Item i : items) 
      {
        Log.i(TAG, i.toString());
        if ((i.category == Item.CATEGORY_FOOD || i.category == Item.CATEGORY_DRINK) &&
            i.attributes.containsAttribute("origin")) foodItems.add(i);
      }
      receipts.move(1);
    }

    //plot origins
    plotOrigins(foodItems);
  }

  // For each item, fetches the GeoPoint for the item (converts the "origin" attribute) and adds 
  // an overlay for it to the set of overlays.  Then prepares the set of overlays for display and
  // adds it to the MapView.
  private void plotOrigins(List<Item> foodItems) {
    
    //fetch GeoPoints
    for (Item i : foodItems) {
      String origin = i.attributes.getAttribute("origin");
      OverlayItem o = new OverlayItem(getGeoPointForOrigin(origin), i.name, origin); //assumes the origin attribute exists
      itemizedOverlay.addOverlay(o);
    }
    
    //zoom map to reasonable area
    zoomToAllVisible();
    
    //add to mapview
    mapview.getOverlays().add(itemizedOverlay);
  }
  
  //zooms the map to a scale such that all overlay items are visible
  private void zoomToAllVisible() {
    if (itemizedOverlay.size() > 0) {
      int minLat = itemizedOverlay.getItem(0).getPoint().getLatitudeE6();
      int maxLat = minLat;
      int minLong = itemizedOverlay.getItem(0).getPoint().getLongitudeE6();
      int maxLong = minLong;
      
      for (OverlayItem o : itemizedOverlay)
      {
        int latitude = o.getPoint().getLatitudeE6();
        minLat = latitude < minLat ? latitude : minLat;
        maxLat = latitude > maxLat ? latitude : maxLat;
        
        int longitude = o.getPoint().getLongitudeE6();
        minLong = longitude < minLong ? longitude : minLong;
        maxLong = longitude > maxLong ? longitude : maxLong;
      }
      
      mapview.getController().zoomToSpan(maxLat - minLat, maxLong - minLong);
    }
  }

  // converts the origin attribute (an address) to a GeoPoint using the Google Geocoding API
  private GeoPoint getGeoPointForOrigin(String origin) {
    GeoPoint result = null;

    try 
    {
      URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" +
          escape(origin) + "&sensor=true");

      // collect json returned by the server into a single string
      BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
      StringBuilder jsonBldr = new StringBuilder();
      String str; Boolean first = true;
      while ((str = in.readLine()) != null) 
      {
        if (first)
        {
          first = false;
          jsonBldr.append(str);
        }
        else jsonBldr.append("\n".intern() + str);
      }
      in.close();
      //Log.i(TAG, "Fetched geocoding response: " + jsonBldr.toString());

      //parse json to retrieve location
      JSONObject response = (JSONObject) new JSONTokener(jsonBldr.toString()).nextValue();
      if (!response.getString("status").equals("OK")) throw new IOException("Bad status from server response.");
      JSONObject location = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
      result = new GeoPoint((int)(location.getDouble("lat") * 1E6), (int)(location.getDouble("lng") * 1E6));
      //Log.i(TAG, "Parsed GeoPoint: " + result.toString());

    } 
    catch (Exception e) 
    {
      //bail
      e.printStackTrace();
      System.exit(1);
    }

    return result;
  }

  //replaces space characters in origin with plus characters
  private String escape(String origin) {
    return origin.replaceAll("[ ]", "[+]");
  }

  @Override
  protected boolean isRouteDisplayed() {
    return false;
  }

}
