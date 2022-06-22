package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExpenseUserActivity extends AppCompatActivity {

    private EditText editExpenseDescription;
    private EditText editExpenseDate;
    private EditText editExpenseAmount;
    private Button expenseValidation;
    private Button userExpenseAddition;
    private Spinner spinner;
    private RecyclerView recyclerViewUserExpense;
    private Trip tripIntent;
    private ArrayList<User> tripUsers,expenseUsers;
    private ArrayList<Expense> tripExpenses;
    private int currentPosition;
    private AdapterUser adapterUser;
    private Expense expense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_user);

        editExpenseDescription=findViewById(R.id.editTextExpenseUserDescription);
        editExpenseDate=findViewById(R.id.editTextExpenseUserDate);
        editExpenseAmount=findViewById(R.id.editTextExpenseUserAmount);
        expenseValidation=findViewById(R.id.saveExpenseUser);
        userExpenseAddition=findViewById(R.id.expenseUserValidation);
        spinner=findViewById(R.id.expenseUserSpinner);
        recyclerViewUserExpense=findViewById(R.id.recyclerExpenseUserView);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        recyclerViewUserExpense.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        Intent intent =getIntent();
        tripIntent=intent.getParcelableExtra("tripExpense");

        tripUsers=tripIntent.getUsers();

        tripExpenses=tripIntent.getExpenses();

        editExpenseDescription.setText("");
        editExpenseAmount.setText("0");
        editExpenseDate.setText("");

        ArrayAdapter<User> adapter= new ArrayAdapter<User>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,tripUsers);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentPosition=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        userExpenseAddition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utilities.checkFormat(getApplicationContext(),editExpenseDate.getText().toString()) && !editExpenseDescription.getText().toString().equals("") && Utilities.checkAmountFormat(getApplicationContext(),editExpenseAmount.getText().toString())){
                    if (expenseUsers.size()==0){
                        //expenseUsers.add(tripusers.get)
                    }else{
                        /*boolean userExists=false;
                        for (User myUser:tripUsers){
                            if (users.get(position).getName().equals(myUser.getName())){
                                userExists=true;
                                break;
                            }
                        }
                        if (!userExists){
                            trip.addUser(users.get(position));
                            tripUsers.add(users.get(position));
                        }*/
                    }
                }
            }
        });

        expenseValidation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utilities.checkFormat(getApplicationContext(),editExpenseDate.getText().toString()) && !editExpenseDescription.getText().toString().equals("") && Utilities.checkAmountFormat(getApplicationContext(),editExpenseAmount.getText().toString())){
                    if(!editExpenseDate.getText().toString().equals("")){
                        //checkExpenseDescriptionExists
                        expense=new Expense(editExpenseDescription.getText().toString(),Integer.parseInt(editExpenseAmount.getText().toString()),editExpenseDate.getText().toString(),"");
                        checkExpense();
                    }else{
                        Toast.makeText(ExpenseUserActivity.this,"There must be a expense description",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        adapterUser=new AdapterUser(tripUsers);
        recyclerViewUserExpense.setAdapter(adapterUser);
    }

    private void checkExpense(){
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("trip").document(tripIntent.getId())
                .collection("expenses").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                            String expenseId=documentSnapshot.getId();
                            for (Expense expense:tripIntent.getExpenses()){
                                if (expenseId.equals(expense.getId())){
                                    updateExpense();
                                }else{
                                    createExpense();
                                }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("trip").document(tripIntent.getId())
                .collection("expenses").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots){

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateExpense(){

    }

    private void createExpense(){

        Map<String,Object> map=new HashMap<>();
        FirebaseFirestore db= FirebaseFirestore.getInstance();

        db.collection("trips").document(tripIntent.getId())
                .set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}