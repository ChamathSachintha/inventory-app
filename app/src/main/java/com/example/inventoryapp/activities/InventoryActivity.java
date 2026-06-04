package com.example.inventoryapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventoryapp.R;
import com.example.inventoryapp.adapter.ProductAdapter;
import com.example.inventoryapp.database.DBHelper;
import com.example.inventoryapp.models.Product;

import java.util.ArrayList;

// Activity for viewing and managing product inventory
public class InventoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText etSearch;

    ArrayList<Product> productList;
    ArrayList<Product> fullList;

    DBHelper dbHelper;
    ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory); // Set the layout file

        // Initialize UI components
        recyclerView = findViewById(R.id.recyclerView);
        etSearch = findViewById(R.id.etSearch);

        dbHelper = new DBHelper(this); // Initialize database helper

        productList = new ArrayList<>();
        fullList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Use vertical list for products

        // Adjust padding to handle notch and system bars
        View vRoot = findViewById(R.id.inventory_root);
        int pLeft = vRoot.getPaddingLeft();
        int pTop = vRoot.getPaddingTop();
        int pRight = vRoot.getPaddingRight();
        int pBottom = vRoot.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(vRoot, (v, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(pLeft + systemBars.left, pTop + systemBars.top, pRight + systemBars.right, pBottom + systemBars.bottom);
            return windowInsets;
        });

        // Set up the product adapter with edit and delete actions
        adapter = new ProductAdapter(productList, new ProductAdapter.OnItemActionListener() {
            @Override
            public void onEdit(Product product) {
                // Navigate to edit screen with product data
                Intent intent = new Intent(InventoryActivity.this, AddEditProductActivity.class);

                intent.putExtra("id", product.getId());
                intent.putExtra("name", product.getName());
                intent.putExtra("qty", product.getQuantity());
                intent.putExtra("price", product.getPrice());
                intent.putExtra("image", product.getImageUri());

                startActivity(intent);
            }

            @Override
            public void onDelete(Product product) {
                // Show confirmation before deleting
                new androidx.appcompat.app.AlertDialog.Builder(InventoryActivity.this)
                        .setTitle("Delete Product")
                        .setMessage("Are you sure you want to delete " + product.getName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> deleteProduct(product.getId()))
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        recyclerView.setAdapter(adapter);

        // Filter product list as user types in search bar
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadProducts(); // Initial data load from DB
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts(); // Refresh list whenever returning to this screen
    }

    // Load all products from the SQLite database
    private void loadProducts() {
        productList.clear();
        fullList.clear();

        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_PRODUCTS, null)) {

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_NAME));
                    int qty = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_QUANTITY));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.COL_PRICE));
                    String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_IMAGE_URI));

                    Product p = new Product(id, name, qty, price, imageUri);

                    productList.add(p);
                    fullList.add(p);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            android.util.Log.e("InventoryActivity", "Error loading products", e);
        }

        adapter.notifyDataSetChanged(); // Tell RecyclerView to refresh its items
    }

    // Filter products based on search term
    private void filterProducts(String text) {

        productList.clear();

        if (text.isEmpty()) {
            productList.addAll(fullList); // Show everything if search is empty
        } else {
            for (Product p : fullList) {
                if (p.getName().toLowerCase().contains(text.toLowerCase())) {
                    productList.add(p);
                }
            }
        }

        adapter.notifyDataSetChanged(); // Refresh the list with filtered data
    }

    // Permanently remove a product from the database
    private void deleteProduct(int id) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            int result = db.delete(
                    DBHelper.TABLE_PRODUCTS,
                    DBHelper.COL_ID + "=?",
                    new String[]{String.valueOf(id)}
            );

            if (result > 0) {
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                loadProducts(); // Reload list after deletion
            } else {
                Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            android.util.Log.e("InventoryActivity", "Error deleting product", e);
            Toast.makeText(this, "Error deleting product", Toast.LENGTH_SHORT).show();
        }
    }
}