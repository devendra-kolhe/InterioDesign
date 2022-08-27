package com.example.interiodesign;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExpandableAdapter extends RecyclerView.Adapter<ExpandableAdapter.ExpandableViewHolder> {
    private ArrayList<String> textureList;
    private Context context;


    public ExpandableAdapter(Context context, ArrayList<String> textureList) {
        this.context = context;
        this.textureList = textureList;
    }

    @NonNull
    @Override
    public ExpandableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expandable_row, parent, false);
        return new ExpandableAdapter.ExpandableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpandableViewHolder holder, int position) {
        if(getItemCount() == 1)
            holder.materialTextView.setText("All Materials");
        else
            holder.materialTextView.setText("Material "+ (position+1));
        holder.textureImageView.setImageResource(context.getResources().getIdentifier(textureList.get(position), "drawable", context.getPackageName()));
    }

    @Override
    public int getItemCount() {
        return textureList.size();
    }

    public class ExpandableViewHolder extends RecyclerView.ViewHolder {
        private TextView materialTextView;
        private ImageView textureImageView;
        public ExpandableViewHolder(@NonNull View itemView) {
            super(itemView);

            materialTextView = itemView.findViewById(R.id.materialTextView);
            textureImageView = itemView.findViewById(R.id.textureImageView);
        }
    }
}
