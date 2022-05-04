package com.example.expensemanager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TripListActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private ArrayList<Trip> trips;
    private FloatingActionButton createTrip;
    private ActivityResultLauncher<Intent> listActivityResultLauncher,tripActivityResultLauncher;
    private AdapterTrip adapter;
    private Trip trip;
    private int position;
    private ImageButton editUserProfile;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);
        createTrip=findViewById(R.id.floatingActionButton);
        editUserProfile=findViewById(R.id.userProfileButton);

        recycler=findViewById(R.id.recyclerExpenseView);
        recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        Intent listActivityIntent=getIntent();
        user=listActivityIntent.getParcelableExtra("currentUser");

        listActivityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK && result.getData()!=null){
                   trips=result.getData().getParcelableArrayListExtra("result");

                    adapter= new AdapterTrip(trips, new AdapterTrip.OnItemClickListener() {
                        @Override
                        //public void onItemClick(Trip item) {
                        public void onItemClick(View view,Trip item) {
                            moveToTripView(item);
                            position=(int)view.getTag();
                            Log.i("distance",""+position);
                        }
                    });
                    recycler.setAdapter(adapter);
                }
            }
        });

        tripActivityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK && result.getData()!=null){
                    trip=result.getData().getParcelableExtra("myTripBack");
                    trips.set(position,trip);

                    adapter= new AdapterTrip(trips, new AdapterTrip.OnItemClickListener() {
                        @Override
                        //public void onItemClick(Trip item) {
                        public void onItemClick(View view,Trip item) {
                            moveToTripView(item);
                            position=(int)view.getTag();
                        }
                    });
                    recycler.setAdapter(adapter);
                }
            }
        });

        createTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TripListActivity.this,TripEditActivity.class);
                if (trips==null){
                    trips=new ArrayList<Trip>();
                    trips.add(new Trip(null,"",""));
                }
                intent.putParcelableArrayListExtra("enter",trips);
                listActivityResultLauncher.launch(intent);
            }
        });

        editUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userProfileIntent=new Intent(TripListActivity.this,UserProfileActivity.class);
                userProfileIntent.putExtra("modUser",user);
                startActivity(userProfileIntent);
            }
        });

        //adapter= new AdapterTrip(trips);
        //recycler.setAdapter(adapter);
    }

    public void moveToTripView(Trip trip){
        Intent intentView= new Intent(this,TripViewActivity.class);
        intentView.putExtra("item",trip);
        tripActivityResultLauncher.launch(intentView);

    }
}