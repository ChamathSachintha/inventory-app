package com.example.inventoryapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inventoryapp.R;
import com.example.inventoryapp.api.CurrencyApi;
import com.example.inventoryapp.api.ProductApi;
import com.example.inventoryapp.adapter.ApiProductAdapter;
import com.example.inventoryapp.models.ApiProduct;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class SmartHubActivity extends AppCompatActivity {

    // ===== Currency UI =====
    EditText etAmount;
    Button btnConvert;
    TextView tvResult;

    // ===== Trending Products UI =====
    RecyclerView recyclerView;
    ArrayList<ApiProduct> productList;
    ApiProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_hub);

        // ================= CURRENCY =================
        etAmount = findViewById(R.id.etAmount);
        btnConvert = findViewById(R.id.btnConvert);
        tvResult = findViewById(R.id.tvResult);

        btnConvert.setOnClickListener(v -> convertCurrency());

        // ================= PRODUCTS =================
        recyclerView = findViewById(R.id.recyclerView);
        productList = new ArrayList<>();

        adapter = new ApiProductAdapter(productList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadProducts();
    }

    // ================= CURRENCY =================
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

                double result = amount * lkrRate;

                tvResult.setText(String.format(
                        Locale.US,
                        "%.2f USD = %.2f LKR",
                        amount,
                        result
                ));
            }

            @Override
            public void onError() {
                Toast.makeText(SmartHubActivity.this,
                        "Currency API failed",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================= PRODUCTS =================
    private void loadProducts() {

        ProductApi.fetchProducts(this, new ProductApi.Callback() {
            @Override
            public void onSuccess(org.json.JSONArray array) {

                productList.clear();

                for (int i = 0; i < array.length(); i++) {
                    try {
                        JSONObject obj = array.getJSONObject(i);

                        productList.add(new ApiProduct(
                                obj.getString("title"),
                                obj.getString("price"),
                                obj.getString("image")
                        ));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError() {
                Toast.makeText(SmartHubActivity.this,
                        "Failed to load products",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}