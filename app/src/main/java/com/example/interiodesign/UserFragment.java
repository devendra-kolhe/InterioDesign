package com.example.interiodesign;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

public class UserFragment extends Fragment {

    private Boolean permissionBool = false;
    private TextView noPermissionTextView;
    private ImageButton logoutButton;
    private TextView userName;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private UserAdapter userAdapter;
    private int[] tabIcons = { R.drawable.outline_crop_original_24, R.drawable.outline_video_library_24,R.drawable.outline_favorite_24};


    public UserFragment() {
        // Required empty public constructor
    }




    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_container_home,new UserFragment()).commit();
                }
            });

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.e("manual","already permitted for read storage");
            permissionBool = true;
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }


    private void logoutClicked(View view){
        Log.e("manual","logout clicked");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("Sure! Logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.e("manual","yes clicked");
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getContext(),LoginRegister.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).create();
        builder.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_user, container, false);

        viewPager2 = view.findViewById(R.id.viewPager2);
        tabLayout = view.findViewById(R.id.tabLayout);

        logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(this::logoutClicked);

        String userNameString[] = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@",2);
        userName = view.findViewById(R.id.userName);
        userName.setText(userNameString[0]);
        checkPermission();


        userAdapter = new UserAdapter((FragmentActivity) view.getContext(),permissionBool);

        viewPager2.setAdapter(userAdapter);
        new TabLayoutMediator(tabLayout,viewPager2,(tab, position) -> tab.setIcon(tabIcons[position])).attach();

        return view;
    }
}