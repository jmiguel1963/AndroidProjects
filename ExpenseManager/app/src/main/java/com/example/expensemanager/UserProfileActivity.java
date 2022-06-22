package com.example.expensemanager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private User user;
    private ImageButton btnEditProfile;
    private ArrayList<User> users;
    private Button btnSave,btnLogout;
    private EditText editName,editUserName;
    private String name,userName;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Uri userUri;
    private ImageView image;
    private int userNumber;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private String uriPath;
    private String userId;
    private String userEmail;
    private String userPass;
    private Bitmap bitmap;
    private ProgressBar uploadProgressBar,downloadProgressBar;
    private File localFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        btnEditProfile=findViewById(R.id.btnEditProfile);
        btnLogout=findViewById(R.id.btnLogout);
        btnSave=findViewById(R.id.btnSave);
        editName=findViewById(R.id.editName);
        editUserName=findViewById(R.id.editUserName);
        image=findViewById(R.id.imgAvatar);
        uploadProgressBar=findViewById(R.id.progressBarUpload);
        uploadProgressBar.setVisibility(View.INVISIBLE);
        downloadProgressBar=findViewById(R.id.progressBarDownload);
        downloadProgressBar.setVisibility(View.INVISIBLE);
        downloadProgressBar.setScaleY(6f);

        storage=FirebaseStorage.getInstance();
        storageRef=storage.getReference();

        db= FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            downloadProgressBar.setVisibility(View.VISIBLE);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String,Object> map=document.getData();
                                if (firebaseUser.getEmail().equals(map.get("email").toString())){
                                    editName.setText(map.get("name").toString());
                                    editUserName.setText((map.get("email").toString()));
                                    editUserName.setFocusableInTouchMode(false);
                                    userId= document.getId();
                                    userEmail=map.get("email").toString();
                                    userPass=map.get("password").toString();
                                    uriPath=map.get("uriPath").toString();
                                    if (!uriPath.equals("")){

                                        try {
                                            localFile=File.createTempFile("expenseManagerTempFile",".jpg");
                                            String path = localFile.getAbsolutePath();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        StorageReference urlReference = FirebaseStorage.getInstance().getReferenceFromUrl(uriPath);

                                        urlReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Handler handler= new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        downloadProgressBar.setProgress(0);
                                                    }
                                                },5000);
                                                image.setImageURI(Uri.fromFile(localFile));
                                                deleteCache();
                                                downloadProgressBar.setVisibility(View.INVISIBLE);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                               Toast.makeText(UserProfileActivity.this,"Downloading failed",Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onProgress(@NonNull FileDownloadTask.TaskSnapshot tasksnapshot) {
                                                double progress=(100*tasksnapshot.getBytesTransferred()/tasksnapshot.getTotalByteCount());
                                                downloadProgressBar.setProgress((int)progress);
                                            }
                                        });
                                    }
                                }
                            }
                        } else {
                            Log.w("hello", "Error getting documents.", task.getException());
                        }
                    }
                });

        activityResultLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK && result.getData()!=null){
                    userUri = result.getData().getData();
                    image.setImageURI(userUri);
                }
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (pickPhoto.resolveActivity(getPackageManager())!=null){
                    activityResultLauncher.launch(pickPhoto);
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userUri!=null){
                    Toast.makeText(UserProfileActivity.this,"Uploading image...",Toast.LENGTH_LONG).show();
                    uploadProgressBar.setVisibility(View.VISIBLE);
                    upLoadPicture();
                }else{
                    if (!editName.getText().toString().equals("")){
                        updateBaseDate();
                    }
                    finish();
                    //Toast.makeText(UserProfileActivity.this,"No image has been chosen",Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( UserProfileActivity.this,MainActivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                startActivity( intent );
            }
        });

    }

    private void updateBaseDate(){
        User myUser= new User(editName.getText().toString(),userPass,userEmail,uriPath);
        Map<String,Object> updatedMap=new HashMap<>();
        updatedMap.put("email",userEmail);
        updatedMap.put("name",editName.getText().toString());
        updatedMap.put("password","");
        updatedMap.put("uriPath",uriPath);
        db.collection("users").document(userId).update(updatedMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UserProfileActivity.this,"Data was successfully saved in database",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void upLoadPicture(){
        StorageReference userImageRef=storageRef.child("imageUsers/"+userId);
        userImageRef.putFile(userUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        uriPath = task.getResult().toString();
                        Handler handler= new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                uploadProgressBar.setProgress(0);
                            }
                        },5000);
                        updateBaseDate();
                        finish();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot tasksnapshot) {
                double progress=(100*tasksnapshot.getBytesTransferred()/tasksnapshot.getTotalByteCount());
                uploadProgressBar.setProgress((int)progress);
            }
        });
    }

    void deleteCache(){
        final File cache = getCacheDir();
        File[] files = cache.listFiles();

        for ( final File file : files ) {
            if (file.getName().startsWith("expenseManager")){
                file.delete();
            }
        }
    }
}



                                        /*urlReference.getBytes(FIFTY_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                BitmapFactory.Options options = new BitmapFactory.Options();
                                                options.inJustDecodeBounds = false;
                                                bitmap=BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                                                image.setImageBitmap(bitmap);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(UserProfileActivity.this,"Image downloading failed",Toast.LENGTH_SHORT).show();
                                            }
                                        });*/



                                        /*FirebaseStorage.getInstance().getReferenceFromUrl(uriPath).getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri myUri) {
                                                        Picasso.get().load(Uri.parse(uriPath)).into(image);
                                                        //image.setImageURI(myUri);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                               Toast.makeText(UserProfileActivity.this,"UserImage download error",Toast.LENGTH_SHORT).show();
                                            }
                                        });*/

 /*if (!editName.getText().toString().equals("") && userUri!=null){
                    upLoadPicture();
                }*/
                /*userName=editUserName.getText().toString();
                user.setEmail(userName);
                user.setUriPath(userUri.toString());
                users.set(userNumber,user);
                SharedPrefConfig.writeListPref(getApplicationContext(),users);*/