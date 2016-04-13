package com.example.kuba.monitoring;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Kuba on 02.04.2016.
 */
public class SitesDictOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    public static final String DICTIONARY_TABLE_NAME = "sitesdict";
    public static final String DATABASE_NAME = "db";
    public static final String KEY1 = "name";
    public static final String KEY2 = "status";

    private static final String DICTIONARY_TABLE_CREATE =
            "CREATE TABLE " + DICTIONARY_TABLE_NAME + " (" +
                    BaseColumns._ID  + "  integer primary key," +
                    KEY1 + " TEXT, " +
                    KEY2 + " TEXT);";

    private static final String DICTIONARY_TABLE_DELETE =
            "DROP TABLE IF EXISTS " + DICTIONARY_TABLE_NAME + " ;";

    SitesDictOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DICTIONARY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DICTIONARY_TABLE_DELETE);
        db.execSQL(DICTIONARY_TABLE_CREATE);
    }

}