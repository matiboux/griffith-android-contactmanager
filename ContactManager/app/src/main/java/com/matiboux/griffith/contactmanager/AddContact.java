package com.matiboux.griffith.contactmanager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddContact extends AppCompatActivity {

    DBOpenHelper db;

    EditText inputFirstname;
    EditText inputLastname;
    EditText inputPhone;
    EditText inputEmail;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // Action Bar attributes
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add a new Contact");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Components
        inputFirstname = findViewById(R.id.input_firstname);
        inputLastname = findViewById(R.id.input_lastname);
        inputPhone = findViewById(R.id.input_phone);
        inputEmail = findViewById(R.id.input_email);
        btnSubmit = findViewById(R.id.btn_submit);

        // Database
        db = new DBOpenHelper(this, "test.db", null, 1);

        // Events
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the information to add
                String firstname = inputFirstname.getText().toString();
                String lastname = inputLastname.getText().toString();
                String phone = inputPhone.getText().toString();
                String email = inputEmail.getText().toString();

                if(insertDB(lastname, firstname, phone, email))
                {
                    Toast.makeText(AddContact.this, "Contact added succesfully!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish(); // Finish
                }
                else {
                    Toast.makeText(AddContact.this, "An error occurred", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean insertDB(String lastname, String firstname, String phone, String email) {
        ContentValues cv = new ContentValues();
        cv.put("lastname", lastname);
        cv.put("firstname", firstname);
        cv.put("phone", phone);
        cv.put("email", email);
        return db.sdb.insert("test", null, cv) >= 0;
    }

}