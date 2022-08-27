package com.example.interiodesign;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.regex.Pattern;


public class RegisterFragment extends Fragment {
    public FragmentManager fragmentManager;
    private FirebaseAuth fauth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference;
    TextInputEditText email,password,confirm_password;
    private ProgressDialog progressDialog;
    Button register,login;
    public RegisterFragment() {
        // Required empty public constructor
    }

    private void toLoginPage(){
        //getActivity().getFragmentManager().popBackStackImmediate();
        fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout_container,new LoginFragment());
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
    private boolean validateEmail(){
        //String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches();
    }
    private boolean validatePassword(){
        Pattern PASSWORD_PATTERN =
                Pattern.compile("^(?=\\S+$).{0,}$");
        return PASSWORD_PATTERN.matcher(password.getText().toString()).matches();
    }

    private void validateAndCreate(){
        if(email.getText().toString().trim().isEmpty()||password.getText().toString().trim().isEmpty()||confirm_password.getText().toString().trim().isEmpty()){
            Toast.makeText(getContext(), "Please fill all the fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!validateEmail()){
            Toast.makeText(getContext(), "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!validatePassword()){
            Toast.makeText(getContext(), "Password must not contain any whitespaces.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.getText().toString().equals(confirm_password.getText().toString())){
            Toast.makeText(getContext(), "Password and Confirm password did not match.", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Registering...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);

        fauth.createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "User created successfully.", Toast.LENGTH_SHORT).show();
                    toLoginPage();
                } else {
                    progressDialog.dismiss();
                    String exception = task.getException().toString();
                    String[] error = exception.split(":");
                    Toast.makeText(getContext(), error[1], Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        register = view.findViewById(R.id.register_button);
        progressDialog = new ProgressDialog(getActivity());
        login = view.findViewById(R.id.login_button);
        email = view.findViewById(R.id.email_reg);
        password = view.findViewById(R.id.password_reg);
        confirm_password = view.findViewById(R.id.confirm_password);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndCreate();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toLoginPage();
            }
        });
        return view;
    }
}