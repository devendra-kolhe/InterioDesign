package com.example.interiodesign;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class UserAdapter extends FragmentStateAdapter {

    private int[] tabIcons = { R.drawable.outline_crop_original_24,
            R.drawable.outline_video_library_24,R.drawable.outline_favorite_24};
    private Boolean permissionBool;

    public UserAdapter(@NonNull FragmentActivity fragmentActivity,Boolean permissionBool) {
        super(fragmentActivity);
        this.permissionBool = permissionBool;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position)
        {
            case 0:
                return (permissionBool)? new ImagesFragment():new NoPermissionFragment();

            case 1:
                return (permissionBool)? new VideosFragment():new NoPermissionFragment();

            case 2:
                return  new FavoritesFragment();

        }
        return (permissionBool)? new ImagesFragment():new NoPermissionFragment();
    }

    @Override
    public int getItemCount() {
        return tabIcons.length;
    }
}


