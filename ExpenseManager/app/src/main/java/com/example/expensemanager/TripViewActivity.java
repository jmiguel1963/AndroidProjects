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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TripViewActivity extends AppCompatActivity {

    private ImageView imgTrip;
    private TextView tripDescription;
    private TextView tripDate;
    private Trip trip;
    private FloatingActionButton addNewExpense;
    private ActivityResultLauncher<Intent> activityExpenseResultLauncher,activityEditTripResultLauncher;
    private ActivityResultLauncher<Intent> activityNewUserResultLauncher;
    private AdapterExpense adapter;
    private ImageButton editTrip;
    private RecyclerView recycler;
    private Button addNewUser;
    private TripExpense tripExpense;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_view);
        imgTrip=findViewById(R.id.imgTripView);
        tripDescription=findViewById(R.id.tripTextViewDescription);
        tripDate=findViewById(R.id.tripTextViewDate);
        recycler=findViewById(R.id.recyclerViewExpense);
        recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        Intent intent=getIntent();
        trip=intent.getParcelableExtra("item");
        imgTrip.setImageURI(trip.getUri());
        tripDescription.setText(trip.getDescription());
        tripDate.setText(trip.getDate());
        addNewExpense=findViewById(R.id.btnAddNewExpense);
        editTrip=findViewById(R.id.btnEditTrip);
        addNewUser=findViewById(R.id.btnAddNewUser);

        activityExpenseResultLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK && result.getData()!=null){
                    trip=result.getData().getParcelableExtra("backExpenseTrip");
                    adapter= new AdapterExpense(trip.getExpenses(), new AdapterExpense.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, Expense item) {
                            position=(int)view.getTag();
                            tripExpense=new TripExpense(trip,position);
                            moveToExpenseView(tripExpense);
                        }
                    });
                    recycler.setAdapter(adapter);
                }
            }
        });

        activityEditTripResultLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK && result.getData()!=null){
                    trip=result.getData().getParcelableExtra("edit");
                    tripDescription.setText(trip.getDescription());
                    tripDate.setText(trip.getDate());
                }
            }
        });

        activityNewUserResultLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK && result.getData()!=null){
                    trip=result.getData().getParcelableExtra("addUser");
                }
            }
        });

        addNewExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (trip.getUsers().size()!=0){
                    Intent expenseIntent=new Intent(TripViewActivity.this,ExpenseActivity.class);
                    tripExpense=new TripExpense(trip,-1);
                    expenseIntent.putExtra("tripExpense",tripExpense);
                    activityExpenseResultLauncher.launch(expenseIntent);
                }else{
                    Toast.makeText(getApplicationContext(),"Users must be added to trip before adding expenses",Toast.LENGTH_SHORT).show();
                }
            }
        });

        editTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editIntent=new Intent(TripViewActivity.this,TripEditActivity.class);
                editIntent.putExtra("trip",trip);
                activityEditTripResultLauncher.launch(editIntent);
            }
        });

        addNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newUserIntent = new Intent(TripViewActivity.this, TripUsersActivity.class);
                newUserIntent.putExtra("newUser",trip);
                activityNewUserResultLauncher.launch(newUserIntent);
            }
        });

        adapter=new AdapterExpense(trip.getExpenses(), new AdapterExpense.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Expense item) {
                position=(int)view.getTag();
                tripExpense=new TripExpense(trip,position);
                moveToExpenseView(tripExpense);

            }
        });
        recycler.setAdapter(adapter);
    }

    public void moveToExpenseView(TripExpense tripExpense){
        Intent intentView= new Intent(this,ExpenseActivity.class);
        intentView.putExtra("tripExpense",tripExpense);
        activityExpenseResultLauncher.launch(intentView);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("myTripBack", trip);
        setResult(RESULT_OK,intent);
        finish();
    }
}