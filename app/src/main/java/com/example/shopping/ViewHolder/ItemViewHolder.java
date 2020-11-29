package com.example.shopping.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Interface.ItemClickListner;
import com.example.shopping.R;


public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName , txtProductDesc, txtProductPrice, txtProductState;
    public ImageView ProductImag;
    public ItemClickListner listner;

    public ItemViewHolder(@NonNull View itemView)
    {
        super(itemView);
        txtProductName=itemView.findViewById(R.id.seller_name_item);
        ProductImag=itemView.findViewById(R.id.seller_image_item);
        txtProductDesc=itemView.findViewById(R.id.seller_desc_item);
        txtProductPrice=itemView.findViewById(R.id.seller_price_item);
        txtProductState=itemView.findViewById(R.id.seller_status_item);
    }



    @Override
    public void onClick(View v) {
        listner.onClick(v,getAdapterPosition(),false);
    }

    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner=listner;
    }

}

