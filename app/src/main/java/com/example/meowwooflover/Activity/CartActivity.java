package com.example.meowwooflover.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.meowwooflover.Helper.ManagmentCart;
import com.example.meowwooflover.Adapter.CartAdapter;
import com.example.meowwooflover.Helper.ChangeNumberItemsListener;
import com.example.meowwooflover.databinding.ActivityCartBinding;

public class CartActivity extends BaseActivity {
    private ActivityCartBinding binding;
    private ManagmentCart managmentCart;
    private double tax = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        setVariable();
        initCartList();
        calculateCart();
    }

    private void calculateCart() {
        double percentTax = 0.02;
        double delivery = 15.0;
        tax = Math.round((managmentCart.getTotalFee() * percentTax) * 100) / 100.0;
        double total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100) / 100;
        double itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100;

        binding.totalFeeTxt.setText("$" + itemTotal);
        binding.taxTxt.setText("$" + tax);
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);
    }

    private void initCartList() {
        binding.cartView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
        binding.cartView.setAdapter(
                new CartAdapter(managmentCart.getListCart(), this, new ChangeNumberItemsListener() {
                    @Override
                    public void onChanged() {
                        calculateCart();
                    }
                })
        );
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
    }
}