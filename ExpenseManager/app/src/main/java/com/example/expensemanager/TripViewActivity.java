package com.example.expensemanager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TripViewActivity extends AppCompatActivity {

    private ImageView imgTrip;
    private TextView tripDescription;
    private TextView tripDate;
    private Trip trip;
    private FloatingActionButton addNewExpense;
    private ActivityResultLauncher<Intent> activityExpenseResultLauncher,activityEditTripResultLauncher;
    private AdapterTrip adapter;
    private ImageButton editTrip;
    private Expense expense;

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
        editTrip=findViewById(R.id.btnEditTrip);

        activityExpenseResultLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK && result.getData()!=null){
                    expense=result.getData().getParcelableExtra("expense");

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

        addNewExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent expenseIntent=new Intent(TripViewActivity.this,ExpenseActivity.class);
                activityExpenseResultLauncher.launch(expenseIntent);
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
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("myTripBack", trip);
        setResult(RESULT_OK,intent);
        finish();
    }
}