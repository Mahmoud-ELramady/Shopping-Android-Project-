package com.example.shopping.Buyers.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopping.Buyers.HomeActivity;
import com.example.shopping.Buyers.MainActivity;
import com.example.shopping.Prevalent.Prevalent;
import com.example.shopping.R;
import com.example.shopping.Buyers.ResetPasswordActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {
    private static final int RESULT_SELECT_IMG = 1;
    CircleImageView ImageSetting;
TextView ChangeProfile;
TextInputEditText FullNameSetting , PhoneSetting, AddressSetting;
MaterialButton SaveSetting,SecurityButton;
private Uri imageUri;
private String myUrl= "" ;
private StorageTask uploadTask;
private StorageReference storageReferenceImageProfile;
private String Checker= "" ;
private ProgressDialog progressDialog;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_setting, container, false);
         ImageSetting=root.findViewById(R.id.profile_image_setting);
         ChangeProfile =root.findViewById(R.id.change_profile_setting);
         FullNameSetting=root.findViewById(R.id.profile_name_setting_ed);
         PhoneSetting=root.findViewById(R.id.profile_phone_setting_ed);
        AddressSetting=root.findViewById(R.id.profile_address_setting_ed);
        SecurityButton=root.findViewById(R.id.btn_security_questions);
        SaveSetting=root.findViewById(R.id.btn_save_settings);

        storageReferenceImageProfile= FirebaseStorage.getInstance().getReference().child("profile pictures");

        progressDialog=new ProgressDialog(getContext());

        displayUserInfo(ImageSetting,FullNameSetting,PhoneSetting,AddressSetting);


        SaveSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Checker.equals("clicked")){
                    saveUserInfo();

                }else{
                    updateUserInfo();
                }
            }
        });


        SecurityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), ResetPasswordActivity.class);
                intent.putExtra("check","settings");
                startActivity(intent);

            }
        });


        ChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Checker="clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getActivity(),SettingFragment.this);

            }

        });



        return root;

    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK ){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
                imageUri=result.getUri();
                ImageSetting.setImageURI(imageUri);
        }else{
            Toast.makeText(getContext(), "Error, Try again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(),SettingFragment.class));
            getActivity().finish();

        }
    }







    private void updateUserInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        HashMap<String,Object> userMap=new HashMap<>();
        userMap.put("name",FullNameSetting.getText().toString());
        userMap.put("phone",PhoneSetting.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(getContext(), HomeActivity.class));
        getActivity().finish();
        Toast.makeText(getContext(), "Profile info update successfully", Toast.LENGTH_SHORT).show();
    }

    private void saveUserInfo() {

        if (TextUtils.isEmpty(FullNameSetting.getText().toString())){
            Toast.makeText(getContext(), "name is mandatory", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(AddressSetting.getText().toString()))
        {
            Toast.makeText(getContext(), "Address is mandatory", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(PhoneSetting.getText().toString()))
        {
            Toast.makeText(getContext(), "Phone is mandatory", Toast.LENGTH_SHORT).show();
        }else if(Checker.equals("clicked")) {
            uploadImage();
        }


    }

    private void uploadImage() {
        progressDialog.setTitle("Login Account");
        progressDialog.setMessage("Please Wait, While We are updating your account setting");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri!=null){
            StorageReference fileRef=storageReferenceImageProfile
                    .child(Prevalent.currentOnlineUser.getPhone()+".jpg");
            uploadTask=fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        myUrl=downloadUri.toString();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
                        HashMap<String,Object> userMap=new HashMap<>();
                        userMap.put("name",FullNameSetting.getText().toString());
                        userMap.put("phone",PhoneSetting.getText().toString());
                        userMap.put("address",AddressSetting.getText().toString());
                        userMap.put("image",myUrl);
                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);
                        progressDialog.dismiss();
                        startActivity(new Intent(getContext(), MainActivity.class));
                        getActivity().finish();
                        Toast.makeText(getContext(), "Profile info update successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        progressDialog.dismiss();
                        String ErrorMassege=task.getException().getMessage();
                        Toast.makeText(getContext(), ErrorMassege, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else{
            Toast.makeText(getContext(), "Image is not Selected", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    private void displayUserInfo(CircleImageView imageSetting, TextInputEditText fullNameSetting, TextInputEditText phoneSetting, TextInputEditText addressSetting) {
        DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("users").child(Prevalent.currentOnlineUser.getPhone());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("image").exists()){
                        String image=snapshot.child("image").getValue().toString();
                        String name=snapshot.child("name").getValue().toString();
                        String phone=snapshot.child("phone").getValue().toString();
                        String address=snapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(imageSetting);
                        fullNameSetting.setText(name);
                        addressSetting.setText(address);
                        phoneSetting.setText(phone);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }






//    private void selectImageFromGallary()
//    {
////        Create intent to Open Image
//        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//        startActivityForResult(galleryIntent, RESULT_SELECT_IMG);
//
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent,"Select Picture"), RESULT_SELECT_IMG);
//
//    }















    }