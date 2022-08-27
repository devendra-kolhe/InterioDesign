package com.example.interiodesign;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);


        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_container_home, new HomeFragment()).commit();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomnavigationbar);
        bottomNavigationView.setBackground(null);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment temp = null;
                switch (item.getItemId())
                {
                    case R.id.home:
                        temp = new HomeFragment();
                        break;
                    case R.id.collection :
                        temp = new CollectionFragment();
                        break;
                    case R.id.cart :
                        temp = new CartFragment();
                        break;
                    case R.id.user:
                        temp = new UserFragment();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_container_home, temp).commit();
                return true;
            }
        });

        findViewById(R.id.ar_button).setOnClickListener(view -> {
            Intent intent = new Intent(getBaseContext(),showAR.class);
            startActivity(intent);
        });


    }
}