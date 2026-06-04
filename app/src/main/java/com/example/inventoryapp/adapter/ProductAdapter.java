package com.example.inventoryapp.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.bumptech.glide.Glide;
import com.example.inventoryapp.R;
import com.example.inventoryapp.models.Product;

import java.util.List;
import java.util.Locale;

// Adapter class to bind product data to the RecyclerView list
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    // Interface to handle clicks on individual items
    public interface OnItemActionListener {
        void onEdit(Product product);
        void onDelete(Product product);
    }

    private final List<Product> list;
    private final OnItemActionListener listener;

    public ProductAdapter(List<Product> list, OnItemActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    // Holder class to find and store references to item views
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvQty, tvPrice;
        ImageView imgProduct;
        Button btnEdit, btnDelete;
        MaterialCardView cardProduct;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            cardProduct = itemView.findViewById(R.id.cardProduct);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    // Creates new view holders when needed by the RecyclerView
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);

        return new ViewHolder(view);
    }

    // Binds actual product data to the views in a holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Product p = list.get(position);

        // Populate text views with product details
        holder.tvName.setText(p.getName());
        holder.tvQty.setText("Qty: " + p.getQuantity());
        holder.tvPrice.setText(String.format(Locale.US, "Price: LKR %.2f", p.getPrice()));

        // Set card appearance
        holder.cardProduct.setStrokeWidth(0);
        holder.cardProduct.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.surface));

        // Load product image using Glide library for smooth scrolling and caching
        if (p.getImageUri() != null && !p.getImageUri().isEmpty()) {
            Glide.with(holder.imgProduct.getContext())
                    .load(Uri.parse(p.getImageUri()))
                    .placeholder(R.drawable.ic_product_placeholder) // Image while loading
                    .error(R.drawable.ic_product_placeholder) // Image if load fails
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_product_placeholder); // Default if no image
        }

        // Trigger edit callback when button is clicked
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(p);
            }
        });

        // Trigger delete callback when button is clicked
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(p);
            }
        });
    }

    // Returns the total number of products in the list
    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}