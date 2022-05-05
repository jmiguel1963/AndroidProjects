package com.example.expensemanager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterExpense extends RecyclerView.Adapter<AdapterExpense.ViewHolderExpense> {

    private ArrayList<Expense> expenses;
    //final AdapterExpense.OnItemClickListener listener;
    final AdapterExpense.ClickListener listener;
    /*public interface OnItemClickListener{
        void onItemClick(View view,Expense item);
    }*/

   public interface ClickListener{
        void onItemClick(View view,Expense item);
        boolean onItemLongClick(View view,Expense item);
    }

    //public AdapterExpense(ArrayList<Expense> expenses,AdapterExpense.OnItemClickListener listener){
    public AdapterExpense(ArrayList<Expense> expenses,AdapterExpense.ClickListener listener){
        this.expenses=expenses;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolderExpense onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element,parent,false);
        return new ViewHolderExpense(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderExpense holder, int position) {
        holder.assignExpense(expenses.get(position));
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public class ViewHolderExpense extends RecyclerView.ViewHolder {

        TextView descriptionViewTripText;
        TextView amountViewTripText;
        TextView dateViewTripText;
        public ViewHolderExpense(@NonNull View itemView) {
            super(itemView);
            descriptionViewTripText=itemView.findViewById(R.id.descriptionViewTrip);
            amountViewTripText=itemView.findViewById(R.id.amountViewTrip);
            dateViewTripText=itemView.findViewById(R.id.dateViewTrip);
        }

        public void assignExpense(Expense expense){
            descriptionViewTripText.setText(expense.getDescription());
            dateViewTripText.setText(expense.getDate());
            amountViewTripText.setText(""+expense.getAmount());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(view,expense);
                }
            });
            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(view,expense);
                }
            });*/
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemLongClick(view,expense);
                    return true;
                }
            });
        }
    }
}