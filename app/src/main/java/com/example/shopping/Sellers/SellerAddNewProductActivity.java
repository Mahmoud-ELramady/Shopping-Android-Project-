package com.example.shopping.Sellers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shopping.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SellerAddNewProductActivity extends AppCompatActivity {
    @BindView(R.id.add_product_toolbar) Toolbar addProductToolbar;
    @BindView(R.id.select_image_product) ImageView ProductImage;
    @BindView(R.id.product_name_ed) TextInputEditText ProductName;
    @BindView(R.id.product_desc_ed) TextInputEditText ProductDesc;
    @BindView(R.id.profile_price_setting_ed) TextInputEditText ProductPrice;
    @BindView(R.id.btn_add_product) MaterialButton BtnAddProduct;

    private  String CategoryName ,  Pname , Pdesc , Price , saveCurrentDate ,saveCurrentTime , ProductRandomKey
            ,DownLoadImageUri;
    private static final int GalleryPick=1;
    private Uri ImageUri;
    private StorageReference ProductImageRef;
    private DatabaseReference productRef,sellerRef;
    private ProgressDialog progressDialog;

    private String sellerName,sellerEmail,sellerPhone,sellerAddress,sid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_new_product);

        ButterKnife.bind(this);
        progressDialog=new ProgressDialog(this);

        setSupportActionBar(addProductToolbar);
        getSupportActionBar().setTitle("Add Product");

          productRef=FirebaseDatabase.getInstance().getReference().child("Products");
        sellerRef= FirebaseDatabase.getInstance().getReference().child("Sellers");

        CategoryName=getIntent().getExtras().get("category").toString();

        ProductImageRef= FirebaseStorage.getInstance().getReference().child("Product Images");

        ProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        BtnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });

        sellerRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            sellerName=snapshot.child("name").getValue().toString();
                            sellerEmail=snapshot.child("email").getValue().toString();
                            sellerPhone=snapshot.child("phone").getValue().toString();
                            sellerAddress=snapshot.child("address").getValue().toString();
                            sid=snapshot.child("sid").getValue().toString();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void ValidateProductData() {
        Pname=ProductName.getText().toString();
        Pdesc=ProductDesc.getText().toString();
        Price=ProductPrice.getText().toString();

        if (ImageUri==null){
            Toast.makeText(this, "Please Choose Image", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(Pname)){
            Toast.makeText(this, "Please Enter Product name", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(Pdesc)){
            Toast.makeText(this, "Please Enter The Description", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(Price)){
            Toast.makeText(this, "Please Enter The Price", Toast.LENGTH_SHORT).show();
        }else{
           StoreProductInfo();
        }

    }

    private void StoreProductInfo() {

        progressDialog.setTitle("Adding New Product");
        progressDialog.setMessage("Dear Seller, While We are adding the new product");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());
        ProductRandomKey=saveCurrentDate + saveCurrentTime;


        StorageReference filePath=ProductImageRef.child(ImageUri.getLastPathSegment()+ProductRandomKey+".jpg");
        UploadTask uploadTask =filePath.putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            String message=e.toString();
                Toast.makeText(SellerAddNewProductActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SellerAddNewProductActivity.this, "Image Uploaded Successful", Toast.LENGTH_SHORT).show();
                Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        DownLoadImageUri=filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()){

                            DownLoadImageUri=task.getResult().toString();
                            Toast.makeText(SellerAddNewProductActivity.this, "got The Product Image Uri Successfully", Toast.LENGTH_SHORT).show();
                            saveProductInfotoDataBase();
                        }
                    }
                });
            }
        });
    }

    private void saveProductInfotoDataBase() {

        HashMap<String,Object> ProductMap=new HashMap<>();
        ProductMap.put("pid",ProductRandomKey);
        ProductMap.put("date",saveCurrentDate);
        ProductMap.put("time",saveCurrentTime);
        ProductMap.put("description",Pdesc);
        ProductMap.put("image",DownLoadImageUri);
        ProductMap.put("price",Price);
        ProductMap.put("name",Pname);
        ProductMap.put("category",CategoryName);

        ProductMap.put("productState","Not Approved");
        ProductMap.put("sellerName",sellerName);
        ProductMap.put("sellerAddress",sellerAddress);
        ProductMap.put("sellerPhone",sellerPhone);
        ProductMap.put("sellerEmail",sellerEmail);
        ProductMap.put("sid",sid);


        productRef.child(ProductRandomKey).updateChildren(ProductMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            Intent intent=new Intent(SellerAddNewProductActivity.this, SellerHomeActivity.class);
                            startActivity(intent);
                            finish();
                            progressDialog.dismiss();
                            Toast.makeText(SellerAddNewProductActivity.this, "Product is added successfully", Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                            String message=task.getException().toString();
                            Toast.makeText(SellerAddNewProductActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();

                        }



                    }
                });

    }

    private void openGallery() {
        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GalleryPick && resultCode==RESULT_OK && data !=null)
        {
                ImageUri=data.getData();
                ProductImage.setImageURI(ImageUri);
        }
    }

    private void SellerInformation(){


    }
}