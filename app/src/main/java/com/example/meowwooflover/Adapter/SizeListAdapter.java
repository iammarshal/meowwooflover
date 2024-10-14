package com.example.meowwooflover.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meowwooflover.R;
import com.example.meowwooflover.databinding.ViewholderSizeBinding;
import java.util.List;

public class SizeListAdapter extends RecyclerView.Adapter<SizeListAdapter.ViewHolder> {
    private final List<String> items;
    private int selectedPosition = -1;
    private int lastSelectedPosition = -1;
    private Context context;

    public SizeListAdapter(List<String> items) {
        this.items = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderSizeBinding binding;

        public ViewHolder(ViewholderSizeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderSizeBinding binding = ViewholderSizeBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.sizeTxt.setText(items.get(position));

        holder.binding.getRoot().setOnClickListener(v -> {
            lastSelectedPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(lastSelectedPosition);
            notifyItemChanged(selectedPosition);
        });

        if (selectedPosition == position) {
            holder.binding.sizeLayout.setBackgroundResource(R.drawable.green_bg3);
            holder.binding.sizeTxt.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.binding.sizeLayout.setBackgroundResource(R.drawable.grey_bg);
            holder.binding.sizeTxt.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}