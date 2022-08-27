package com.example.interiodesign;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {
    private CardView favoriteCardView;
    private RecyclerView favoritesRecyclerView;
    private FirebaseData firebaseData;

    public FavoritesFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        favoriteCardView = view.findViewById(R.id.favoriteCardView);
        favoritesRecyclerView = view.findViewById(R.id.favoritesRecyclerView);
        favoritesRecyclerView.setHasFixedSize(true);
        favoritesRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(),2));


        firebaseData = new FirebaseData();
        firebaseData.getFavoriteList(FirebaseAuth.getInstance().getUid());
        firebaseData.getProductMutableLiveList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Product>>() {
            @Override
            public void onChanged(ArrayList<Product> products) {
                if(products.size() == 0){
                    favoriteCardView.setVisibility(View.VISIBLE);
                }
                favoritesRecyclerView.setAdapter(new InterioDesignAdapter(R.layout.collection_row,products,false,null));
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