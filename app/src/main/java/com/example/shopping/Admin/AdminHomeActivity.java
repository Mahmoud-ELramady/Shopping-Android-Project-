package com.example.shopping.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.shopping.Buyers.HomeActivity;
import com.example.shopping.Buyers.MainActivity;
import com.example.shopping.R;
import com.example.shopping.Sellers.SellerProductCategoryActivity;
import com.google.android.material.button.MaterialButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class AdminHomeActivity extends AppCompatActivity {
    @BindView(R.id.btn_logout_admin) MaterialButton btnLogoutAdmin;
    @BindView(R.id.maintain_btn) MaterialButton MaintainProduct;
    @BindView(R.id.btn_check_orders) MaterialButton btnCheckOrders;
    @BindView(R.id.btn_check_approve_orders) MaterialButton btnCheckApprovedOrders;

    public static final String  MY_PREFS_NAME="MyPrefsFile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        ButterKnife.bind(this);



        MaintainProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor=getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE).edit();
                editor.putString("ali","ali");
                editor.apply();

                Intent intent =new Intent(AdminHomeActivity.this, HomeActivity.class);
                intent.putExtra("Admin","Admin");
                startActivity(intent);
            }
        });


        btnLogoutAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().destroy();
                Intent intent=new Intent(AdminHomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnCheckOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminHomeActivity.this, AdminNewOrderActivity.class);
                startActivity(intent);
            }
        });


        btnCheckApprovedOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminHomeActivity.this, AdminCheckNewProductsActivity.class);
                startActivity(intent);
                
            }
        });




    }
}