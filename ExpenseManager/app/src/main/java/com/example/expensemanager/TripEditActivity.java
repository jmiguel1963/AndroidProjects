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

import java.util.ArrayList;

public class TripEditActivity extends AppCompatActivity {

    private ImageButton choosePhoto;
    private Button validation;
    private EditText descriptionText;
    private EditText dateText;
    private ImageView image;
    private String tripDescription;
    private String tripDate;
    private Uri tripUri;
    private ArrayList<Trip> trips;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_edit);
        choosePhoto=findViewById(R.id.changeUsePhoto);
        validation=findViewById(R.id.btnSave);
        descriptionText=findViewById(R.id.editDescription);
        dateText=findViewById(R.id.editDate);
        image=findViewById(R.id.imageView1);

        Intent originalIntent=getIntent();
        trips=originalIntent.getParcelableArrayListExtra("enter");
        if (trips.get(0).getUri()==null){
            trips=new ArrayList<Trip>();
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
                Trip trip=new Trip(tripUri,tripDate,tripDescription);
                trips.add(trip);
                for (Trip mytrip:trips){
                    Log.i("distance",mytrip.getDescription());
                }
                Intent resultIntent=new Intent();
                resultIntent.putParcelableArrayListExtra("result",trips);
                setResult(RESULT_OK,resultIntent);
                finish();
            }
        });
    }
}