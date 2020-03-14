package com.matiboux.griffith.contactmanager;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ShowContact extends AppCompatActivity {
    private static final int ASK_FOR_UPDATE = 1;

    private DBOpenHelper db;

    long contactId;
    private ContactInfo contactInfo;

    private CollapsingToolbarLayout toolbarLayout;
    private ImageView contactPicture;
    private FloatingActionButton fab;
    private ListView listViewFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contact);

        // Database
        db = new DBOpenHelper(this, ContactInfo.DB_NAME, null, 1);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Components
        toolbarLayout = findViewById(R.id.toolbar_layout);
        contactPicture = findViewById(R.id.contact_picture);
        fab = findViewById(R.id.fab);
        listViewFields = findViewById(R.id.lv_info_contact);

        // Get contact id
        contactId = getIntent().getLongExtra("contactId", -1);

        // Set picture max height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        contactPicture.setMaxHeight(Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels));

        setListeners(); // Events
        reloadContactInfo(); // Initial load
    }

    private void setListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Edit contact
                Intent intent = new Intent(ShowContact.this, AddContact.class);
                intent.putExtra("contactId", contactInfo.id);
                startActivityForResult(intent, ASK_FOR_UPDATE);
            }
        });
        listViewFields.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final FieldInfo fieldInfo = (FieldInfo) parent.getItemAtPosition(position);

                final EditText input = new EditText(ShowContact.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                input.setText(fieldInfo.value);

                new AlertDialog.Builder(ShowContact.this)
                        .setTitle("Edit \"" + fieldInfo.name + "\"")
                        .setView(input)
                        .setMessage("Update the value for this value if you'd like to change it.")
                        .setIcon(android.R.drawable.ic_menu_edit)
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Toast toast = Toast.makeText(ShowContact.this, null, Toast.LENGTH_SHORT);
                                        String newvalue = input.getText().toString().replaceAll("\\s+", " ").trim();
                                        if (updateDB(contactInfo.id, fieldInfo.field, newvalue)) {
                                            toast.setText("Field successfully updated.");
                                            setResult(RESULT_OK);
                                            reloadContactInfo();
                                        } else toast.setText("An error occurred.");
                                        toast.show();
                                    }
                                })
                        .setNegativeButton(android.R.string.no, null).show();
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
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;

            case R.id.action_edit:
                // Edit contact
                Intent intent = new Intent(this, AddContact.class);
                intent.putExtra("contactId", contactInfo.id);
                startActivityForResult(intent, ASK_FOR_UPDATE);
                return true;

            case R.id.action_delete:
                // Delete contact
                new AlertDialog.Builder(this)
                        .setTitle("Delete " + contactInfo.getFullName() + "?")
                        .setMessage(
                                "Do you really want to delete this contact?\n" +
                                        "This cannot be undone.")
                        .setIcon(android.R.drawable.ic_delete)
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Toast toast = Toast.makeText(ShowContact.this, null, Toast.LENGTH_SHORT);
                                        if (ContactInfo.deleteById(db, contactId)) {
                                            toast.setText("Contact successfully deleted.");
                                            setResult(RESULT_OK);
                                            finish(); // Quit activity
                                            return;
                                        } else toast.setText("An error occurred.");
                                        toast.show();
                                    }
                                })
                        .setNegativeButton(android.R.string.no, null).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ASK_FOR_UPDATE)
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                reloadContactInfo();
            }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void reloadContactInfo() {
        contactInfo = ContactInfo.getById(db, contactId);
        if (contactInfo == null) finish();

        // Set title
        toolbarLayout.setTitle(contactInfo.getFullName());

        // Set contact picture
        contactPicture.setImageBitmap(contactInfo.getPicture(this));

        // Display contact information
        ArrayList<FieldInfo> arrayFields = new ArrayList<>();
        arrayFields.add(new FieldInfo("firstname", "First name", contactInfo.firstname));
        arrayFields.add(new FieldInfo("lastname", "Last name", contactInfo.lastname));
        arrayFields.add(new FieldInfo("phone", "Phone", contactInfo.phone));
        arrayFields.add(new FieldInfo("email", "Email", contactInfo.email));
        ShowContactAdapter adapterFields = new ShowContactAdapter(this, arrayFields);
        listViewFields.setAdapter(adapterFields);
    }

    private boolean updateDB(long id, String field, String value) {
        ContentValues cv = new ContentValues();
        cv.put(field, value);
        return ContactInfo.updateById(db, cv, id);
    }
}