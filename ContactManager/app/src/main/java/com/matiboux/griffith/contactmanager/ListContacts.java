package com.matiboux.griffith.contactmanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListContacts extends AppCompatActivity {

    DBOpenHelper db;

    FloatingActionButton fab;
    ListView listViewContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contacts);

        // Action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Components
        fab = findViewById(R.id.fab);
        listViewContacts = findViewById(R.id.lv_list_contacts);

        // Database
        db = new DBOpenHelper(this, "test.db", null, 1);

        // Events
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add contact
                startActivityForResult(new Intent(ListContacts.this, AddContact.class), 1);
            }
        });

        listViewContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //ItemClicked item = parent.getItemAtPosition(position);

                Intent intent = new Intent(ListContacts.this, ShowContact.class);
                intent.putExtra("contactId", id); // ???
                //based on item add info to intent
                startActivity(intent);
            }
        });

        // Load from database
        reloadListContacts();
    }

    @Override
    protected void onDestroy() {
        String table = "test";
        String where = null;
        String[] where_args = null;
        db.sdb.delete(table, where, where_args);

        super.onDestroy();
    }

    private void reloadListContacts() {
        // Show database entries
        String table = "test";
        String[] columns = new String[]{"*"};
        String where = null;
        String[] where_args = null;
        String group_by = null;
        String having = null;
        String order_by = null;
        Cursor c = db.sdb.query(table, columns, where, where_args, group_by, having, order_by);

        ArrayList<String> arrayContacts = new ArrayList<>();

        c.moveToFirst();
        for (int i = 0; i < c.getCount(); i++) {
            arrayContacts.add(c.getString(2) + " " + c.getString(1));
            c.moveToNext();
        }

        ArrayAdapter<String> adapterContacts = new ArrayAdapter<>(this, R.layout.adapter_list_contacts, R.id.txv_list_contacts, arrayContacts);

        listViewContacts.setAdapter(adapterContacts);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_add_contact:
                // Move to Add contact Activity
                startActivityForResult(new Intent(this, AddContact.class), 1);
                return true;

            case R.id.action_settings:
                // Move to Settings Activity
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.action_about:
                // Move to About Activity
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1)
            // Make sure the request was successful
            if (resultCode == RESULT_OK)
                reloadListContacts();

        super.onActivityResult(requestCode, resultCode, data);
    }
}