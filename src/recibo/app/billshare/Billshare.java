package recibo.app.billshare;

import java.util.ArrayList;
import java.util.HashMap;

import recibo.platform.R;
import recibo.platform.ReciboContentProvider;
import recibo.platform.model.Item;
import recibo.platform.model.Receipt;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class Billshare extends Activity implements OnClickListener {
	
	private static final String TAG = "debug";
	private static final int ADD_USER_DIALOG = 1;
	private static final int MUST_ADD_USER_DIALOG = 2;
	private static final int ALREADY_TAKEN_DIALOG = 3;
	
	private ArrayList <User> users = new ArrayList<User>();
	private User activeUser = null;
	private Receipt activeReceipt = null;
	HashMap<Integer, String> accountedItems = new HashMap<Integer, String>();
	
	//TODO Allow items to be split across multiple users
	
	//this class is for a user who has been added to a receipt.
	//
	//variables:
	//Item[] items			for the items user is responsible for paying for
	//string name			the user's name and identifier
	private static class User{
		
		ArrayList <Item> items = new ArrayList<Item>();
		public final String name;
		public final int color;
		
		private static final ArrayList<Integer> colors = new ArrayList<Integer>();
		
		static {
			colors.add(Color.BLUE);
			colors.add(Color.CYAN);
			colors.add(Color.GREEN);
			colors.add(Color.RED);
			colors.add(Color.YELLOW);
		}
		 
		private Integer getNewColor() {
			return colors.remove(0);
		}
	
		public User(String name){
			this.name=name;
			this.color=getNewColor();
		}
		
		//add an item to a given user.
		public void addItem(Item i){
			this.items.add(i);			
		}
		
		public String getName(){
			return this.name;
		}
		
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.billshare_receiptview);

        TableLayout table = (TableLayout) findViewById(R.id.tableLayout1);
        Cursor rcp = ReciboContentProvider.dummyQuery(); 
        rcp.moveToPosition(0);
        activeReceipt = new Receipt(rcp);
               
        //build table view using receipt items
        for (int i = 0; i < activeReceipt.items.length; i++){	
        	TextView name = new TextView(this);
        	name.setText(activeReceipt.items[i].name);
        	TextView price = new TextView(this);
        	price.setText(Double.toString(activeReceipt.items[i].price));
        	TableRow tr = new TableRow(this);
        	tr.setId(activeReceipt.items[i]._id);
        	tr.addView(name);
        	tr.addView(price);
        	tr.setOnClickListener(this);
        	table.addView(tr);	
        }
       
        Button b = new Button(this);
        b.setText("Add Person");
        b.setOnClickListener(this);
        
        table.addView(b);
    }
    
    public void onClick (View v){
    	if (v.getClass().getName() == android.widget.Button.class.getName())
    	{
    		showDialog(ADD_USER_DIALOG); //set the active user	
    	}
    	else if (v.getClass().getName() == android.widget.TableRow.class.getName())
    	{
    		if (activeUser == null) showDialog(MUST_ADD_USER_DIALOG); //insist on at least one user
    		else
    		{	
          		Item i = getItemByID(((TableRow)v).getId());
          		
          		if (i != null)
          		{
          		//add to accounted for items map.
          			if (!accountedItems.containsKey(i._id))
          			{
          				//add item
          				accountedItems.put(i._id, activeUser.getName());
          				((TableRow)v).setBackgroundColor(activeUser.color);
          				
          				//check if everything is taken
          				if (accountedItems.keySet().size() == activeReceipt.items.length)
          				{
          					//TODO: compute total
          					//switch view and change totals by person
          					//
          			        //gridlayout.addview(tableLayout1)
          			        //gridLayout.removeView(tableLayout1)
          			        //replace new view

          				}
          			}
          			else
          			{
          				showDialog(ALREADY_TAKEN_DIALOG);
          			}
          		}      		
    		}            		
    	}
    	else
    	{
    		throw new RuntimeException("Unrecognized click.");
    	}
    	
    }
    
    public Dialog onCreateDialog(int id, Bundle args) {
    	switch (id) {
    	case ADD_USER_DIALOG:
    		return createAddUserDialog();
    	case MUST_ADD_USER_DIALOG:
    		return createMustAddDialog();
    	case ALREADY_TAKEN_DIALOG:
    		return createItemAlreadyAccountedDialog();
    		default:
    			throw new RuntimeException("Dialog creation failed");
    	}
    }
    
    private Dialog createItemAlreadyAccountedDialog(){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	alert.setTitle("Error");
    	alert.setMessage("That item is already accounted for.");
    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();	
			}
		});
    	return alert.create();
    }
    
    private Dialog createMustAddDialog() {
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	alert.setTitle("Error");
    	alert.setMessage("Must add user before selecting items.");
    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();	
			}
		});
    	return alert.create();
    }
    
  //create add user dialog
    private Dialog createAddUserDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
    	alert.setTitle("Add person");
    	alert.setMessage("Enter name:");
	    final EditText input = new EditText(this);		
		alert.setView(input);
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			  String value = input.getText().toString();
			  User u = new User(value);
			  users.add(u);
			  activeUser = u;
			  
			  TextView tv = (TextView)findViewById(R.id.textView3);
			  tv.setText(activeUser.getName());
			  }
			});
	
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Cancelled.
			  }
			});
		
		return alert.create();
    }
    
    private Item getItemByID(int itemID){
        for (int i = 0; i < activeReceipt.items.length; i++){
        	if (itemID == activeReceipt.items[i]._id)
        		return activeReceipt.items[i];
        }
        return null;   	
    }    
}



