package com.example.expensemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExpenseActivity extends AppCompatActivity {

    private Button saveExpense,expenseValidation;
    private EditText editExpenseDescription;
    private EditText editExpenseAmount;
    private EditText editExpenseDate;
    private Expense expense;
    private String expenseDescription;
    private int expenseAmount;
    private String expenseDate;
    private Trip trip;
    private Spinner spinner;
    private RecyclerView recyclerView;
    private ArrayList<User> tripUsers;
    private int currentPosition=-1;
    private int totalAmount=0;
    private ArrayList<PayerUser> payerUsers = new ArrayList<PayerUser>() ;
    private PayerUser payerUser;
    private TripExpense tripExpense;
    private int previousPosition;
    private AdapterPayerUser adapterPayerUser;
    private boolean noUsers=false;
    private Map<String,Integer> expensePayers;
    private String firstSelection;
    private boolean noPayer=true;
    private Uri oldUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        saveExpense=findViewById(R.id.saveExpense);
        editExpenseDescription=findViewById(R.id.editTextExpenseDescription);
        editExpenseAmount=findViewById(R.id.editTextExpenseAmount);
        editExpenseDate=findViewById(R.id.editTextExpenseDate);
        expenseValidation=findViewById(R.id.expenseValidation);
        recyclerView=findViewById(R.id.recyclerExpenseView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        spinner=findViewById(R.id.expenseSpinner);


        Intent intent =getIntent();
        tripExpense=intent.getParcelableExtra("tripExpense");
        trip=tripExpense.getTrip();
        previousPosition=tripExpense.getPosition();
        tripUsers=trip.getUsers();
        if (tripUsers.size()==0){
            noUsers=true;
        }

        if (previousPosition==-1){
            editExpenseDescription.setText("");
            editExpenseAmount.setText("0");
            editExpenseDate.setText("");

        }else{
           editExpenseDescription.setText(trip.getExpenses().get(previousPosition).getDescription());
           editExpenseDate.setText(trip.getExpenses().get(previousPosition).getDate());
           expensePayers=trip.getExpenses().get(previousPosition).getPayers();
           editExpenseDescription.setEnabled(false);
           editExpenseDate.setEnabled(false);
           for (int j=0;j<trip.getUsers().size();j++){
               if (expensePayers.containsKey(trip.getUsers().get(j).getName())){
                   spinner.setSelection(j);
                   firstSelection=trip.getUsers().get(j).getName();
                   editExpenseAmount.setText(String.valueOf(expensePayers.get(firstSelection)));
                   break;
               }
           }
            for (Map.Entry<String,Integer>set :expensePayers.entrySet()){
                for (User myUser:tripUsers){
                    if (myUser.getName().equals(set.getKey())){
                        oldUri=Uri.parse(myUser.getUriPath());
                        break;
                    }
                }
                payerUser=new PayerUser(set.getKey(),set.getValue(),oldUri);
                payerUsers.add(payerUser);
            }
        }

        ArrayAdapter<User> adapter= new ArrayAdapter<User>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,tripUsers);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentPosition=i;
                for (PayerUser myPayerUser:payerUsers){
                    if (tripUsers.get(currentPosition).getName().equals(myPayerUser.getName())) {
                        editExpenseAmount.setText(String.valueOf(myPayerUser.getAmount()));
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        saveExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!noUsers) {
                    expenseDescription=editExpenseDescription.getText().toString();
                    expenseAmount=Integer.parseInt(editExpenseAmount.getText().toString());
                    expenseDate=editExpenseDate.getText().toString();
                    if(!expenseDescription.equals("") && expenseAmount!=0 && !expenseDate.equals("")){
                        editExpenseDescription.setEnabled(false);
                        editExpenseDate.setEnabled(false);
                        int myIndex=0;
                        for (int i=0;i<payerUsers.size();i++){
                            if (payerUsers.get(i).getName().equals(tripUsers.get(currentPosition).getName())){
                                noPayer=false;
                                myIndex=i;
                                break;
                            }
                        }
                        if (noPayer){
                            Uri uri=null;
                            for (User myUser:tripUsers){
                                if (myUser.getName().equals(tripUsers.get(currentPosition).getName())){
                                    uri=Uri.parse(myUser.getUriPath());
                                }
                            }
                            payerUser=new PayerUser(tripUsers.get(currentPosition).getName(),expenseAmount,uri);
                            payerUsers.add(payerUser);
                        }else{
                          payerUsers.get(myIndex).setAmount(expenseAmount);
                        }
                        adapterPayerUser =new AdapterPayerUser(payerUsers);
                        recyclerView.setAdapter(adapterPayerUser);
                    }else{
                        Toast.makeText(getApplicationContext(),"At least one of the fields is empty",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"There are no users to be selected as payers",Toast.LENGTH_SHORT).show();
                }
            }
        });

        expenseValidation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!noUsers){
                    int totalAmount=0;
                    if (previousPosition==-1){
                        if (!expenseDescription.equals("")){
                            expense=new Expense(expenseDescription,0,expenseDate);
                            for (PayerUser myPayerUser:payerUsers){
                                expense.AddNewPayer(myPayerUser.getName(),myPayerUser.getAmount());
                                totalAmount +=myPayerUser.getAmount();
                            }
                            expense.setAmount(totalAmount);
                            trip.addExpense(expense);
                        }
                    }else{
                        expense=trip.getExpenses().get(previousPosition);
                        for (PayerUser myPayerUser:payerUsers){
                            expense.AddNewPayer(myPayerUser.getName(),myPayerUser.getAmount());
                            totalAmount += myPayerUser.getAmount();
                        }
                        expense.setAmount(totalAmount);
                    }
                }
                Intent expenseBackIntent=new Intent();
                expenseBackIntent.putExtra("backExpenseTrip",trip);
                setResult(RESULT_OK,expenseBackIntent);
                finish();
            }
        });
        adapterPayerUser =new AdapterPayerUser(payerUsers);
        recyclerView.setAdapter(adapterPayerUser);
    }
}