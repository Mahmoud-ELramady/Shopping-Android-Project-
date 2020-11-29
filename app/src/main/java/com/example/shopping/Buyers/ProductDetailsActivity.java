package com.example.shopping.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.shopping.Model.Products;
import com.example.shopping.Prevalent.Prevalent;
import com.example.shopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductDetailsActivity extends AppCompatActivity {
    @BindView(R.id.image_product_details) ImageView productImage;

    @BindView(R.id.name_product_details) TextView productName;
    @BindView(R.id.desc_product_details) TextView productDesc;
    @BindView(R.id.price_product_details) TextView productPrice;

    @BindView(R.id.btn_number) ElegantNumberButton ButtonNumber;
    @BindView(R.id.btn_add_to_cart) MaterialButton btnAddCart;


    private  String productId="";

    private  String SaveCurrentDate, SaveCurrentTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        ButterKnife.bind(this);

        productId=getIntent().getStringExtra("pid");

        getProdductDetails(productId);

        btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingToCartList();
            }
        });


    }

    private void addingToCartList() {

        Calendar calForDate=Calendar.getInstance();

        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        SaveCurrentDate =currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        SaveCurrentTime =currentTime.format(calForDate.getTime());

        DatabaseReference cartRef=FirebaseDatabase.getInstance().getReference().child("Cart List");
        HashMap<String,Object> cartMap=new HashMap<>();
        cartMap.put("pid",productId);
        cartMap.put("name",productName.getText().toString());
        cartMap.put("description",productDesc.getText().toString());
        cartMap.put("price",productPrice.getText().toString());
        cartMap.put("quantity",ButtonNumber.getNumber());
        cartMap.put("discount","");
        cartMap.put("date",SaveCurrentDate);
        cartMap.put("time",SaveCurrentTime);
        cartRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products").child(productId).updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                   if (task.isSuccessful()){
                       cartRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                               .child("Products").child(productId).updateChildren(cartMap)
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                  if (task.isSuccessful()){
                                      Toast.makeText(ProductDetailsActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                                      Intent intent=new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                      startActivity(intent);
                                      finish();
                                  }
                                   }
                               });
                   }
                    }
                });

    }

    private void getProdductDetails(String productId) {

        DatabaseReference productRef= FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Products products=snapshot.getValue(Products.class);
                    productName.setText(products.getName());
                    productDesc.setText(products.getDescription());
                    productPrice.setText(products.getPrice());
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}

