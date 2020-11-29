package com.example.shopping.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.shopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SellerLoginActivity extends AppCompatActivity {

    @BindView(R.id.seller_email_login_ed)  TextInputEditText emailLoginSeller;
    @BindView(R.id.seller_password_login_ed) TextInputEditText passwordLoginSeller;
    @BindView(R.id.btn_seller_login)
    MaterialButton LoginButton;

    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSeller();
            }
        });



    }

    private void loginSeller() {
        String email=emailLoginSeller.getText().toString();
        String password=passwordLoginSeller.getText().toString();

        if (!email.equals("") && !password.equals("")) {

            progressDialog.setTitle("Seller Account Login");
            progressDialog.setMessage("Please Wait, While We are Checking the Credentails");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(SellerLoginActivity.this, "you are Logged in Successfully", Toast.LENGTH_SHORT).show();

                                Intent intent=new Intent(SellerLoginActivity.this, SellerHomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }else {
                                String ErrorMassege=task.getException().getMessage();
                                Toast.makeText(SellerLoginActivity.this, "Error:"+ErrorMassege, Toast.LENGTH_SHORT).show();

                            }
                            progressDialog.dismiss();
                        }
                    });



        }else {
            Toast.makeText(this, "Enter The Empty Fields", Toast.LENGTH_SHORT).show();
        }


    }
}