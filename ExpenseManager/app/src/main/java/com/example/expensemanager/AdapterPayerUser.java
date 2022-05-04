package com.example.expensemanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterPayerUser extends RecyclerView.Adapter<AdapterPayerUser.ViewHolderPayerUser>{

    private ArrayList<PayerUser> payerUsers;

    public AdapterPayerUser(ArrayList<PayerUser> payerUsers){
        this.payerUsers=payerUsers;
    }

    @NonNull
    @Override
    public AdapterPayerUser.ViewHolderPayerUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_payer,parent,false);
        return new AdapterPayerUser.ViewHolderPayerUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPayerUser.ViewHolderPayerUser holder, int position) {
            holder.assignPayerUser(payerUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return payerUsers.size();
    }

    public class ViewHolderPayerUser extends RecyclerView.ViewHolder{

        ImageView imagePayerUserView;
        TextView namePayerUserView;
        TextView amountPayerUserView;

        public ViewHolderPayerUser(@NonNull View itemView) {
            super(itemView);
            imagePayerUserView=itemView.findViewById(R.id.imageListPayer);
            namePayerUserView=itemView.findViewById(R.id.nameListPayer);
            amountPayerUserView=itemView.findViewById(R.id.amountListPayer);
        }

        public void assignPayerUser(PayerUser payerUser){
            imagePayerUserView.setImageURI(payerUser.getUri());
            namePayerUserView.setText(payerUser.getName());
            amountPayerUserView.setText(String.valueOf(payerUser.getAmount()));
        }
    }
}
