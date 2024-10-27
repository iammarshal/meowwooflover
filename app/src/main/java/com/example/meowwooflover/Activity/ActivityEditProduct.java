package com.example.meowwooflover.Activity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class ActivityEditProduct extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_SELLER_IMAGE_REQUEST = 2;

    private EditText titleEditText, priceEditText, descriptionEditText, sellerNameEditText, sellerPhoneEditText, sizeEditText;
    private Button updateProductBtn, chooseImageBtn, chooseSellerImageBtn;
    private ImageView productImageView, sellerImageView;
    private Uri imageUri, sellerImageUri;
    private ProgressBar progressBar;
    private String key;
    private ItemsModel loadedItem;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private String oldProductImageUrl, oldSellerImageUrl;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        titleEditText = findViewById(R.id.titleEditText);
        priceEditText = findViewById(R.id.priceEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        sellerNameEditText = findViewById(R.id.sellerNameEditText);
        sellerPhoneEditText = findViewById(R.id.sellerPhoneEditText);
        sizeEditText = findViewById(R.id.sizeEditText);

        updateProductBtn = findViewById(R.id.updateProductBtn);
        chooseImageBtn = findViewById(R.id.chooseImageBtn);
        chooseSellerImageBtn = findViewById(R.id.chooseSellerPicBtn);
        productImageView = findViewById(R.id.productImageView);
        sellerImageView = findViewById(R.id.sellerImageView);
        progressBar = findViewById(R.id.progressBar);

        storageReference = FirebaseStorage.getInstance().getReference("Products");
        databaseReference = FirebaseDatabase.getInstance().getReference("Items");

        key = getIntent().getStringExtra("key");
        if (key != null) {
            loadProductData(key);
        } else {
            Toast.makeText(this, "Invalid product key", Toast.LENGTH_SHORT).show();
        }

        chooseImageBtn.setOnClickListener(v -> openFileChooser(PICK_IMAGE_REQUEST));
        chooseSellerImageBtn.setOnClickListener(v -> openFileChooser(PICK_SELLER_IMAGE_REQUEST));
        updateProductBtn.setOnClickListener(v -> uploadProductImage());
    }

    private void loadProductData(String productId) {
        if (productId != null) {
            databaseReference.child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        loadedItem = dataSnapshot.getValue(ItemsModel.class);
                        if (loadedItem != null) {
                            oldProductImageUrl = loadedItem.getPicUrl().get(0);
                            oldSellerImageUrl = loadedItem.getSellerPic();
                            if (!TextUtils.isEmpty(oldProductImageUrl)) {
                                Glide.with(ActivityEditProduct.this)
                                        .load(oldProductImageUrl)
                                        .into(productImageView);
                            }
                            if (!TextUtils.isEmpty(oldSellerImageUrl)) {
                                Glide.with(ActivityEditProduct.this)
                                        .load(oldSellerImageUrl)
                                        .into(sellerImageView);
                            }
                            titleEditText.setText(loadedItem.getTitle());
                            descriptionEditText.setText(loadedItem.getDescription());
                            priceEditText.setText(String.valueOf(loadedItem.getPrice()));
                            sellerNameEditText.setText(loadedItem.getSellerName());
                            sellerPhoneEditText.setText(String.valueOf(loadedItem.getSellerTell()));
                            sizeEditText.setText(TextUtils.join(", ", loadedItem.getSize()));
                        }
                    } else {
                        Toast.makeText(ActivityEditProduct.this, "Product not found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ActivityEditProduct.this, "Failed to load product data.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No product ID provided.", Toast.LENGTH_SHORT).show();
        }
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
                sellerImageUri = selectedImageUri;
                sellerImageView.setImageURI(sellerImageUri);
            }
        } else {
            Toast.makeText(this, "Image selection failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadProductImage() {
        progressBar.setVisibility(View.VISIBLE);

        String finalProductImageUrl = (imageUri != null) ? imageUri.toString() : oldProductImageUrl;
        String finalSellerImageUrl = (sellerImageUri != null) ? sellerImageUri.toString() : oldSellerImageUrl;


        // Proceed with uploading images
        if (imageUri != null && sellerImageUri != null) {
            uploadImage(imageUri, "ProductImages", productImageUrl -> {
                uploadImage(sellerImageUri, "SellerImages", sellerImageUrl -> {
                    updateProductInDatabase(productImageUrl.toString(), sellerImageUrl.toString());
                });
            });
        } else {
            updateProductInDatabase(null, null);
        }
    }


    private void uploadImage(Uri imageUri, String folder, OnSuccessListener<Uri> onSuccessListener) {
        String fileExtension = getFileExtension(imageUri);
        StorageReference fileReference = storageReference.child(folder + "/" + System.currentTimeMillis() + "." + fileExtension);

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(onSuccessListener))
                .addOnFailureListener(e -> handleFailure("Failed to upload image"));
    }

    private String getFileExtension(Uri uri) {
        if (uri == null) {
            return null;
        }
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void updateProductInDatabase(String productImageUrl, String sellerImageUrl) {
        if (key == null) {
            handleFailure("Product key is missing.");
            return;
        }

        String sellerImageUpdate;

        ArrayList<String> picUrlList = new ArrayList<>(loadedItem.getPicUrl());

        if (productImageUrl != null) {
            picUrlList.add(productImageUrl);
        }

        if (sellerImageUrl != null) {
            sellerImageUpdate = sellerImageUrl;
        } else {
            sellerImageUpdate = loadedItem.getSellerPic();
        }

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

        try {
            double price = Double.parseDouble(priceString);
            int sellerPhoneInt = Integer.parseInt(sellerPhone);

            ItemsModel updatedProduct = new ItemsModel(title, price, description, picUrlList, sellerName, sellerPhoneInt, sizeList, sellerImageUpdate);

            databaseReference.child(key).setValue(updatedProduct)
                    .addOnSuccessListener(aVoid -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(ActivityEditProduct.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ActivityEditProduct.this, AdminActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> handleFailure("Failed to update product"));
        } catch (NumberFormatException e) {
            handleFailure("Please enter valid numeric values.");
        }
    }



    private void handleFailure(String message) {
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(ActivityEditProduct.this, message, Toast.LENGTH_SHORT).show();
    }
}
