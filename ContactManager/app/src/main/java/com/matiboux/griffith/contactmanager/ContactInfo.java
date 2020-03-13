package com.matiboux.griffith.contactmanager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.security.InvalidParameterException;

public class ContactInfo {
    public final int id;
    public final String lastname;
    public final String firstname;
    public final String phone;
    public final String email;
    public final String picture;

    public ContactInfo(int id, String lastname, String firstname, String phone, String email, String picture) {
        if (id < 0) throw new InvalidParameterException();
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.phone = phone;
        this.email = email;
        this.picture = picture;
    }

    public ContactInfo(int id, String lastname, String firstname, String phone, String email) {
        this(id, lastname, firstname, phone, email, null);
    }

    public String getFullName() {
        return firstname + " " + lastname;
    }

    // *** Static Methods

    public static final String DB_NAME = "contacts.db";
    public static final String TABLE_NAME = "contacts";

    public static ContactInfo getById(DBOpenHelper db, int id) {
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
                c.getString(4),
                c.getString(5));
        c.close();
        return contactInfo;
    }

    public static boolean insert(DBOpenHelper db, ContentValues cv) {
        return db.sdb.insert(TABLE_NAME, null, cv) >= 0;
    }

    public static boolean updateById(DBOpenHelper db, ContentValues cv, int id) {
        return db.sdb.update(TABLE_NAME, cv, "id = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public static boolean deleteById(DBOpenHelper db, int id) {
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
}

