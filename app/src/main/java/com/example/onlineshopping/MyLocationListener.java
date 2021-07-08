package com.example.onlineshopping;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class MyLocationListener implements LocationListener {
    private Context activityContext;

    public MyLocationListener(Context C)
    {
        activityContext = C;
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(activityContext,location.getLatitude()+", "+location.getLongitude(),Toast.LENGTH_LONG).show();

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Toast.makeText(activityContext,"GPS Enabled",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(activityContext,"GPS Disabled",Toast.LENGTH_LONG).show();
    }
}