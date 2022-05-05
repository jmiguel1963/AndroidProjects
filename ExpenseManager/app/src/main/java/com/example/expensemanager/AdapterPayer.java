package com.example.expensemanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterPayer extends RecyclerView.Adapter<AdapterPayer.ViewHolderPayer>{

    private ArrayList<PayerUser> payerUsers;

    public AdapterPayer(ArrayList<PayerUser> payerUsers){
        this.payerUsers=payerUsers;
    }

    @NonNull
    @Override
    public AdapterPayer.ViewHolderPayer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_payer_user,parent,false);
        return new AdapterPayer.ViewHolderPayer(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPayer.ViewHolderPayer holder, int position) {
        holder.assignPayer(payerUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return payerUsers.size();
    }

    public class ViewHolderPayer extends RecyclerView.ViewHolder{

        ImageView imagePayerView;

        public ViewHolderPayer(@NonNull View itemView) {
            super(itemView);
            imagePayerView=itemView.findViewById(R.id.imageListImagePayer);
        }

        public void assignPayer(PayerUser payerUser){
            imagePayerView.setImageURI(payerUser.getUri());
        }
    }
}
