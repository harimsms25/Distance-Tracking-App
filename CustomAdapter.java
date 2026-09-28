package com.example.distancetrackingapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CustomAdapter extends ArrayAdapter<LocationParceable> {
    List<LocationParceable> list;

    List<Long> timesList;
    Context context;
    int xmlResource;
    public CustomAdapter(@NonNull Context context, int resource, @NonNull List<LocationParceable> s, @NonNull List<Long> n){
        super(context, resource, s);
        xmlResource = resource;
        list = s;
        timesList = n;
        this.context = context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(MainActivity.LAYOUT_INFLATER_SERVICE);
        View adapterLayout = layoutInflater.inflate(xmlResource, null);

        TextView addy = adapterLayout.findViewById(R.id.address);
        TextView time = adapterLayout.findViewById(R.id.time);
        try {
            Location location = list.get(position).getLocation();
            Geocoder geocoder = new Geocoder(context, Locale.US);
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            addy.setText(addresses.get(0).getAddressLine(0));
            if(position >= timesList.size()){
                time.setText("Currently Here");
            }
            else {
                time.setText(timesList.get(position) + "");
            }

        }catch(IOException e){
            e.printStackTrace();
        }catch(IndexOutOfBoundsException e){
            Log.d("Here","List Size:"+list.size());
            Log.d("Here2","What it's calling:"+position);
        }




        return adapterLayout;

    }
}
