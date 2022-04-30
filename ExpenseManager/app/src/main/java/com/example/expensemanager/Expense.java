package com.example.expensemanager;

import java.util.HashMap;
import java.util.Map;

public class Expense {
    private String description;
    private int amount;
    private String date;
    private Map<String,Integer> payers;

    Expense(String description, int amount, String date){
        this.description = description;
        this.amount = amount;
        this.date = date;
        payers = new HashMap<String, Integer>();
    }

    public int getAmount () {
        return this.amount;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getAmountSpentByUser(String userName) {
        return payers.get(userName);
    }

    public void AddNewPayer (String name, int amount) {
        payers.put(name,amount);
    }
    public void RemovePayer (String name) {
        payers.remove(name);
    }
}
