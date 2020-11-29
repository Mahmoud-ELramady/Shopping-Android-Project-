package com.example.shopping.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;


import com.example.shopping.Model.Users;
import com.example.shopping.Prevalent.Prevalent;
import com.example.shopping.R;
import com.example.shopping.Sellers.SellerHomeActivity;
import com.example.shopping.Sellers.SellerRegisterActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.start_btn_buyer) MaterialButton BuyerButton;
    @BindView(R.id.start_btn_Seller) MaterialButton SellerButton;
    FirebaseUser currentUser;
    private final static int REQ_CODE_EXTERNAL = 5;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_CODE_EXTERNAL);
        }

        ButterKnife.bind(this);
        Paper.init(this);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog=new ProgressDialog(this);




        BuyerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, StartUserActivity.class);
                startActivity(intent);
            }
        });


        SellerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, SellerRegisterActivity.class);
                startActivity(intent);
            }
        });




        String userPhoneKey=Paper.book().read(Prevalent.UserPhoneKey);
        String userPasswordKey=Paper.book().read(Prevalent.UserPasswordKey);
        if (userPhoneKey != "" && userPasswordKey!=""){

            if (!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPasswordKey)){



                ConnectivityManager manager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo=manager.getActiveNetworkInfo();
                if (networkInfo!=null && networkInfo.isConnected()) {


                    AllowAccess(userPhoneKey, userPasswordKey);


                    progressDialog.setTitle("Already logged in");
                    progressDialog.setMessage("Please Wait.........");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                }else {
                    Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();

                }

            }




        }

    }

    private void AllowAccess(String phone, String password) {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("users").child(phone).exists())){

                    Users userData=dataSnapshot.child("users").child(phone).getValue(Users.class);
                    if (userData.getPhone().equals(phone)){
                        if (userData.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this, "You are already logged in....", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            Intent intent=new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUser=userData;
                            startActivity(intent);
                            finish();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Password in incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {

                    Toast.makeText(MainActivity.this, "Account With this " + phone + "numer doesn't exist", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


//
//    private void sendtoHome() {
//        if (currentUser != null) {
//            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }


    @Override
    protected void onStart() {
        super.onStart();
        currentUser=FirebaseAuth.getInstance().getCurrentUser() ;
        if (currentUser!=null){
            Intent intent=new Intent(MainActivity.this, SellerHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission is allowed", Toast.LENGTH_SHORT).show();
                }
                return;

        }

    }

}