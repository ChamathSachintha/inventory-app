package com.example.inventoryapp.activities;

import android.content.ContentValues;
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

import com.example.inventoryapp.R;
import com.example.inventoryapp.database.DBHelper;

// Activity for new user registration
public class SignUpActivity extends AppCompatActivity {

    EditText etFullName, etUsername, etPassword, etConfirmPassword;
    View btnSignUp;
    TextView tvLogin;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup); // Set the layout file

        // Initialize UI components
        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvLogin = findViewById(R.id.tvLogin);

        dbHelper = new DBHelper(this); // Initialize database helper

        // Set up insets to handle system bar padding
        View vRoot = findViewById(R.id.signup_root);
        ViewCompat.setOnApplyWindowInsetsListener(vRoot, (v, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return windowInsets;
        });

        // Handle sign up button click
        btnSignUp.setOnClickListener(v -> registerUser());

        // Return to login screen
        tvLogin.setOnClickListener(v -> finish());
    }

    // Logic to register a new user in the database
    private void registerUser() {
        String name = etFullName.getText().toString().trim();
        String user = etUsername.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        // Validate that all fields are filled
        if (name.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ensure both passwords match
        if (!pass.equals(confirmPass)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert new user data into SQLite
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COL_FULL_NAME, name);
            values.put(DBHelper.COL_USERNAME, user);
            values.put(DBHelper.COL_PASSWORD, pass);

            long id = db.insert(DBHelper.TABLE_USERS, null, values);

            if (id != -1) {
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                finish(); // Close registration screen on success
            } else {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }
}