package com.example.gpslocation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    TextView tv_lat, tv_lon, tv_address, tv_distance, tv_totalDistance, tv_time, tv_totalTime;
    LocationManager locationManager;
    Location oldLocation;
    SystemClock systemClock;
    Long timeInitial=systemClock.elapsedRealtime(), time=timeInitial, time2;
    float totalDistance=0;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_lat = findViewById(R.id.textView_latitude);
        tv_lon = findViewById(R.id.textView_longitude);
        tv_address = findViewById(R.id.textView_address);
        tv_distance = findViewById(R.id.textView_distance);
        tv_totalDistance = findViewById(R.id.textView_totalDistance);
        tv_time = findViewById(R.id.textView_time);
        tv_totalTime = findViewById(R.id.textView_totalTime);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("TAG", "Requesting permission");
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 1);
            return;
        } else locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("TAG", "onLocationChanged");
        time2 = systemClock.elapsedRealtime();
        List <Address> addresses = null;
        Geocoder geocoder = new Geocoder(this, Locale.US);
        try {
            Log.d("TAG", "address");
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tv_address.setText("Address: " + addresses.get(0).getAddressLine(0));
        } catch (IOException e) {
            Log.d("TAG", "address fail");
            e.printStackTrace();
        }
        tv_lat.setText("Latitude: " + location.getLatitude());
        tv_lon.setText("Longitude: " + location.getLongitude());

        if (count>0) {
            tv_distance.setText("Distance Traveled: " + (float)(location.distanceTo(oldLocation) / 1609.34) + " miles");
            totalDistance += (location.distanceTo(oldLocation) / 1609.34);
            Log.d("TAG", totalDistance+"");
            tv_totalDistance.setText("Total Distance: " + totalDistance + " miles");
            tv_time.setText("Time Traveled: " + (time2-time) + " ms");
            tv_totalTime.setText("Total Time: " + (time2-timeInitial) + " ms");
        }
        oldLocation = location;
        time = time2;
        if (count == 0) count++;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("TAG", "onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
                }  else {
                    Toast.makeText(this, "Permission for location denied", Toast.LENGTH_LONG);
                }
                return;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}