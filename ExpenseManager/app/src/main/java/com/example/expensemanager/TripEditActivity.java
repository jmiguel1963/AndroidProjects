package com.example.expensemanager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TripEditActivity extends AppCompatActivity {

    private ImageButton choosePhoto;
    private Button validation;
    private Button insertDate;
    private DatePickerDialog datePickerDialog;
    private EditText descriptionText;
    private ImageView image;
    private String tripDescription;
    private String tripDate;
    private Uri tripUri;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private boolean createTrip=true;
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private FirebaseUser firebaseUser;
    private ProgressBar uploadProgressBar;
    private String urlPath="";
    private String tripId;
    private Trip myTrip;
    private boolean tripExists=false;
    private ArrayList<User> users=new ArrayList<>();
    private User tripUser;
    private ArrayList<String> emailUsers;
    private boolean tripCreation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_edit);
        choosePhoto=findViewById(R.id.changeUsePhoto);
        validation=findViewById(R.id.btnSave);
        descriptionText=findViewById(R.id.editDescription);
        image=findViewById(R.id.imagePhoto);
        uploadProgressBar=findViewById(R.id.progressBarTripUpload);
        uploadProgressBar.setScaleY(6f);

        initDatePicker();
        insertDate=findViewById(R.id.modDate);

        Intent originalIntent=getIntent();
        myTrip=originalIntent.getParcelableExtra("trip");

        if (myTrip!=null){
            insertDate.setText(myTrip.getDate());
            //choosePhoto.setEnabled(false);
            descriptionText.setText(myTrip.getDescription());
            urlPath=myTrip.getUrlPath();
            if (urlPath!=""){
                new ImageDownloader(image).execute(urlPath);
            }else{
              image.setImageResource(R.drawable.trip);
            }
            tripId= myTrip.getId();
            tripCreation=false;
            validation.setText("MODIFY");

        }else{
            insertDate.setText(getTodayDate());
            //choosePhoto.setEnabled(true);
            tripCreation=true;
        }

        storage=FirebaseStorage.getInstance();
        storageRef=storage.getReference();

        FirebaseFirestore db= FirebaseFirestore.getInstance();

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

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
                tripDate=insertDate.getText().toString();
                boolean isFormatCorrect=Utilities.checkFormat(getApplicationContext(),tripDate);
                if (isFormatCorrect && !tripDescription.equals("")){
                    checkTrip();
                }
            }
        });

        insertDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
    }

    private void initDatePicker(){
       DatePickerDialog.OnDateSetListener dateSetListener= new DatePickerDialog.OnDateSetListener() {
           @Override
           public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    month=month+1;
                    String date=makeDateString(year,month,day);
                    insertDate.setText(date);
           }
       };

       Calendar cal =Calendar.getInstance();
       int year=cal.get(Calendar.YEAR);
       int month=cal.get(Calendar.MONTH);
       int day=cal.get(Calendar.DAY_OF_MONTH);

       int style= AlertDialog.THEME_HOLO_DARK;
       datePickerDialog= new DatePickerDialog(this,style,dateSetListener,year,month,day);
    }

    public String makeDateString(int year,int month,int day){
        String strMonth="";
        String strDay="";
        if (day<10){
            strDay="0"+String.valueOf(day);
        }else{
            strDay=String.valueOf(day);
        }
        if (month<10){
            strMonth="0"+String.valueOf(month);
        }else{
            strMonth= String.valueOf(month);
        }

        return strDay+"/"+strMonth+"/"+String.valueOf(year);
    }

    private String getTodayDate(){
        Calendar cal=Calendar.getInstance();
        int year=cal.get(Calendar.YEAR);
        int month=cal.get(Calendar.MONTH);
        month=month+1;
        int day=cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(year,month,day);
    }

    private void createTrip(){

        String email=firebaseUser.getEmail();
        emailUsers= new ArrayList<>();
        emailUsers.add(email);
        Map<String,Object> selectMap=new HashMap<>();
        selectMap.put("urlPath",urlPath);
        selectMap.put("date",insertDate.getText().toString());
        selectMap.put("description",descriptionText.getText().toString());
        selectMap.put("id","0");
        selectMap.put("users",emailUsers);
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        db.collection("trips")
                .add(selectMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        tripId = documentReference.getId();
                        if (tripUri!=null){
                            upLoadPicture(tripId);
                        }else{
                            updateBaseDate(tripId);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = e.toString();
                    }
                });
    }

    private void checkTrip(){
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        db.collection("trips")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document:task.getResult()){
                                Map<String, Object> map = document.getData();
                                if (map.get("date").toString().equals(insertDate.getText().toString()) && map.get("description").toString().equals(descriptionText.getText().toString())) {
                                    tripExists = true;
                                    break;
                                }
                            }
                            if (tripCreation){
                                if (!tripExists){
                                    createTrip();
                                }else{
                                    Toast.makeText(TripEditActivity.this,"This trip already exists",Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                if (!tripExists) {
                                    Intent intent= new Intent();
                                    myTrip.setDate(insertDate.getText().toString());
                                    myTrip.setDescription(descriptionText.getText().toString());
                                    intent.putExtra("edit", myTrip);
                                    setResult(RESULT_OK,intent);
                                    updateBaseDate(tripId);
                                } else {
                                    if (tripUri!=null){
                                        upLoadPicture(tripId);
                                    }else{
                                        finish();
                                    }
                                }
                            }
                        }else{
                            Toast.makeText(TripEditActivity.this,"Error getting documents",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void upLoadPicture(String tripId){
        StorageReference userImageRef=storageRef.child("imageTrips/"+tripId);
        userImageRef.putFile(tripUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        urlPath = task.getResult().toString();
                        Handler handler= new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                uploadProgressBar.setProgress(0);
                            }
                        },5000);
                        updateBaseDate(tripId);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TripEditActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot tasksnapshot) {
                double progress=(100*tasksnapshot.getBytesTransferred()/tasksnapshot.getTotalByteCount());
                uploadProgressBar.setProgress((int)progress);
            }
        });
    }

    private void updateBaseDate(String tripId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference tripRef = db.collection("trips").document(tripId);
        Map<String,Object> updatedMap=new HashMap<>();
        updatedMap.put("urlPath",urlPath);
        updatedMap.put("id",tripId);
        updatedMap.put("date",insertDate.getText().toString());
        updatedMap.put("description",descriptionText.getText().toString());
        tripRef.update(updatedMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(TripEditActivity.this,"Data was successfully saved in database",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TripEditActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}