package com.example.shopping.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shopping.R;
import com.example.shopping.Sellers.SellerProductCategoryActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdminMaintainProductsActivity extends AppCompatActivity {

    @BindView(R.id.maintain_product_name_ed) TextInputEditText maintainName;
    @BindView(R.id.maintain_product_desc_ed) TextInputEditText maintainDescription;
    @BindView(R.id.maintain_price_product_ed) TextInputEditText maintainPrice;
    @BindView(R.id.maintain_product_btn) MaterialButton maintainButton;
    @BindView(R.id.maintain_delete_product_btn) MaterialButton maintainDeleteButton;
    @BindView(R.id.maintain_product_image_item) ImageView maintainImage;

    private  String productId="";
    private DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);
        ButterKnife.bind(this);

        productId=getIntent().getStringExtra("pid");
        productRef= FirebaseDatabase.getInstance().getReference().child("Products").child(productId);

        displaySpecificProductInfo();


        maintainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChanges();
            }
        });

        maintainDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteThisProduct();
            }
        });



    }

    private void deleteThisProduct() {

        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AdminMaintainProductsActivity.this, "Product is deleted", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(AdminMaintainProductsActivity.this, AdminHomeActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        });
    }


    private void displaySpecificProductInfo() {


        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    String name=snapshot.child("name").getValue().toString();
                    String description=snapshot.child("description").getValue().toString();
                    String price=snapshot.child("price").getValue().toString();
                    String image=snapshot.child("image").getValue().toString();

                    maintainName.setText(name);
                    maintainDescription.setText(description);
                    maintainPrice.setText(price);
                    Picasso.get().load(image).into(maintainImage);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void applyChanges() {

        String name=maintainName.getText().toString();
        String description=maintainDescription.getText().toString();
        String price=maintainPrice.getText().toString();


        if (name.equals("")){
            Toast.makeText(this, "write down product name!", Toast.LENGTH_SHORT).show();
        }else if (description.equals("")){
            Toast.makeText(this, "write down product description!", Toast.LENGTH_SHORT).show();
        } else if (price.equals("")){
            Toast.makeText(this, "write down product price", Toast.LENGTH_SHORT).show();
        }else{
            HashMap<String,Object> ProductMap=new HashMap<>();
            ProductMap.put("pid",productId);
            ProductMap.put("description",description);
            ProductMap.put("price",price);
            ProductMap.put("name",name);


            productRef.updateChildren(ProductMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){
                        Toast.makeText(AdminMaintainProductsActivity.this, "changes applied successfully", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(AdminMaintainProductsActivity.this, AdminHomeActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            });
        }


    }

}