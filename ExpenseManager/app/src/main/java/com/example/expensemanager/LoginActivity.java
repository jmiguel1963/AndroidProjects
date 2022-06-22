package com.example.expensemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailLayout,passwordLayout;
    private TextInputEditText emailText,passwordText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLayout=findViewById(R.id.tilEmail);
        emailText=findViewById(R.id.etEmail);
        passwordLayout=findViewById(R.id.tilPassword);
        passwordText=findViewById(R.id.etPassword);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();



    }
}