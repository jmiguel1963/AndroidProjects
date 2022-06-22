package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TripUsersActivity extends AppCompatActivity {

    private Spinner spinner;
    private RecyclerView recycleViewTripUsers;
    private Trip userTrip,expenseTrip;
    private ArrayList<User> users= new ArrayList<>();
    private Button userSelection,btnBack;
    private int position;
    private ArrayList<User> tripUsers=new ArrayList<User>();
    private AdapterUser adapterUser;
    private ArrayAdapter<User> adapter;
    private String tripId;
    private boolean isExpenseUsers;
    private ArrayList<User> expenseUsers=new ArrayList<>();
    private FirebaseUser currentUser;
    private Expense expense;
    private String expenseId="";
    private String expenseDescription;
    private ArrayList<User> ownUser= new ArrayList<>();
    private int expensePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_users);
        spinner=findViewById(R.id.spinner);
        recycleViewTripUsers=findViewById(R.id.recyclerListUserView);
        recycleViewTripUsers.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        userSelection=findViewById(R.id.buttonValidation);
        btnBack=findViewById(R.id.buttonBack);

        //users=SharedPrefConfig.readListPref(this);
        Intent intent=getIntent();
        userTrip=intent.getParcelableExtra("addUserToTrip");
        if (userTrip==null){
            isExpenseUsers=true;
            expenseTrip= intent.getParcelableExtra("addUserToExpense");
            expensePosition=intent.getIntExtra("expensePosition",0);
            expense=expenseTrip.getExpenses().get(expensePosition);
            //users=expenseTrip.getUsers();
            for(User myUser:expenseTrip.getUsers()){
                users.add(myUser);
            }
            if (expense.getUsers().size()>0){
                for (User myUser:expenseTrip.getExpenses().get(expensePosition).getUsers()){
                    expenseUsers.add(myUser);
                }
            }
        }else{
            FirebaseAuth mAuth=FirebaseAuth.getInstance();
            currentUser=mAuth.getCurrentUser();
            tripId=userTrip.getId();
            isExpenseUsers=false;
            if (userTrip.getUsers().size()!=0){
                tripUsers= new ArrayList<>();
                for (User myUser:userTrip.getUsers()){
                    tripUsers.add(myUser);
                }
            }
        }

        //adapter= new ArrayAdapter<User>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,users);
        //spinner.setAdapter(adapter);
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
                if (!isExpenseUsers){
                    if (tripUsers.size()==0){
                        userTrip.addUser(users.get(position));
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
                            userTrip.addUser(users.get(position));
                            tripUsers.add(users.get(position));
                        }
                    }
                    adapterUser=new AdapterUser(tripUsers);
                }else{
                    if (expenseUsers.size()==0){
                        expense.addUser(users.get(position));
                        expenseUsers.add(users.get(position));
                    }else{
                        boolean userExists=false;
                        for (User myUser:expenseUsers){
                            if (users.get(position).getName().equals(myUser.getName())){
                                userExists=true;
                                break;
                            }
                        }
                        if (!userExists){
                            expense.addUser(users.get(position));
                            expenseUsers.add(users.get(position));
                        }
                    }
                    adapterUser=new AdapterUser(expenseUsers);
                }
                recycleViewTripUsers.setAdapter(adapterUser);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBack= new Intent();
                if (!isExpenseUsers){
                    /*for (User myUser:ownUser){
                        userTrip.addUser(myUser);
                    }*/
                    Log.i("hola","tripUsers size "+userTrip.getUsers().size());
                    intentBack.putExtra("addTripUser",userTrip);
                    setResult(RESULT_OK,intentBack);
                    updateTripUsers();
                }else{
                    if (expenseTrip.getExpenses().size()!=0){
                        intentBack.putExtra("addExpenseUser",expenseTrip);
                        setResult(RESULT_OK,intentBack);
                        finish();
                    }else{
                        Toast.makeText(TripUsersActivity.this,"A user must added for this expense",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        if (!isExpenseUsers){
            adapterUser=new AdapterUser(tripUsers);
        }else{
            adapterUser=new AdapterUser(expenseUsers);
        }

        recycleViewTripUsers.setAdapter(adapterUser);
        adapter= new ArrayAdapter<User>(TripUsersActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,users);
        spinner.setAdapter(adapter);
        if (!isExpenseUsers){
            readRegisteredUsers();
        }
    }

    void readRegisteredUsers(){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference userRef=db.collection("users");
        userRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc:queryDocumentSnapshots){
                    User user=doc.toObject(User.class);
                    if (!user.getEmail().equals(currentUser.getEmail())){
                        users.add(user);
                    }else{
                        ownUser.add(user);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TripUsersActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    void updateTripUsers(){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference tripRef=db.collection("trips").document(tripId);
        ArrayList<String> emails= new ArrayList<>();
        for (User myUser:userTrip.getUsers()){
            emails.add(myUser.getEmail());
        }
        Map<String,Object> map=new HashMap<>();
        map.put("users",emails);
        tripRef.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(TripUsersActivity.this,"Users saved in current Trip",Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TripUsersActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}