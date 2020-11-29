package com.example.shopping.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Interface.ItemClickListner;
import com.example.shopping.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView CartProductName, CartProductQuantity, CartProductPrice;
    public ItemClickListner itemClickListner;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        CartProductName =itemView.findViewById(R.id.name_item_cart);
        CartProductQuantity =itemView.findViewById(R.id.quantity_item_cart);
        CartProductPrice =itemView.findViewById(R.id.price_item_cart);
    }

    @Override
    public void onClick(View v) {
            itemClickListner.onClick(v,getAdapterPosition(),false);

    }


    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }
}
