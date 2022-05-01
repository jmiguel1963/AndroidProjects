package com.example.expensemanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterExpense extends RecyclerView.Adapter<AdapterExpense.ViewHolderExpense> {

    private ArrayList<Expense> expenses;

    public AdapterExpense(ArrayList<Expense> expenses){
        this.expenses=expenses;
    }

    @NonNull
    @Override
    public ViewHolderExpense onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element,null,false);
        return new AdapterExpense.ViewHolderExpense(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderExpense holder, int position) {
        holder.assignExpense(expenses.get(position));
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public class ViewHolderExpense extends RecyclerView.ViewHolder {
        public ViewHolderExpense(@NonNull View itemView) {
            super(itemView);
        }

        public void assignExpense(Expense expense){

        }
    }
}
