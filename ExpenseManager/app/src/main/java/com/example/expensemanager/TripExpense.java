package com.example.expensemanager;

import android.os.Parcel;
import android.os.Parcelable;

public class TripExpense implements Parcelable {

    private Trip trip;
    private int position;

    public TripExpense(Trip trip, int position) {
        this.trip = trip;
        this.position = position;
    }

    protected TripExpense(Parcel in) {
        trip = in.readParcelable(Trip.class.getClassLoader());
        position = in.readInt();
    }

    public static final Creator<TripExpense> CREATOR = new Creator<TripExpense>() {
        @Override
        public TripExpense createFromParcel(Parcel in) {
            return new TripExpense(in);
        }

        @Override
        public TripExpense[] newArray(int size) {
            return new TripExpense[size];
        }
    };

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(trip, i);
        parcel.writeInt(position);
    }
}
