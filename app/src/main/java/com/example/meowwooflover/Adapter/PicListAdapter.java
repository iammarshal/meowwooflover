package com.example.meowwooflover.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.meowwooflover.R;
import com.example.meowwooflover.databinding.ViewholderPicListBinding;
import java.util.List;

public class PicListAdapter extends RecyclerView.Adapter<PicListAdapter.ViewHolder> {
    private final List<String> items;
    private final ImageView picMain;
    private int selectedPosition = -1;
    private int lastSelectedPosition = -1;
    private Context context;

    public PicListAdapter(List<String> items, ImageView picMain) {
        this.items = items;
        this.picMain = picMain;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderPicListBinding binding;

        public ViewHolder(ViewholderPicListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderPicListBinding binding = ViewholderPicListBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(holder.itemView.getContext())
                .load(items.get(position))
                .into(holder.binding.picList);

        holder.binding.getRoot().setOnClickListener(v -> {
            lastSelectedPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(lastSelectedPosition);
            notifyItemChanged(selectedPosition);

            Glide.with(holder.itemView.getContext())
                    .load(items.get(position))
                    .into(picMain);
        });

        if (selectedPosition == position) {
            holder.binding.picLayout.setBackgroundResource(R.drawable.grey_bg_selected);
        } else {
            holder.binding.picLayout.setBackgroundResource(R.drawable.grey_bg);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}