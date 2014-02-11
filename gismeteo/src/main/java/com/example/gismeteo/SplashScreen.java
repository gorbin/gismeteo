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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


public class SplashScreen extends Activity implements RegionTaskListener, ForecastTaskListener, LocationFound {
	private Thread thread;
	private ProgressBar progress;
	private TextView noty;
	private ForecastForRegion task;
//    private String region = new String();
    private String giscode = new String();
    private RegionTask rt;
    private boolean active, locationFound;
	private LocationListenerPlayServices locationListener;
    private LocationListenerStandart locationListener2;
    private final static String STATUS = "status", OK = "OK", RESULTS = "results", ADDRESS_COMPONENTS = "address_components", TYPES = "types", ADML1 = "administrative_area_level_1", SHORT_NAME = "short_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        active = true;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);
		if (getIntent().getBooleanExtra(Constants.EXIT, false)) {
			finish();
		}
        Intent intent = getIntent();
        giscode = intent.getStringExtra(Constants.REGION);
		noty = (TextView) findViewById(R.id.noty);
		noty.setText(this.getString(R.string.pd_message));
		progress = (ProgressBar) findViewById(R.id.progress);
		locationFound = false;
		
    }
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        giscode = data.getStringExtra(Constants.REGION);
    }
    @Override
    protected void onResume() {
        super.onResume();
		if (giscode != null && giscode.length() != 0){
            showForecast(giscode);
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
		
//		thread=  new Thread(){
//        @Override
//        public void run(){
//            try {
//                synchronized(this){
//                    wait(20000);
//                }
//            }
//            catch(InterruptedException ex){
//				Log.e(Constants.LOG_TAG, "interrupted");
//            }
//            // TODO
//			Log.e(Constants.LOG_TAG, "fine");
//
//        }
//    };
//    thread.start();
		Log.e(Constants.LOG_TAG, "after?");
//	disableLocationListeners();
//	if(region == null && active) {
//		gpsAlertBox(this.getString(R.string.GPS_error), this);
//	}
	
	}
	private void showForecast(String giscode){
	    task = new ForecastForRegion(this, giscode, false, this);
		task.execute();
	}
	public void onTaskComplete(ArrayList<Weather> forecast){
		if(forecast != null && active) {
			Intent intent = new Intent(this,MainActivity.class);
			intent.putParcelableArrayListExtra(Constants.FORECAST, forecast);
            intent.putExtra(Constants.REGION, giscode);
			startActivity(intent);
			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			finish();
		} else {
			alert(this.getString(R.string.error), this);
		}
	}
	public void onRegionTaskComplete(String giscode){
		if(giscode == null && active) {
			gpsAlertBox(this.getString(R.string.GPS_error), this);
		} else {
			this.giscode = giscode;
			noty.setText(this.getString(R.string.pd_forecast));
			showForecast(giscode);
		}
	}
	 @Override
    public void locationFound(Location location) {
//        synchronized(thread){
//            thread.notifyAll();
//        }
		if(!locationFound) {
			locationFound = true;
			rt = new RegionTask(this, location.getLatitude(), location.getLongitude(), this);
			rt.execute();
			disableLocationListeners();
		}
	}
	public void disableLocationListeners() {
		if(locationListener != null && locationListener.clientConnected()){
            locationListener.disableMyLocation();
        }
        if(locationListener2 != null && locationListener2.request){
            locationListener2.disableLocationUpdates();
        }
	}
//	============================================================
					// synchronized (THREAD_WAIT) {
                        // try {
                            // THREAD_WAIT.wait(20000);
                        // } catch (InterruptedException e) {e.printStackTrace();}}
//  =============================================================
	class RegionTask extends AsyncTask<Void, String, String> {
        private Context context;
		private double lat, lng;
        private RegionTaskListener callback;

		public RegionTask(Context context, double lat, double lng, RegionTaskListener callback) {
            this.context = context;
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
            String giscode = new String();
            try {
                regionName = regionFromLocation(lat, lng);
                giscode = gisCodeFromRegion(regionName, context);
                return giscode;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
			callback.onRegionTaskComplete(result);
		} 
		
    }

    private String regionFromLocation(double lat, double lng) throws JSONException {
		String regionName = new String();
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
	}
	private String gisCodeFromRegion(String region, Context context) throws XmlPullParserException, IOException {
	 if(region != null){
            String gisCode = new String();
            XmlPullParser xpp= context.getResources().getXml(R.xml.gismeteo_city);
            String tagName = new String();
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if(xpp.getEventType() == XmlPullParser.START_TAG) {
                    tagName = xpp.getName();
                }
                if(xpp.getEventType() == XmlPullParser.TEXT) {
                    if (tagName.equals(Constants.REG_NAME))
                        if(xpp.getText().equals(region)) {
                            return gisCode;
                        }
                    if(tagName.equals(Constants.REG_CODE)) {
                        gisCode = xpp.getText();
                    }
                }
                xpp.next();
            }
        }
        return null;
	}
	
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
