package com.example.expensemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TripResumeActivity extends AppCompatActivity {

    private RecyclerView recyclerResumeView;
    private ImageView imageResumeView;
    private TextView descriptionResumeView,amountResumeView;
    private String descriptionResume;
    private Trip trip;
    private ArrayList<Expense> expenses;
    private ArrayList<User> users;
    private Map<String,Integer> totalPayers=new HashMap<String,Integer>();
    private AdapterPayerResume adapterPayerResume;
    private ArrayList<PayerUserDebt> payerUsers = new ArrayList<>() ;
    private PayerUserDebt payerUser;
    private String uriPath;
    private ArrayList<Integer> averageAmount= new ArrayList<>();
    private ArrayList<Integer> remainderAmount= new ArrayList<>();
    private Map<String,Integer> myPayers=new HashMap<String,Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_resume);
        imageResumeView=findViewById(R.id.imageResume);
        descriptionResumeView=findViewById(R.id.resumeDescription);
        amountResumeView=findViewById(R.id.resumeAmount);
        recyclerResumeView=findViewById(R.id.recyclerExpenseResumeView);
        recyclerResumeView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        Intent intent=getIntent();
        trip=intent.getParcelableExtra("trip");
        descriptionResume=trip.getDescription();
        descriptionResumeView.setText("Description: "+descriptionResume);
        expenses=trip.getExpenses();
        if (!trip.getUrlPath().equals("")){
            new ImageDownloader(imageResumeView).execute(trip.getUrlPath());
        }else{
            imageResumeView.setImageResource(R.drawable.trip);
        }

        //imageResumeView.setImageURI(trip.getUri());
        int totalAmount=0;
        for (Expense expense:expenses){
            totalAmount += expense.getAmount();
        }
        amountResumeView.setText("Amount: "+totalAmount);
        users=trip.getUsers();
        for(User myUser:users){
            totalPayers.put(myUser.getName(),0);
        }

        for (int i=0;i<trip.getExpenses().size();i++){
            int numberUsers=trip.getExpenses().get(i).getUsers().size();
            int amountExpense=trip.getExpenses().get(i).getAmount();
            int average=(amountExpense/numberUsers);
            averageAmount.add(average);
            int remainder=(amountExpense%numberUsers);
            remainderAmount.add(remainder);
        }

        for (int i=0;i<trip.getExpenses().size();i++){
            int count=0;
            for (int j=0;j<trip.getExpenses().get(i).getUsers().size();j++){
                if (count==remainderAmount.get(i)){
                    break;
                }else{
                    count++;
                    if (myPayers.containsKey(trip.getExpenses().get(i).getUsers().get(j).getName())){
                       int prevAmount=myPayers.get(trip.getExpenses().get(i).getUsers().get(j).getName());
                       myPayers.put(trip.getExpenses().get(i).getUsers().get(j).getName(),prevAmount+1);
                    }else{
                        myPayers.put(trip.getExpenses().get(i).getUsers().get(j).getName(),1);
                    }
                }
            }
        }

        for (int i=0;i<trip.getExpenses().size();i++){
            for (int j=0;j<trip.getExpenses().get(i).getUsers().size();j++){
                if (myPayers.containsKey(trip.getExpenses().get(i).getUsers().get(j).getName())){
                    int prevAmount=myPayers.get(trip.getExpenses().get(i).getUsers().get(j).getName());
                    myPayers.put(trip.getExpenses().get(i).getUsers().get(j).getName(),prevAmount+averageAmount.get(i));
                }else{
                    myPayers.put(trip.getExpenses().get(i).getUsers().get(j).getName(),averageAmount.get(i));
                }
            }
        }

        for (Expense expense:expenses){
            for (Map.Entry<String,Integer>set :expense.getPayers().entrySet()){
                String payerName=set.getKey();
                int amount=set.getValue();
                int previousAmount=totalPayers.get(payerName);
                totalPayers.put(payerName,amount+previousAmount);
            }
        }
        for (Map.Entry<String,Integer>set :totalPayers.entrySet()){
            for (User myUser:users){
                if (myUser.getName().equals(set.getKey())){
                    uriPath= myUser.getUriPath();
                    break;
                }
            }
            payerUser=new PayerUserDebt(set.getKey(),set.getValue(),uriPath,0);
            payerUsers.add(payerUser);
        }

        for (PayerUserDebt payerUserDebt:payerUsers){
            for (Map.Entry<String,Integer>set: myPayers.entrySet()){
                if (payerUserDebt.getName().equals(set.getKey())) {
                    payerUserDebt.setDebt(set.getValue()-payerUserDebt.getAmount());
                }
            }
        }
        adapterPayerResume =new AdapterPayerResume(payerUsers);
        recyclerResumeView.setAdapter(adapterPayerResume);
    }
}