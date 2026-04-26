package com.example.inventoryapp.activities;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.inventoryapp.R;
import com.example.inventoryapp.database.DBHelper;

public class AddEditProductActivity extends AppCompatActivity {

    EditText etName, etQty, etPrice;
    Button btnSave;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        // Initialize UI
        etName = findViewById(R.id.etName);
        etQty = findViewById(R.id.etQty);
        etPrice = findViewById(R.id.etPrice);
        btnSave = findViewById(R.id.btnSave);

        // Initialize database
        dbHelper = new DBHelper(this);

        // Save button click
        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {

        String name = etName.getText().toString().trim();
        String qtyStr = etQty.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        // Validation
        if (name.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int qty;
        double price;

        try {
            qty = Integer.parseInt(qtyStr);
            price = Double.parseDouble(priceStr);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert into database
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("quantity", qty);
        values.put("price", price);

        long result = db.insert("products", null, values);

        if (result == -1) {
            Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
            finish(); // go back to home
        }
    }
}