package com.example.expensemanager;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{

    private String name;
    private String email;
    private String uriPath;
    private String password;
    //private int amountSpent;

    /*User(String email){
        this.email=email;
    }*/

    User (String name, String password,String email, String uriPath){
        this.name = name;
        this.email = email;
        this.uriPath = uriPath;
        this.password=password;
        //this.amountSpent = 0;
    }

    User(){

    }

    protected User(Parcel in) {
        name = in.readString();
        email = in.readString();
        password=in.readString();
        uriPath = in.readString();
        //amountSpent = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(uriPath);
        //dest.writeInt(amountSpent);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //public void ResetAmount(){
       // amountSpent = 0;
    //}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUriPath() {
        return uriPath;
    }

    public void setUriPath(String uriPath) {
        this.uriPath = uriPath;
    }

    //public int getAmountSpent() {
        //return amountSpent;
    //}

    //public void AddAmountSpent (int amount) {
     //   this.amountSpent+= amount;
    //}

    @Override
    public String toString() {
        return this.getName();
    }
}
