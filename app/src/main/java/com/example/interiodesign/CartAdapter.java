package com.example.interiodesign;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHodler> {
    private ArrayList<Product> cartList;
    private FirebaseData firebaseData;
    private Context context;

    public CartAdapter(Context context,ArrayList<Product> cartList) {
        this.context = context;
        this.cartList = cartList;
        this.firebaseData = new FirebaseData();
    }

    @NonNull
    @Override
    public CartViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_row, parent, false);
        return new CartViewHodler(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CartViewHodler holder, int position) {

        Picasso.get().load(cartList.get(position).getImg())
                .into(holder.image);

        holder.title.setText(cartList.get(position).getTitle());
        holder.price.setText(cartList.get(position).getPrice().toString() + " Rs");
        holder.quantity.setText(cartList.get(position).getQuantity().toString());
        holder.expandableRecyclerView.setAdapter(new ExpandableAdapter(context,cartList.get(position).getTexture()));

        holder.cartItem.setOnClickListener(view -> {
            Log.e("manual", "responding to clcik");
            if(holder.expandable.getVisibility() == View.GONE)
                holder.expandable.setVisibility(View.VISIBLE);
            else
                holder.expandable.setVisibility(View.GONE);
        });


        holder.deleteButton.setOnClickListener(view -> {
            firebaseData.removeFromCart(FirebaseAuth.getInstance().getUid(), cartList.get(position).getKey());
            //notifyItemRemoved(position);
        });

        holder.increaseQuantityButton.setOnClickListener(view -> {
            Long quantity = cartList.get(position).getQuantity();
            firebaseData.changeQuantity(FirebaseAuth.getInstance().getUid(), cartList.get(position).getKey(), quantity + 1);
        });

        holder.decreaseQuantityButton.setOnClickListener(view -> {
            Long quantity = cartList.get(position).getQuantity();
            if (quantity == 1)
                return;
            firebaseData.changeQuantity(FirebaseAuth.getInstance().getUid(), cartList.get(position).getKey(), quantity - 1);
        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }


    public class CartViewHodler extends RecyclerView.ViewHolder {

        private TextView title, price, quantity;
        private ImageView image;

        private ImageView deleteButton;
        private ImageButton increaseQuantityButton, decreaseQuantityButton;

        private LinearLayout cartItem, expandable;
        private RecyclerView expandableRecyclerView;

        public CartViewHodler(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.quantity);

            deleteButton = itemView.findViewById(R.id.deleteButton);
            quantity = itemView.findViewById(R.id.quantity);
            increaseQuantityButton = itemView.findViewById(R.id.increaseQuantityButton);
            decreaseQuantityButton = itemView.findViewById(R.id.decreaseQuantityButton);

            cartItem = itemView.findViewById(R.id.cartItem);
            expandable = itemView.findViewById(R.id.expandable);

            expandableRecyclerView = itemView.findViewById(R.id.expandableRecyclerView);
            expandableRecyclerView.setHasFixedSize(true);
            expandableRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}