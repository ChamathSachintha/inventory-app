package com.example.inventoryapp.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.inventoryapp.R;
import com.example.inventoryapp.database.DBHelper;

// Activity for adding a new product or editing an existing one
public class AddEditProductActivity extends AppCompatActivity {

    EditText etName, etQty, etPrice;
    Button btnSave, btnSelectImage;
    ImageView imgProduct;

    DBHelper dbHelper;

    String imageUri = null;

    int productId = -1;
    boolean isEditMode = false;

    ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product); // Set the layout file

        // Initialize UI components
        etName = findViewById(R.id.etName);
        etQty = findViewById(R.id.etQty);
        etPrice = findViewById(R.id.etPrice);
        btnSave = findViewById(R.id.btnSave);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        imgProduct = findViewById(R.id.imgProduct);

        dbHelper = new DBHelper(this); // Initialize database helper

        // Adjust padding to accommodate screen notches and system bars
        View vRoot = findViewById(R.id.add_edit_root);
        int pLeft = vRoot.getPaddingLeft();
        int pTop = vRoot.getPaddingTop();
        int pRight = vRoot.getPaddingRight();
        int pBottom = vRoot.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(vRoot, (v, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(pLeft + systemBars.left, pTop + systemBars.top, pRight + systemBars.right, pBottom + systemBars.bottom);
            return windowInsets;
        });

        // Check if we are in "Edit" mode by looking for an ID in the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {

            isEditMode = true;
            productId = intent.getIntExtra("id", -1);

            // Populate fields with existing product data
            etName.setText(intent.getStringExtra("name"));
            etQty.setText(String.valueOf(intent.getIntExtra("qty", 0)));
            etPrice.setText(String.valueOf(intent.getDoubleExtra("price", 0)));

            imageUri = intent.getStringExtra("image");

            // Load existing product image safely
            try {
                if (imageUri != null && !imageUri.isEmpty()) {
                    imgProduct.setImageURI(Uri.parse(imageUri));
                }
            } catch (Exception e) {
                imgProduct.setImageResource(R.drawable.ic_product_placeholder); // Fallback to placeholder
            }
        }

        // Register callback for image picker results
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            try {
                                // Request persistent permission to access the selected image
                                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                                getContentResolver().takePersistableUriPermission(uri, takeFlags);
                            } catch (Exception ignored) {}

                            imgProduct.setImageURI(uri); // Show preview
                            imageUri = uri.toString(); // Store path for database
                        }
                    }
                }
        );

        // Open system document picker for images
        btnSelectImage.setOnClickListener(v -> {
            Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent1.addCategory(Intent.CATEGORY_OPENABLE);
            intent1.setType("image/*");
            imagePickerLauncher.launch(intent1);
        });

        // Trigger save logic
        btnSave.setOnClickListener(v -> saveProduct());
    }

    // Logic to save or update product in database
    private void saveProduct() {
        String name = etName.getText().toString().trim();
        String qtyStr = etQty.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        // Validate mandatory fields
        if (name.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int qty;
        double price;

        // Parse numerical inputs safely
        try {
            qty = Integer.parseInt(qtyStr);
            price = Double.parseDouble(priceStr);
            
            if (qty < 0 || price < 0) {
                Toast.makeText(this, "Quantity and Price cannot be negative", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save data to SQLite database
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COL_NAME, name);
            values.put(DBHelper.COL_QUANTITY, qty);
            values.put(DBHelper.COL_PRICE, price);
            values.put(DBHelper.COL_IMAGE_URI, imageUri);

            if (isEditMode && productId != -1) {
                // Update existing record
                db.update(DBHelper.TABLE_PRODUCTS, values, DBHelper.COL_ID + "=?", new String[]{String.valueOf(productId)});
                Toast.makeText(this, "Product Updated", Toast.LENGTH_SHORT).show();
            } else {
                // Insert new record
                long result = db.insert(DBHelper.TABLE_PRODUCTS, null, values);
                if (result == -1) {
                    Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
            }
            finish(); // Close activity and return
        } catch (Exception e) {
            android.util.Log.e("AddEditProduct", "Error saving product", e);
            Toast.makeText(this, "Error saving product", Toast.LENGTH_SHORT).show();
        }
    }
}