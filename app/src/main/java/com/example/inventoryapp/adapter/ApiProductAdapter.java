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

import java.util.List;

public class ApiProductAdapter extends RecyclerView.Adapter<ApiProductAdapter.ViewHolder> {

    List<ApiProduct> list;

    public ApiProductAdapter(List<ApiProduct> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, price;
        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tvTitle);
            price = itemView.findViewById(R.id.tvPrice);
            image = itemView.findViewById(R.id.imgProduct);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_api_product, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ApiProduct p = list.get(position);

        holder.title.setText(p.getTitle());
        holder.price.setText("$ " + p.getPrice());

        Glide.with(holder.image.getContext())
                .load(p.getImage())
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}