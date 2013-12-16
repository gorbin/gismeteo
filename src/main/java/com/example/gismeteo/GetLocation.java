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
	private String region = new String();
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
		if(currentLocation != null){
			region = setRegion(currentLocation.getLatitude(), currentLocation.getLongitude());
		} else{
			region = null;
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
    private String setRegion(double lat, double lng) {
        String regionName = new String();
		try {
            JSONObject jsonObj = parser_Json.getJSONfromURL("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=true");
            String Status = jsonObj.getString("status");
            if (Status.equalsIgnoreCase("OK")) {
                JSONArray Results = jsonObj.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
				for(int j=0;j<addrComp.length();j++){
					String adminArea = ((JSONArray)((JSONObject)addrComp.get(j)).get("types")).getString(0);
					if (adminArea.compareTo("administrative_area_level_1") == 0) {
						regionName = ((JSONObject)addrComp.get(j)).getString("long_name");
					}
				}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		catch (JSONException e) {
                alert();
                e.printStackTrace();
            }
        return regionName;
    }
    public Location getCurrentLocation(){
        return currentLocation;
    }
	public String getRegion(){
        return region;
    }
}
