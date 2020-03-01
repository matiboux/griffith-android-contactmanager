package com.matiboux.griffith.contactmanager;

import android.content.ContentValues;
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

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_table);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not recommended for a real application
        // Just for testing
        db.execSQL(drop_table);
        db.execSQL(create_table);
    }

    private static final String create_table = "create table test(" +
            "id integer primary key autoincrement, " +
            "lastname string, " +
            "firstname string, " +
            "phone string, " +
            "email string)";

    private static final String drop_table = "drop table test";
}
