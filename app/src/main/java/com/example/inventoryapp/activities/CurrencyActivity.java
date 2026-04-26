package com.example.inventoryapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.inventoryapp.R;

import org.json.JSONObject;

public class CurrencyActivity extends AppCompatActivity {

    EditText etAmount;
    Button btnConvert;
    TextView tvResult;

    RequestQueue requestQueue;

    String URL = "https://api.exchangerate-api.com/v4/latest/USD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);

        etAmount = findViewById(R.id.etAmount);
        btnConvert = findViewById(R.id.btnConvert);
        tvResult = findViewById(R.id.tvResult);

        requestQueue = Volley.newRequestQueue(this);

        btnConvert.setOnClickListener(v -> convertCurrency());
    }

    private void convertCurrency() {

        String amountStr = etAmount.getText().toString();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                response -> {

                    try {
                        JSONObject rates = response.getJSONObject("rates");
                        double lkrRate = rates.getDouble("LKR");

                        double result = amount * lkrRate;

                        tvResult.setText("LKR: " + result);

                    } catch (Exception e) {
                        tvResult.setText("Error parsing data");
                    }

                },
                error -> tvResult.setText("API Error")
        );

        requestQueue.add(request);
    }
}