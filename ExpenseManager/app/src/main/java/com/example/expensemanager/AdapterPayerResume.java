package com.example.expensemanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterPayerResume extends RecyclerView.Adapter<AdapterPayerResume.ViewHolderPayerResume> {
    private ArrayList<PayerUserDebt> payerUsers;

    public AdapterPayerResume(ArrayList<PayerUserDebt> payerUsers){
        this.payerUsers=payerUsers;
    }

    @NonNull
    @Override
    public AdapterPayerResume.ViewHolderPayerResume onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_payer_resume,parent,false);
        return new AdapterPayerResume.ViewHolderPayerResume(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPayerResume holder, int position) {
        holder.assignPayerResume(payerUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return payerUsers.size();
    }

    public class ViewHolderPayerResume extends RecyclerView.ViewHolder{

        ImageView imagePayerUserView;
        TextView namePayerUserView;
        TextView amountCompletedUserView;
        TextView amountDueUserView;

        public ViewHolderPayerResume(@NonNull View itemView) {
            super(itemView);
            imagePayerUserView=itemView.findViewById(R.id.imageAlternativeListPayer);
            namePayerUserView=itemView.findViewById(R.id.nameListPayer);
            amountCompletedUserView=itemView.findViewById(R.id.amountCompletePayer);
            amountDueUserView=itemView.findViewById(R.id.amountDuePayer);
        }

        public void assignPayerResume(PayerUserDebt payerUser){
            if (!payerUser.getUriPath().equals("")){
                new ImageDownloader(imagePayerUserView).execute(payerUser.getUriPath());
                //imagePayerUserView.setImageResource(R.drawable.user_avatar);
            }else{
                imagePayerUserView.setImageResource(R.drawable.user_avatar);
            }
            namePayerUserView.setText(payerUser.getName());
            amountCompletedUserView.setText(String.valueOf(payerUser.getAmount()));
            amountDueUserView.setText(String.valueOf(payerUser.getDebt()));
        }
    }
}
