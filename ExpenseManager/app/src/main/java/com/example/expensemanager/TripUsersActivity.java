package com.example.expensemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class TripUsersActivity extends AppCompatActivity {

    private Spinner spinner;
    private RecyclerView recycleViewTripUsers;
    private Trip trip;
    private ArrayList<User> users;
    private Button userSelection,btnBack;
    private int position;
    private ArrayList<User> tripUsers=new ArrayList<User>();
    private AdapterUser adapterUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_users);
        spinner=findViewById(R.id.spinner);
        recycleViewTripUsers=findViewById(R.id.recyclerListUserView);
        recycleViewTripUsers.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        userSelection=findViewById(R.id.buttonValidation);
        btnBack=findViewById(R.id.buttonBack);

        users=SharedPrefConfig.readListPref(this);
        Intent intent=getIntent();
        trip=intent.getParcelableExtra("newUser");
        if (trip.getUsers()!=null && trip.getUsers().size()!=0){
            tripUsers=trip.getUsers();
        }
        ArrayAdapter<User> adapter= new ArrayAdapter<User>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,users);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                position=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        userSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tripUsers.size()==0){
                    trip.addUser(users.get(position));
                    tripUsers.add(users.get(position));
                }else{
                    boolean userExists=false;
                    for (User myUser:tripUsers){
                        if (users.get(position).getName().equals(myUser.getName())){
                            userExists=true;
                            break;
                        }
                    }
                    if (!userExists){
                        trip.addUser(users.get(position));
                        tripUsers.add(users.get(position));
                    }
                }
                adapterUser=new AdapterUser(tripUsers);
                recycleViewTripUsers.setAdapter(adapterUser);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBack= new Intent();
                intentBack.putExtra("addUser",trip);
                setResult(RESULT_OK,intentBack);
                finish();
            }
        });

        adapterUser=new AdapterUser(tripUsers);
        recycleViewTripUsers.setAdapter(adapterUser);
    }
}