package com.example.inventoryapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // Database name and version
    private static final String DB_NAME = "inventory.db";
    private static final int DB_VERSION = 1;

    // Table name
    public static final String TABLE_PRODUCTS = "products";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create products table
        String query = "CREATE TABLE " + TABLE_PRODUCTS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "quantity INTEGER, "
                + "price REAL"
                + ")";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop old table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);

        // Recreate table
        onCreate(db);
    }
}