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
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;

import com.example.gismeteo.dialogs.SimpleDialogs;
import com.example.gismeteo.json.JSONFromURL;
import com.example.gismeteo.task.ForecastForRegion;
import com.example.gismeteo.interfaces.ForecastTaskListener;
import com.example.gismeteo.interfaces.RegionTaskListener;
import com.example.gismeteo.utils.Weather;
import com.example.gismeteo.constants.Constants;
import com.example.gismeteo.location.*;
import com.example.gismeteo.dialogs.SimpleDialogs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


public class SplashScreen extends Activity implements RegionTaskListener, ForecastTaskListener, LocationFound {
	private ProgressBar progress;
	private TextView noty;
	private ForecastForRegion task;
    private Context context;
    private String giscode = new String();
    private RegionTask rt;
    private boolean active;
    private boolean locationFound;
    private Handler waitHandler = new Handler();
	private LocationListenerPlayServices locationListener;
    private LocationListenerStandart locationListener2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);
        context = this;
		if (getIntent().getBooleanExtra(Constants.EXIT, false)) {
			finish();
		}
        Intent intent = getIntent();
        giscode = intent.getStringExtra(Constants.REGION);
		noty = (TextView) findViewById(R.id.noty);
		noty.setText(context.getString(R.string.pd_message));
		progress = (ProgressBar) findViewById(R.id.progress);
		locationFound = false;
        active = true;
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
//            if ((locationListener != null) && (locationListener2 != null)) {
//                locationListener.clear();
//                locationListener2.clear();
//            }
		    showRegion();
		}
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
        if (task != null && task.getStatus() != AsyncTask.Status.FINISHED) {
            task.cancel(true);
        }
    }

    private Runnable longLocation  = new Runnable (){
        @Override
        public void run() {
            disableLocationListeners();
            locationFound = true;
            if(giscode == null && active) {
                SimpleDialogs.gpsAlertBox(context.getString(R.string.GPS_error), context, active);
            }
        }
    };
	private void showRegion(){
        boolean connected = false;
		locationListener = LocationListenerPlayServices.getInstance(context);
        locationListener2 = LocationListenerStandart.getInstance(context);
        if(locationListener.servicesConnected(context)) {
            locationListener.enableMyLocation();
            locationListener.setLocationFound(this);
//            connected = true;
        }
        if(locationListener2.providersEnabled(context)) {
            locationListener2.setLocationFound(this);
            locationListener2.startLocation();
//            connected = true;
        }
//        if (!connected) {
//            locationFound = true;
//            gpsAlertBox(context.getString(R.string.GPS_error), context);
//        } else {
            waitHandler.postDelayed(longLocation, Constants.TIMEOUT);
//        }
	}

	private void showForecast(String giscode){
	    task = new ForecastForRegion(context, giscode, false, this);
		task.execute();
	}

	public void onTaskComplete(ArrayList<Weather> forecast){
		if(forecast != null && active) {
			Intent intent = new Intent(context,MainActivity.class);
			intent.putParcelableArrayListExtra(Constants.FORECAST, forecast);
            intent.putExtra(Constants.REGION, giscode);
			startActivity(intent);
			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			finish();
		} else {
            SimpleDialogs.alert(context.getString(R.string.error), context, active);
		}
	}

	public void onRegionTaskComplete(String giscode){
		if(giscode == null && active) {
			SimpleDialogs.gpsAlertBox(context.getString(R.string.GPS_error), context, active);
		} else {
			this.giscode = giscode;
			noty.setText(context.getString(R.string.pd_forecast));
			showForecast(giscode);
		}
	}
	 @Override
    public void locationFound(Location location) {
         if(!locationFound) {
            if(waitHandler!=null){
                 waitHandler.removeCallbacks(longLocation);
            }
			locationFound = true;
			rt = new RegionTask(context, location.getLatitude(), location.getLongitude(), this);
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
        JSONObject jsonObj = JSONFromURL.getJSON(String.format(context.getString(R.string.gapi_region_url), lat + "," + lng));
		String Status = jsonObj.getString(Constants.STATUS);
		if (Status.equalsIgnoreCase(Constants.OK)) {
			JSONArray results = jsonObj.getJSONArray(Constants.RESULTS).getJSONObject(0).getJSONArray(Constants.ADDRESS_COMPONENTS);
			for(int j=1;j<results.length();j++){
				String adminArea;
				adminArea = ((JSONArray)((JSONObject)results.get(j)).get(Constants.TYPES)).getString(0);
				if (adminArea.compareTo(Constants.ADML1) == 0) {
					regionName = ((JSONObject)results.get(j)).getString(Constants.SHORT_NAME);
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
	
//	private void alert(String message, Context context){
//        AlertDialog.Builder ad = new AlertDialog.Builder(context);
//        ad.setMessage(message);
//        ad.setCancelable(true);
//        ad.setPositiveButton(context.getString(R.string.close),	new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int id) {
//				dialog.cancel();
//				finish();
//			}
//		});
//		ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            public void onCancel(DialogInterface dialog) {
//                finish();
//                return;
//            }
//        });
//		ad.create().show();
//        if(active) {
//            ad.show();
//        }
//    }
//    private void gpsAlertBox(String mymessage, Context context) {
//		AlertDialog.Builder ad;
//		ad = new AlertDialog.Builder(context);
//		ad.setMessage(mymessage);
//		ad.setPositiveButton(context.getString(R.string.GPS_button), new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int arg1) {
//				startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//			}
//		});
//		ad.setNegativeButton(context.getString(R.string.listreg_button), new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialog, int arg1) {
//				startActivityForResult(new Intent(((Dialog) dialog).getContext(),RegionList.class),1);
//				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//				return;
//			}
//		});
//		ad.setCancelable(true);
//		ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
//			public void onCancel(DialogInterface dialog) {
//				finish();
//				return;
//			}
//		});
//        if(active) {
//		    ad.show();
//        }
//    }
}
