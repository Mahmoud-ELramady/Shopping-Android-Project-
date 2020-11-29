package com.example.shopping.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.shopping.Buyers.cart.CartFragment;
import com.example.shopping.Prevalent.Prevalent;
import com.example.shopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmFinalOrder extends AppCompatActivity {

    @BindView(R.id.shipment_name_ed) TextInputEditText nameShipment;
    @BindView(R.id.phone_shipment_ed) TextInputEditText phoneShipment;
    @BindView(R.id.address_shipment_ed) TextInputEditText addressShipment;
    @BindView(R.id.city_shipment_ed) TextInputEditText cityShipment;
    @BindView(R.id.btn_confirm_shipment) MaterialButton confirmShipment;

    private String totalAmount="";
    private  String SaveCurrentDate, SaveCurrentTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);
        ButterKnife.bind(this);

        totalAmount=getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Price = " + totalAmount, Toast.LENGTH_SHORT).show();


        confirmShipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });

    }

    private void check() {
        if (TextUtils.isEmpty(nameShipment.getText().toString())){
            Toast.makeText(this, "Please Provide Your name", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(phoneShipment.getText().toString())){
            Toast.makeText(this, "Please Provide Your Phone number", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(addressShipment.getText().toString())){
            Toast.makeText(this, "Please Provide Your Address", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(cityShipment.getText().toString())){
            Toast.makeText(this, "Please Provide Your city", Toast.LENGTH_SHORT).show();

        }else{
            confirmOrder();
        }
    }



    private void confirmOrder() {
        Calendar calForDate=Calendar.getInstance();

        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        SaveCurrentDate =currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        SaveCurrentTime =currentTime.format(calForDate.getTime());

        DatabaseReference orderRef= FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String,Object> orderMap=new HashMap<>();
        orderMap.put("totalAmount",totalAmount);
        orderMap.put("name",nameShipment.getText().toString());
        orderMap.put("phone",phoneShipment.getText().toString());
        orderMap.put("address",addressShipment.getText().toString());
        orderMap.put("city",cityShipment.getText().toString());
        orderMap.put("date",SaveCurrentDate);
        orderMap.put("time",SaveCurrentTime);
        orderMap.put("state","not shipped");
        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ConfirmFinalOrder.this, "your final order has been placed successfully.", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(ConfirmFinalOrder.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });

                }
            }
        });











    }
}