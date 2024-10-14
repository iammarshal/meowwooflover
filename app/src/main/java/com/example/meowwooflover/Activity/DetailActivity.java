package com.example.meowwooflover.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.meowwooflover.Helper.ManagmentCart;
import com.example.meowwooflover.Adapter.PicListAdapter;
import com.example.meowwooflover.Adapter.SizeListAdapter;
import com.example.meowwooflover.Model.ItemsModel;
import com.example.meowwooflover.databinding.ActivityDetailBinding;

import java.util.ArrayList;

public class DetailActivity extends BaseActivity {
    private ActivityDetailBinding binding;
    private ItemsModel item;
    private int numberOrder = 1;
    private ManagmentCart managmentCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        getBundle();
        initList();
    }

    private void initList() {
        ArrayList<String> sizeList = new ArrayList<>();
        for (String size : item.getSize()) {
            sizeList.add(size.toString());
        }

        binding.sizeList.setAdapter(new SizeListAdapter(sizeList));
        binding.sizeList.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        ArrayList<String> colorList = new ArrayList<>(item.getPicUrl());

        Glide.with(this)
                .load(colorList.get(0))
                .into(binding.picMain);

        binding.picList.setAdapter(new PicListAdapter(colorList, binding.picMain));
        binding.picList.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
    }

    private void getBundle() {
        item = getIntent().getParcelableExtra("object");

        binding.titleTxt.setText(item.getTitle());
        binding.descriptionTxt.setText(item.getDescription());
        binding.priceTxt.setText("$" + item.getPrice());
        binding.ratingTxt.setText(item.getRating() + " ");
        binding.SellerNameTxt.setText(item.getSellerName());

        binding.AddToCartBtn.setOnClickListener(v -> {
            item.setNumberInCart(numberOrder);
            managmentCart.insertItems(item);
        });

        binding.backBtn.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        binding.CartBtn.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));

        Glide.with(this)
                .load(item.getSellerPic())
                .apply(new RequestOptions().transform(new CenterCrop()))
                .into(binding.picSeller);

        binding.msgToSellerBtn.setOnClickListener(v -> {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:" + item.getSellerTell()));
            sendIntent.putExtra("sms_body", "type your message");
            startActivity(sendIntent);
        });

        binding.calToSellerBtn.setOnClickListener(v -> {
            String phone = String.valueOf(item.getSellerTell());
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);
        });
    }
}