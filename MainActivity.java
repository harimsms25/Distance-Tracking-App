package com.example.distancetrackingapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

import java.io.IOException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String LocationKey = "locationsList";

    public static final String TimesKey = "TimesList";

    public static final String SaveLat = "Lat";
    public static final String SaveLon = "Lon";

    public static final String SaveAddress = "Addy";

    public static final String SaveDistance = "KDDD";

    private static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    private static final int PERMISSION_REQ = 100;

    TextView lat, lon, adress, disValue, latLand, lonLand, addressLand, disValueLand;

    LocationManager locationManager;

    long time;

    float distance;

    List<Address> adresses;

    Geocoder geocoder;

    ArrayList<LocationParceable> allPlaces;

    ArrayList<Long> times;

    boolean saving = false;

    boolean saved = false;
    long startTime = SystemClock.elapsedRealtime();

    ListView listed;

    Long pastTime = (long)0;

    LocationListener locationListener;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        lat = (TextView) findViewById(R.id.textView);
        lon = (TextView) findViewById(R.id.textView7);
        adress = (TextView) findViewById(R.id.textView2);
        disValue = (TextView) findViewById(R.id.textView3);
        time = 10;
        distance = 1;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        allPlaces = new ArrayList<>();
        times = new ArrayList<>();
        listed = findViewById(R.id.listView);
        latLand = (TextView)findViewById(R.id.textView6);
        lonLand = (TextView)findViewById(R.id.textView8);
        addressLand = (TextView)findViewById(R.id.textView5);
        disValueLand =(TextView) findViewById(R.id.textView4);


        CustomAdapter adapter = new CustomAdapter(this, R.layout.adapter_layout, allPlaces, times);
        listed.setAdapter(adapter);


        ActivityCompat.requestPermissions(this,new String[]{PERMISSION_ACCESS_COARSE_LOCATION,PERMISSION_ACCESS_FINE_LOCATION}, PERMISSION_REQ);

        geocoder = new Geocoder(this, Locale.US);

        if(savedInstanceState != null){
            allPlaces = savedInstanceState.getParcelableArrayList(LocationKey);
            long[] temp = savedInstanceState.getLongArray(TimesKey);

            times.clear();
            for(int i = 0; i < temp.length; i++){
                times.add(temp[i]);
            }

            CustomAdapter adapterNew = new CustomAdapter(MainActivity.this, R.layout.adapter_layout, allPlaces, times);
            listed.setAdapter(adapterNew);

            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                latLand.setText(savedInstanceState.getString(SaveLat));
                lonLand.setText(savedInstanceState.getString(SaveLon));
                addressLand.setText(savedInstanceState.getString(SaveAddress));
                disValueLand.setText(savedInstanceState.getString(SaveDistance));
            }else{
                lat.setText(savedInstanceState.getString(SaveLat));
                lon.setText(savedInstanceState.getString(SaveLon));
                adress.setText(savedInstanceState.getString(SaveAddress));
                disValue.setText(savedInstanceState.getString(SaveDistance));
            }

            saving = false;
            saved = true;

        }



        //adresses = geocoder.getFromLocation();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && !saving) {
                    locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        if(!saved) {
                            if (allPlaces.size() > 0) {
                                long timer = ((SystemClock.elapsedRealtime() - startTime)) / 1000 ;
                                times.add(timer - pastTime);
                                pastTime = timer;
                            }

                            Log.d("Location", "" + location.getLongitude() + location.getLatitude());
                            //latLon.setText("Longitude:" + location.getLongitude() + " Latitiude: " + location.getLatitude());
                            try {
                                allPlaces.add(new LocationParceable(location));
                            } catch (NullPointerException e) {

                            }

                            try {

                                adresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                                //adress.setText(adresses.get(0).getAddressLine(0));

                                double sum = 0;

                                if (allPlaces.size() > 0) {
                                    for (int i = 1; i < allPlaces.size(); i++) {
                                        sum += allPlaces.get(i).getLocation().distanceTo(allPlaces.get(i - 1).getLocation());
                                    }
                                }
                                //long timer = ((SystemClock.elapsedRealtime() - startTime))/1000%60;
                                //disValue.setText("distance: " + sum);
                                String Lat = " Latitiude: " + location.getLatitude();
                                String Lon = "Longitude:" + location.getLongitude();
                                String addy = adresses.get(0).getAddressLine(0);
                                String dissy = "distance: " + sum;

                                updateAll(Lon,Lat,addy,dissy);

                                CustomAdapter adapter = new CustomAdapter(MainActivity.this, R.layout.adapter_layout, allPlaces, times);
                                listed.setAdapter(adapter);


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{
                            saved = false;
                        }

                    }
                };



                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, distance, locationListener);
                }catch(SecurityException e){
                    e.printStackTrace();
                }


            }
            else{
                ActivityCompat.requestPermissions(this,new String[]{PERMISSION_ACCESS_COARSE_LOCATION,PERMISSION_ACCESS_FINE_LOCATION}, PERMISSION_REQ);
            }
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        locationManager.removeUpdates(locationListener);
        //saved = true;
        outState.putParcelableArrayList(LocationKey,allPlaces);
        long[] temp = new long[times.size()];
        for(int i = 0; i < temp.length; i++){
            temp[i] = times.get(i);
        }
        outState.putLongArray(TimesKey,temp);
        saving = true;
        //locationManager.removeUpdates(location -> {});

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            outState.putString(SaveLat,latLand.getText().toString());
            outState.putString(SaveLon,lonLand.getText().toString());
            outState.putString(SaveAddress,addressLand.getText().toString());
            outState.putString(SaveDistance,disValueLand.getText().toString());
        }else{
            outState.putString(SaveLat,lat.getText().toString());
            outState.putString(SaveLon,lon.getText().toString());
            outState.putString(SaveAddress,adress.getText().toString());
            outState.putString(SaveDistance,disValue.getText().toString());
        }


    }
    public void updateAll(String Lon, String Lat, String address, String distance){
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            latLand.setText(Lat);
            lonLand.setText(Lon);
            addressLand.setText(address);
            disValueLand.setText(distance);
        }else{
            lat.setText(Lat);
            lon.setText(Lon);
            adress.setText(address);
            disValue.setText(distance);
        }
    }

}