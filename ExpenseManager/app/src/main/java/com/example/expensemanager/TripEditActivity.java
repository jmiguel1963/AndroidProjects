package com.example.expensemanager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Locale;

public class TripEditActivity extends AppCompatActivity {

    private ImageButton choosePhoto;
    private Button validation;
    private EditText descriptionText;
    private EditText dateText;
    private ImageView image;
    private String tripDescription;
    private String tripDate;
    private Uri tripUri;
    private Trip trip;
    private ArrayList<Trip> trips;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private boolean createTrip=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_edit);
        choosePhoto=findViewById(R.id.changeUsePhoto);
        validation=findViewById(R.id.btnSave);
        descriptionText=findViewById(R.id.editDescription);
        dateText=findViewById(R.id.editDate);
        image=findViewById(R.id.imagePhoto);

        Intent originalIntent=getIntent();
        trips=originalIntent.getParcelableArrayListExtra("enter");
        if (trips==null){
            trip=originalIntent.getParcelableExtra("trip");
            choosePhoto.setEnabled(false);
            validation.setText("Modify");
            createTrip=false;
            image.setImageURI(trip.getUri());
            descriptionText.setText(trip.getDescription());
            dateText.setText(trip.getDate());
        }else{
            if (trips.get(0).getUri()==null){
                trips=new ArrayList<Trip>();
            }
        }


        activityResultLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK && result.getData()!=null){
                    tripUri = result.getData().getData();
                    image.setImageURI(tripUri);
                }
            }
        });

        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (pickPhoto.resolveActivity(getPackageManager())!=null){
                    activityResultLauncher.launch(pickPhoto);
                }
            }
        });

        validation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tripDescription=descriptionText.getText().toString();
                tripDate=dateText.getText().toString();
                Intent resultIntent=new Intent();
                if (createTrip){
                    Trip newTrip=new Trip(tripUri,tripDate,tripDescription);
                    trips.add(newTrip);
                    resultIntent.putParcelableArrayListExtra("result",trips);
                }else{
                    trip.setDate(tripDate);
                    trip.setDescription(tripDescription);
                    resultIntent.putExtra("edit",trip);
                }
                setResult(RESULT_OK,resultIntent);
                finish();
            }
        });
    }

    /*private boolean checkFormat(String input){
        boolean isValidFormat = input.matches("([0-9]{2})/([0-9]{2})/([0-9]{4})");

        return isValidFormat;
    }*/
}