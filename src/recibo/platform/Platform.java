package recibo.platform;

import android.app.Activity;
import android.os.Bundle;

/**
 * Start-up activity for the recibo platform: launches a service
 * that handles point-of-sale interactions (which populate the 
 * receipt database).
 * 
 * The receipt database is exposed using the general Android content
 * provider mechanism.  See <code>ReciboContentProvider</code> for
 * more details.
 *
 * @author Lere Williams
 * @modified May 9, 2011
 *
 */
public class Platform extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}