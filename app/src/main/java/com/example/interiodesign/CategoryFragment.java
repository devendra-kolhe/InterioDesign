package com.example.interiodesign;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {
    private FirebaseData firebaseData;
    private RecyclerView categoryRecyclerView;

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        String categoryName = getArguments().getString("categoryName");
        Log.e("manual",this.getClass().getSimpleName()+" "+categoryName);

        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        categoryRecyclerView.setHasFixedSize(true);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        firebaseData = new FirebaseData();
        firebaseData.getProductRelatedTo(categoryName);
        firebaseData.getProductMutableLiveList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Product>>() {
            @Override
            public void onChanged(ArrayList<Product> products) {
                categoryRecyclerView.setAdapter(new InterioDesignAdapter(R.layout.category_row,products,false,null));
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