package com.example.interiodesign;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;


public class CartFragment extends Fragment {
    private RecyclerView cartRecyclerView;
    private FirebaseData firebaseData;
    private CardView cartCardView;
    private TextView cartTotal;

    public CartFragment() {
        // Required empty public constructor
    }
    private Long calculateCartTotalPrice(ArrayList<Product> products){
        Long cartTotalPrice = 0L;
        for(int i=0;i< products.size();i++){
            cartTotalPrice += products.get(i).getPrice()* products.get(i).getQuantity();
        }
        return cartTotalPrice;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartCardView = view.findViewById(R.id.cartCardView);
        cartTotal = view.findViewById(R.id.cartTotal);
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setHasFixedSize(true);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));


        firebaseData = new FirebaseData();
        firebaseData.getCartList(FirebaseAuth.getInstance().getUid());
        firebaseData.getProductMutableLiveList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Product>>() {
            @Override
            public void onChanged(ArrayList<Product> products) {
                CartAdapter cartAdapter = new CartAdapter(getContext(),products);
                cartRecyclerView.setAdapter(cartAdapter);
                //cartAdapter.notifyDataSetChanged();
                Long cartTotalPrice = calculateCartTotalPrice(products);
                cartTotal.setText(cartTotalPrice.toString()+" Rs");
                if(products.size() == 0)
                    cartCardView.setVisibility(View.VISIBLE);

            }
        });
        firebaseData.getErrorMutableLive().observe(getViewLifecycleOwner(), new Observer<DatabaseError>() {
            @Override
            public void onChanged(DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                firebaseDataremove(viewHolder.getAdapterPosition());
//                cartAdapter.notifyDataSetChanged();

            }
        }).attachToRecyclerView(cartRecyclerView);


        return view;
    }
}