package com.example.expensemanager;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Trip implements Parcelable{

    private Uri uri;
    private String date;
    private String description;
    private ArrayList<Expense> expenses;
    private ArrayList<User> users;

    public Trip(Uri uri, String date, String description) {
        this.uri = uri;
        this.date = date;
        this.description = description;
        expenses=new ArrayList<Expense>();
        users=new ArrayList<User>();
    }

    protected Trip(Parcel in) {
        uri = in.readParcelable(Uri.class.getClassLoader());
        date = in.readString();
        description = in.readString();
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

    public void addExpense(Expense expense){
        expenses.add(expense);
    }

    public void addUser(User user){
        users.add(user);
    }

    public ArrayList<Expense> getExpenses(){
        return this.expenses;
    }

    public ArrayList<User> getUsers() {
        return this.users;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
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
    public void writeToParcel(Parcel parcel,int i) {
        parcel.writeParcelable(uri,i);
        parcel.writeString(date);
        parcel.writeString(description);
    }
}
