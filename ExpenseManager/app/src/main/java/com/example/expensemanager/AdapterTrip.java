package com.example.expensemanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterTrip extends RecyclerView.Adapter<AdapterTrip.ViewHolderTrip> {

    private ArrayList<Trip> trips;
    final AdapterTrip.OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(View view,Trip item);
    }

    public AdapterTrip(ArrayList<Trip> trips,AdapterTrip.OnItemClickListener listener) {
        this.trips = trips;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolderTrip onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,null,false);
        return new ViewHolderTrip(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderTrip holder, int position) {
        holder.assignTrip(trips.get(position));
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class ViewHolderTrip extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView descriptionText;
        TextView dateText;

        public ViewHolderTrip(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            descriptionText=itemView.findViewById(R.id.descriptionText);
            dateText=itemView.findViewById(R.id.dateText);
        }

        public void assignTrip(Trip trip) {
            imageView.setImageURI(trip.getUri());
            descriptionText.setText(trip.getDescription());
            dateText.setText(trip.getDate());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(view,trip);
                    //notifyItemChanged(getBindingAdapterPosition());
                }
            });
        }
    }
}
