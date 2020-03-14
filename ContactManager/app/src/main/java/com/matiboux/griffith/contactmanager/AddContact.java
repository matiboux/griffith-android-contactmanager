package com.matiboux.griffith.contactmanager;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;

public class AddContact extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;

    private DBOpenHelper db;
    private ContactInfo contactInfo;

    private ImageView inputPicture;
    private Button btnResetPicture;
    private boolean inputPictureDefault = true;
    private EditText inputFirstname;
    private EditText inputLastname;
    private EditText inputPhone;
    private EditText inputEmail;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        // Database
        db = new DBOpenHelper(this, ContactInfo.DB_NAME, null, 1);

        // Components
        ActionBar actionBar = getSupportActionBar();
        inputPicture = findViewById(R.id.input_picture);
        btnResetPicture = findViewById(R.id.btn_reset_picture);
        inputFirstname = findViewById(R.id.input_firstname);
        inputLastname = findViewById(R.id.input_lastname);
        inputPhone = findViewById(R.id.input_phone);
        inputEmail = findViewById(R.id.input_email);
        btnSubmit = findViewById(R.id.btn_submit);

        // Get contact id
        long contactId = getIntent().getLongExtra("contactId", -1);
        contactInfo = contactId >= 0 ? ContactInfo.getById(db, contactId) : null;

        // Set information
        String actionBarTitle;
        if (contactInfo == null) actionBarTitle = getString(R.string.add_contact_title);
        else {
            // Set title
            actionBarTitle = getString(R.string.edit_contact_title) + ": " + contactInfo.getFullName();

            // Set contact picture
            Bitmap picture = contactInfo.getPicture(this, false);
            if (picture != null) {
                inputPicture.setImageBitmap(picture);
                inputPictureDefault = false;
            }

            // Set fields
            inputFirstname.setText(contactInfo.firstname);
            inputLastname.setText(contactInfo.lastname);
            inputPhone.setText(contactInfo.phone);
            inputEmail.setText(contactInfo.email);
            btnSubmit.setText(R.string.edit_contact_submit_button);
        }

        // Action Bar attributes
        if (actionBar != null) {
            actionBar.setTitle(actionBarTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setListeners(); // Events
    }

    private void setListeners() {
        inputPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                startActivityForResult(chooserIntent, PICK_IMAGE);
            }
        });
        btnResetPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset to default avatar
                BitmapDrawable drawable = (BitmapDrawable) getDrawable(R.drawable.default_avatar);
                if (drawable == null) return;
                Bitmap bitmap = drawable.getBitmap();
                inputPicture.setImageBitmap(bitmap);
                inputPictureDefault = true;
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSubmit();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;

            // Submit
            case R.id.action_submit:
                actionSubmit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_IMAGE)
            // Make sure the request was successful
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (bitmap != null) {
                    inputPicture.setImageBitmap(bitmap);
                    inputPictureDefault = false;
                } else {
                    Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
                }
            }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void actionSubmit() {
        // Get the contact information
        String firstname = inputFirstname.getText().toString().replaceAll("\\s+", " ").trim();
        if (TextUtils.isEmpty(firstname)) {
            Toast.makeText(this, "The first name must not be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        String lastname = inputLastname.getText().toString().replaceAll("\\s+", " ").trim();
        String phone = inputPhone.getText().toString().replaceAll("\\s+", " ").trim();
        String email = inputEmail.getText().toString().replaceAll("\\s+", " ").trim();
        Bitmap picture = !inputPictureDefault ? ((BitmapDrawable) inputPicture.getDrawable()).getBitmap() : null;

        if (contactInfo != null) {
            if (updateContact(contactInfo.id, lastname, firstname, phone, email, picture)) {
                Toast.makeText(AddContact.this, "Contact successfully updated!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish(); // Finish
                return;
            }
        } else if (insertContact(lastname, firstname, phone, email, picture)) {
            Toast.makeText(AddContact.this, "Contact successfully added!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish(); // Finish
            return;
        }

        // Else,
        Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
    }

    private boolean insertContact(String lastname, String firstname, String phone, String email, Bitmap picture) {
        ContentValues cv = new ContentValues();
        cv.put("lastname", lastname);
        cv.put("firstname", firstname);
        cv.put("phone", phone);
        cv.put("email", email);
        cv.put("picture", BitmapHelper.encodeBitmap(picture));
        return ContactInfo.insert(db, cv);
    }

    private boolean updateContact(long id, String lastname, String firstname, String phone, String email, Bitmap picture) {
        ContentValues cv = new ContentValues();
        cv.put("lastname", lastname);
        cv.put("firstname", firstname);
        cv.put("phone", phone);
        cv.put("email", email);
        cv.put("email", email);
        cv.put("picture", BitmapHelper.encodeBitmap(picture));
        return ContactInfo.updateById(db, cv, id);
    }
}