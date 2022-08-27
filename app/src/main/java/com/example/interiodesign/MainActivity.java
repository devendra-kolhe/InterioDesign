package com.example.interiodesign;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseData firebaseData;
    private FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseData = new FirebaseData();
        firebaseData.getAllProducts();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("manual",this. getClass(). getSimpleName()+" "+"taking to LoginRegister after 3 sec");
                fauth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = fauth.getCurrentUser();

                if(currentUser != null){
                    Log.e("manual",this. getClass(). getSimpleName()+" "+currentUser.getUid());
                    Log.e("manual",this. getClass(). getSimpleName()+" "+"user is already logged in.taking to home");
                    Intent i = new Intent(getBaseContext(),Home.class);
                    startActivity(i);
                    finish();
                }
                else{
                    Intent i = new Intent(getBaseContext(), LoginRegister.class);
                    startActivity(i);
                    finish();
                }

            }
        }, 3000);

    }
}