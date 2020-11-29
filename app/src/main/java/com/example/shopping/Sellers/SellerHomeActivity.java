package com.example.shopping.Sellers;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.shopping.Admin.AdminCheckNewProductsActivity;
import com.example.shopping.Buyers.HomeActivity;
import com.example.shopping.Buyers.MainActivity;
import com.example.shopping.Model.Products;
import com.example.shopping.R;
import com.example.shopping.ViewHolder.ItemViewHolder;
import com.example.shopping.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.paperdb.Paper;


public class SellerHomeActivity extends AppCompatActivity {

    BottomNavigationView navViewBottom;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);

        Toolbar toolbarSeller = findViewById(R.id.toolbar_seller);
        toolbarSeller.setTitle("Un/Verified Products");
        setSupportActionBar(toolbarSeller);

       navViewBottom = findViewById(R.id.nav_seller_view);
        recyclerView=findViewById(R.id.seller_home_recycler);

        unverifiedProducts= FirebaseDatabase.getInstance().getReference().child("Products");

        recyclerView.setHasFixedSize(true);
        layoutManager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);


        navViewBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                return true;


                case R.id.navigation_add:
                    Intent intentAdd=new Intent(SellerHomeActivity.this, SellerProductCategoryActivity.class);
                    startActivity(intentAdd);

                return true;

                case R.id.navigation_logout:
                    FirebaseAuth mAuth;
                    mAuth=FirebaseAuth.getInstance();
                    mAuth.signOut();

                    Intent intentMain=new Intent(SellerHomeActivity.this, MainActivity.class);
                    intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentMain);
                   finish();
                    return true;

                     default:
                return false;
            }
        }
    });




    }

    @Override
    protected void onStart() {
        super.onStart();



        FirebaseRecyclerOptions<Products> options=new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(unverifiedProducts.orderByChild("sid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()),Products.class)
                .build();


        FirebaseRecyclerAdapter<Products, ItemViewHolder> adapter=new FirebaseRecyclerAdapter<Products, ItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Products model) {
                holder.txtProductName.setText(model.getName());
                holder.txtProductDesc.setText(model.getDescription());
                holder.txtProductState.setText(model.getProductState());
                holder.txtProductPrice.setText("Price= " + model.getPrice()+" LE");
                Picasso.get().load(model.getImage()).into(holder.ProductImag );

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String ProductId=model.getPid();

                        CharSequence options[]=new CharSequence[]{
                                "Yes",
                                "No"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(SellerHomeActivity.this);
                        builder.setTitle("Do You Want to delete this Product, Are You Sure?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {

                                if (position==0){
                                    deleteProduct(ProductId);
                                }
                                if (position==1){

                                }

                            }
                        });
                        builder.show();
                    }
                });

            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_item_view,parent,false);
                ItemViewHolder holder=new ItemViewHolder(view);
                return holder;

            }
        };
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();















    }

    private void deleteProduct(String productId) {

        unverifiedProducts.child(productId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SellerHomeActivity.this, "That Item has been deleted", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}