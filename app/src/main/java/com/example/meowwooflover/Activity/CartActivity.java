package com.example.meowwooflover.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.meowwooflover.Api.CreateOrder;
import com.example.meowwooflover.Helper.ManagmentCart;
import com.example.meowwooflover.Adapter.CartAdapter;
import com.example.meowwooflover.Helper.ChangeNumberItemsListener;
import com.example.meowwooflover.databinding.ActivityCartBinding;

import org.json.JSONObject;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class CartActivity extends BaseActivity {
    private ActivityCartBinding binding;
    private ManagmentCart managmentCart;
    private double tax = 0.0;
    private String totalString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        managmentCart = new ManagmentCart(this);
        setVariable();
        initCartList();
        calculateCart();
        // Allowing network operations on the main thread (not recommended for production)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK initialization
        ZaloPaySDK.init(2553, Environment.SANDBOX);

    }

    private void calculateCart() {
        double percentTax = 0.1;
        double delivery = 15.0;
        tax = Math.round((managmentCart.getTotalFee() * percentTax) * 100) / 100.0;
        double total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100) / 100;
        double itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100;

        binding.totalFeeTxt.setText("₫" + itemTotal);
        binding.taxTxt.setText("₫" + tax);
        binding.deliveryTxt.setText("₫" + delivery);
        binding.totalTxt.setText("₫" + total);

        totalString = String.format("%.0f", total);  // Store total as string for later use
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

        // On click checkout button
        binding.checkOutBtn.setOnClickListener(v -> {
            CreateOrder orderApi = new CreateOrder();

            try {
                JSONObject data = orderApi.createOrder(totalString);
                Log.d("Amount", totalString);
                Log.d("Order Data", data.toString());
                String code = data.getString("return_code");

                if (code.equals("1")) {
                    String token = data.getString("zp_trans_token");
                    ZaloPaySDK.getInstance().payOrder(CartActivity.this, token, "demozpdk://app", new PayOrderListener() {
                        @Override
                        public void onPaymentSucceeded(String transactionId, String zpOrderId, String appTransId) {
                            Toast.makeText(CartActivity.this, "Thanh Toán Thành Công!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPaymentCanceled(String zpOrderId, String appTransId) {
                            Toast.makeText(CartActivity.this, "Thanh Toán Đã Bị Hủy!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPaymentError(ZaloPayError zaloPayError, String zpOrderId, String appTransId) {
                            Toast.makeText(CartActivity.this, "Lỗi Thanh Toán: " + zaloPayError.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(CartActivity.this, "Create Order Failed with code: " + code, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(CartActivity.this, "Lỗi tạo đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
        }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
    }
}
