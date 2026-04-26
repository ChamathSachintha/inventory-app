package com.example.inventoryapp.api;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ApiClient {

    private static RequestQueue queue;

    public static RequestQueue getInstance(Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return queue;
    }
}