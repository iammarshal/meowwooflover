package com.example.meowwooflover.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.meowwooflover.Activity.DetailActivity;
import com.example.meowwooflover.Model.ItemsModel;
import com.example.meowwooflover.databinding.ViewholderBestSellerBinding;
import java.util.List;

public class BestSellerAdapter extends RecyclerView.Adapter<BestSellerAdapter.ViewHolder> {
    private final List<ItemsModel> items;
    private Context context;

    public BestSellerAdapter(List<ItemsModel> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderBestSellerBinding binding;

        public ViewHolder(ViewholderBestSellerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderBestSellerBinding binding = ViewholderBestSellerBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemsModel item = items.get(position);
        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.priceTxt.setText(String.format("₫%.0f", item.getPrice()));
        holder.binding.ratingTxt.setText(String.format("%.1f", item.getRating()));

        RequestOptions requestOptions = new RequestOptions().transform(new CenterCrop());
        Glide.with(holder.itemView.getContext())
                .load(item.getPicUrl().get(0))
                .apply(requestOptions)
                .into(holder.binding.picBestSeller);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
            intent.putExtra("object", item);
            holder.itemView.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }
}