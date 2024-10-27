package com.example.meowwooflover.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.meowwooflover.Adapter.ProductAdapter;
import com.example.meowwooflover.Model.ItemsModel;
import com.example.meowwooflover.R;
import com.example.meowwooflover.ViewModel.AdminViewModel;
import com.example.meowwooflover.ViewModel.OnProductClickListener;
import com.example.meowwooflover.databinding.ActivityAdminBinding;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity implements OnProductClickListener {

    private ActivityAdminBinding binding;
    private ProductAdapter adapter;
    private List<ItemsModel> itemsList = new ArrayList<>();
    private Button editBtn, addBtn, deleteBtn;
    private ProgressBar progressBar;
    private AdminViewModel adminViewModel;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editBtn = binding.editBtn;
        addBtn = binding.addBtn;
        deleteBtn = binding.deleteBtn;
        progressBar = binding.progressBarBestSeller;

        binding.viewBestSeller.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductAdapter(itemsList, this);
        binding.viewBestSeller.setAdapter(adapter);

        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        initBestSeller();

        setupButtonListeners();
    }

    @Override
    public void onProductClick(String key) {
        this.key = key;
    }

    private void initBestSeller() {
        progressBar.setVisibility(View.VISIBLE);
        adminViewModel.getBestSeller().observe(this, items -> {
            itemsList.clear();
            if (items != null) {
                itemsList.addAll(items);
                adapter.notifyDataSetChanged();
            }
            progressBar.setVisibility(View.GONE);
        });
        adminViewModel.loadBestSeller();
    }

    private void setupButtonListeners() {
        // Edit button
        editBtn.setOnClickListener(view -> {
            if (!itemsList.isEmpty()) {
                Intent intent = new Intent(AdminActivity.this, ActivityEditProduct.class);
                intent.putExtra("key", this.key);
                startActivity(intent);
            } else {
                Toast.makeText(AdminActivity.this, "No items available for editing", Toast.LENGTH_SHORT).show();
            }
        });

        addBtn.setOnClickListener(view -> {
            Intent intent = new Intent(AdminActivity.this, ActivityAddProduct.class);
            startActivity(intent);
        });

        deleteBtn.setOnClickListener(view -> {
            if (adapter.getSelectedItem() != null) {
                String selectedItemKey = key;
                String pathToDelete = "Items/" + selectedItemKey;

                adminViewModel.deleteItem(pathToDelete, new AdminViewModel.OnDeleteItemListener() {
                    @Override
                    public void onDeleteSuccess() {
                        Toast.makeText(AdminActivity.this, "Deleted item: " + selectedItemKey, Toast.LENGTH_SHORT).show();
                        initBestSeller();
                    }

                    @Override
                    public void onDeleteFailure(Exception e) {
                        Toast.makeText(AdminActivity.this, "Error deleting item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(AdminActivity.this, "No item selected", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
