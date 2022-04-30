package com.example.expensemanager;

import android.net.Uri;

public class User {
    private String name;
    private String email;
    private Uri uri;
    private int amountSpent;

    User (String name, String email, Uri uri){
        this.name = name;
        this.email = email;
        this.uri = uri;
        this.amountSpent = 0;
    }

    public void ResetAmount(){
        amountSpent = 0;
    }

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

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public int getAmountSpent() {
        return amountSpent;
    }

    public void AddAmountSpent (int amount) {
        this.amountSpent+= amount;
    }
}
