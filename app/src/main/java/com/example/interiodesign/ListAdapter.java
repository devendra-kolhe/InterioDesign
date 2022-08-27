package com.example.interiodesign;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {
    ArrayList<Integer> list;
    Integer current;
    TextView materialText;

    public ListAdapter(ArrayList<Integer> list, TextView materialText){
        this.list = list;
        this.materialText = materialText;
    }
    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        return new ListAdapter.ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.listButton.setText(list.get(position).toString());

        holder.listButton.setOnClickListener(view -> {
            Log.e("manual",list.get(position).toString());
            materialText.setText(list.get(position).toString());
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        Button listButton;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            listButton = itemView.findViewById(R.id.listButton);
        }
    }
}
