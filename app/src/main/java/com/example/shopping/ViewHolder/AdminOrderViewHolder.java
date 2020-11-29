package com.example.shopping.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Interface.ItemClickListner;
import com.example.shopping.R;
import com.google.android.material.button.MaterialButton;

public class AdminOrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView orderAdminName, orderAdminPhone, orderAdminPrice, orderAdminaddress,orderAdminTime;
    public MaterialButton btnShowOrderDetails;

    private ItemClickListner clickListner;


    public AdminOrderViewHolder(@NonNull View itemView) {
        super(itemView);
        orderAdminName=itemView.findViewById(R.id.item_order_user_name_admin);
        orderAdminPhone=itemView.findViewById(R.id.item_order_phone_admin);
        orderAdminaddress=itemView.findViewById(R.id.item_order_address_admin);
        orderAdminPrice=itemView.findViewById(R.id.item_order_price_admin);
        orderAdminTime=itemView.findViewById(R.id.item_order_time_admin);
        btnShowOrderDetails=itemView.findViewById(R.id.btn_show_order_details);


    }

    @Override
    public void onClick(View v) {
        clickListner.onClick(v,getAdapterPosition(),false);


    }

    public void setItemClickListner(ItemClickListner clickListner) {
        this.clickListner = clickListner;
    }
}
