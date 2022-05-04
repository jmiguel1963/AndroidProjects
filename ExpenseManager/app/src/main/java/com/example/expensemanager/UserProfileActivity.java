package com.example.expensemanager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

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

        Intent intent=getIntent();
        user=intent.getParcelableExtra("modUser");
        editName.setText(user.getName());
        editUserName.setText(user.getEmail());
        if (user.getUriPath()!=null){
            userUri=Uri.parse(user.getUriPath());
            image.setImageURI(userUri);
        }
        name=editName.getText().toString();

        users=SharedPrefConfig.readListPref(getApplicationContext());
        for (int i=0;i<users.size();i++){
            if (users.get(i).getName().equals(name)){
                userNumber=i;
                break;
            }
        }

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
                userName=editUserName.getText().toString();
                user.setEmail(userName);
                user.setUriPath(userUri.toString());
                users.set(userNumber,user);
                SharedPrefConfig.writeListPref(getApplicationContext(),users);
                finish();
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
}