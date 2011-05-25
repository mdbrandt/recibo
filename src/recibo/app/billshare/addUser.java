package recibo.app.billshare;
import android.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class addUser extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		//setContentView(R.layout.billshare_receipt);
		super.onCreate(savedInstanceState);
    	TextView tv = new TextView(this);
    	tv.setText("Enter name:");
    	EditText newName = new EditText(this);
    	setContentView(tv);
    	
	}
	
}