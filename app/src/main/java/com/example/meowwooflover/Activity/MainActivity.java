package com.example.meowwooflover.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import com.example.meowwooflover.Adapter.BestSellerAdapter;
import com.example.meowwooflover.Adapter.CategoryAdapter;
import com.example.meowwooflover.Adapter.SliderAdapter;
import com.example.meowwooflover.Model.SliderModel;
import com.example.meowwooflover.ViewModel.MainViewModel;
import com.example.meowwooflover.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends BaseActivity {

    private final MainViewModel viewModel = new MainViewModel();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initBanners();
        initCategories();
        initBestSeller();
        bottomNavigation();
    }

    private void bottomNavigation() {
        binding.cartBtn.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
    }

    private void initBestSeller() {
        binding.progressBarBestSeller.setVisibility(View.VISIBLE);
        viewModel.getBestSeller().observe(this, items -> {
            binding.viewBestSeller.setLayoutManager(new GridLayoutManager(this, 2));
            binding.viewBestSeller.setAdapter(new BestSellerAdapter(items));
            binding.progressBarBestSeller.setVisibility(View.GONE);
        });
        viewModel.loadBestSeller();
    }

    private void initCategories() {
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        viewModel.getCategory().observe(this, categories -> {
            binding.viewCategory.setLayoutManager(
                    new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false)
            );
            binding.viewCategory.setAdapter(new CategoryAdapter(categories));
            binding.progressBarCategory.setVisibility(View.GONE);
        });
        viewModel.loadCategory();
    }

    private void initBanners() {
        binding.progressBarBanner.setVisibility(View.VISIBLE);
        viewModel.getBanners().observe(this, images -> {
            banners(images);
            binding.progressBarBanner.setVisibility(View.GONE);
        });
        viewModel.loadBanners();
    }

    private void banners(List<SliderModel> images) {
        binding.viewPagerSlider.setAdapter(new SliderAdapter(images, binding.viewPagerSlider));
        binding.viewPagerSlider.setClipToPadding(false);
        binding.viewPagerSlider.setClipChildren(false);
        binding.viewPagerSlider.setOffscreenPageLimit(3);
        binding.viewPagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        binding.viewPagerSlider.setPageTransformer(compositePageTransformer);

        if (images.size() > 1) {
            binding.dotIndicator.setVisibility(View.VISIBLE);
            binding.dotIndicator.attachTo(binding.viewPagerSlider);
        }
    }
}