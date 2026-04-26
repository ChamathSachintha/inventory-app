package com.example.inventoryapp.api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class CurrencyApi {

    public interface Callback {
        void onSuccess(double lkrRate);
        void onError();
    }

    private static final String URL =
            "https://api.exchangerate-api.com/v4/latest/USD";

    public static void getLkrRate(Context context, Callback callback) {

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                response -> {
                    try {
                        JSONObject rates = response.getJSONObject("rates");
                        double lkr = rates.getDouble("LKR");
                        callback.onSuccess(lkr);
                    } catch (Exception e) {
                        callback.onError();
                    }
                },
                error -> callback.onError()
        );

        ApiClient.getInstance(context).add(request);
    }
}