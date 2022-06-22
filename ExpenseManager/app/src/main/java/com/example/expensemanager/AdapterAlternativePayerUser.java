package com.example.expensemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterAlternativePayerUser extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<PayerUser> payerUsers;
    private Activity activity;

    public AdapterAlternativePayerUser(ArrayList<PayerUser> payerUsers,Activity activity){
        this.payerUsers=payerUsers;
        this.activity=activity;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch(viewType){
            case 0: {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View listItem= layoutInflater.inflate(R.layout.list_item_expense_text, parent, false);
                return new ViewHolderNoPayer(listItem);
            }
            case 1:
            default:
            {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                View listItem= layoutInflater.inflate(R.layout.list_item_payer_wt_3buttons, parent, false);
                return new ViewHolderPayer(listItem);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder globalHolder, int position) {
        switch(globalHolder.getItemViewType()){
            case 0:
            {
                ViewHolderNoPayer holder=(ViewHolderNoPayer)globalHolder;
                holder.warningText.setText("The list of Payers is empty.You must add a Payer.");
                break;
            }
            case 1:
            {
                ViewHolderPayer holder=(ViewHolderPayer)globalHolder;
                if (!payerUsers.get(position).getUriPath().equals("")){
                    new ImageDownloader(holder.payerImageView).execute(payerUsers.get(position).getUriPath());
                    //holder.payerImageView.setImageResource(R.drawable.user_avatar);
                }else{
                    holder.payerImageView.setImageResource(R.drawable.user_avatar);
                }
                holder.payerNameView.setText(payerUsers.get(position).getName());
                holder.btn_amount.setText(""+payerUsers.get(position).getAmount());
                PayerUser payerUser=payerUsers.get(position);
                if (payerUser.isCalculated()){
                    holder.btnToogle.setBackgroundColor(Color.GREEN);
                }else{
                    holder.btnToogle.setBackgroundColor(Color.RED);
                }

                holder.btnToogle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position=holder.getBindingAdapterPosition();
                        payerUsers.get(position).setCalculated(!payerUsers.get(position).isCalculated());
                        if (payerUsers.get(position).isCalculated()){
                            view.findViewById(R.id.fixAmountListPayer).setBackgroundColor(Color.GREEN);
                        }else{
                            view.findViewById(R.id.fixAmountListPayer).setBackgroundColor(Color.RED);
                        }
                        int totalAmount=0;
                        int listEnableChangeSize=0;
                        for (int i=0;i<payerUsers.size();i++){
                            if (payerUsers.get(i).isCalculated()){
                                totalAmount +=payerUsers.get(i).getAmount();
                                listEnableChangeSize +=1;
                            }
                        }
                        if (listEnableChangeSize!=0){
                            int averageAmount=totalAmount/listEnableChangeSize;
                            int remainderAmount=totalAmount%listEnableChangeSize;
                            for (int i=0;i<payerUsers.size();i++){
                                if (payerUsers.get(i).isCalculated()){
                                    payerUsers.get(i).setAmount(averageAmount);
                                    notifyItemChanged(i);
                                }
                            }
                            int count=0;
                            for (int k=0;k<payerUsers.size();k++){
                                if (count==remainderAmount){
                                    break;
                                }else{
                                    if (payerUsers.get(k).isCalculated()){
                                        count++;
                                        payerUsers.get(k).setAmount(payerUsers.get(k).getAmount()+1);
                                        notifyItemChanged(k);
                                    }
                                }
                            }
                        }
                    }
                });

                holder.btn_amount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
                        builder.setTitle("Amount for "+payerUser.getName()+" ");
                        View viewInflated = LayoutInflater.from(activity).inflate(R.layout.amount_input,null, false);
                        final EditText input = viewInflated.findViewById(R.id.input);
                        input.setText(""+payerUser.getAmount());
                        builder.setView(viewInflated);

                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                                String inputAmount = input.getText().toString();
                                if (Utilities.checkAmountFormat(activity.getApplicationContext(),inputAmount)){
                                    int amount=Integer.parseInt(inputAmount);
                                    int pos= holder.getBindingAdapterPosition();
                                    int totalAmount=0;
                                    for (PayerUser myPayerUser:payerUsers){
                                        totalAmount +=myPayerUser.getAmount();
                                    }
                                    payerUser.setAmount(amount);
                                    notifyItemChanged(pos);
                                    totalAmount=totalAmount-amount;
                                    int listEnableChangeSize=payerUsers.size()-1;
                                    for (int j=0;j<payerUsers.size();j++){
                                        if(j!=pos && !payerUsers.get(j).isCalculated()){
                                            totalAmount -=payerUsers.get(j).getAmount();
                                            listEnableChangeSize -=1;
                                        }
                                    }
                                    if (listEnableChangeSize>=1 && totalAmount>0){
                                        int averageAmount=totalAmount/listEnableChangeSize;
                                        int remainderAmount=totalAmount%listEnableChangeSize;
                                        for (int j=0;j<payerUsers.size();j++){
                                            if (j!=pos && payerUsers.get(j).isCalculated()){
                                                payerUsers.get(j).setAmount(averageAmount);
                                                notifyItemChanged(j);
                                            }
                                        }
                                        int count=0;
                                        for (int k=0;k<payerUsers.size();k++){
                                            if (count==remainderAmount){
                                                break;
                                            }else{
                                                if (k!=pos && payerUsers.get(k).isCalculated()){
                                                    count++;
                                                    payerUsers.get(k).setAmount(payerUsers.get(k).getAmount()+1);
                                                    notifyItemChanged(k);
                                                }
                                            }
                                        }
                                    }else if (listEnableChangeSize>=1 && totalAmount<=0){
                                        for (int j=0;j<payerUsers.size();j++){
                                            if (j!=pos && payerUsers.get(j).isCalculated()){
                                                payerUsers.get(j).setAmount(0);
                                                notifyItemChanged(j);
                                            }
                                        }
                                    }
                                }else{
                                    Toast.makeText(activity.getApplicationContext(),"Number format is incorrect",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    }
                });

                holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(activity)
                                .setTitle("Do you really want to delete the payer " +  payerUser.getName() + "?")
                                .setMessage("")

                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                        int pos = holder.getBindingAdapterPosition();
                                        int totalAmount=0;
                                        for(PayerUser myPayerUser:payerUsers){
                                            totalAmount += myPayerUser.getAmount();
                                        }
                                        int totalMemoryAmount=totalAmount;
                                        payerUsers.remove(pos);
                                        notifyItemRemoved(pos);
                                        int listEnableChangeSize=payerUsers.size();
                                        if (payerUsers.size()!=0){
                                            for (PayerUser myPayerUser:payerUsers){
                                                if (!myPayerUser.isCalculated()){
                                                    totalAmount -= myPayerUser.getAmount();
                                                    listEnableChangeSize -=1;
                                                }
                                            }
                                            if (listEnableChangeSize>=1 && totalAmount>0){
                                                int averageAmount=totalAmount/listEnableChangeSize;
                                                int remainderAmount=totalAmount%listEnableChangeSize;
                                                for(int i=0; i<payerUsers.size();i++){
                                                    if (payerUsers.get(i).isCalculated()){
                                                        payerUsers.get(i).setAmount(averageAmount);
                                                        notifyItemChanged(i);
                                                    }
                                                }
                                                int count=0;
                                                for (int k=0;k<payerUsers.size();k++){
                                                    if (count==remainderAmount){
                                                        break;
                                                    }else{
                                                        if (payerUsers.get(k).isCalculated()){
                                                            count++;
                                                            payerUsers.get(k).setAmount(payerUsers.get(k).getAmount()+1);
                                                            notifyItemChanged(k);
                                                        }
                                                    }
                                                }
                                            }else if (listEnableChangeSize>=1 && totalAmount<=0){
                                                for (int j=0;j<payerUsers.size();j++){
                                                    if (payerUsers.get(j).isCalculated()){
                                                        payerUsers.get(j).setAmount(0);
                                                        notifyItemChanged(j);
                                                    }
                                                }
                                            }else if (listEnableChangeSize==0 && payerUsers.size()==1){
                                                for (int j=0;j<payerUsers.size();j++){
                                                    payerUsers.get(j).setAmount(totalMemoryAmount);
                                                    notifyItemChanged(j);
                                                }
                                            }
                                        }
                                    }
                                })

                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });
            }

        }
    }

    @Override
    public int getItemCount() {
        if (payerUsers.size() == 0){
            return 1;
        }
        return payerUsers.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (payerUsers.size()==0)
            return 0;
        return 1;
    }

    public class ViewHolderPayer extends RecyclerView.ViewHolder {

        public ImageView payerImageView;
        public TextView payerNameView;
        public Button btn_amount;
        public Button btn_delete;
        public ToggleButton btnToogle;

        public ViewHolderPayer(View itemView){
            super(itemView);
            payerImageView=itemView.findViewById(R.id.imageAlternativeListPayer);
            payerNameView=itemView.findViewById(R.id.nameAlternativeListPayer);
            btn_amount=itemView.findViewById(R.id.amountEditListPayer);
            btn_delete=itemView.findViewById(R.id.deleteListPayer);
            btnToogle=itemView.findViewById(R.id.fixAmountListPayer);
        }
    }

    public class ViewHolderNoPayer extends RecyclerView.ViewHolder {

        public TextView warningText;
        public ViewHolderNoPayer(View itemView) {
            super(itemView);
            warningText=itemView.findViewById(R.id.expenseText);
        }
    }
}
