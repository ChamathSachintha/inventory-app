package com.example.inventoryapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.inventoryapp.R;
import com.example.inventoryapp.models.ApiProduct;
import com.google.android.material.button.MaterialButton;

import java.util.List;

// Adapter for displaying trending market products from external API
public class ApiProductAdapter extends RecyclerView.Adapter<ApiProductAdapter.ViewHolder> {

    // Interface to handle "Add to Inventory" action
    public interface OnAddClickListener {
        void onAdd(ApiProduct product);
    }

    private final List<ApiProduct> list;
    private final OnAddClickListener listener;

    public ApiProductAdapter(List<ApiProduct> list, OnAddClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    // ViewHolder to hold the views for each trending product item
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, price;
        ImageView image;
        MaterialButton btnAdd;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tvTitle);
            price = itemView.findViewById(R.id.tvPrice);
            image = itemView.findViewById(R.id.imgProduct);
            btnAdd = itemView.findViewById(R.id.btnAddApiProduct);
        }
    }

    // Creates new ViewHolders by inflating the item layout
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_api_product, parent, false);

        return new ViewHolder(view);
    }

    // Binds API data to the UI views
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ApiProduct p = list.get(position);

        // Set title and USD price
        holder.title.setText(p.getTitle());
        holder.price.setText("$" + p.getPrice());

        // Load network image using Glide library
        Glide.with(holder.image.getContext())
                .load(p.getImage())
                .placeholder(R.drawable.ic_product_placeholder) // While loading
                .error(R.drawable.ic_product_placeholder) // If fail
                .into(holder.image);

        // Execute callback logic when "Add to Inventory" is clicked
        holder.btnAdd.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAdd(p);
            }
        });
    }

    // Total number of items to display
    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}