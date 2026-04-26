package com.example.inventoryapp.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.inventoryapp.R;
import com.example.inventoryapp.database.DBHelper;

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
        setContentView(R.layout.activity_add_edit_product);

        etName = findViewById(R.id.etName);
        etQty = findViewById(R.id.etQty);
        etPrice = findViewById(R.id.etPrice);
        btnSave = findViewById(R.id.btnSave);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        imgProduct = findViewById(R.id.imgProduct);

        dbHelper = new DBHelper(this);

        // SAFE INTENT HANDLING
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("id")) {

            isEditMode = true;

            productId = intent.getIntExtra("id", -1);

            etName.setText(intent.getStringExtra("name"));
            etQty.setText(String.valueOf(intent.getIntExtra("qty", 0)));
            etPrice.setText(String.valueOf(intent.getDoubleExtra("price", 0)));

            imageUri = intent.getStringExtra("image");

            // SAFE IMAGE LOAD
            try {
                if (imageUri != null && !imageUri.isEmpty()) {
                    imgProduct.setImageURI(Uri.parse(imageUri));
                }
            } catch (Exception e) {
                imgProduct.setImageResource(R.mipmap.ic_launcher);
            }
        }

        // IMAGE PICKER
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        Uri uri = result.getData().getData();

                        if (uri != null) {

                            try {
                                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                                getContentResolver().takePersistableUriPermission(uri, takeFlags);
                            } catch (Exception ignored) {}

                            imgProduct.setImageURI(uri);
                            imageUri = uri.toString();
                        }
                    }
                }
        );

        btnSelectImage.setOnClickListener(v -> {
            Intent intent1 = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent1.addCategory(Intent.CATEGORY_OPENABLE);
            intent1.setType("image/*");
            imagePickerLauncher.launch(intent1);
        });

        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        String name = etName.getText().toString().trim();
        String qtyStr = etQty.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();

        if (name.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int qty;
        double price;

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

        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COL_NAME, name);
            values.put(DBHelper.COL_QUANTITY, qty);
            values.put(DBHelper.COL_PRICE, price);
            values.put(DBHelper.COL_IMAGE_URI, imageUri);

            if (isEditMode && productId != -1) {
                db.update(DBHelper.TABLE_PRODUCTS, values, DBHelper.COL_ID + "=?", new String[]{String.valueOf(productId)});
                Toast.makeText(this, "Product Updated", Toast.LENGTH_SHORT).show();
            } else {
                long result = db.insert(DBHelper.TABLE_PRODUCTS, null, values);
                if (result == -1) {
                    Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, "Product Added Successfully", Toast.LENGTH_SHORT).show();
            }
            finish();
        } catch (Exception e) {
            android.util.Log.e("AddEditProduct", "Error saving product", e);
            Toast.makeText(this, "Error saving product", Toast.LENGTH_SHORT).show();
        }
    }
}