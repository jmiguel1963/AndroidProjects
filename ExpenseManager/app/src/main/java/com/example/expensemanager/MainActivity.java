package com.example.expensemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //declaration of all Objects and primitives of Main Activity
    //which need to be accessed from any method or function of this activity
    private Button loginButton;
    private Button registerButton;
    private EditText personName;
    private EditText password;
    private String name;
    private String pass;
    private ArrayList<User> users;

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

        //code executed when clicking Login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //editing fields reading
                name=personName.getText().toString();
                pass=password.getText().toString();
                //checking if editing fields are filled
                if (!name.equals("") || !pass.equals("")){
                    users=SharedPrefConfig.readListPref(getApplicationContext());
                    if (users==null){
                        users= new ArrayList<User>();
                    }
                    boolean rightUser=false;
                    User currentUser=null;
                    for (User oldUser:users){
                        if (oldUser.getName().equals(name) && oldUser.getPassword().equals((pass))){
                            rightUser=true;
                            currentUser=oldUser;
                            break;
                        }
                    }
                    if (rightUser){
                        Intent intent=new Intent(MainActivity.this,TripListActivity.class);
                        personName.setText("");
                        password.setText("");
                        intent.putExtra("currentUser",currentUser);
                        startActivity(intent);
                    }else{
                        Toast.makeText(getApplicationContext(),"Name or Password incorrect.You must register it",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"the 2 fields must be filled",Toast.LENGTH_SHORT).show();
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
}