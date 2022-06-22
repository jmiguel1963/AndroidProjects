package com.example.expensemanager;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Expense implements Parcelable {
    private String description;
    private int amount;
    private String date;
    private String id;
    private Map<String,Integer> payers;
    private ArrayList<User> users;

    Expense(String description, int amount, String date,String id){
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.id=id;
        users=new ArrayList<>();
        payers = new HashMap<String, Integer>();
    }

    Expense(){

    }

    protected Expense(Parcel in) {
        description = in.readString();
        amount = in.readInt();
        date = in.readString();
        id=in.readString();
        users = in.createTypedArrayList(User.CREATOR);
        payers=new HashMap<String,Integer>();
        in.readMap(payers,Integer.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeInt(amount);
        dest.writeString(date);
        dest.writeString(id);
        dest.writeTypedList(users);
        dest.writeMap(payers);
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

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public Map<String, Integer> getPayers() {
        return payers;
    }

    public void addUser(User user){
        users.add(user);
    }

    public void removeUser(User user){
        users.remove(user);
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
