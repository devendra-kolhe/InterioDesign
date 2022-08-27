package com.example.interiodesign;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ImagesFragment extends Fragment {
    private RecyclerView imagesRecyclerView;
    private MediaAdapter mediaAdapter;
    private ArrayList<String> imagesList = new ArrayList<>();


    public ImagesFragment() {
        // Required empty public constructor
    }

    @SuppressLint("Range")
    public void listOfImages(Context context) {
        String uri = MediaStore.Images.Media.DATA;
        // if GetImageFromThisDirectory is the name of the directory from which image will be retrieved
        String condition = uri + " like '%/InterioDesign/%'";
        Log.e("manual"," condition "+condition);
        String[] projection = {uri, MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.SIZE};
        Log.e("manual"," projection.toString() "+ projection.toString());
        try {

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                    condition, null, null);

            if (cursor != null) {
                boolean isDataPresent = cursor.moveToFirst();
                if (isDataPresent) {
                    do {
                        Log.e("imagepath", cursor.getString(cursor.getColumnIndex(uri)));
//                        imagesList.add(cursor.getString(cursor.getColumnIndex(uri)));
                        imagesList.add(cursor.getString(cursor.getColumnIndex(uri)));
                    } while (cursor.moveToNext());
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.e("manual",e.toString());
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_images, container, false);

        imagesRecyclerView = view.findViewById(R.id.imagesRecyclerView);

        imagesRecyclerView.setHasFixedSize(true);
        imagesRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(),3));

        listOfImages(view.getContext());
        Log.e("manual",imagesList.toString());
        mediaAdapter = new MediaAdapter(imagesList,getContext());
        imagesRecyclerView.setAdapter(mediaAdapter);
        mediaAdapter.notifyDataSetChanged();
        return view;
    }

}