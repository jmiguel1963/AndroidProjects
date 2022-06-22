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
    private ItemClickListener clickListener;


   public interface ItemClickListener{
        void onItemClick(View view,int position);
        boolean onItemLongClick(View view,int position);
    }

    public AdapterExpense(ArrayList<Expense> expenses){
        this.expenses=expenses;
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

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class ViewHolderExpense extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

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
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null)
                clickListener.onItemClick(view,getBindingAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (clickListener != null)
                clickListener.onItemLongClick(view,getBindingAdapterPosition());
                return true;
        }
    }
}