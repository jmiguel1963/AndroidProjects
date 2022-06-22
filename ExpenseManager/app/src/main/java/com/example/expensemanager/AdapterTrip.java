package com.example.expensemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterTrip extends RecyclerView.Adapter<AdapterTrip.ViewHolderTrip> {

    private ArrayList<Trip> trips;
    private ItemClickListener clickListener;
    //private Context activityContext;

    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    public AdapterTrip(ArrayList<Trip> trips) {
        this.trips = trips;
    }

    @NonNull
    @Override
    public ViewHolderTrip onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_trip, null, false);
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

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public class ViewHolderTrip extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;
        TextView descriptionText;
        TextView dateText;

        public ViewHolderTrip(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            dateText = itemView.findViewById(R.id.dateText);
            itemView.setOnClickListener(this);
        }

        public void assignTrip(Trip trip) {
            if (!trip.getUrlPath().equals("")) {
                new ImageDownloader(imageView).execute(trip.getUrlPath());
                //imageView.setImageResource(R.drawable.trip);
            }else{
                imageView.setImageResource(R.drawable.trip);
            }

            descriptionText.setText(trip.getDescription());
            dateText.setText(trip.getDate());
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null)
                clickListener.onClick(view, getBindingAdapterPosition());
        }
    }
}
