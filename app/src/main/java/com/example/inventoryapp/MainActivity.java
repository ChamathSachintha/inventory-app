package com.example.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.inventoryapp.activities.AddEditProductActivity;
import com.example.inventoryapp.activities.InventoryActivity;
import com.example.inventoryapp.activities.LoginActivity;
import com.example.inventoryapp.activities.SmartHubActivity;
import com.example.inventoryapp.database.DBHelper;

import java.util.Locale;

// Main Dashboard Activity
public class MainActivity extends AppCompatActivity {

    Button btnInventory, btnAdd, btnSmartHub;
    TextView tvTotalStock, tvTotalValue;
    ImageView ivProfile;
    DBHelper dbHelper;
    String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the layout file

        // Get the logged-in username from the intent
        currentUsername = getIntent().getStringExtra("USERNAME");

        // UI Initialization
        btnInventory = findViewById(R.id.btnInventory);
        btnAdd = findViewById(R.id.btnAdd);
        btnSmartHub = findViewById(R.id.btnSmartHub);
        tvTotalStock = findViewById(R.id.tvTotalStock);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        ivProfile = findViewById(R.id.ivProfile);

        dbHelper = new DBHelper(this); // Initialize database helper

        // Open profile popup when clicking the profile icon
        ivProfile.setOnClickListener(v -> showProfilePopup());

        // Handle system bar insets for proper layout alignment
        View vRoot = findViewById(R.id.main_root_layout);
        int pLeft = vRoot.getPaddingLeft();
        int pTop = vRoot.getPaddingTop();
        int pRight = vRoot.getPaddingRight();
        int pBottom = vRoot.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(vRoot, (v, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(pLeft + systemBars.left, pTop + systemBars.top, pRight + systemBars.right, pBottom + systemBars.bottom);
            return windowInsets;
        });

        // Calculate and display inventory summary
        updateSummary();

        // Navigate to Inventory screen
        btnInventory.setOnClickListener(v ->
                startActivity(new Intent(this, InventoryActivity.class)));

        // Navigate to Add Product screen
        btnAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddEditProductActivity.class)));

        // Navigate to Smart Hub screen
        btnSmartHub.setOnClickListener(v ->
                startActivity(new Intent(this, SmartHubActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSummary(); // Refresh stats when returning to this screen
    }

    // Fetch and calculate total stock and valuation from DB
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

    // Display profile details and logout option in a popup
    private void showProfilePopup() {
        String fullName = "User";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_USERS, new String[]{DBHelper.COL_FULL_NAME},
                DBHelper.COL_USERNAME + "=?", new String[]{currentUsername},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            fullName = cursor.getString(0);
            cursor.close();
        }

        // Inflate and set up the custom dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_profile, null);
        TextView tvPopupName = dialogView.findViewById(R.id.tvPopupName);
        TextView tvPopupUsername = dialogView.findViewById(R.id.tvPopupUsername);
        Button btnLogout = dialogView.findViewById(R.id.btnLogout);

        tvPopupName.setText(fullName);
        tvPopupUsername.setText("@" + currentUsername);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Make background transparent for rounded corners
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Handle logout action
        btnLogout.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(this, R.string.logged_out, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish(); // Close main activity
        });

        dialog.show();
    }
}