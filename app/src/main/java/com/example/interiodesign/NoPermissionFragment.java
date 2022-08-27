package com.example.interiodesign;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class NoPermissionFragment extends Fragment {
    private TextView noPermissionTextView;


    public NoPermissionFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_no_permission, container, false);
        noPermissionTextView = view.findViewById(R.id.noPermissionTextView);
        noPermissionTextView.setOnClickListener(view1 -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_container_home,new UserFragment()).commit();
        });
        return view;
    }

}
