package com.example.shopping.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopping.Admin.AdminHomeActivity;
import com.example.shopping.Sellers.SellerProductCategoryActivity;
import com.example.shopping.Model.Users;
import com.example.shopping.Prevalent.Prevalent;
import com.example.shopping.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;


public class login_activity extends AppCompatActivity {
    @BindView(R.id.login_ed_phone) TextInputEditText phoneInput;
    @BindView(R.id.login_ed_password) TextInputEditText passwordInput;
    @BindView(R.id.btn_login) MaterialButton LoginButton;
    @BindView(R.id.login_cb_remember) MaterialCheckBox checkBoxButton;
    @BindView(R.id.login_tv_admin_link) TextView AdminLink;
    @BindView(R.id.login_tv_notadmin_link) TextView NotAdminLink;
    @BindView(R.id.login_tv_link_forget) TextView forgetPassword;
    private ProgressDialog progressDialog;
    private String ParentDbName="users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        ButterKnife.bind(this);
        progressDialog=new ProgressDialog(this);
        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                ParentDbName="Admins";
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                ParentDbName="users";

            }
        });


        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(login_activity.this, ResetPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);
            }
        });





    }

    private void LoginUser() {
        String phone=phoneInput.getText().toString();
        String password=passwordInput.getText().toString();


        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Please Enter Your Phone number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
        }else if (phone.length()<11 | phone.length()>11) {
            Toast.makeText(this, "Please Enter right number", Toast.LENGTH_SHORT).show();
        }
        else{

            ConnectivityManager manager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo=manager.getActiveNetworkInfo();
            if (networkInfo!=null && networkInfo.isConnected()){
                Toast.makeText(this, "Internet is Connected", Toast.LENGTH_SHORT).show();
                progressDialog.setTitle("Login Account");
                progressDialog.setMessage("Please Wait, While We are Checking the Credentails");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                AccessToAccount(phone,password);
            }else {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            }


        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void AccessToAccount(String phone, String password) {

        if (checkBoxButton.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }

        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child(ParentDbName).child(phone).exists())){

                    Users userData=dataSnapshot.child(ParentDbName).child(phone).getValue(Users.class);
                    if (userData.getPhone().equals(phone)){
                        if (userData.getPassword().equals(password)){
                            if (ParentDbName.equals("Admins")){
                                Toast.makeText(login_activity.this, "Welcome Admin , You are Logged in Successfully....", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                                Intent intent=new Intent(login_activity.this, AdminHomeActivity.class);
                                startActivity(intent);
                                finish();
                            }else if (ParentDbName.equals("users")){
                                Toast.makeText(login_activity.this, "Loged in Successfully....", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                                Intent intent=new Intent(login_activity.this, HomeActivity.class);
                                Prevalent.currentOnlineUser=userData;
                                startActivity(intent);
                                finish();
                            }


                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(login_activity.this, "Password in incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {

                    Toast.makeText(login_activity.this, "Account With this " + phone + "numer doesn't exist", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}