package com.example.meowwooflover.Activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meowwooflover.R;

public class OrderPaymentActivity extends AppCompatActivity {

    TextView paymentStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_payment);

        // Initialize the view
        paymentStatusTextView = findViewById(R.id.paymentStatusTextView);

        // Get payment result passed from ZaloPay (assuming you pass the status as a string)
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String paymentStatus = extras.getString("paymentStatus", "Payment result not available");
            paymentStatusTextView.setText(paymentStatus);
        } else {
            Toast.makeText(this, "No payment result found!", Toast.LENGTH_SHORT).show();
        }
    }
}
