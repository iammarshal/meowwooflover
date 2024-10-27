package com.example.meowwooflover.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.meowwooflover.Model.ItemsModel;
import com.example.meowwooflover.ViewModel.OnProductClickListener;
import com.example.meowwooflover.databinding.ViewholderBestSellerBinding;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final List<ItemsModel> itemsList;
    private int selectedPosition = -1;
    private Context context;
    private OnProductClickListener listener;

    public ProductAdapter(List<ItemsModel> itemsList, OnProductClickListener listener) {
        this.itemsList = itemsList;
        this.listener = listener;
    }

    public ItemsModel getSelectedItem() {
        if (selectedPosition != -1) {
            return itemsList.get(selectedPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderBestSellerBinding binding = ViewholderBestSellerBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ItemsModel item = itemsList.get(position);

        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.priceTxt.setText(String.format("₫%.0f", item.getPrice()));

        RequestOptions requestOptions = new RequestOptions().transform(new CenterCrop());
        Glide.with(context)
                .load(item.getPicUrl().get(0))
                .apply(requestOptions)
                .into(holder.binding.picBestSeller);

        holder.itemView.setBackgroundColor(selectedPosition == position ? Color.LTGRAY : Color.TRANSPARENT);

        holder.itemView.setOnClickListener(view -> {
            selectedPosition = position;
            notifyDataSetChanged();
            if (listener != null) {
                listener.onProductClick(String.valueOf(selectedPosition));
            }
        });

        if (item.getRating() >= 0) {
            holder.binding.ratingTxt.setText(String.valueOf(item.getRating()));
        } else {
            holder.binding.ratingTxt.setText("N/A");
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderBestSellerBinding binding;

        public ProductViewHolder(@NonNull ViewholderBestSellerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
