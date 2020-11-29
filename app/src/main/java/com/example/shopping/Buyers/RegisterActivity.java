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
import android.widget.Toast;

import com.example.shopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.register_ed_name)
    TextInputEditText nameInput;
    @BindView(R.id.register_ed_phone)
    TextInputEditText phoneInput;
    @BindView(R.id.register_ed_password)
    TextInputEditText passwordInput;
    @BindView(R.id.btn_register)
    MaterialButton RegisterButton;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();

            }
        });

    }


    private void createAccount() {
        String name=nameInput.getText().toString();
        String phone=phoneInput.getText().toString();
        String password=passwordInput.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Please Enter Your Phone number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
        }else if (password.length()<6) {
            Toast.makeText(this, "Password must be at least 6 letters", Toast.LENGTH_SHORT).show();
        }else if (phone.length()<11 | phone.length()>11) {
            Toast.makeText(this, "Please Enter right number", Toast.LENGTH_SHORT).show();
        }

        else{

            ConnectivityManager manager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo=manager.getActiveNetworkInfo();
            if (networkInfo!=null && networkInfo.isConnected()){
                Toast.makeText(this, "Internet is Connected", Toast.LENGTH_SHORT).show();
                progressDialog.setTitle("Creating Account");
                progressDialog.setMessage("Please Wait, While We are Checking the Credentails");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                validatePhoneNumber(name,phone,password);
            }else
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();


            }

    }

    private void validatePhoneNumber(String name, String phone, String password) {
      final   DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.child("users").child(phone).exists())){
                    HashMap<String,Object> userdataMap=new HashMap<>();
                    userdataMap.put("name",name);
                    userdataMap.put("phone",phone);
                    userdataMap.put("password",password);
                    RootRef.child("users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()){
                                   Toast.makeText(RegisterActivity.this, "Congrats, Your Account has been created", Toast.LENGTH_SHORT).show();
                                   progressDialog.dismiss();
                                   Intent intent=new Intent(RegisterActivity.this, login_activity.class);
                                   startActivity(intent);
                               }else {
                                   progressDialog.dismiss();
                                   Toast.makeText(RegisterActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                               }
                                }
                            });
                }else{
                    Toast.makeText(RegisterActivity.this, "This"+phone+" already exists", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Try again using another phone", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}