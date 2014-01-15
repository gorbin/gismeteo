package com.example.gismeteo;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class GetLocation {
	private final static String STATUS = "status", OK = "OK", RESULTS = "results", ADDRESS_COMPONENTS = "address_components", TYPES = "types", ADML1 = "administrative_area_level_1", SHORT_NAME = "short_name";
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
			if(currentLocation != null && currentLocation.getTime() > Calendar.getInstance().getTimeInMillis() - 180 * 60 * 1000) {
				currentLocation = null;
			}
        }
        if(currentLocation == null){
            getGeo(LocationManager.NETWORK_PROVIDER, 1000, 0);
        }
        if(currentLocation == null){
            getGeo(LocationManager.GPS_PROVIDER, 1000, 0);
        }
    }
	
    public void checkRegion() throws JSONException {
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
                    locationManager.removeUpdates(this);
                    synchronized ("getLoc") {
						"getLoc".notify();
                    }
                }
                else
                {
                    currentLocation = null;
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        locationManager.requestLocationUpdates(provider, minTime, minM, locationListener, Looper.getMainLooper());
    }
    private String setRegion(double lat, double lng) throws JSONException {
		String regionName = new String();
	    JSONObject jsonObj = JSONFromURL.getJSON(String.format(context.getString(R.string.gapi_region_url), lat + "," + lng));
		String Status = jsonObj.getString(STATUS);
		if (Status.equalsIgnoreCase(OK)) {
			JSONArray results = jsonObj.getJSONArray(RESULTS).getJSONObject(0).getJSONArray(ADDRESS_COMPONENTS);
			for(int j=1;j<results.length();j++){
				String adminArea;
				adminArea = ((JSONArray)((JSONObject)results.get(j)).get(TYPES)).getString(0);
				if (adminArea.compareTo(ADML1) == 0) {
					regionName = ((JSONObject)results.get(j)).getString(SHORT_NAME);
				}
			}
		}
        return regionName;
    }
	public String getRegion(){
        return region;
    }
	public boolean getNoLocation(){
        return noLocation;
    }
}
