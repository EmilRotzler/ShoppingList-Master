package org.projects.shoppinglist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;


import java.util.ArrayList;

import static android.R.id.input;
import static org.projects.shoppinglist.MainActivity.context;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "com.example.StateChange";

    private Product p;
    static Context context;

    static deleteDialog deletedialog;
    static clearDialog cleardialog;

    static FirebaseListAdapter<Product> adapter;
    static ArrayList<Product> backupArray = new ArrayList<>();
    static ListView listView;
    private TextView Welcome;
    String databasename = "list1";

    //static ArrayList<Product> bag = new ArrayList<Product>();
    static public FirebaseListAdapter<Product> getMyAdapter()
    {
        return adapter;
    }
    DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child(databasename);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = new Intent(MainActivity.this, Product.class);
        setContentView(R.layout.activity_main);
        final View parent = findViewById(R.id.mainLayout);
        Log.d(TAG, "onCreate");
        this.context = this;
        Welcome = (TextView)findViewById(R.id.Welcome);
        FirebaseInstanceId.getInstance().getToken();

        databasename = SettingsFragment.getList(this);
        Welcome.setText("Welcome to list: " + databasename);


        final DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child(databasename);

        final String name = SettingsFragment.getName(this);
        String message = "Welcome back "+name ;
        Toast toast = Toast.makeText(this,message,Toast.LENGTH_LONG);
        toast.show();

        findViewById(R.id.mainLayout).requestFocus();

