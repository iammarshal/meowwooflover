package com.example.meowwooflover.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.meowwooflover.Helper.ManagmentCart;
import com.example.meowwooflover.Helper.ChangeNumberItemsListener;
import com.example.meowwooflover.Model.ItemsModel;
import com.example.meowwooflover.databinding.ViewholderCartBinding;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private final ArrayList<ItemsModel> listItemSelected;
    private final ManagmentCart managmentCart;
    private final ChangeNumberItemsListener changeNumberItemsListener;

    public CartAdapter(ArrayList<ItemsModel> listItemSelected, Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.listItemSelected = listItemSelected;
        this.managmentCart = new ManagmentCart(context);
        this.changeNumberItemsListener = changeNumberItemsListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderCartBinding binding;

        public ViewHolder(ViewholderCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderCartBinding binding = ViewholderCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemsModel item = listItemSelected.get(position);

        holder.binding.TitleTxt.setText(item.getTitle());
        holder.binding.feeEachItem.setText("₫" + item.getPrice());
        holder.binding.totalEachItem.setText("₫" + Math.round(item.getNumberInCart() * item.getPrice()));
        holder.binding.numberItemTxt.setText(String.valueOf(item.getNumberInCart()));

        Glide.with(holder.itemView.getContext())
                .load(item.getPicUrl().get(0))
                .apply(new RequestOptions().transform(new CenterCrop()))
                .into(holder.binding.picCart);

        holder.binding.plusCartBtn.setOnClickListener(v ->
                managmentCart.plusItem(listItemSelected, position, () -> {
                    notifyDataSetChanged();
                    if (changeNumberItemsListener != null) {
                        changeNumberItemsListener.onChanged();
                    }
                })
        );

        holder.binding.minusCartBtn.setOnClickListener(v ->
                managmentCart.minusItem(listItemSelected, position, () -> {
                    notifyDataSetChanged();
                    if (changeNumberItemsListener != null) {
                        changeNumberItemsListener.onChanged();
                    }
                })
        );
    }

    @Override
    public int getItemCount() {
        return listItemSelected.size();
    }
}