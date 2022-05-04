package com.example.expensemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private Button registerButton;
    private EditText registerPersonName;
    private EditText registerPassword;
    private ArrayList<User> users;
    private String registerName;
    private String registerPass;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerButton=findViewById(R.id.registerValidationButton);
        registerPersonName=findViewById(R.id.registerPersonName);
        registerPassword=findViewById(R.id.registerPassword);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerPass=registerPassword.getText().toString();
                registerName=registerPersonName.getText().toString();
                users=SharedPrefConfig.readListPref(getApplicationContext());
                if (users==null){
                    users=new ArrayList<User>();
                    user= new User(registerName,registerPass,"",null);
                    users.add(user);
                    SharedPrefConfig.writeListPref(getApplicationContext(),users);
                    finish();
                }else{
                    boolean nameValid=true;
                    for (User oldUser:users) {
                        if (oldUser.getName().equals(registerName)) {
                            nameValid = false;
                            break;
                        }
                    }
                    if (!nameValid){
                        Toast.makeText(getApplicationContext(), "User already exists", Toast.LENGTH_SHORT).show();
                    }else{
                        if (!registerName.equals("") && !registerPass.equals("")){
                            user= new User(registerName,registerPass,"",null);
                            users.add(user);
                            SharedPrefConfig.writeListPref(getApplicationContext(),users);
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(), "Name or/and Password are empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                }


            }
        });
    }
}