//        if(savedInstanceState != null){
//            bag = savedInstanceState.getParcelableArrayList("savedList");
//        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //getting our listview - you can check the ID in the xml to see that it
        //is indeed specified as "list"
        listView = (ListView) findViewById(R.id.list);
        // adapter =  new ArrayAdapter<Product>(this,android.R.layout.simple_list_item_checked,bag );
        adapter = new FirebaseListAdapter<Product>(this,Product.class, android.R.layout.simple_list_item_checked,firebase){
            @Override
            protected void populateView(View view, Product product, int i) {
                ((TextView)view.findViewById(android.R.id.text1)).setText(product.toString());
        }};

        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        Button addButton = (Button) findViewById(R.id.addButton);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText addEditText = (EditText) findViewById(R.id.editText);
                final String testAddEditText = addEditText.getText().toString();
                int testAddEditQuantity = 0;
                final EditText addEditQuantity = (EditText) findViewById(R.id.editQuantity);
                if(!TextUtils.isEmpty(addEditQuantity.getText().toString())) {
                    testAddEditQuantity = Integer.parseInt(addEditQuantity.getText().toString());
                }
                if(!TextUtils.isEmpty(testAddEditText) && testAddEditQuantity>0) {
                    //bag.add(new Product(testAddEditText, testAddEditQuantity));
                    Product p = new Product(testAddEditText, testAddEditQuantity);
                    firebase.push().setValue(p);
                    //The next line is needed in order to say to the ListView
                    //that the data has changed - we have added stuff now!
                    getMyAdapter().notifyDataSetChanged();
                }
                else{
                    Toast toastelse2 = Toast.makeText(getApplicationContext(), "Make sure to add an item and quantity", Toast.LENGTH_SHORT);
                    toastelse2.setGravity(Gravity.CENTER| Gravity.CENTER, 0, 0);
                    toastelse2.show();
                }
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //backupArray = adapter;
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(parent.getWindowToken(), 0);
                SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
                int itemCount = adapter.getCount();
                for(int i=0; i <itemCount; i++){
                    if(checkedItemPositions.get(i)){
                        backupArray.add(adapter.getItem(i));
                        getMyAdapter().getRef(i).setValue(null);
                    }
                }
                listView.clearChoices();
                getMyAdapter().notifyDataSetChanged();
                Snackbar snackbar = Snackbar
                        .make(parent, "Item(s) deleted.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //This code will ONLY be executed in case that
                                //the user has hit the UNDO button
                                int itemCount = backupArray.size();
                                for(int i=0; i <itemCount; i++) {
                                    firebase.push().setValue(backupArray.get(i));
                                }
                                backupArray.clear();
                                Snackbar snackbar = Snackbar.make(parent, "Old item(s) restored.", Snackbar.LENGTH_LONG);
                                //on this snackbar there is NO UNDO - so no SetAction method is called
                                listView.clearChoices();
                                getMyAdapter().notifyDataSetChanged();
                                snackbar.show();
                            }
                        });

                snackbar.show();
            }
        });



        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getMyAdapter().notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        adapter.cleanup();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //ALWAYS CALL THE SUPER METHOD - To be nice!
        super.onSaveInstanceState(outState);

        //Log.d(TAG, "onSaveInstanceState");
		/* Here we put code now to save the state */
        //outState.putString("savedName", name);
        //outState.putParcelableArrayList("savedList", bag);
    }


    /*
    protected void onRestoreInstanceState(Bundle savedState) {
        //MOST UI elements will automatically store the information
        //if we call the super.onRestoreInstaceState
        //but other data will be lost.
        super.onRestoreInstanceState(savedState);
        Log.d(TAG, "onRestoreInstanceState");


        //TextView savedName = (TextView) findViewById(R.id.name);
        ListView savedList  = (ListView) findViewById(R.id.list);

        //in the line below, notice key value matches the key from onSaved
        //this is of course EXTREMELY IMPORTANT

        //this.name = savedState.getString("savedName");
        this.bag = savedState.getStringArrayList("savedList");

        //since this method is called AFTER onCreate
        //we need to set the text field
        //try to comment the line below out and
        //see the effect after orientation change (after saving some name)

        //savedName.setText("Saved Name:"+name);
        adapter =  new ArrayAdapter<String>(this,android.R.layout.simple_list_item_checked, this.bag );
        savedList.setAdapter(adapter);


    }

   */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1) //the code means we came back from settings
        {
            //I can can these methods like this, because they are static
            String name = SettingsFragment.getName(this);
            databasename = SettingsFragment.getList(this);
            firebase = FirebaseDatabase.getInstance().getReference().child(databasename);
            listView = (ListView) findViewById(R.id.list);
            // adapter =  new ArrayAdapter<Product>(this,android.R.layout.simple_list_item_checked,bag );
            adapter = new FirebaseListAdapter<Product>(this,Product.class, android.R.layout.simple_list_item_checked,firebase){
                @Override
                protected void populateView(View view, Product product, int i) {
                    ((TextView)view.findViewById(android.R.id.text1)).setText(product.toString());
                }};

            listView.setAdapter(adapter);
            Welcome.setText("Welcome to list: "+databasename);

            String message = "Welcome back "+name+" "+databasename;
            Toast toast = Toast.makeText(this,message,Toast.LENGTH_LONG);
            toast.show();
            getMyAdapter().notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public Product getItem(int index)
    {
        return (Product) getMyAdapter().getItem(index);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Toast.makeText(this, "Application icon clicked!",
                        Toast.LENGTH_SHORT).show();
                return true; //return true, means we have handled the event
            case R.id.action_settings:
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivityForResult(intent,1);
                return true; //return true, means we have handled the event
            case R.id.item_clear:
                showClearDialog();

                //The next line is needed in order to say to the ListView
                //that the data has changed - we have added stuff now!
                getMyAdapter().notifyDataSetChanged();
                return true;
            case R.id.item_share:
                //showShareDialog();
                Intent sendIntent = new Intent();
                int itemCount = listView.getCount();
                String sharetext = "";
                String temptext = "";

                for(int i=itemCount-1; i >= 0; i--){
                    temptext = getMyAdapter().getItem(i)+", "+sharetext;
                    sharetext = temptext;
                }

                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, sharetext);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Intent"));

                //The next line is needed in order to say to the ListView
                //that the data has changed - we have added stuff now!
                return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showClearDialog() {
        //showing our dialog.
        cleardialog = new MyClearDialog();
        //Here we show the dialog
        //The tag "MyFragement" is not important for us.
        cleardialog.show(getFragmentManager(), "MyFragment");
    }

    public void showDeleteDialog(View v) {
        //showing our dialog.
        deletedialog = new MyDeleteDialog();
        //Here we show the dialog
        //The tag "MyFragement" is not important for us.
        deletedialog.show(getFragmentManager(), "MyFragment");
    }
    public static class MyClearDialog extends clearDialog{

        @Override
        public void positiveClick() {
            Toast toast = Toast.makeText(context,
                    "Bag was cleared", Toast.LENGTH_LONG);
            toast.show();
            int itemCount = listView.getCount();
            for(int i=itemCount-1; i >= 0; i--){
                    getMyAdapter().getRef(i).setValue(null);
            };

            getMyAdapter().notifyDataSetChanged();
            listView.clearChoices();
        }


        @Override
        protected void negativeClick() {
            //Here we override the method and can now do something
            Toast toast = Toast.makeText(context,
                    "Bag not cleared", Toast.LENGTH_SHORT);
            toast.show();
        }
    }



    public static class MyDeleteDialog extends deleteDialog {

        @Override
        public void positiveClick() {
            //Do your update stuff here to the listview
            //and the bag etc
            //just to show how to get arguments from the bag.
            Toast toast = Toast.makeText(context,
                    "Item(s) deleted", Toast.LENGTH_LONG);
            toast.show();

            SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
            int itemCount = listView.getCount();

            for(int i=itemCount-1; i >= 0; i--){
                if(checkedItemPositions.get(i)){
                    getMyAdapter().getRef(i).setValue(null);
                    // bag.remove(bag.get(i));
                }
            }

            //bag.remove(remove);

            //The next line is needed in order to say to the ListView
            //that the data has changed - we have added stuff now!
            getMyAdapter().notifyDataSetChanged();
            listView.clearChoices();
            //}
            //else{
            //  Toast toastelse = Toast.makeText(getApplicationContext(), "Nothing to delete", Toast.LENGTH_SHORT);
            // toastelse.setGravity(Gravity.CENTER| Gravity.CENTER, 0, 0);
            // toastelse.show();
            //}
        }


        @Override
    protected void negativeClick() {
        //Here we override the method and can now do something
        Toast toast = Toast.makeText(context,
                "Item(s) not deleted", Toast.LENGTH_SHORT);
        toast.show();
    }
    }


}
