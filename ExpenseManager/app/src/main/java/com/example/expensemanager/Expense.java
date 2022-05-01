package com.example.expensemanager;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class Expense implements Parcelable {
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

    protected Expense(Parcel in) {
        description = in.readString();
        amount = in.readInt();
        date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeInt(amount);
        dest.writeString(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Expense> CREATOR = new Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };

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
