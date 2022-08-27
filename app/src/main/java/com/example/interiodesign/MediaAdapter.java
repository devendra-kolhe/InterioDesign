package com.example.interiodesign;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {

    private ArrayList<String> mediaList;
    private Context context;
    private Intent intent;

    public MediaAdapter(ArrayList<String> mediaList, Context context) {
        this.mediaList = mediaList;
        this.context = context;
        this.intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
    }

    @NotNull
    @Override
    public MediaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.media_row,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MediaAdapter.ViewHolder holder, int position) {
        String mediaString = mediaList.get(position);
        Glide.with(context).load(mediaString).into(holder.media);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("manual",mediaString);
                String ext = mediaString.substring(mediaString.lastIndexOf(".") + 1);
                Log.e("manual",ext);
                if(ext.equals("mp4"))
                    intent.setDataAndType(Uri.parse(mediaString), "video/*");
                else
                    intent.setDataAndType(Uri.parse(mediaString), "image/*");
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        ImageView media;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            media = itemView.findViewById(R.id.media);
        }
    }
}