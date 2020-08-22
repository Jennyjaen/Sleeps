package com.projects.sleeps;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class GPSListener implements LocationListener {
    private GPSTracker gpsTracker;
    public GPSListener(GPSTracker tracker){
        gpsTracker=tracker;
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        gpsTracker.LogGPS(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}
