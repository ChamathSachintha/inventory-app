package com.example.inventoryapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "inventory.db";
    private static final int DB_VERSION = 2;

    public static final String TABLE_PRODUCTS = "products";

    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_PRICE = "price";
    public static final String COL_IMAGE_URI = "image_uri";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_PRODUCTS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_QUANTITY + " INTEGER NOT NULL, "
                + COL_PRICE + " REAL NOT NULL, "
                + COL_IMAGE_URI + " TEXT"
                + ")";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // PRODUCTION SAFE UPGRADE (better than drop in real apps)
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_PRODUCTS + " ADD COLUMN " + COL_IMAGE_URI + " TEXT");
        }
    }
}