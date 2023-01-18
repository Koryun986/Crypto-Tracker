package com.samsung.cryptotracker.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private Context context;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    public void openDB () {
        db = dbHelper.getWritableDatabase();
    }

    public void insertToDB (String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.NAME, name);
        db.insert(DBConstants.TABLE_NAME, null, contentValues);
    }

    public void deleteFromDB(String name) {
        db.execSQL(DBConstants.SQL_DELETE_ITEM(name));
    }

    public List<String> getFromDb() {
        List<String> list = new ArrayList<>();
        Cursor cursor = db.query(DBConstants.TABLE_NAME, null, null, null,
                null, null, null);
        while ( cursor.moveToNext() ) {
            String item = cursor.getString((int) cursor.getColumnIndex(DBConstants.NAME));
            list.add(item);
        }
        cursor.close();
        return list;
    }



    public void closeDb () {
        dbHelper.close();
    }
}
