package com.example.gismeteo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;

import com.example.gismeteo.dialogs.SimpleDialogs;
import com.example.gismeteo.task.ForecastForRegion;
import com.example.gismeteo.task.RegionTask;
import com.example.gismeteo.utils.Weather;
import com.example.gismeteo.constants.Constants;
import com.example.gismeteo.location.*;


public class GetGiscode implements RegionTask.RegionTaskListener, LocationFound {

	public interface GetGiscodeListener {
        public void onGetGiscode(String giscode);
    }
	
	private static volatile GetGiscode instance;
	
	private Context context;
//	private boolean wait;
	private boolean locationFound;
	private LocationListenerPlayServices locationListener;
    private LocationListenerStandart locationListener2;
	private GetGiscodeListener callback;
	private Handler waitHandler = new Handler();
	private RegionTask rt;
	SharedPreferences sPref;
    SharedPreferences.Editor ed;
	
	private GetGiscode(final Context context) {
		this.context = context;
//		this.wait = wait;
		locationFound = false; 
		sPref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        ed = sPref.edit();
	}
	
	public static GetGiscode getInstance(final Context context) {
        if (instance == null) {
            synchronized (LocationListenerPlayServices.class) {
                if (instance == null)
                    instance = new GetGiscode(context);
            }
        }
        return instance;
    }
	
	public void setGiscodeListener(GetGiscodeListener callback) {
        this.callback = callback;
    }
	
	public void clear() {
        instance = null;
		if (locationListener != null) {
            locationListener.clear();
		}
		if (locationListener2 != null) {
            locationListener2.clear();
        }
    }
	private Runnable longLocation  = new Runnable (){
        @Override
        public void run() {
            disableLocationListeners();
            locationFound = true;
			callback.onGetGiscode(Constants.LONG_LOC);
        }
    };
	public void showRegion(boolean wait){
		locationListener = LocationListenerPlayServices.getInstance(context);
        locationListener2 = LocationListenerStandart.getInstance(context);
        if(locationListener.servicesConnected(context)) {
            locationListener.enableMyLocation();
            locationListener.setLocationFound(this);
        }
        if(locationListener2.providersEnabled(context)) {
            locationListener2.setLocationFound(this);
            locationListener2.startLocation();
        }
		if (wait) {
			waitHandler.postDelayed(longLocation, Constants.TIMEOUT);
		}
	}
	 @Override
    public void locationFound(Location location) {
        if(!locationFound) {
			locationFound = true;
			disableLocationListeners();
            if(waitHandler != null){
                waitHandler.removeCallbacks(longLocation);
            }
			ed.putLong(Constants.LOC_TIME, location.getTime());
            ed.commit();
            rt = new RegionTask(context, location.getLatitude(), location.getLongitude(), this);
			rt.execute();
		}
	}
	private void disableLocationListeners() {
		if(locationListener != null && locationListener.clientConnected()) {
            locationListener.disableMyLocation();
        }
        if(locationListener2 != null && locationListener2.request) {
            locationListener2.disableLocationUpdates();
        }
	}
	public void onRegionTaskComplete(String giscode){
		callback.onGetGiscode(giscode);
	}
}