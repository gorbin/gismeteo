package com.example.gismeteo;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

public class GetLocation {
    private Location currentLocation;
	private boolean noLocation = true;
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
        if(provider !=null){
            currentLocation = locationManager.getLastKnownLocation(provider);
        }
        if(currentLocation == null){
            getGeo(LocationManager.NETWORK_PROVIDER, 10000, 0);
        }
        if(currentLocation == null){
            getGeo(LocationManager.GPS_PROVIDER, 10000, 0);
        }
		Timer timer = new Timer();
		timer.schedule(new UpdateTimeTask(), 0, 20000); 
		class UpdateTimeTask extends TimerTask {
			public void run() {
					noLocation = false;
					} 
		}
    }
    private Runnable locationRun = new Runnable() {

        @Override
        public void run() {
        currentLocation = null;

        }
    };
    public void checkRegion() {

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
					// "getLoc".notify();
					noLocation = false;
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
        locationManager.requestLocationUpdates(provider, minTime, minM, locationListener, Looper.getMainLooper());
    }
    private String setRegion(double lat, double lng) {
        String regionName = new String();
		try {
            JSONObject jsonObj = JSONFromURL.getJSON("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=true");
            String Status = jsonObj.getString("status");
            if (Status.equalsIgnoreCase("OK")) {
                JSONArray results = jsonObj.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
				for(int j=1;j<results.length();j++){
                    String adminArea;
                    adminArea = ((JSONArray)((JSONObject)results.get(j)).get("types")).getString(0);
                    if (adminArea.compareTo("administrative_area_level_1") == 0) {
                        regionName = ((JSONObject)results.get(j)).getString("short_name");
                    }
                }
            }
        } catch (Exception e) {
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
	public boolean getNoLocation(){
        return noLocation;
    }
}
