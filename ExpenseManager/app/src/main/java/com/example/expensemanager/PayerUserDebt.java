package com.example.expensemanager;

public class PayerUserDebt {

    private String name;
    private int amount;
    private String uriPath;
    private int debt;

    public PayerUserDebt(String name, int amount, String uriPath, int debt) {
        this.name = name;
        this.amount = amount;
        this.uriPath = uriPath;
        this.debt = debt;
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

    public int getDebt() {
        return debt;
    }

    public void setDebt(int debt) {
        this.debt = debt;
    }
}
