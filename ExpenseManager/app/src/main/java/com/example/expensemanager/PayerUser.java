package com.example.expensemanager;

import android.net.Uri;

public class PayerUser {
    private String name;
    private int amount;
    private String uriPath;
    private boolean isCalculated;
    private int debt;

    PayerUser(String name, int amount,String uriPath){
        this.name=name;
        this.amount=amount;
        this.uriPath=uriPath;
        this.isCalculated=true;
        this.debt=0;
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

    public String getUriPath() {
        return uriPath;
    }

    public void setUriPath(String uriPath) {
        this.uriPath = uriPath;
    }

    public boolean isCalculated() {
        return isCalculated;
    }

    public void setCalculated(boolean isCalculated) {
        this.isCalculated = isCalculated;
    }

    public int getDebt() {
        return debt;
    }

    public void setDebt(int debt) {
        this.debt = debt;
    }
}
