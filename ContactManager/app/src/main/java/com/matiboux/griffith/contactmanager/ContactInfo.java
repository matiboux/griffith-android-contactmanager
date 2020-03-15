package com.matiboux.griffith.contactmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class ContactInfo {
    public final long id;
    public final String lastname;
    public final String firstname;
    public final String phone;
    public final String email;

    private Bitmap picture = null;

    public ContactInfo(long id, String lastname, String firstname, String phone, String email) {
        if (id < 0) throw new InvalidParameterException();
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.phone = phone;
        this.email = email;
    }

    public String getFullName() {
        return !TextUtils.isEmpty(lastname) ? firstname + " " + lastname : firstname;
    }

    public Bitmap getPicture(Context context, boolean fallbackDefault) {
        if (picture == null) {
            DBOpenHelper db = new DBOpenHelper(context, ContactInfo.DB_NAME, null, 1);
            String field = getFieldById(db, "picture", id);
            if (field != null) picture = BitmapHelper.decodeBitmap(field);
        }

        if (picture != null) return picture;
        return fallbackDefault ? getDefaultPicture(context) : null;
    }

    public Bitmap getPicture(Context context) {
        return getPicture(context, true);
    }

    // *** Static Database Methods

    public static final String DB_NAME = "contacts.db";
    public static final String TABLE_NAME = "contacts";

    public static String getFieldById(DBOpenHelper db, String column, long id) {
        // Select the contact
        Cursor c = db.sdb.query(TABLE_NAME, new String[]{column},
                "id = ?", new String[]{String.valueOf(id)},
                null, null, null);

        // Check if the cursor works
        try {
            // Does not exist or id is not unique
            if (c.getCount() != 1) return null;
        } catch (Exception e) {
            return null;
        }

        // Retrieve contact information
        c.moveToFirst();
        String result = c.getString(0);
        c.close();

        return result;
    }

    public static ContactInfo getById(DBOpenHelper db, long id) {
        // Select the contact
        Cursor c = db.sdb.query(TABLE_NAME, new String[]{"id", "lastname", "firstname", "phone", "email"},
                "id = ?", new String[]{String.valueOf(id)},
                null, null, null);

        // Check if the cursor works
        try {
            // Does not exist or id is not unique
            if (c.getCount() != 1) return null;
        } catch (Exception e) {
            return null;
        }

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
                ContactInfo.TABLE_NAME, new String[]{"id", "lastname", "firstname", "phone", "email"},
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
        return db.sdb.insert(TABLE_NAME, null, cv) >= 0;
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
            "email string, " +
            "picture string)";
    public static final String DROP_TABLE = "drop table " + TABLE_NAME;
    public static final String UPGRADE_TABLE = "alter table " + TABLE_NAME + " add column picture string";

    // *** Static Picture Methods

    public static Bitmap getDefaultPicture(Context context) {
        BitmapDrawable drawable = (BitmapDrawable) context.getDrawable(R.drawable.default_avatar);
        return drawable != null ? drawable.getBitmap() : null;
    }
}

