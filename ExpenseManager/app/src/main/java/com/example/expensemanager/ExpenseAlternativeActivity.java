package com.example.expensemanager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExpenseAlternativeActivity extends AppCompatActivity {

    private Button addPayer;
    private Button savePayers;
    private Button addExpenseUsers;
    private RecyclerView recyclerView;
    private EditText expenseDescriptionView;
    private EditText expenseAmountView;
    private EditText expenseDateView;
    private Spinner spinner;
    private String expenseDescription;
    private int expenseAmount;
    private String expenseDate;
    private TripExpense tripExpense;
    private Trip trip;
    private int previousPosition;
    private ArrayList<User> tripUsers=new ArrayList<>();
    private boolean noUsers=false;
    private Map<String,Integer> expensePayers;
    private String oldUriPath;
    private ArrayList<PayerUser> payerUsers = new ArrayList<PayerUser>() ;
    private PayerUser payerUser;
    private int currentPosition=-1;
    private AdapterAlternativePayerUser adapterPayerUser;
    private Expense expense;
    private boolean noPayer=true;
    private boolean expenseExists;
    private String expenseId;
    private ArrayList<User> users= new ArrayList<>();
    private ActivityResultLauncher<Intent> expenseUsersResultLauncher;
    private boolean isExpenseCreation;
    private ArrayList<User> expenseUsers=new ArrayList<>();
    private ArrayAdapter<User> adapter;
    private int expensePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_alternative);
        expenseDescriptionView=findViewById(R.id.editTextExpenseDescription);
        expenseAmountView=findViewById(R.id.editTextExpenseAmount);
        expenseDateView=findViewById(R.id.editTextExpenseDate);
        addPayer=findViewById(R.id.saveExpense);
        savePayers=findViewById(R.id.expenseValidation);
        addExpenseUsers=findViewById(R.id.addExpenseUsers);
        recyclerView=findViewById(R.id.recyclerExpenseView);
        spinner=findViewById(R.id.expenseSpinner);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        //definition of recyclerview layout parameters
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        //reading data intent coming from TripViewActivity
        //trip and item position in expenses recyclerview are included in Bundle and passed in intent
        Intent intent =getIntent();
        trip=intent.getParcelableExtra("tripExpenseCreate");
        if (trip!=null){
            isExpenseCreation=true;
            tripUsers=trip.getUsers();
            previousPosition=-1;
            Toast.makeText(this,"You must fill description, amount and date and finally add the users of this expense",Toast.LENGTH_SHORT).show();
            addPayer.setEnabled(false);
            addPayer.setVisibility(View.INVISIBLE);
            savePayers.setEnabled(false);
            savePayers.setVisibility(View.INVISIBLE);
            spinner.setEnabled(false);
            spinner.setVisibility(View.INVISIBLE);
            recyclerView.setEnabled(false);
            recyclerView.setVisibility(View.INVISIBLE);

        }else{
            isExpenseCreation=false;
            trip=intent.getParcelableExtra("tripExpenseEdition");
            previousPosition=intent.getIntExtra("position",0);
            expenseId=trip.getExpenses().get(previousPosition).getId();
            expenseUsers=trip.getExpenses().get(previousPosition).getUsers();
            tripUsers=trip.getUsers();
            if (expenseUsers.size()==0){
                noUsers=true;
            }
        }

        /*if (tripUsers.size()==0){
            noUsers=true;
        }*/

        if (isExpenseCreation){
            expenseDescriptionView.setText("");
            expenseAmountView.setText("0");
            expenseDateView.setText("");

        }else{
            expenseDescriptionView.setText(trip.getExpenses().get(previousPosition).getDescription());
            expenseDateView.setText(trip.getExpenses().get(previousPosition).getDate());
            expenseAmountView.setText(""+trip.getExpenses().get(previousPosition).getAmount());
            expensePayers=trip.getExpenses().get(previousPosition).getPayers();


            for (Map.Entry<String,Integer>set :expensePayers.entrySet()){
                for (User myUser:expenseUsers){
                    if (myUser.getName().equals(set.getKey())){
                        oldUriPath= myUser.getUriPath();
                        break;
                    }
                }
                payerUser=new PayerUser(set.getKey(),set.getValue(),oldUriPath);
                payerUsers.add(payerUser);
            }
        }
        adapter= new ArrayAdapter<User>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,expenseUsers);
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

        expenseUsersResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode()==RESULT_OK && result.getData()!=null){
                    trip=result.getData().getParcelableExtra("addExpenseUser");
                    for (Expense myExpense:trip.getExpenses()){
                        if (myExpense.getDescription().equals(expenseDescriptionView.getText().toString())) {
                            if (expenseUsers.size()>0){
                                expenseUsers=new ArrayList<>();
                            }
                            for (User myUser:myExpense.getUsers()){
                                expenseUsers.add(myUser);

                            }
                            break;
                        }
                    }
                    if (!recyclerView.isEnabled()){
                        recyclerView.setEnabled(true);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    if (!spinner.isEnabled()){
                        spinner.setEnabled(true);
                        spinner.setVisibility(View.VISIBLE);
                    }
                    if (!savePayers.isEnabled()){
                        savePayers.setEnabled(true);
                        savePayers.setVisibility(View.VISIBLE);
                    }
                    if (!addPayer.isEnabled()){
                        addPayer.setEnabled(true);
                        addPayer.setVisibility(View.VISIBLE);
                    }
                    adapter= new ArrayAdapter<User>(getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,expenseUsers);
                    spinner.setAdapter(adapter);
                }
            }
        });

        addExpenseUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utilities.checkFormat(getApplicationContext(),expenseDateView.getText().toString()) && !expenseDescriptionView.getText().toString().equals("") && Utilities.checkAmountFormat(getApplicationContext(),expenseAmountView.getText().toString())){
                    if (Integer.parseInt(expenseAmountView.getText().toString())!=0){
                        Intent usersIntent= new Intent(ExpenseAlternativeActivity.this,TripUsersActivity.class);
                        if (isExpenseCreation){
                            if(trip.getExpenses().size()==0){
                                Expense newExpense= new Expense(expenseDescriptionView.getText().toString(),Integer.parseInt(expenseAmountView.getText().toString()),expenseDateView.getText().toString(),"");
                                trip.addExpense(newExpense);
                            }else{
                                boolean expenseCreated=false;
                                for (Expense checkExpense:trip.getExpenses()){
                                    if (checkExpense.getDescription().equals(expenseDescriptionView.getText().toString())){
                                        expenseCreated=true;
                                        break;
                                    }
                                }
                                if (!expenseCreated){
                                    Expense newExpense= new Expense(expenseDescriptionView.getText().toString(),Integer.parseInt(expenseAmountView.getText().toString()),expenseDateView.getText().toString(),"");
                                    trip.addExpense(newExpense);
                                }
                            }
                            expensePosition=trip.getExpenses().size()-1;
                        }else{
                            //trip.getExpenses().get(previousPosition).setDescription(expenseDescriptionView.getText().toString());
                            expensePosition=previousPosition;
                        }
                        usersIntent.putExtra("addUserToExpense",trip);
                        usersIntent.putExtra("expensePosition",expensePosition);
                        expenseUsersResultLauncher.launch(usersIntent);
                    }else{
                        Toast.makeText(ExpenseAlternativeActivity.this,"The amount cannot be 0",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ExpenseAlternativeActivity.this,"Some general expense data is wrong or missing",Toast.LENGTH_SHORT).show();
                }
            }
        });

        addPayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utilities.checkFormat(getApplicationContext(),expenseDateView.getText().toString()) && !expenseDescriptionView.getText().toString().equals("") && Utilities.checkAmountFormat(getApplicationContext(),expenseAmountView.getText().toString())){
                    if (Integer.parseInt(expenseAmountView.getText().toString())!=0){
                        expenseAmount=Integer.parseInt(expenseAmountView.getText().toString());
                        if (!noUsers){
                            int myIndex=0;
                            noPayer=true;
                            for (int i=0;i<payerUsers.size();i++){
                                if (payerUsers.get(i).getName().equals(expenseUsers.get(currentPosition).getName())){
                                    noPayer=false;
                                    myIndex=i;
                                    break;
                                }
                            }
                            if(noPayer){
                                String uriPath="";
                                for (User myUser:expenseUsers){
                                    if (myUser.getName().equals(expenseUsers.get(currentPosition).getName())){
                                        uriPath=myUser.getUriPath();
                                    }
                                }
                                if (payerUsers.size()==0){
                                    payerUser=new PayerUser(expenseUsers.get(currentPosition).getName(),expenseAmount,uriPath);
                                    payerUsers.add(payerUser);
                                    adapterPayerUser.notifyItemChanged(payerUsers.size()-1);
                                }else{
                                    int totalAmount=0;

                                    for (PayerUser myPayerUser:payerUsers){
                                        totalAmount +=myPayerUser.getAmount();
                                    }
                                    int listEnableChangeSize=payerUsers.size();
                                    for(int k=0;k<payerUsers.size();k++){
                                        if(!payerUsers.get(k).isCalculated()){
                                            totalAmount -=payerUsers.get(k).getAmount();
                                            listEnableChangeSize -=1;
                                        }
                                    }
                                    if (listEnableChangeSize>=1 && totalAmount>0) {
                                        int averageAmount = totalAmount / (listEnableChangeSize + 1);
                                        int remainderAmount = totalAmount % (listEnableChangeSize + 1);
                                        for (int k = 0; k < payerUsers.size(); k++) {
                                            if (payerUsers.get(k).isCalculated()) {
                                                payerUsers.get(k).setAmount(averageAmount);
                                                adapterPayerUser.notifyItemChanged(k);
                                            }
                                        }
                                        payerUser = new PayerUser(expenseUsers.get(currentPosition).getName(), averageAmount, uriPath);
                                        payerUsers.add(payerUser);
                                        adapterPayerUser.notifyItemInserted(payerUsers.size() - 1);
                                        int count = 0;
                                        for (int k = 0; k < payerUsers.size(); k++) {
                                            if (count == remainderAmount) {
                                                break;
                                            } else {
                                                if (payerUsers.get(k).isCalculated()) {
                                                    count++;
                                                    payerUsers.get(k).setAmount(payerUsers.get(k).getAmount() + 1);
                                                    adapterPayerUser.notifyItemChanged(k);
                                                }
                                            }
                                        }
                                    }else if (listEnableChangeSize==0 || totalAmount==0){
                                        payerUser = new PayerUser(tripUsers.get(currentPosition).getName(),0, uriPath);
                                        payerUsers.add(payerUser);
                                        adapterPayerUser.notifyItemInserted(payerUsers.size() - 1);
                                    }
                                }
                            }else{
                               Toast.makeText(getApplicationContext(),payerUser.getName()+" is already a payer",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"There are no users in this trip",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"The amount of the expense cannot be 0", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Data are missing and/or data format is incorrect",Toast.LENGTH_SHORT).show();
                }
            }
        });

        savePayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utilities.checkFormat(getApplicationContext(),expenseDateView.getText().toString()) && !expenseDescriptionView.getText().toString().equals("") && Utilities.checkAmountFormat(getApplicationContext(),expenseAmountView.getText().toString())){
                    if (Integer.parseInt(expenseAmountView.getText().toString())!=0){
                        if (!noUsers){
                            if (previousPosition==-1){
                                //expense=new Expense(expenseDescription,0,expenseDate);
                                expense=trip.getExpenses().get(trip.getExpenses().size()-1);
                                for (PayerUser myPayerUser:payerUsers){
                                    expense.AddNewPayer(myPayerUser.getName(),myPayerUser.getAmount());
                                }
                                expense.setAmount(Integer.parseInt(expenseAmountView.getText().toString()));
                                expense.setDescription(expenseDescriptionView.getText().toString());
                                expense.setDate(expenseDateView.getText().toString());
                                //trip.addExpense(expense);
                            }else{
                                expense=trip.getExpenses().get(previousPosition);
                                expense.setAmount(Integer.parseInt(expenseAmountView.getText().toString()));
                                expense.setDescription(expenseDescriptionView.getText().toString());
                                expense.setDate(expenseDateView.getText().toString());
                                for (PayerUser myPayerUser:payerUsers){
                                    expense.AddNewPayer(myPayerUser.getName(),myPayerUser.getAmount());
                                }
                            }
                            int totalAmount=0;
                            for (Map.Entry<String,Integer>set :expense.getPayers().entrySet()){
                                totalAmount += set.getValue();
                            }
                            if (totalAmount==expense.getAmount()){

                                Intent expenseBackIntent=new Intent();
                                expenseBackIntent.putExtra("backExpenseTrip",trip);
                                setResult(RESULT_OK,expenseBackIntent);
                                if (isExpenseCreation){
                                    checkExpense();;
                                }else{
                                    updateExpense(trip.getExpenses().get(previousPosition).getId());
                                }
                                finish();

                            }else{
                                Toast.makeText(getApplicationContext(),"Total expenseAmount does not fit with sum of payers individual amount. Modify payers!!",Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(getApplicationContext(),"You must add at least one payer",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Amount must be different from 0",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Data are incorrect",Toast.LENGTH_SHORT).show();
                }
            }
        });


        adapterPayerUser =new AdapterAlternativePayerUser(payerUsers,ExpenseAlternativeActivity.this);
        recyclerView.setAdapter(adapterPayerUser);
    }

    void checkExpense(){
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        CollectionReference expenseRef=db.collection("trips").document(trip.getId()).collection("expenses");
        expenseRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    expenseExists=false;
                    for (QueryDocumentSnapshot document:task.getResult()){
                        Expense previousExpense= document.toObject(Expense.class);
                        if (previousExpense.getDate().equals(expenseDateView.getText().toString()) && previousExpense.getDescription().equals(expenseDescriptionView.getText().toString())) {
                            expenseExists = true;
                            break;
                        }
                    }
                    if (!expenseExists){
                        createExpense();
                    }else{
                        Toast.makeText(ExpenseAlternativeActivity.this,"This expense already exists",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ExpenseAlternativeActivity.this,"Some problems during firebase reading",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ExpenseAlternativeActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    void createExpense(){
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        CollectionReference expenseRef=db.collection("trips").document(trip.getId()).collection("expenses");
        Expense newExpense=trip.getExpenses().get(trip.getExpenses().size()-1);
        expenseRef.add(newExpense).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                expenseId = documentReference.getId();
                trip.getExpenses().get(trip.getExpenses().size()-1).setId(expenseId);
                updateExpense(expenseId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ExpenseAlternativeActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    void updateExpense(String expenseId){
        FirebaseFirestore db= FirebaseFirestore.getInstance();
        DocumentReference documentReference=db.collection("trips").document(trip.getId()).collection("expenses").document(expenseId);
        Map<String,Object> map= new HashMap<>();
        map.put("id",expenseId);
        if (!isExpenseCreation){
            map.put("description",trip.getExpenses().get(previousPosition).getDescription());
            map.put("date",trip.getExpenses().get(previousPosition).getDate());
            map.put("amount",trip.getExpenses().get(previousPosition).getAmount());
            map.put("amount",trip.getExpenses().get(previousPosition).getAmount());
            map.put("users",trip.getExpenses().get(previousPosition).getUsers());
            map.put("payers",trip.getExpenses().get(previousPosition).getPayers());
        }

        documentReference.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ExpenseAlternativeActivity.this,"Data was successfully saved in database",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ExpenseAlternativeActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}