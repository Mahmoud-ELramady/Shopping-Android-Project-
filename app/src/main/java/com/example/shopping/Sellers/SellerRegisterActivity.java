package com.example.shopping.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.example.shopping.Buyers.MainActivity;
import com.example.shopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SellerRegisterActivity extends AppCompatActivity {

    @BindView(R.id.seller_name_register_ed) TextInputEditText nameRegisterSeller;
    @BindView(R.id.seller_phone_register_ed) TextInputEditText phoneRegisterSeller;
    @BindView(R.id.seller_email_register_ed) TextInputEditText emailRegisterSeller;
    @BindView(R.id.seller_password_register_ed) TextInputEditText passwordRegisterSeller;
    @BindView(R.id.seller_address_register_ed) TextInputEditText addressRegisterSeller;


    @BindView(R.id.btn_seller_register)
    MaterialButton RegisterButton;
    @BindView(R.id.tv_seller_have_account)
    TextView haveAccountSeller;

    FirebaseAuth mAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_register);
        ButterKnife.bind(this);


        mAuth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerSeller();
            }
        });

        haveAccountSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SellerRegisterActivity.this,SellerLoginActivity.class);
                startActivity(intent);
            }
        });



    }

    private void registerSeller() {
        String name=nameRegisterSeller.getText().toString();
        String phone=phoneRegisterSeller.getText().toString();
        String email=emailRegisterSeller.getText().toString();
        String password=passwordRegisterSeller.getText().toString();
        String address=addressRegisterSeller.getText().toString();

        if (!name.equals("") && !phone.equals("") && !email.equals("") && !password.equals("") && !address.equals("")){

            progressDialog.setTitle("Creating Seller Account");
            progressDialog.setMessage("Please Wait, While We are Checking the Credentails");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                DatabaseReference rootRef;
                                rootRef= FirebaseDatabase.getInstance().getReference();
                                String sid=mAuth.getCurrentUser().getUid();

                                HashMap<String,Object> sellerMap=new HashMap<>();

                                sellerMap.put("sid",sid);
                                sellerMap.put("name",name);
                                sellerMap.put("phone",phone);
                                sellerMap.put("email",email);
                                sellerMap.put("address",address);
                                sellerMap.put("password",password);

                                rootRef.child("Sellers").child(sid).updateChildren(sellerMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    progressDialog.dismiss();
                                                    Toast.makeText(SellerRegisterActivity.this, "you are Registered Successfully", Toast.LENGTH_SHORT).show();

                                                    Intent intent=new Intent(SellerRegisterActivity.this, SellerHomeActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }else {
                                                    String message=task.getException().getMessage();
                                                    Toast.makeText(SellerRegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                            }
                        }
                    });

        }else{
            Toast.makeText(this, "Please Complete the Registration", Toast.LENGTH_SHORT).show();
        }

    }
}