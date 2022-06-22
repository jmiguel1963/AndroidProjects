package com.example.expensemanager;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.ViewHolderUser>{

    private ArrayList<User> users;

    public AdapterUser(ArrayList<User> users){
        this.users=users;
    }

    @NonNull
    @Override
    public AdapterUser.ViewHolderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user,parent,false);
        return new AdapterUser.ViewHolderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUser.ViewHolderUser holder, int position) {
        holder.assignUser(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolderUser extends RecyclerView.ViewHolder{

        ImageView imageUserView;
        TextView nameUserView;
        TextView userNameUserView;

        public ViewHolderUser(@NonNull View itemView) {
            super(itemView);
            imageUserView=itemView.findViewById(R.id.imageListUser);
            nameUserView=itemView.findViewById(R.id.nameListUser);
            userNameUserView=itemView.findViewById(R.id.userNameList);
        }

        public void assignUser(User user){
            if(!user.getUriPath().equals("")){
                new ImageDownloader(imageUserView).execute(user.getUriPath());
                //imageUserView.setImageResource(R.drawable.user_avatar);
            }else{
                imageUserView.setImageResource(R.drawable.user_avatar);
            }
            //imageUserView.setImageURI(Uri.parse(user.getUriPath()));
            nameUserView.setText(user.getName());
            userNameUserView.setText(user.getEmail());

        }
    }
}
