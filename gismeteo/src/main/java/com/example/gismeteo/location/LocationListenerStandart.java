package com.example.gismeteo.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public final class LocationListenerStandart implements LocationListener{
    public boolean request = false;
    private Location currentLocation;
    private LocationManager locationManager;
    private static volatile LocationListenerStandart instance;
    private LocationFound callback;
    String provider;

    private LocationListenerStandart(final Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(false);
        provider = locationManager.getBestProvider(criteria, true);
    }

    public static LocationListenerStandart getInstance(final Context context) {
        if (instance == null) {
            synchronized (LocationListenerStandart.class) {
                if (instance == null)
                    instance = new LocationListenerStandart(context);
            }
        }
        return instance;
    }

    public void clear() {
        instance = null;
    }

    public boolean providersEnabled(Context context) {
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            return true;
        } else {
            return false;
        }
    }
    public void setLocationFound(LocationFound callback)
    {
        this.callback = callback;
    }

    public void startLocation() {
        if(provider !=null){
            currentLocation = locationManager.getLastKnownLocation(provider);
            if(currentLocation != null && System.currentTimeMillis() - currentLocation.getTime() < 180 * 60 * 1000) {
                this.callback.locationFound(currentLocation);
            } else {
                getGeo(LocationManager.NETWORK_PROVIDER, 0, 0);
                getGeo(LocationManager.GPS_PROVIDER, 0, 0);
            }
        }
    }
    private void getGeo(String provider, int minTime, int minM) {
        locationManager.requestLocationUpdates(provider, minTime, minM, this);
        request = true;
    }

    public void disableLocationUpdates(){
        locationManager.removeUpdates(this);
        request = false;
    }
    @Override
    public void onLocationChanged(Location location) {
        if(location!=null) {
            currentLocation = location;
            disableLocationUpdates();
            callback.locationFound(location);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
