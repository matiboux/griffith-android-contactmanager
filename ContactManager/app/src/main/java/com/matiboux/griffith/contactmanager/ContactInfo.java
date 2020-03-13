package com.matiboux.griffith.contactmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class ContactInfo {
    public final long id;
    public final String lastname;
    public final String firstname;
    public final String phone;
    public final String email;

    public ContactInfo(long id, String lastname, String firstname, String phone, String email, String picture) {
        if (id < 0) throw new InvalidParameterException();
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.phone = phone;
        this.email = email;
    }

    public ContactInfo(long id, String lastname, String firstname, String phone, String email) {
        this(id, lastname, firstname, phone, email, null);
    }

    public String getFullName() {
        return !TextUtils.isEmpty(lastname) ? firstname + " " + lastname : firstname;
    }

    public Bitmap getPicture(Context context, boolean fallbackDefault) {
        FileInputStream fileInputStream;
        try {
            fileInputStream = context.openFileInput(String.valueOf(id));
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "Fallback!", Toast.LENGTH_SHORT).show();
            return fallbackDefault ? getDefaultPicture(context) : null;
        }

        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "Read! " + stringBuilder.length(), Toast.LENGTH_SHORT).show();

        byte[] bytes = Base64.decode(stringBuilder.toString(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public Bitmap getPicture(Context context) {
        return getPicture(context, true);
    }

    // *** Static Database Methods

    public static final String DB_NAME = "contacts.db";
    public static final String TABLE_NAME = "contacts";

    public static ContactInfo getById(DBOpenHelper db, long id) {
        // Select the contact
        Cursor c = db.sdb.query(TABLE_NAME, new String[]{"*"},
                "id = ?", new String[]{String.valueOf(id)},
                null, null, null);

        // Does not exist or id is not unique
        if (c.getCount() != 1) return null;

        // Retrieve contact information
        c.moveToFirst();
        ContactInfo contactInfo = new ContactInfo(
                c.getInt(0),
                c.getString(1),
                c.getString(2),
                c.getString(3),
                c.getString(4));
        c.close();
        return contactInfo;
    }

    public static List<ContactInfo> getAll(DBOpenHelper db) {
        // Select the contact
        Cursor c = db.sdb.query(
                ContactInfo.TABLE_NAME, new String[]{"*"},
                null, null,
                null, null, "firstname, lastname COLLATE NOCASE");

        ArrayList<ContactInfo> arrayContacts = new ArrayList<>();

        c.moveToFirst();
        while (!c.isAfterLast()) {
            arrayContacts.add(new ContactInfo(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4)));
            c.moveToNext();
        }
        c.close();

        return arrayContacts;
    }

    public static boolean insert(DBOpenHelper db, ContentValues cv) {
        long id = db.sdb.insert(TABLE_NAME, null, cv);
        cv.put("id", id);
        return id >= 0;
    }

    public static boolean updateById(DBOpenHelper db, ContentValues cv, long id) {
        return db.sdb.update(TABLE_NAME, cv, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public static boolean deleteById(DBOpenHelper db, long id) {
        return db.sdb.delete(TABLE_NAME, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" +
            "id integer primary key autoincrement, " +
            "lastname string, " +
            "firstname string, " +
            "phone string, " +
            "email string)";
    public static final String DROP_TABLE = "drop table " + TABLE_NAME;
    public static final String UPGRADE_TABLE = "alter table " + TABLE_NAME + " drop column picture";

    // *** Static Picture Methods

    public static Bitmap getDefaultPicture(Context context) {
        BitmapDrawable drawable = (BitmapDrawable) context.getDrawable(R.drawable.default_avatar);
        return drawable != null ? drawable.getBitmap() : null;
    }

    public static boolean updatePicture(Context context, Bitmap bitmap, long id) {
        File file = new File(context.getFilesDir(), String.valueOf(id));
        if (!file.exists())
            file.mkdirs();

        // Restore to default
        if (bitmap == null) {
            file.delete();
            return true;
        }

        try {
            FileWriter fileWriter = new FileWriter(file);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            fileWriter.write(Base64.encodeToString(byteArray, Base64.DEFAULT));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }
}

