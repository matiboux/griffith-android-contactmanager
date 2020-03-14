package com.matiboux.griffith.contactmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
    public final SQLiteDatabase sdb;

    // Constructor
    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        sdb = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sdb) {
        sdb.execSQL(ContactInfo.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sdb, int oldVersion, int newVersion) {
        sdb.execSQL(ContactInfo.UPGRADE_TABLE);
    }
}
