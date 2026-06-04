package com.example.inventoryapp.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.inventoryapp.MainActivity;
import com.example.inventoryapp.R;
import com.example.inventoryapp.database.DBHelper;

// Activity for user authentication
public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    View btnLogin;
    TextView tvSignUp;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Set the layout file

        // Initialize UI components
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignUp = findViewById(R.id.tvSignUp);

        dbHelper = new DBHelper(this); // Initialize database helper

        // Adjust padding to avoid overlap with system notches/bars
        View vRoot = findViewById(R.id.login_root);
        ViewCompat.setOnApplyWindowInsetsListener(vRoot, (v, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return windowInsets;
        });

        // Handle login button click
        btnLogin.setOnClickListener(v -> loginUser());

        // Navigate to registration screen
        tvSignUp.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
    }

    // Logic to verify user credentials
    private void loginUser() {
        String user = etUsername.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        // Check if input fields are empty
        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query database to check if user exists with correct password
        try (SQLiteDatabase db = dbHelper.getReadableDatabase()) {
            String query = "SELECT * FROM " + DBHelper.TABLE_USERS + " WHERE " +
                    DBHelper.COL_USERNAME + "=? AND " + DBHelper.COL_PASSWORD + "=?";
            Cursor cursor = db.rawQuery(query, new String[]{user, pass});

            if (cursor.moveToFirst()) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("USERNAME", user); // Pass username to dashboard
                startActivity(intent);
                finish(); // Close login screen
            } else {
                Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        } catch (Exception e) {
            Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show();
        }
    }
}