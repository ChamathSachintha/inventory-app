package com.example.inventoryapp.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Color;
import android.util.TypedValue;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.bumptech.glide.Glide;
import com.example.inventoryapp.R;
import com.example.inventoryapp.models.Product;

import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Product p = list.get(position);

        holder.tvName.setText(p.getName());
        holder.tvQty.setText("Qty: " + p.getQuantity());
        holder.tvPrice.setText(String.format(Locale.US, "Price: LKR %.2f", p.getPrice()));

        // LOW STOCK HIGHLIGHT
        if (p.getQuantity() < 5) {
            holder.cardProduct.setStrokeColor(Color.RED);
            holder.cardProduct.setStrokeWidth(4);
            holder.cardProduct.setCardBackgroundColor(Color.parseColor("#FFF0F0")); // Very light red
        } else {
            holder.cardProduct.setStrokeWidth(0);
            holder.cardProduct.setStrokeColor(Color.TRANSPARENT);
            
            // DYNAMICALLY GET SURFACE COLOR FROM THEME
            TypedValue typedValue = new TypedValue();
            holder.itemView.getContext().getTheme().resolveAttribute(com.google.android.material.R.attr.colorSurface, typedValue, true);
            holder.cardProduct.setCardBackgroundColor(typedValue.data);
        }

        // IMAGE (safe + modern)
        if (p.getImageUri() != null && !p.getImageUri().isEmpty()) {
            Glide.with(holder.imgProduct.getContext())
                    .load(Uri.parse(p.getImageUri()))
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.mipmap.ic_launcher);
        }

        // EDIT
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(p);
            }
        });

        // DELETE
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}