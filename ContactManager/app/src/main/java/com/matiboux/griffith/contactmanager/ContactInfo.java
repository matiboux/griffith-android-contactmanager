package com.matiboux.griffith.contactmanager;

import android.database.Cursor;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class ContactInfo {

    public final int id;
    public final String lastname;
    public final String firstname;
    public final String phone;
    public final String email;

    public ContactInfo(int id, String lastname, String firstname, String phone, String email) {
        if (id < 0) throw new InvalidParameterException();
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.phone = phone;
        this.email = email;

    }

    public String getFullName() {
        return firstname + " " + lastname;
    }

    public static ContactInfo getById(DBOpenHelper db, String table, int id) {
        String[] columns = new String[]{"*"};
        String where = "id = ?";
        String[] where_args = new String[]{String.valueOf(id)};
        Cursor c = db.sdb.query(table, columns, where, where_args, null, null, null);

        if (c.getCount() != 1) return null;

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
}

