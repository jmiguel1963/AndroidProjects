package com.example.expensemanager;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Trip implements Parcelable{

    private String urlPath;
    private String date;
    private String description;
    private ArrayList<Expense> expenses;
    private ArrayList<User> users;
    private String id;

    public Trip(String urlPath, String date, String description, String id) {
        this.urlPath = urlPath;
        this.date = date;
        this.description = description;
        this.id=id;
        expenses=new ArrayList<Expense>();
        users=new ArrayList<User>();

    }

    protected Trip(Parcel in) {
        urlPath = in.readString();
        date = in.readString();
        description = in.readString();
        id=in.readString();
        expenses = in.createTypedArrayList(Expense.CREATOR);
        users = in.createTypedArrayList(User.CREATOR);
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addExpense(Expense expense){
        expenses.add(expense);
    }

    public void addUser(User user){
        users.add(user);
    }

    public void clearExpenses(){
        expenses=new ArrayList<Expense>();
    }

    public ArrayList<Expense> getExpenses(){
        return this.expenses;
    }

    public ArrayList<User> getUsers() {
        return this.users;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(urlPath);
        parcel.writeString(date);
        parcel.writeString(description);
        parcel.writeString(id);
        parcel.writeTypedList(expenses);
        parcel.writeTypedList(users);
    }
}
