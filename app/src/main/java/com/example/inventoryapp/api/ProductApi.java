package com.example.inventoryapp.api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

public class ProductApi {

    public interface Callback {
        void onSuccess(org.json.JSONArray array);
        void onError();
    }

    public static void fetchProducts(Context context, Callback callback) {

        String url = "https://fakestoreapi.com/products";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                callback::onSuccess,
                error -> callback.onError()
        );

        ApiClient.getInstance(context).add(request);
    }
}