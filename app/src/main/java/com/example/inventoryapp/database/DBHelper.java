package com.example.inventoryapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Class to manage local SQLite database operations
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "inventory.db"; // Database file name
    private static final int DB_VERSION = 4; // Database version (increments trigger onUpgrade)

    // Product Table Columns
    public static final String TABLE_PRODUCTS = "products";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_PRICE = "price";
    public static final String COL_IMAGE_URI = "image_uri";

    // User Table Columns
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_FULL_NAME = "full_name";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Called when the database is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL query to create the products table
        String createProducts = "CREATE TABLE " + TABLE_PRODUCTS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_QUANTITY + " INTEGER NOT NULL, "
                + COL_PRICE + " REAL NOT NULL, "
                + COL_IMAGE_URI + " TEXT"
                + ")";

        // SQL query to create the users table
        String createUsers = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_FULL_NAME + " TEXT, "
                + COL_USERNAME + " TEXT UNIQUE, "
                + COL_PASSWORD + " TEXT"
                + ")";

        db.execSQL(createProducts);
        db.execSQL(createUsers);

        // Create the default Admin account automatically
        ContentValues adminValues = new ContentValues();
        adminValues.put(COL_FULL_NAME, "Administrator");
        adminValues.put(COL_USERNAME, "Admin");
        adminValues.put(COL_PASSWORD, "admin123");
        db.insert(TABLE_USERS, null, adminValues);

        // Preload Sample Products for initial app demonstration
        preloadProducts(db);
    }

    // Logic to insert sample data into the products table
    private void preloadProducts(SQLiteDatabase db) {
        String[][] samples = {
                {"iPhone 15 Pro", "10", "345000"},
                {"Samsung S24 Ultra", "15", "380000"},
                {"MacBook Air M3", "5", "420000"},
                {"Sony WH-1000XM5", "20", "85000"},
                {"Logitech MX Master 3S", "30", "28000"}
        };

        for (String[] sample : samples) {
            ContentValues v = new ContentValues();
            v.put(COL_NAME, sample[0]);
            v.put(COL_QUANTITY, Integer.parseInt(sample[1]));
            v.put(COL_PRICE, Double.parseDouble(sample[2]));
            db.insert(TABLE_PRODUCTS, null, v);
        }
    }

    // Handles database structure updates between versions
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add image URI column in version 2
            db.execSQL("ALTER TABLE " + TABLE_PRODUCTS + " ADD COLUMN " + COL_IMAGE_URI + " TEXT");
        }
        if (oldVersion < 3) {
            // Create user table and admin account in version 3
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " ("
                    + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_FULL_NAME + " TEXT, "
                    + COL_USERNAME + " TEXT UNIQUE, "
                    + COL_PASSWORD + " TEXT"
                    + ")");

            ContentValues values = new ContentValues();
            values.put(COL_FULL_NAME, "Administrator");
            values.put(COL_USERNAME, "Admin");
            values.put(COL_PASSWORD, "admin123");
            db.insert(TABLE_USERS, null, values);
        }
        if (oldVersion < 4) {
            // Preload sample products in version 4
            preloadProducts(db);
        }
    }
}