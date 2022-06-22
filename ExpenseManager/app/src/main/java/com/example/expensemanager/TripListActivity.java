package com.example.expensemanager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TripListActivity extends AppCompatActivity implements AdapterTrip.ItemClickListener {

    private RecyclerView recycler;
    private ArrayList<Trip> trips=new ArrayList<>();
    private FloatingActionButton createTrip;
    private ActivityResultLauncher<Intent>tripActivityResultLauncher;
    private AdapterTrip adapter;
    private ImageButton editUserProfile;
    private Trip resultTrip;
    private FirebaseUser currentUser;
    private int currentPosition=-1;
    private boolean everyID;
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);
        createTrip=findViewById(R.id.floatingActionButton);
        editUserProfile=findViewById(R.id.userProfileButton);

        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();

        tripActivityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK && result.getData()!=null){
                    resultTrip=result.getData().getParcelableExtra("resultTrip");
                    trips.set(currentPosition,resultTrip);
                    adapter.notifyItemChanged(currentPosition);
                }
            }
        });

        createTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createTripIntent=new Intent(TripListActivity.this,TripEditActivity.class);
                startActivity(createTripIntent);
            }
        });

        editUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userProfileIntent=new Intent(TripListActivity.this,UserProfileActivity.class);
                startActivity(userProfileIntent);
            }
        });

        recycler=findViewById(R.id.recyclerExpenseView);
        adapter=new AdapterTrip(trips);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        adapter.setClickListener(this);
        recycler.setAdapter(adapter);
        tripsCollectionListing();
    }

    void tripsCollectionListing(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference tripsRef = db.collection("trips");
        listenerRegistration=tripsRef.whereArrayContains("users", currentUser.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Toast.makeText(TripListActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    Log.i("hola","problema");
                    return;
                }
                for(DocumentChange doc:value.getDocumentChanges()){
                    if (doc.getType()==DocumentChange.Type.ADDED){
                        Map<String,Object> map= new HashMap<>();
                        map=doc.getDocument().getData();
                        String date = map.get("date").toString();
                        String description = map.get("description").toString();
                        String urlPath = map.get("urlPath").toString();
                        String id=map.get("id").toString();
                        Trip newTrip = new Trip(urlPath, date, description,id);
                        trips.add(newTrip);
                    }else if (doc.getType()==DocumentChange.Type.MODIFIED){
                        everyID=true;
                        Map<String,Object> map= new HashMap<>();
                        map=doc.getDocument().getData();
                        String date = map.get("date").toString();
                        String description = map.get("description").toString();
                        String urlPath = map.get("urlPath").toString();
                        String id=map.get("id").toString();
                        for(int i=0;i<trips.size();i++){
                            if (trips.get(i).getId().equals("0")){
                                Trip newTrip = new Trip(urlPath, date, description,id);
                                trips.set(i,newTrip);
                                everyID=false;
                                break;
                            }
                        }
                        if (everyID){
                            int currentPosition=-1;
                            for(int i=0;i<trips.size();i++){
                                if (id.equals(trips.get(i).getId())){
                                    currentPosition=i;
                                    break;
                                }
                            }
                            Trip newTrip = new Trip(urlPath, date, description,id);
                            trips.set(currentPosition,newTrip);
                        }
                    }else if (doc.getType()==DocumentChange.Type.REMOVED){
                        Map<String,Object> map= new HashMap<>();
                        String id=doc.getDocument().getId();
                        int currentPosition=-1;
                        for(int i=0;i<trips.size();i++){
                            if (id.equals(trips.get(i).getId())){
                                currentPosition=i;
                                break;
                            }
                        }
                        trips.remove(currentPosition);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        currentPosition=position;
        Intent intentView= new Intent(this,TripViewActivity.class);
        intentView.putExtra("itemTrip",trips.get(position));
        tripActivityResultLauncher.launch(intentView);
    }

    @Override
    protected void onDestroy() {
        listenerRegistration.remove();
        FirebaseAuth.getInstance().signOut();
        super.onDestroy();
    }
}