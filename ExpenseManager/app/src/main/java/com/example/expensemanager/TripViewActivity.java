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

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripViewActivity extends AppCompatActivity implements AdapterExpense.ItemClickListener {

    private ImageView imgTrip;
    private TextView tripDescription;
    private TextView tripDate;
    private Trip trip;
    private FloatingActionButton addNewExpense;
    private ActivityResultLauncher<Intent> activityExpenseResultLauncher,activityEditTripResultLauncher;
    private ActivityResultLauncher<Intent> activityNewUserResultLauncher;
    private AdapterExpense adapter;
    private ImageButton editTrip,editResume,deleteTrip;
    private RecyclerView recycler,recyclerPayer;
    private Button addNewUser;
    private TripExpense tripExpense;
    private int selectedPosition;
    private String oldUriPath;
    private String urlPath;
    private String tripId;
    private ArrayList<Expense> expenses=new ArrayList<>();
    private Expense expense;
    private ArrayList<Expense> testExpenses=new ArrayList<>();
    private ArrayList<User> currentUsers=new ArrayList<>();
    private ListenerRegistration listenerRegistration;
    private ArrayList<String> expenseIds=new ArrayList<>();
    private boolean isFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_view);


        imgTrip=findViewById(R.id.imgTripView);
        tripDescription=findViewById(R.id.tripTextViewDescription);
        tripDate=findViewById(R.id.tripTextViewDate);
        recycler=findViewById(R.id.recyclerViewExpense);
        recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerPayer=findViewById(R.id.recyclerViewPayers);
        recyclerPayer.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        Intent intent=getIntent();
        trip=intent.getParcelableExtra("itemTrip");
        urlPath=trip.getUrlPath();

        if (!urlPath.equals("")){
            new ImageDownloader(imgTrip).execute(urlPath);
            //imgTrip.setImageResource(R.drawable.trip);
        }
        else{
            imgTrip.setImageResource(R.drawable.trip);
        }

        tripDescription.setText(trip.getDescription());
        tripDate.setText(trip.getDate());
        tripId=trip.getId();

        readRegisteredUsers();

        addNewExpense=findViewById(R.id.btnAddNewExpense);
        editTrip=findViewById(R.id.btnEditTrip);
        deleteTrip=findViewById(R.id.btnDeleteTrip);
        editResume=findViewById(R.id.btnResumeTrip);
        addNewUser=findViewById(R.id.btnAddNewUser);


        activityExpenseResultLauncher= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK && result.getData()!=null){
                    /*trip=result.getData().getParcelableExtra("backExpenseTrip");
                    if (selectedPosition==-1){
                        expenses.add(trip.getExpenses().get(trip.getExpenses().size()-1));
                        adapter.notifyItemInserted(expenses.size()-1);
                    }else{
                        expense=trip.getExpenses().get(selectedPosition);
                        expenses.set(selectedPosition,expense);
                        adapter.notifyItemChanged(selectedPosition);
                    }*/
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
                    trip=result.getData().getParcelableExtra("addTripUser");
                }
            }
        });

        addNewExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent expenseIntent=new Intent(TripViewActivity.this,ExpenseAlternativeActivity.class);
                selectedPosition=-1;
                expenseIntent.putExtra("tripExpenseCreate",trip);
                activityExpenseResultLauncher.launch(expenseIntent);
            }
        });

        deleteTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new android.app.AlertDialog.Builder(TripViewActivity.this)
                        .setTitle("Deleting trip: " + trip.getDescription())
                        .setMessage("Do you really want to delete this trip?")

                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteTripDatabase();
                                TripViewActivity.this.finish();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Cancel operation nothing to do.
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
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

        editResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editResumeIntent=new Intent(TripViewActivity.this,TripResumeActivity.class);
                editResumeIntent.putExtra("trip",trip);
                startActivity(editResumeIntent);
            }
        });

        addNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newUserIntent = new Intent(TripViewActivity.this, TripUsersActivity.class);
                newUserIntent.putExtra("addUserToTrip",trip);
                activityNewUserResultLauncher.launch(newUserIntent);
            }
        });

        adapter=new AdapterExpense(expenses);
        adapter.setClickListener(this);
        recycler.setAdapter(adapter);
        expensesCollectionListing();
    }

    void expensesCollectionListing(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef=db.collection("trips").document(tripId);
        listenerRegistration=docRef.collection("expenses").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    Toast.makeText(TripViewActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                    return;
                }
                for(DocumentChange doc:value.getDocumentChanges()){
                    if (doc.getType()==DocumentChange.Type.ADDED){
                        Expense newExpense=doc.getDocument().toObject(Expense.class);
                        expenses.add(newExpense);
                    }else if (doc.getType()==DocumentChange.Type.MODIFIED){
                        Expense newExpense=doc.getDocument().toObject(Expense.class);
                        if (selectedPosition==-1){
                            expenses.set(expenses.size()-1,newExpense);
                        }else{
                            expenses.set(selectedPosition,newExpense);
                        }
                    }else if (doc.getType()==DocumentChange.Type.REMOVED){
                        for (int i=0;i<expenses.size();i++){
                            if (expenses.get(i).getId().equals(doc.getDocument().getId())){
                                expenses.remove(i);
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                if (expenses.size()>0){
                    if (trip.getExpenses().size()>0){
                        trip.clearExpenses();
                    }
                }
                for (Expense myExpense:expenses){
                    trip.addExpense(myExpense);
                }
            }
        });
    }

    void readRegisteredUsers(){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference userRef=db.collection("users");
        userRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc:queryDocumentSnapshots){
                    User testUser=doc.toObject(User.class);
                    currentUsers.add(testUser);
                }
                recoveringUsers();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TripViewActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    void recoveringUsers(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference tripsRef = db.collection("trips").document(tripId);
        tripsRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> emails=(ArrayList<String>) documentSnapshot.get("users");
                for(String str:emails){
                    for(User myUser:currentUsers){
                        if (str.equals(myUser.getEmail())){
                            trip.addUser(myUser);
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               Toast.makeText(TripViewActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intentView= new Intent(this,ExpenseAlternativeActivity.class);
        intentView.putExtra("tripExpenseEdition",trip);
        intentView.putExtra("position",position);
        selectedPosition=position;
        activityExpenseResultLauncher.launch(intentView);
    }

    @Override
    public boolean onItemLongClick(View view, int position) {
        view.findViewById(R.id.recycleLinear).setBackgroundColor(Color.LTGRAY);
        int childViewsCount=(int)recycler.getChildCount();
        for (int c=0;c<childViewsCount;c++){
            if (c!=position){
                recycler.getChildAt(c).findViewById(R.id.recycleLinear).setBackgroundColor(Color.parseColor("#00FFFF"));
            }
        }

        ArrayList<PayerUser> payerUsers= new ArrayList<PayerUser>();
        PayerUser payerUser;

        ArrayList<User> expenseUsers=trip.getExpenses().get(position).getUsers();
        for (Map.Entry<String,Integer>set :trip.getExpenses().get(position).getPayers().entrySet()){
            for (User myUser:expenseUsers){
                if (myUser.getName().equals(set.getKey())){
                    oldUriPath= myUser.getUriPath();
                    break;
                }
            }
            payerUser=new PayerUser(set.getKey(),set.getValue(),oldUriPath);
            payerUsers.add(payerUser);
        }
        AdapterPayer adapterPayer=new AdapterPayer(payerUsers);
        recyclerPayer.setAdapter(adapterPayer);

        return true;
    }

    void deleteTripDatabase(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("trips").document(trip.getId()).collection("expenses")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                WriteBatch batch=FirebaseFirestore.getInstance().batch();

                List<DocumentSnapshot> snapshotList=queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot doc:snapshotList){
                    batch.delete(doc.getReference());
                }
                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        checkTripImage(trip.getId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TripViewActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TripViewActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    void deleteFinalTrip(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("trips").document(trip.getId())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(TripViewActivity.this,"Trip completely deleted",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TripViewActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    void checkTripImage(String id) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference userImageRef = storageRef.child("imageTrips/");
        userImageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                int count = 0;
                for (StorageReference item : listResult.getItems()) {
                    String[] parts = item.toString().split("/");
                    if (id.equals(parts[parts.length - 1])) {
                        count++;
                        deleteTripImage(id);
                    }
                }
                if (count == 0) {
                    deleteFinalTrip();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TripViewActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    void deleteTripImage(String id){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference userImageRef = storageRef.child("imageTrips/"+id);
        userImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(TripViewActivity.this,"TripImage deleted",Toast.LENGTH_SHORT).show();
                deleteFinalTrip();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TripViewActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }




    @Override
    protected void onDestroy() {
        listenerRegistration.remove();
        super.onDestroy();
    }
}
