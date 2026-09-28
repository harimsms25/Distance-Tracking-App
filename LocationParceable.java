package com.example.distancetrackingapp;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class LocationParceable implements Parcelable {
    private Location location;

    public LocationParceable(Parcel input){
        location=Location.CREATOR.createFromParcel(input);
    }

    public LocationParceable(Location location){
        this.location = location;
    }
    public Location getLocation(){
        return location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        location.writeToParcel(dest, flags);
    }
    public static final Parcelable.Creator<LocationParceable> CREATOR
            = new Parcelable.Creator<LocationParceable>() {
        public LocationParceable createFromParcel(Parcel in) {
            return new LocationParceable(in);
        }

        public LocationParceable[] newArray(int size) {
            return new LocationParceable[size];
        }
    };
}
