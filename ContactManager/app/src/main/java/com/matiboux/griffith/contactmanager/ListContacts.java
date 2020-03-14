package com.matiboux.griffith.contactmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ListContacts extends AppCompatActivity {
    private static final int ASK_FOR_UPDATE = 1;

    private DBOpenHelper db;

    private FloatingActionButton fab;
    private ListView listViewContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contacts);

        // Database
        db = new DBOpenHelper(this, ContactInfo.DB_NAME, null, 1);

        // Action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Components
        fab = findViewById(R.id.fab);
        listViewContacts = findViewById(R.id.lv_list_contacts);

        setListeners(); // Events
        reloadData(); // Initial load
    }

    private void setListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add contact
                startActivityForResult(new Intent(ListContacts.this, AddContact.class), ASK_FOR_UPDATE);
            }
        });
        listViewContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactInfo contactInfo = (ContactInfo) parent.getItemAtPosition(position);

                Intent intent = new Intent(ListContacts.this, ShowContact.class);
                intent.putExtra("contactId", contactInfo.id); // Pass the contact id
                startActivityForResult(intent, ASK_FOR_UPDATE);
            }
        });
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

        switch (id) {
            case R.id.action_add_contact:
                // Move to Add contact Activity
                startActivityForResult(new Intent(this, AddContact.class), ASK_FOR_UPDATE);
                return true;

            case R.id.action_about:
                // Move to About Activity
                startActivity(new Intent(this, About.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ASK_FOR_UPDATE)
            // Make sure the request was successful
            if (resultCode == RESULT_OK)
                reloadData();

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void reloadData() {
        List<ContactInfo> arrayContacts = ContactInfo.getAll(db);
        ListContactsAdapter adapterContacts = new ListContactsAdapter(this, arrayContacts);
        listViewContacts.setAdapter(adapterContacts);
    }
}