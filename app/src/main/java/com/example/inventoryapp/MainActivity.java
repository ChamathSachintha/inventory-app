package com.example.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.inventoryapp.activities.AddEditProductActivity;
import com.example.inventoryapp.activities.InventoryActivity;
import com.example.inventoryapp.activities.SmartHubActivity;
import com.example.inventoryapp.database.DBHelper;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button btnInventory, btnAdd, btnSmartHub;
    TextView tvTotalStock, tvTotalValue;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnInventory = findViewById(R.id.btnInventory);
        btnAdd = findViewById(R.id.btnAdd);
        btnSmartHub = findViewById(R.id.btnSmartHub);
        tvTotalStock = findViewById(R.id.tvTotalStock);
        tvTotalValue = findViewById(R.id.tvTotalValue);

        dbHelper = new DBHelper(this);

        updateSummary();

        btnInventory.setOnClickListener(v ->
                startActivity(new Intent(this, InventoryActivity.class)));

        btnAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditProductActivity.class)));

        btnSmartHub.setOnClickListener(v ->
                startActivity(new Intent(this, SmartHubActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSummary();
    }

    private void updateSummary() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT SUM(" + DBHelper.COL_QUANTITY + "), " +
                "SUM(" + DBHelper.COL_QUANTITY + " * " + DBHelper.COL_PRICE + ") " +
                "FROM " + DBHelper.TABLE_PRODUCTS;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            int totalStock = cursor.getInt(0);
            double totalValue = cursor.getDouble(1);

            tvTotalStock.setText(String.valueOf(totalStock));
            tvTotalValue.setText(String.format(Locale.US, "LKR %.2f", totalValue));
        }

        cursor.close();
    }
}