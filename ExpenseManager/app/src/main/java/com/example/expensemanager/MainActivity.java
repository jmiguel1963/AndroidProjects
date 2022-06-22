package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //declaration of all Objects and primitives of Main Activity
    //which need to be accessed from any method or function of this activity
    private Button loginButton;
    private Button registerButton;
    private EditText personName;
    private EditText password;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private boolean userExists=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //widgets inflation in constraintLayout
        loginButton=findViewById(R.id.buttonLogin);
        registerButton=findViewById(R.id.buttonRegister);
        personName=findViewById(R.id.loginPersonName);
        password=findViewById(R.id.loginPassword);

        //editing fields clearing
        personName.setText("");
        password.setText("");



        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            GotoNextActivity();
        }

        //code executed when clicking Login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (personName.getText().toString().equals("") || password.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Fields must be filled before Login ",Toast.LENGTH_SHORT).show();
                }else{
                    mAuth.signInWithEmailAndPassword(personName.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        FirebaseFirestore db=FirebaseFirestore.getInstance();
                                        User myUser= new User("",password.getText().toString(),personName.getText().toString(),"");
                                        userExists=false;
                                        db.collection("users")
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                Map<String, Object> map = document.getData();
                                                                if (map.get("email").toString().equals(personName.getText().toString())) {
                                                                    userExists = true;
                                                                    break;
                                                                }
                                                            }
                                                            if (!userExists) {
                                                                db.collection("users")
                                                                        .add(myUser)
                                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentReference documentReference) {
                                                                                Toast.makeText(MainActivity.this,"User saved successfully",Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                        } else {
                                                            Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                        GotoNextActivity();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        //code executed when clicking Register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void GotoNextActivity(){
        Intent intent=new Intent(MainActivity.this,TripListActivity.class);
        startActivity(intent);
    }
}