package com.example.interiodesign;

import android.util.Log;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import androidx.recyclerview.widget.RecyclerView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FirebaseData {
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
//    private ShimmerFrameLayout shimmerFrameLayout;

    public MutableLiveData<ArrayList<Product>> getProductMutableLiveList() {
        return productMutableLiveList;
    }

    public MutableLiveData<DatabaseError> getErrorMutableLive() {
        return errorMutableLive;
    }

    public MutableLiveData<Boolean> getExistsInFavoriteList() {
        return existsInFavoriteList;
    }


    private MutableLiveData<ArrayList<Product>> productMutableLiveList = new MutableLiveData<>();
    private MutableLiveData<DatabaseError> errorMutableLive = new MutableLiveData<>();
    private MutableLiveData<Boolean> existsInFavoriteList = new MutableLiveData<>() ;


    public FirebaseData(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    public StorageReference getModelFromStorage(String modelName){
        return storageReference.child(modelName+".glb");
    }

    public void getAllProducts(){
        ArrayList<Product> productList = new ArrayList<>();
        databaseReference.child("Categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        Product product=new Product(
                                dataSnapshot1.child("img").getValue().toString(),
                                (Long) dataSnapshot1.child("price").getValue(),
                                dataSnapshot1.child("title").getValue().toString(),
                                dataSnapshot1.getKey()
                        );
                        productList.add(product);
                    }
                }
                Log.e("manual",this.getClass().getSimpleName()+" "+productList.toString());
                //onRealTimeDatabaseTaskComplete.onSuccess(productList);
//                shimmerFrameLayout.stopShimmer();
//                shimmerFrameLayout.setVisibility(View.GONE);
//                collectionRecyclerView.setVisibility(View.VISIBLE);

                productMutableLiveList.setValue(productList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //onRealTimeDatabaseTaskComplete.onFailure(error);
                errorMutableLive.setValue(error);
            }
        });
    }

    public void getProductRelatedTo(String categoryName){
        ArrayList<Product> productList = new ArrayList<>();
        databaseReference.child("Categories/"+categoryName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Log.e("manual",this.getClass().getSimpleName()+"key: "+dataSnapshot.getKey());
                    Product product = new Product(
                            dataSnapshot.child("img").getValue().toString(),
                            (Long) dataSnapshot.child("price").getValue(),
                            dataSnapshot.child("title").getValue().toString(),
                            dataSnapshot.getKey()
                    );
                    productList.add(product);
                }
                Log.e("manual",this.getClass().getSimpleName()+" "+productList.toString());
                //onRealTimeDatabaseTaskComplete.onSuccess(productList);
                productMutableLiveList.setValue(productList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //onRealTimeDatabaseTaskComplete.onFailure(error);
                errorMutableLive.setValue(error);
            }
        });
    }

    public void getRecentlyViewedList(String uid){
        ArrayList<Product> recentlyViewedList = new ArrayList<>();
        Query recentlyViewed = databaseReference.child("Users/"+uid+"/Recently Viewed").orderByChild("time").limitToLast(5);
        recentlyViewed.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recentlyViewedList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Log.e("manual",this.getClass().getSimpleName()+"key: "+dataSnapshot.getKey());
                    Product product = new Product(
                            dataSnapshot.child("img").getValue().toString(),
                            (Long) dataSnapshot.child("price").getValue(),
                            dataSnapshot.child("title").getValue().toString(),
                            dataSnapshot.getKey()
                    );
                    recentlyViewedList.add(product);
                }
                Log.e("manual",this.getClass().getSimpleName()+" "+recentlyViewedList.toString());
                //onRealTimeDatabaseTaskComplete.onSuccess(recentlyViewedList);
                productMutableLiveList.setValue(recentlyViewedList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //onRealTimeDatabaseTaskComplete.onFailure(error);
                errorMutableLive.setValue(error);
            }
        });
    }

    public void getCartList(String uid){
        ArrayList<Product> cartList = new ArrayList<>();
        databaseReference.child("Users/"+uid+"/Cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Product product = new Product(
                            dataSnapshot.child("img").getValue().toString(),
                            (Long) dataSnapshot.child("price").getValue(),
                            dataSnapshot.child("title").getValue().toString(),
                            dataSnapshot.getKey(),
                            (Long) dataSnapshot.child("quantity").getValue(),
                            (ArrayList<String>) dataSnapshot.child("texture").getValue()
                    );
                    cartList.add(product);
                }
                productMutableLiveList.setValue(cartList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorMutableLive.setValue(error);
            }
        });
    }

    public void getFavoriteList(String uid){
        ArrayList<Product> favoriteList = new ArrayList<>();
        databaseReference.child("Users/"+uid+"/Favorites").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                favoriteList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Product product = new Product(
                            dataSnapshot.child("img").getValue().toString(),
                            (Long) dataSnapshot.child("price").getValue(),
                            dataSnapshot.child("title").getValue().toString(),
                            dataSnapshot.getKey()
                    );
                    favoriteList.add(product);
                }
                productMutableLiveList.setValue(favoriteList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorMutableLive.setValue(error);
            }
        });
    }


    public void addToRecentlyViewedList(String uid,Product productObject){
        databaseReference.child("Users/"+uid+"/Recently Viewed/"+productObject.getKey()).setValue(productObject);
    }

    public void addToCartList(String uid,Product productObject){
        databaseReference.child("Users/"+uid+"/Cart/"+productObject.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Log.e("manual",this.getClass().getSimpleName()+" "+snapshot.toString());
                Long quantity = 0L;
                if(snapshot.exists()){
                   // Log.e("manual",this.getClass().getSimpleName()+" "+productObject.getKey()+"exists");
                    quantity = (Long) snapshot.child("quantity").getValue();
                    //Log.e("manual", snapshot.child("quantity").getValue().toString());
                }
                productObject.setQuantity(quantity + productObject.getQuantity());
                databaseReference.child("Users/"+uid+"/Cart/"+productObject.getKey()).setValue(productObject);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorMutableLive.setValue(error);
            }
        });
    }

    public void addToFavoriteList(String uid,Product productObject){
        databaseReference.child("Users/"+uid+"/Favorites/"+productObject.getKey()).setValue(productObject);
    }

    public void removeFromFavoriteList(String uid,String productKey){
        databaseReference.child("Users/"+uid+"/Favorites/"+productKey).removeValue();
    }

    public void removeFromCart(String uid,String productKey){
        databaseReference.child("Users/"+uid+"/Cart/"+productKey).removeValue();
    }
    public void changeQuantity(String uid,String productKey,Long quantity){
        databaseReference.child("Users/"+uid+"/Cart/"+productKey+"/quantity").setValue(quantity);
    }

    public void doesFavoriteListContains(String uid,String productKey){
        databaseReference.child("Users/"+uid+"/Favorites/"+productKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    existsInFavoriteList.setValue(true);
                else
                    existsInFavoriteList.setValue(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorMutableLive.setValue(error);
            }
        });
    }

}
