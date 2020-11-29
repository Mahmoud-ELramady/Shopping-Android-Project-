package com.example.shopping.Buyers.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.shopping.Admin.AdminMaintainProductsActivity;
import com.example.shopping.Buyers.HomeActivity;
import com.example.shopping.Buyers.cart.CartFragment;
import com.example.shopping.Model.Products;
import com.example.shopping.Buyers.ProductDetailsActivity;
import com.example.shopping.R;
import com.example.shopping.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import static android.content.Context.MODE_PRIVATE;
import static com.example.shopping.Admin.AdminHomeActivity.MY_PREFS_NAME;


public class HomeFragment extends Fragment {
    private DatabaseReference productRef;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager manager;
    public static String type2 = "";


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        SharedPreferences preferences = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        type2 = preferences.getString("ali", "no");


        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        recyclerView = root.findViewById(R.id.rv_products);

        recyclerView.setHasFixedSize(true);
        manager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(manager);






        return root;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        inflater.inflate(R.menu.search, menu);


        MenuItem searchItem = menu.findItem(R.id.action_search);


        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint("search here");

        searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }

        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                onStart();
                return false;
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(productRef.orderByChild("productState").equalTo("Approved"), Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter
                = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
                holder.txtProductName.setText(model.getName());
                holder.txtProductDesc.setText(model.getDescription());
                holder.txtProductPrice.setText("Price= " + model.getPrice() + " LE");
                Picasso.get().load(model.getImage()).into(holder.ProductImag);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (type2.equals("ali")) {

                            Intent intent = new Intent(getActivity(), AdminMaintainProductsActivity.class);
                            intent.putExtra("pid", model.getPid());
                            startActivity(intent);
                            Toast.makeText(getActivity(), type2, Toast.LENGTH_SHORT).show();
                            SharedPreferences preferences = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                            preferences.edit().clear().commit();

                        } else {
                            Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                            intent.putExtra("pid", model.getPid());
                            startActivity(intent);
                        }


                    }
                });


            }


            @Override
            public Products getItem(int position) {
                return super.getItem(getItemCount()-1-position);
            }


            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout, parent, false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;

            }

        };

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
//        recyclerView.smoothScrollToPosition(adapter.getItemCount());
        adapter.startListening();

    }


    private void firebaseSearch(String searchText) {

        String query = searchText;

        Query firebaseSearchQuery = productRef.orderByChild("name").startAt(searchText);

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(firebaseSearchQuery, Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter
                = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
                holder.txtProductName.setText(model.getName());
                holder.txtProductDesc.setText(model.getDescription());
                holder.txtProductPrice.setText("Price= " + model.getPrice() + " LE");
                Picasso.get().load(model.getImage()).into(holder.ProductImag);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (type2.equals("ali")) {

                            Intent intent = new Intent(getActivity(), AdminMaintainProductsActivity.class);
                            intent.putExtra("pid", model.getPid());
                            startActivity(intent);
                            Toast.makeText(getActivity(), type2, Toast.LENGTH_SHORT).show();
                            SharedPreferences preferences = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                            preferences.edit().clear().commit();

                        } else {
                            Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                            intent.putExtra("pid", model.getPid());
                            startActivity(intent);
                        }
                    }
                });
            }

//            @Override
//            public Products getItem(int position) {
//                return super.getItem(getItemCount()-1-position);
//            }


            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_layout, parent, false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;

            }

        };

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.startListening();

    }









}