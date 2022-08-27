package com.example.interiodesign;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;


public class ProductFragment extends Fragment {
    private ImageView image,decreaseQuantityButton,increaseQuantityButton;
    private TextView title, price, quantity;
    private Button showAR_button,show3D_button,addToCartButton;
    private CheckBox favoriteButton;
    private FirebaseData firebaseData;


    public ProductFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        firebaseData = new FirebaseData();

        image = view.findViewById(R.id.product_image);
        title = view.findViewById(R.id.productName);
        price = view.findViewById(R.id.productPrice);
        quantity = view.findViewById(R.id.quantity);
        favoriteButton = view.findViewById(R.id.favoriteButton);
        increaseQuantityButton = view.findViewById(R.id.increaseQuantityButton);
        decreaseQuantityButton = view.findViewById(R.id.decreaseQuantityButton);
        showAR_button = view.findViewById(R.id.showAR_button);
        show3D_button = view.findViewById(R.id.show3D_button);
        addToCartButton = view.findViewById(R.id.addToCartButton);




        Product productObject = getArguments().getParcelable("productObject");
        Log.e("manual",this. getClass(). getSimpleName()+" "+productObject.toString()+"in product fragment");
        String modelName = productObject.getKey();
        Picasso.get().load(productObject.getImg())
                .into(image);
        title.setText(productObject.getTitle());
        price.setText(productObject.getPrice().toString()+" Rs");
        firebaseData.doesFavoriteListContains(FirebaseAuth.getInstance().getUid(), productObject.getKey());
        firebaseData.getExistsInFavoriteList().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    favoriteButton.setButtonDrawable(R.drawable.outline_favorite_24);
                    favoriteButton.setChecked(true);
                    Log.e("manual","inside a bollean if"+favoriteButton.isChecked());
                }
            }
        });
        firebaseData.getErrorMutableLive().observe(getViewLifecycleOwner(), new Observer<DatabaseError>() {
            @Override
            public void onChanged(DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        increaseQuantityButton.setOnClickListener(view1 -> {
            Long quantityValue = Long.parseLong(quantity.getText().toString());
            quantity.setText(Long.toString(quantityValue+1));
        });
        decreaseQuantityButton.setOnClickListener(view1 -> {
            Long quantityValue = Long.parseLong(quantity.getText().toString());
            if (quantityValue == 1)
                return;
            quantity.setText(Long.toString(quantityValue-1));
        });
        Log.e("manual","befor on click "+favoriteButton.isChecked());
        favoriteButton.setOnClickListener(view1 -> {
            if(favoriteButton.isChecked()){
                favoriteButton.setButtonDrawable(R.drawable.outline_favorite_24);
                firebaseData.addToFavoriteList(FirebaseAuth.getInstance().getUid(),productObject);
                Log.e("manual","click if "+favoriteButton.isChecked());
                Toast.makeText(getContext(), "Added to Favorites.", Toast.LENGTH_SHORT).show();
            }
            else {
                favoriteButton.setButtonDrawable(R.drawable.outline_favorite_border_24);
                firebaseData.removeFromFavoriteList(FirebaseAuth.getInstance().getUid(), productObject.getKey());
                Log.e("manual","click else "+favoriteButton.isChecked());
                Toast.makeText(getContext(), "Removed from Favorites.", Toast.LENGTH_SHORT).show();
            }
        });


        showAR_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),showAR.class);
                Log.e("manual",this. getClass(). getSimpleName()+" "+modelName);
                intent.putExtra("productObject",productObject);
                startActivity(intent);
            }
        });

        show3D_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long time = Calendar.getInstance().getTimeInMillis();
                Product product = new Product(productObject);
                product.setTime(time);
                firebaseData.addToRecentlyViewedList(FirebaseAuth.getInstance().getUid(), product);
                Intent intent = new Intent(getContext(),show3D.class);
                Log.e("manual",this. getClass(). getSimpleName()+" "+modelName);
                intent.putExtra("modelName",modelName);
                startActivity(intent);
            }
        });

        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> sample = new ArrayList<>();
                sample.add("default_texture");
                Log.e("manual",quantity.getText().toString());
                productObject.setQuantity(Long.parseLong(quantity.getText().toString()));
                productObject.setTexture(sample);
                firebaseData.addToCartList(FirebaseAuth.getInstance().getUid(),productObject);
                Toast.makeText(getContext(), productObject.getTitle()+" added to Cart.", Toast.LENGTH_SHORT).show();
            }
        });



        return view;
    }

}