package com.samsung.cryptotracker.DB;

public class DBConstants {
    public static final String TABLE_NAME = "favorites";
    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String DB_NAME = "favorites.db";
    public static final int DB_VERSION = 1;
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    NAME + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
    public static final String SQL_DELETE_ITEM(String name){
            return "DELETE FROM " + TABLE_NAME + " WHERE " + NAME + "=\"" + name +"\";";
    }
}
