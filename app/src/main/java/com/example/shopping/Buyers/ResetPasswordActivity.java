package com.example.shopping.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopping.Prevalent.Prevalent;
import com.example.shopping.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
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

public class ResetPasswordActivity extends AppCompatActivity {
@BindView(R.id.toolbar_reset_password) MaterialToolbar toolbar;
@BindView(R.id.security_number_ed) TextInputEditText numberSecurity;
@BindView(R.id.security_question1_ed) TextInputEditText question1;
@BindView(R.id.security_question2_ed) TextInputEditText question2;
@BindView(R.id.security_verify_btn) MaterialButton verifyButton;
@BindView(R.id.tv_reset)
TextView title;


    private String check="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);

//        toolbar.setTitle("Reset Password");
//        setSupportActionBar(toolbar);


        check=getIntent().getStringExtra("check").toString();
    }


    @Override
    protected void onStart() {
        super.onStart();
        numberSecurity.setVisibility(View.GONE);

        if (check.equals("settings")){

            toolbar.setTitle("Set Questions");
            setSupportActionBar(toolbar);
            title.setText("Please set Answers for the Following Security Questions?");
            verifyButton.setText("Set");
            displayAnswersBefore();

            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 setAnswers();
                }
            });

        }

        if (check.equals("login")){
            toolbar.setTitle("Reset Password");
            setSupportActionBar(toolbar);
            numberSecurity.setVisibility(View.VISIBLE);

            verifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    verifyUser();

                }
            });


        }


    }

    private void setAnswers() {
        String answer1=question1.getText().toString().toLowerCase();
        String answer2=question2.getText().toString().toLowerCase();

        if (answer1.equals("")  &&   answer2.equals("")){
            Toast.makeText(ResetPasswordActivity.this, "Please answer both question", Toast.LENGTH_SHORT).show();
        }else {
            DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                    .child("users").child(Prevalent.currentOnlineUser.getPhone());

            HashMap<String,Object> userdataMap=new HashMap<>();
            userdataMap.put("answer1",answer1);
            userdataMap.put("answer2",answer2);

            ref.child("Security questions").updateChildren(userdataMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ResetPasswordActivity.this, "you have set Security questions successfully", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(ResetPasswordActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        }
                    });


        }
    }



    private void displayAnswersBefore(){

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                .child("users").child(Prevalent.currentOnlineUser.getPhone());

        ref.child("Security questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                   String answer1=snapshot.child("answer1").getValue().toString();
                   String answer2=snapshot.child("answer2").getValue().toString();

                   question1.setText(answer1);
                   question2.setText(answer2);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void verifyUser(){

        String phone=numberSecurity.getText().toString();
        String an1=question1.getText().toString().toLowerCase();
        String an2=question2.getText().toString().toLowerCase();

        if (!phone.equals("") && !an1.equals("") && !an2.equals("")){


            DatabaseReference ref= FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(phone);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()){
                        String nPhone=snapshot.child("phone").getValue().toString();


                        if (snapshot.hasChild("Security questions")){
                            String answer1=snapshot.child("Security questions").child("answer1").getValue().toString();
                            String answer2=snapshot.child("Security questions").child("answer2").getValue().toString();

                            if (!an1.equals(answer1)){
                                Toast.makeText(ResetPasswordActivity.this, "your 1st answer is wrong", Toast.LENGTH_SHORT).show();
                            }else if (!an2.equals(answer2)){
                                Toast.makeText(ResetPasswordActivity.this, "your 2st answer is wrong", Toast.LENGTH_SHORT).show();
                            }else {

                                AlertDialog.Builder builder=new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("New password");
                                TextInputEditText newPassword=new TextInputEditText(ResetPasswordActivity.this);
                                newPassword.setHint("Write new Password");
                                builder.setView(newPassword);

                                builder.setPositiveButton("change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!newPassword.getText().toString().equals("")){
                                            ref.child("password").setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                Toast.makeText(ResetPasswordActivity.this, "Password is Changed", Toast.LENGTH_SHORT).show();
                                                                Intent intent=new Intent(ResetPasswordActivity.this, login_activity.class);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    });

                                        }
                                    }
                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                    }
                                });
                                builder.show();


                            }
                        }


                        else {
                            Toast.makeText(ResetPasswordActivity.this, "you have not set the Security question", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(ResetPasswordActivity.this, "This phone number not exist", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }else{
            if (TextUtils.isEmpty(phone)){
                Toast.makeText(this, "You should enter phone number", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "You Should answer Security Questions", Toast.LENGTH_SHORT).show();
            }
        }


    }


}