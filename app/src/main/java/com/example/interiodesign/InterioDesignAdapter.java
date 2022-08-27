package com.example.interiodesign;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class InterioDesignAdapter extends RecyclerView.Adapter<InterioDesignAdapter.InterioDesignViewHolder> {
    ArrayList<Product> productList;
    Integer xmlName;
    Boolean fromBottomSheet;
    Product productObject;
    LinearLayout bottomSheetLayout;

    public InterioDesignAdapter(Integer xmlName, ArrayList<Product> productList,Boolean fromBottomSheet,LinearLayout bottomSheetLayout){
        this.productList = productList;
        this.xmlName = xmlName;
        this.fromBottomSheet = fromBottomSheet;
        this.bottomSheetLayout = bottomSheetLayout;
    }


    @NonNull
    @Override
    public InterioDesignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(this.xmlName, parent, false);
        return new InterioDesignViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InterioDesignViewHolder holder, int position) {
        Picasso.get().load(productList.get(position).getImg())
                .into(holder.image);

        holder.title.setText(productList.get(position).getTitle());
        holder.price.setText(productList.get(position).getPrice().toString()+" Rs");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fromBottomSheet){
                    Log.e("manual",this.getClass().getSimpleName()+" "+fromBottomSheet);
                    productObject = productList.get(holder.getAdapterPosition());
                    bottomSheetLayout.performClick();
                    return;
                }
                Log.e("manual not in here",this.getClass().getSimpleName()+" "+fromBottomSheet);
                AppCompatActivity activity=(AppCompatActivity) view.getContext();
                ProductFragment productFragment=new ProductFragment();
                Bundle data=new Bundle();
                data.putParcelable("productObject",(Parcelable) productList.get(holder.getAdapterPosition()));
                productFragment.setArguments(data);

                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout_container_home,productFragment)
                        .addToBackStack(null)
                        .commit();

//                transaction.hide(activity.getSupportFragmentManager().findFragmentById(R.id.brands));
//                transaction.add(R.id.framecontainer,productFragment);
//                transaction.addToBackStack(null).commit();
//                  itemClickListener.onItemClick(productList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    public Product getProductObject(){
        return this.productObject;
    }
   /* @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }*/

    public class InterioDesignViewHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        private TextView title;
        private TextView price;

        public InterioDesignViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
        }
    }
}
