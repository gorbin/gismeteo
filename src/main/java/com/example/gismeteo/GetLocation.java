package com.example.gismeteo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GetLocation {
    public Location currentLocation;
    private LocationManager locationManager;
    private Context context;
    public GetLocation(Context context){
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(false);
        String provider = locationManager.getBestProvider(criteria, true);
        currentLocation = locationManager.getLastKnownLocation(provider);
        if(currentLocation == null){
            getGeo(provider, 0, 0);
        }
        if(currentLocation == null){
            getGeo(LocationManager.GPS_PROVIDER, 6000, 1000);
        }
    }
    private void getGeo(String provider, int minTime, int minM){
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location loc) {
                if(loc!=null){
                    currentLocation = loc;
                    Log.e("Coord", "" + loc.getLatitude() + "/" + loc.getLongitude());
                    locationManager.removeUpdates(this);
                }
                else
                {
                    Log.e("Coord","looser");
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        locationManager.requestLocationUpdates(provider, minTime, minM, locationListener);
    }
    public String getAddress(double lat, double lng) {
        String countryName = new String();
        Geocoder gcd = new Geocoder(context, Locale.ENGLISH);
        List<Address> addresses = null;


            String wat = "wat";
        try {
            addresses = gcd.getFromLocation(
                    43.020714, -75.940933, 1);
            if (addresses.size() > 0)
                countryName = addresses.get(0).getCountryName();

        } catch (IOException e) {
            e.printStackTrace();
        }



        return countryName;
    }
    public Location getCurrentLocation(){
        return currentLocation;
    }
}
