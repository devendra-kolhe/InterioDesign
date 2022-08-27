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

public class VideosFragment extends Fragment {

    private RecyclerView videosRecyclerView;
    private MediaAdapter mediaAdapter;
    private ArrayList<String> videosList= new ArrayList<>();

    public VideosFragment() {
        // Required empty public constructor
    }

    @SuppressLint("Range")
    public void listOfVideos(Context context){
        String uri = MediaStore.Video.Media.DATA;

        // if GetImageFromThisDirectory is the name of the directory from which image will be retrieved

        String condition = uri + " like '%/InterioDesign/%'";
        Log.e("manual",condition);
        String[] projection = new String[]{uri, MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.SIZE};
        Log.e("manual",projection.toString());
        try {

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                    condition, null, null);
            if (cursor != null) {
                boolean isDataPresent = cursor.moveToFirst();
                if (isDataPresent) {
                    do {
                        Log.e("videopath", cursor.getString(cursor.getColumnIndex(uri)));
                        videosList.add(cursor.getString(cursor.getColumnIndex(uri)));

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
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        videosRecyclerView = view.findViewById(R.id.videosRecyclerView);

        videosRecyclerView.setHasFixedSize(true);
        videosRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(),3));

        listOfVideos(view.getContext());
        Log.e("manual",videosList.toString());
        mediaAdapter = new MediaAdapter(videosList,getContext());
        videosRecyclerView.setAdapter(mediaAdapter);
        mediaAdapter.notifyDataSetChanged();

        return view;

    }


}