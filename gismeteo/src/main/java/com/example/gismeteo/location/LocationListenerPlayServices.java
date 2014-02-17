package com.example.gismeteo.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import com.example.gismeteo.constants.Constants;

public final class LocationListenerPlayServices implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener
{
    private LocationRequest locationRequest;
    private LocationClient locationClient;
    public Location currentLocation;
    private LocationFound callback;

    private static volatile LocationListenerPlayServices instance;

    private LocationListenerPlayServices(final Context context) {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(Constants.UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(Constants.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
        locationClient = new LocationClient(context, this, this);
    }

    public static LocationListenerPlayServices getInstance(final Context context) {
        if (instance == null) {
            synchronized (LocationListenerPlayServices.class) {
                if (instance == null)
                    instance = new LocationListenerPlayServices(context);
            }
        }
        return instance;
    }

    public void clear() {
        instance = null;
    }

    public void setLocationFound(LocationFound locationFound)
    {
        this.callback = locationFound;
    }

    @Override
    public void onLocationChanged(final Location location) {
        if (location != null) {
            currentLocation = location;
            if (callback != null) {
               callback.locationFound(location);
            }
        }
    }

    @Override
    public void onConnected(final Bundle bundle) {
       if (!useCurrentLocation()) {
           locationClient.requestLocationUpdates(locationRequest, this);
        }
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {

    }

    public static boolean servicesConnected(final Context context) {
        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        return ConnectionResult.SUCCESS == resultCode;
    }
    public boolean clientConnected() {
        return (locationClient != null && locationClient.isConnected());
    }

    public void enableMyLocation() {
        locationClient.connect();
    }

    private boolean useCurrentLocation() {
        final Location location = locationClient.getLastLocation();
        if ((location != null)&&(System.currentTimeMillis() - location.getTime() > Constants.TIME_FOR_LOC)) {
            disableMyLocation();
            if (location != null) {
                currentLocation = location;
                if (callback != null)
                    callback.locationFound(location);
            }
            return true;
        }
        return false;
    }

    public void disableMyLocation() {
        if (locationClient.isConnected()) {
            locationClient.removeLocationUpdates(this);
        }
        locationClient.disconnect();
    }
}
