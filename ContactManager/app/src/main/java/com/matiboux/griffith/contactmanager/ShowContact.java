package com.matiboux.griffith.contactmanager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ShowContact extends AppCompatActivity {

    DBOpenHelper db;

    int contactId;
    ContactInfo contactInfo;

    CollapsingToolbarLayout toolBarLayout;
    TextView txv_contact_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contact);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolBarLayout = findViewById(R.id.toolbar_layout);

        // Database
        db = new DBOpenHelper(this, "test.db", null, 1);

        // Components
        txv_contact_info = findViewById(R.id.txv_contact_info);

        // Get contact id
        contactId = getIntent().getIntExtra("contactId", -1);
        reloadContactInfo();

        // Events
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Edit contact
                Intent intent = new Intent(ShowContact.this, AddContact.class);
                intent.putExtra("contactId", contactInfo.id);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_contact, menu);
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
            case R.id.action_edit:
                // Edit contact
                Intent intent = new Intent(this, AddContact.class);
                intent.putExtra("contactId", contactInfo.id);
                startActivityForResult(intent, 1);
                return true;

            case R.id.action_delete:
                // Delete contact
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1)
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                reloadContactInfo();
            }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void reloadContactInfo() {
        contactInfo = ContactInfo.getById(db, "test", contactId);
        if (contactInfo == null) finish();

        // Set information
        toolBarLayout.setTitle(contactInfo.getFullName());
        // more to do...
    }
}