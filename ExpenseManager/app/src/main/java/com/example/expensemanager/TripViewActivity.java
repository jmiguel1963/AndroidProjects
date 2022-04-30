package com.example.expensemanager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TripViewActivity extends AppCompatActivity {

    private ImageView imgTrip;
    private TextView tripDescription;
    private TextView tripDate;
    private Trip trip;
    private FloatingActionButton addNewExpense;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private AdapterTrip adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_view);
        imgTrip=findViewById(R.id.imgTripView);
        tripDescription=findViewById(R.id.tripTextViewDescription);
        tripDate=findViewById(R.id.tripTextViewDate);

        Intent intent=getIntent();
        trip=intent.getParcelableExtra("item");
        imgTrip.setImageURI(trip.getUri());
        tripDescription.setText(trip.getDescription());
        tripDate.setText(trip.getDate());
        addNewExpense=findViewById(R.id.btnAddNewExpense);

        activityResultLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK && result.getData()!=null){

                }
            }
        });

        addNewExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent expenseIntent=new Intent(TripViewActivity.this,ExpenseActivity.class);
                activityResultLauncher.launch(expenseIntent);
            }
        });
    }
}