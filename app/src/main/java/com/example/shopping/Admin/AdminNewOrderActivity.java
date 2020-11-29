package com.example.shopping.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.shopping.Model.AdminOrders;
import com.example.shopping.R;
import com.example.shopping.ViewHolder.AdminOrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdminNewOrderActivity extends AppCompatActivity {
    @BindView(R.id.rv_orders)  RecyclerView orderList;

    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);
        ButterKnife.bind(this);

        orderRef= FirebaseDatabase.getInstance().getReference().child("Orders");

        orderList.setLayoutManager(new LinearLayoutManager(this));


    }


    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<AdminOrders> options
                =new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(orderRef,AdminOrders.class).build();


        FirebaseRecyclerAdapter<AdminOrders,AdminOrderViewHolder> adapter
                =new FirebaseRecyclerAdapter<AdminOrders, AdminOrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminOrderViewHolder holder, int position, @NonNull AdminOrders model) {

                holder.orderAdminName.setText(model.getName());
                holder.orderAdminPhone.setText(model.getPhone());
                holder.orderAdminaddress.setText(model.getAddress()+", "+model.getCity());
                holder.orderAdminPrice.setText("Total Price: "+model.getTotalAmount() + " LE");
                holder.orderAdminTime.setText(model.getDate()+""+model.getTime());
                holder.btnShowOrderDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String uID=getRef(position).getKey();

                        Intent intent=new Intent(AdminNewOrderActivity.this, AdminUserProducts.class);
                        intent.putExtra("uid",uID);
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CharSequence options []=new CharSequence[]
                                {
                                        "Yes",
                                        "No"
                                };

                        AlertDialog.Builder builder =new AlertDialog.Builder(AdminNewOrderActivity.this);
                        builder.setTitle("Have You Shipped this order Products?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0){

                                    String uID=getRef(position).getKey();

                                    orderRef.child(uID).removeValue();
                                    Toast.makeText(AdminNewOrderActivity.this, "Order Deleted", Toast.LENGTH_SHORT).show();


                                }  if (which==1){

                                                finish();
                                }

                            }
                        });
                        builder.show();




                    }
                });


            }

            @NonNull
            @Override
            public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout,parent,false);
                AdminOrderViewHolder holder=new AdminOrderViewHolder(view);
                return holder;
            }
        };
 orderList.setAdapter(adapter);
 adapter.notifyDataSetChanged();
 adapter.startListening();









    }


}