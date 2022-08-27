package com.example.interiodesign;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class CollectionFragment extends Fragment {
    private FirebaseData firebaseData;
    public RecyclerView collectionRecyclerView;
    public ShimmerFrameLayout shimmerFrameLayout;

    public CollectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_collection, container, false);


        collectionRecyclerView = view.findViewById(R.id.collectionRecyclerView);
//        shimmerFrameLayout=view.findViewById(R.id.shimmerLayout);
//        shimmerFrameLayout.startShimmer();
        collectionRecyclerView.setHasFixedSize(true);
        collectionRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(),2));

        firebaseData = new FirebaseData();
        firebaseData.getAllProducts();
        firebaseData.getProductMutableLiveList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Product>>() {
            @Override
            public void onChanged(ArrayList<Product> products) {
                Log.e("manual",products.get(0).getPrice().toString());
                collectionRecyclerView.setAdapter(new InterioDesignAdapter(R.layout.collection_row,products,false,null));
            }
        });
        firebaseData.getErrorMutableLive().observe(getViewLifecycleOwner(), new Observer<DatabaseError>() {
            @Override
            public void onChanged(DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

}