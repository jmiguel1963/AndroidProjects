package com.example.expensemanager;

import android.net.Uri;

public class PayerUser {
    private String name;
    private int amount;
    private Uri uri;

    PayerUser(String name, int amount,Uri uri){
        this.name=name;
        this.amount=amount;
        this.uri=uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
