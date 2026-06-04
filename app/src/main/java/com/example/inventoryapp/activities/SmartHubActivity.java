package com.example.inventoryapp.activities;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventoryapp.R;
import com.example.inventoryapp.api.CurrencyApi;
import com.example.inventoryapp.api.ProductApi;
import com.example.inventoryapp.adapter.ApiProductAdapter;
import com.example.inventoryapp.database.DBHelper;
import com.example.inventoryapp.models.ApiProduct;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

// Activity for currency conversion and viewing trending market products
public class SmartHubActivity extends AppCompatActivity {

    // ===== Currency UI Components =====
    EditText etAmount;
    Button btnConvert;
    TextView tvResult;

    // ===== Trending Products UI Components =====
    RecyclerView recyclerView;
    ArrayList<ApiProduct> productList;
    ApiProductAdapter adapter;
    
    DBHelper dbHelper;
    double currentLkrRate = 300.0; // Default fallback exchange rate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_hub); // Set layout file

        dbHelper = new DBHelper(this); // Initialize DB helper

        // Initialize currency conversion UI
        etAmount = findViewById(R.id.etAmount);
        btnConvert = findViewById(R.id.btnConvert);
        tvResult = findViewById(R.id.tvResult);

        // Trigger conversion on button click
        btnConvert.setOnClickListener(v -> convertCurrency());

        // Adjust padding for system notch/bars
        View vRoot = findViewById(R.id.smart_hub_root);
        int pLeft = vRoot.getPaddingLeft();
        int pTop = vRoot.getPaddingTop();
        int pRight = vRoot.getPaddingRight();
        int pBottom = vRoot.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(vRoot, (v, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(pLeft + systemBars.left, pTop + systemBars.top, pRight + systemBars.right, pBottom + systemBars.bottom);
            return windowInsets;
        });

        // Initialize trending products list
        recyclerView = findViewById(R.id.recyclerView);
        productList = new ArrayList<>();

        // Set up adapter with "Add to Inventory" logic
        adapter = new ApiProductAdapter(productList, product -> addToInventory(product));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadProducts(); // Fetch trending products from API
        updateLkrRate(); // Get latest exchange rate for accurate conversion
    }

    // Fetch the latest USD to LKR rate from Currency API
    private void updateLkrRate() {
        CurrencyApi.getLkrRate(this, new CurrencyApi.Callback() {
            @Override
            public void onSuccess(double lkrRate) {
                currentLkrRate = lkrRate;
            }
            @Override
            public void onError() {}
        });
    }

    // Save a product from the trending list into the local inventory
    private void addToInventory(ApiProduct p) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            double usdPrice = Double.parseDouble(p.getPrice());
            double lkrPrice = usdPrice * currentLkrRate; // Convert price to LKR

            ContentValues values = new ContentValues();
            values.put(DBHelper.COL_NAME, p.getTitle());
            values.put(DBHelper.COL_QUANTITY, 1); // Default initial quantity
            values.put(DBHelper.COL_PRICE, lkrPrice);
            values.put(DBHelper.COL_IMAGE_URI, p.getImage());

            long result = db.insert(DBHelper.TABLE_PRODUCTS, null, values);
            if (result != -1) {
                Toast.makeText(this, "Added to Inventory at LKR " + String.format("%.2f", lkrPrice), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Logic to convert entered USD amount to LKR
    private void convertCurrency() {
        String amountStr = etAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        CurrencyApi.getLkrRate(this, new CurrencyApi.Callback() {
            @Override
            public void onSuccess(double lkrRate) {
                currentLkrRate = lkrRate;
                double result = amount * lkrRate;
                tvResult.setText(String.format(Locale.US, "%.2f USD = %.2f LKR", amount, result));
            }
            @Override
            public void onError() {
                Toast.makeText(SmartHubActivity.this, "Currency API failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fetch trending products from an external API (Volley)
    private void loadProducts() {
        ProductApi.fetchProducts(this, new ProductApi.Callback() {
            @Override
            public void onSuccess(org.json.JSONArray array) {
                productList.clear();
                for (int i = 0; i < array.length(); i++) {
                    try {
                        JSONObject obj = array.getJSONObject(i);
                        // Add each product from JSON to the local list
                        productList.add(new ApiProduct(
                                obj.getString("title"),
                                obj.getString("price"),
                                obj.getString("image")
                        ));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged(); // Refresh UI list
            }

            @Override
            public void onError() {
                Toast.makeText(SmartHubActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });
    }
}