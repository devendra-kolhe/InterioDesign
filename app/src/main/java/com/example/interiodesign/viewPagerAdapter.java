package com.example.interiodesign;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

class viewPageAdapter extends RecyclerView.Adapter<viewPageAdapter.ViewHolder> {
    ArrayList<onboardingItems> onboardingItemsArrayList;

    public viewPageAdapter(ArrayList<onboardingItems> onboardingItemsArrayList) {
        this.onboardingItemsArrayList = onboardingItemsArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        onboardingItems boardingItems = onboardingItemsArrayList.get(position);
        holder.imageView.setImageResource(boardingItems.imageId);
        holder.textHeading.setText(boardingItems.heading);
        holder.textDesc.setText(boardingItems.description);


    }

    @Override
    public int getItemCount() {

        return onboardingItemsArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textHeading,textDesc;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.firstImage);
            textHeading = itemView.findViewById(R.id.titletext);
            textDesc= itemView.findViewById(R.id.textexpl);


        }
    }
}
//    Context context;

//    int image[]= {
//                R.drawable.musicplayer,
//                R.drawable.headphone,
//                R.drawable.roll,
//                R.drawable.walkman
//    };
//
//
//    int  headings[]={
//            R.string.heading1,
//            R.string.heading2,
//            R.string.heading3,
//            R.string.heading1,
//
//    };
//    int  explanations[]={
//            R.string.expl1,
//            R.string.expl2,
//            R.string.expl3,
//            R.string.expl1,
//
//    };
//






