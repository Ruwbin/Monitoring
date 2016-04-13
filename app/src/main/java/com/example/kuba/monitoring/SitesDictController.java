package com.example.kuba.monitoring;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by Kuba on 02.04.2016.
 */
public class SitesDictController {
    private SitesDictOpenHelper dbhelper;
    private Context context;
    private SQLiteDatabase database;
    String selectQuery = "SELECT * from " + SitesDictOpenHelper.DICTIONARY_TABLE_NAME;

    public SitesDictController(Context context) {
        this.context = context;
    }

    public SitesDictController open()  {
        dbhelper = new SitesDictOpenHelper(context);
        database = dbhelper.getWritableDatabase();
        return this;
    }
    public void insert(String siteName, String state){
        ContentValues contentValues = new ContentValues();
        contentValues.put(SitesDictOpenHelper.KEY1, siteName);
        contentValues.put(SitesDictOpenHelper.KEY2, state);
        database.insert(SitesDictOpenHelper.DICTIONARY_TABLE_NAME, null, contentValues);
    }

    public void delete(int id){
        database.delete(SitesDictOpenHelper.DICTIONARY_TABLE_NAME, BaseColumns._ID + " = " + id, null);
    }
    public Cursor readAll() {
        Cursor cursor = database.rawQuery(selectQuery, null);
        return cursor;
    }

    public void update(int id, String siteName, String state) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SitesDictOpenHelper.KEY1, siteName);
        contentValues.put(SitesDictOpenHelper.KEY2, state);
        database.update(SitesDictOpenHelper.DICTIONARY_TABLE_NAME,contentValues, BaseColumns._ID+ " = "+id,null);
    }
}
