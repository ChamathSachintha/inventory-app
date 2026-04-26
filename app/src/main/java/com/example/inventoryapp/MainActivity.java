package com.example.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.inventoryapp.activities.AddEditProductActivity;
import com.example.inventoryapp.activities.InventoryActivity;
import com.example.inventoryapp.activities.CurrencyActivity;

public class MainActivity extends AppCompatActivity {

    Button btnInventory, btnAdd, btnCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnInventory = findViewById(R.id.btnInventory);
        btnAdd = findViewById(R.id.btnAdd);
        btnCurrency = findViewById(R.id.btnCurrency);

        btnInventory.setOnClickListener(v ->
                startActivity(new Intent(this, InventoryActivity.class)));

        btnAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditProductActivity.class)));

        btnCurrency.setOnClickListener(v ->
                startActivity(new Intent(this, CurrencyActivity.class)));
    }
}