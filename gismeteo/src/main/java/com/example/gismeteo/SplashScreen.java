package com.example.gismeteo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import com.example.gismeteo.json.JSONFromURL;
import com.example.gismeteo.task.ForecastForRegion;
import com.example.gismeteo.interfaces.ForecastTaskListener;
import com.example.gismeteo.interfaces.RegionTaskListener;
import com.example.gismeteo.utils.Weather;
import com.example.gismeteo.utils.GetLocation;
import com.example.gismeteo.constants.Constants;
import com.example.gismeteo.utils.*;

import org.json.JSONArray;
import org.json.JSONObject;


public class SplashScreen extends Activity implements RegionTaskListener, ForecastTaskListener, LocationFound {
	private Thread thread;
	private ProgressBar progress;
	private TextView noty;
	private ForecastForRegion task;
    private String region = new String();
    private RegionTask rt;
    private boolean active;
	// private final String REGION = "region", FORECAST = "forecast", EXIT = "EXIT";
	private LocationListenerPlayServices locationListener;
    private LocationListenerStandart locationListener2;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        active = true;
        setContentView(R.layout.splash_screen);
		if (getIntent().getBooleanExtra(Constants.EXIT, false)) {
			finish();
		}
        Intent intent = getIntent();
        region = intent.getStringExtra(Constants.REGION);
		noty = (TextView) findViewById(R.id.noty);
		noty.setText(this.getString(R.string.pd_message));
		progress = (ProgressBar) findViewById(R.id.progress);
		
    }
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        region = data.getStringExtra(Constants.REGION);
    }
    @Override
    protected void onResume() {
        super.onResume();

		if (region != null && region.length() != 0){
            showForecast();
		} else {
		    showRegion();
		}
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        active = false;
        // if (rt != null && rt.getStatus() != AsyncTask.Status.FINISHED) {
            // rt.cancel(true);
        // }
        if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
            task.cancel(true);
        }
    }
	private void showRegion(){
	    // rt = new RegionTask(this, region, this);
        // rt.execute();
		locationListener = LocationListenerPlayServices.getInstance(this);
        locationListener2 = LocationListenerStandart.getInstance(this);
        if(locationListener.servicesConnected(this)) {
            locationListener.enableMyLocation();
            locationListener.setLocationFound(this);
        }
        if(locationListener2.providersEnabled(this)) {
            locationListener2.setLocationFound(this);
            locationListener2.startLocation();
        }
		
		thread=  new Thread(){
        @Override
        public void run(){
            try {
                synchronized(this){
                    wait(20000);
                }
            }
            catch(InterruptedException ex){   	
				Log.e(Constants.LOG_TAG, "interrupted");
            }
            // TODO 
			Log.e(Constants.LOG_TAG, "fine");
			
        }
    };
    thread.start(); 
		Log.e(Constants.LOG_TAG, "after?");
	disableLocationListeners();	
	if(region == null && active) {
		gpsAlertBox(this.getString(R.string.GPS_error), this);
	}
	
	}
	private void showForecast(){
	    task = new ForecastForRegion(this, region, false, this);
		task.execute();
	}
	public void onTaskComplete(ArrayList<Weather> forecast){
		if(forecast != null && active) {
			Intent intent = new Intent(this,MainActivity.class);
			intent.putParcelableArrayListExtra(Constants.FORECAST, forecast);
            intent.putExtra(Constants.REGION, region);
			startActivity(intent);
			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			finish();
		} else {
			alert(this.getString(R.string.error), this);
		}
	}
	public void onRegionTaskComplete(String region){
		if(region == null && active) {
			gpsAlertBox(this.getString(R.string.GPS_error), this);
		} else {
			this.region = region;
			// this.gisCode = region;
			noty.setText(this.getString(R.string.pd_forecast));
			showForecast();
		}
	}
	 @Override
    public void locationFound(Location location) {
        //label.setText("Location: "+location.getLongitude()+"/"+location.getLatitude());
        synchronized(thread){
            thread.notifyAll();
        }
		rt = new RegionTask(this, location.getLatitude(), location.getLongitude(), this);
        rt.execute();
	}
	public void disableLocationListeners() {
		if(locationListener != null && locationListener.clientConnected()){
            locationListener.disableMyLocation();
        }
        if(locationListener2 != null && locationListener2.request){
            locationListener2.disableLocationUpdates();
        }
	}
    // class RegionTask extends AsyncTask<Void, String, String> {
        // private Context thisContext;
		// private String region;
		// private GetLocation gl;
        // private RegionTaskListener callback;
        // private final String THREAD_WAIT = "getLoc";
        
		// public RegionTask(Context context, String region, RegionTaskListener callback) {
            // thisContext = context;
			// this.region = region;
            // this.callback = callback;

        // }
		
        // @Override
        // protected void onPreExecute() {
            // super.onPreExecute();
        // }
		
        // @Override
        // protected String doInBackground(Void... params) {
            // try {
                // gl = new GetLocation(thisContext);
                // if(region == null || region.length() == 0){
					// synchronized (THREAD_WAIT) {
                        // try {
                            // THREAD_WAIT.wait(20000);
                        // } catch (InterruptedException e) {e.printStackTrace();}}
                    // gl.checkRegion();
                    // region = gl.getRegion();
				// }
                // return region;
            // } catch (Exception e) {
                // e.printStackTrace();
				// return null;
            // }
        // }
        // @Override
        // protected void onPostExecute(String result) {
            // super.onPostExecute(result);
			// if(result == null) {
			    // gpsAlertBox(thisContext.getString(R.string.GPS_error),thisContext);
			// } else {
			    // callback.onRegionTaskComplete(result);
			// }
		// } 
		
    // }
	 class RegionTask extends AsyncTask<Void, String, String> {
        private Context context;
		private double lat, lng;
        private RegionTaskListener callback;
        private final static String STATUS = "status", OK = "OK", RESULTS = "results", ADDRESS_COMPONENTS = "address_components", TYPES = "types", ADML1 = "administrative_area_level_1", SHORT_NAME = "short_name";
		
		public RegionTask(Context context, double lat, double lng, RegionTaskListener callback) {
            this.context = context;
//			this.region = region;
            this.callback = callback;
			this.lat = lat;
			this.lng = lng;
        }
		
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
		
        @Override
        protected String doInBackground(Void... params) {
			String regionName = new String();
			regionName = regionFromLocation(lat, lng);
			return regionName;
			// return gisCodeFromRegion(regionName);
			// try {
                // JSONObject jsonObj = JSONFromURL.getJSON(String.format(context.getString(R.string.gapi_region_url), lat + "," + lng));
				// String Status = jsonObj.getString(STATUS);
				// if (Status.equalsIgnoreCase(OK)) {
					// JSONArray results = jsonObj.getJSONArray(RESULTS).getJSONObject(0).getJSONArray(ADDRESS_COMPONENTS);
					// for(int j=1;j<results.length();j++){
						// String adminArea;
						// adminArea = ((JSONArray)((JSONObject)results.get(j)).get(TYPES)).getString(0);
						// if (adminArea.compareTo(ADML1) == 0) {
							// regionName = ((JSONObject)results.get(j)).getString(SHORT_NAME);
						// }
					// }
				// }
			// return regionName;
            // } catch (Exception e) {
                // e.printStackTrace();
				// return null;
            // }
			
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
			if(result == null) {
			    gpsAlertBox(context.getString(R.string.GPS_error),context);
			} else {
			    callback.onRegionTaskComplete(result);
			}
		} 
		
    }
    private final static String STATUS = "status", OK = "OK", RESULTS = "results", ADDRESS_COMPONENTS = "address_components", TYPES = "types", ADML1 = "administrative_area_level_1", SHORT_NAME = "short_name";

    private String regionFromLocation(double lat, double lng) {
		String regionName = new String();
		try {
            JSONObject jsonObj = JSONFromURL.getJSON(String.format(this.getString(R.string.gapi_region_url), lat + "," + lng));
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
        } catch (Exception e) {
            e.printStackTrace();
			return null;
        }
	}
	
	// private String gisCodeFromRegion(String region) {
	 // if(region != null){
            // String gisCode = new String();
            // XmlPullParser xpp= context.getResources().getXml(R.xml.gismeteo_city);
            // String tagName = new String();
            // while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                // if(xpp.getEventType() == XmlPullParser.START_TAG) {
                    // tagName = xpp.getName();
                // }
                // if(xpp.getEventType() == XmlPullParser.TEXT) {
                    // if (tagName.equals(REG_NAME))
                        // if(xpp.getText().equals(region)) {
                            // return gisCode;
                        // }
                    // if(tagName.equals(GIS_CODE)) {
                        // gisCode = xpp.getText();
                    // }
                // }
                // xpp.next();
            // }
        // }
        // return null;
	// }
	
	public void alert(String message, Context context){
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setMessage(message);
        ad.setCancelable(true);
        ad.setPositiveButton(context.getString(R.string.close),	new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				finish();
			}
		});
		ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                finish();
                return;
            }
        });
		ad.create().show();
        if(active) {
            ad.show();
        }
    }
	public void gpsAlertBox(String mymessage, Context context) {
		AlertDialog.Builder ad;
		ad = new AlertDialog.Builder(context);	
		ad.setMessage(mymessage);
		ad.setPositiveButton(context.getString(R.string.GPS_button), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			}
		});
		ad.setNegativeButton(context.getString(R.string.listreg_button), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int arg1) {
				startActivityForResult(new Intent(((Dialog) dialog).getContext(),RegionList.class),1);
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
				return;
			}
		});
		ad.setCancelable(true);
		ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				finish();
				return;
			}
		});
        if(active) {
		    ad.show();
        }
    }
}
