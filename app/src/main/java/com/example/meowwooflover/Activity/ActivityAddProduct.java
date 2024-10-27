package com.example.meowwooflover.Activity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meowwooflover.Model.ItemsModel;
import com.example.meowwooflover.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ActivityAddProduct extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_SELLER_IMAGE_REQUEST = 2;

    private EditText titleEditText, priceEditText, descriptionEditText, sellerNameEditText, sellerPhoneEditText, sizeEditText;
    private Button addProductBtn, chooseImageBtn, chooseSellerImageBtn;
    private ImageView productImageView, sellerImageView;
    private Uri imageUri, sellerImageUri;
    private ProgressBar progressBar;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        titleEditText = findViewById(R.id.titleEditText);
        priceEditText = findViewById(R.id.priceEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        sellerNameEditText = findViewById(R.id.sellerNameEditText);
        sellerPhoneEditText = findViewById(R.id.sellerPhoneEditText);
        sizeEditText = findViewById(R.id.sizeEditText);

        addProductBtn = findViewById(R.id.addProductBtn);
        chooseImageBtn = findViewById(R.id.chooseImageBtn);
        chooseSellerImageBtn = findViewById(R.id.chooseSellerPicBtn);
        productImageView = findViewById(R.id.productImageView);
        sellerImageView = findViewById(R.id.sellerImageView);
        progressBar = findViewById(R.id.progressBar);

        // Firebase instances
        storageReference = FirebaseStorage.getInstance().getReference("Products");
        databaseReference = FirebaseDatabase.getInstance().getReference("Items");

        chooseImageBtn.setOnClickListener(v -> openFileChooser(PICK_IMAGE_REQUEST));
        chooseSellerImageBtn.setOnClickListener(v -> openFileChooser(PICK_SELLER_IMAGE_REQUEST)); // Set listener for seller image

        addProductBtn.setOnClickListener(v -> {
            if (validateInput()) {
                uploadProductImage();
            }
        });
    }

    private void openFileChooser(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            if (requestCode == PICK_IMAGE_REQUEST) {
                imageUri = selectedImageUri;
                productImageView.setImageURI(imageUri);
            } else if (requestCode == PICK_SELLER_IMAGE_REQUEST) {
                sellerImageUri = selectedImageUri; // Set seller image URI
                sellerImageView.setImageURI(sellerImageUri);
            }
        }
    }

    private boolean validateInput() {
        if (TextUtils.isEmpty(titleEditText.getText())) {
            titleEditText.setError("Product title is required");
            return false;
        }
        if (TextUtils.isEmpty(priceEditText.getText())) {
            priceEditText.setError("Price is required");
            return false;
        }
        if (TextUtils.isEmpty(descriptionEditText.getText())) {
            descriptionEditText.setError("Description is required");
            return false;
        }
        if (TextUtils.isEmpty(sellerNameEditText.getText())) {
            sellerNameEditText.setError("Seller name is required");
            return false;
        }
        if (TextUtils.isEmpty(sellerPhoneEditText.getText())) {
            sellerPhoneEditText.setError("Seller phone is required");
            return false;
        }
        if (TextUtils.isEmpty(sizeEditText.getText())) {
            sizeEditText.setError("At least one size is required");
            return false;
        }
        if (imageUri == null) {
            Toast.makeText(this, "Product image is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (sellerImageUri == null) {
            Toast.makeText(this, "Seller image is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadProductImage() {
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        String productFileExtension = getFileExtension(imageUri);
        final StorageReference productFileReference = storageReference.child("ProductImages/" + System.currentTimeMillis() + "." + productFileExtension);
        String sellerFileExtension = getFileExtension(sellerImageUri);
        final StorageReference sellerFileReference = storageReference.child("SellerImages/" + System.currentTimeMillis() + "." + sellerFileExtension);

        productFileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    productFileReference.getDownloadUrl().addOnSuccessListener(productUrl -> {
                        sellerFileReference.putFile(sellerImageUri)
                                .addOnSuccessListener(taskSnapshot1 -> {
                                    sellerFileReference.getDownloadUrl().addOnSuccessListener(sellerUrl -> addProductToDatabase(productUrl, sellerUrl))
                                            .addOnFailureListener(e -> handleFailure("Failed to get seller image URL"));
                                })
                                .addOnFailureListener(e -> handleFailure("Failed to upload seller image"));
                    }).addOnFailureListener(e -> handleFailure("Failed to get product image URL"));
                })
                .addOnFailureListener(e -> handleFailure("Failed to upload product image"));
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void addProductToDatabase(Uri productUri, Uri sellerUri) {
        String key = databaseReference.push().getKey();
        String title = titleEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String sellerName = sellerNameEditText.getText().toString().trim();
        String sellerPhone = sellerPhoneEditText.getText().toString().trim();
        String sizeText = sizeEditText.getText().toString().trim();
        String[] sizeArray = sizeText.split(",");

        ArrayList<String> sizeList = new ArrayList<>();
        for (String size : sizeArray) {
            sizeList.add(size.trim());
        }

        ArrayList<String> picUrlList = new ArrayList<>();
        picUrlList.add(productUri.toString());

        try {
            double price = Double.parseDouble(priceString);
            int sellerPhoneInt = Integer.parseInt(sellerPhone);
            ItemsModel product = new ItemsModel(title, price, description, picUrlList, sellerName, sellerPhoneInt, sizeList, sellerUri.toString()); // Set sellerPic to sellerUri

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long count = dataSnapshot.getChildrenCount();
                    String newKey = String.valueOf(count);

                    databaseReference.child(newKey).setValue(product)
                            .addOnSuccessListener(aVoid -> {
                                // Thành công
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ActivityAddProduct.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                                clearInputFields();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("AddItem", "Failed to add item: " + e.getMessage());
                            });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("AddItem", "Database error: " + databaseError.getMessage());
                }
            });
        } catch (NumberFormatException e) {
            handleFailure("Invalid price or phone number format");
        }
    }

    private void handleFailure(String message) {
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(ActivityAddProduct.this, message, Toast.LENGTH_SHORT).show();
    }

    private void clearInputFields() {
        titleEditText.setText("");
        priceEditText.setText("");
        descriptionEditText.setText("");
        sellerNameEditText.setText("");
        sellerPhoneEditText.setText("");
        sizeEditText.setText("");
        productImageView.setImageResource(0);
        sellerImageView.setImageResource(0);
        imageUri = null;
        sellerImageUri = null;
    }
}
