package com.example.gismeteo;

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
import com.example.gismeteo.utils.GetGiscode;
import com.example.gismeteo.utils.Weather;
import com.example.gismeteo.constants.Constants;
import com.example.gismeteo.location.*;


public class SplashScreen extends Activity implements ForecastForRegion.ForecastTaskListener,// LocationFound, RegionTask.RegionTaskListener {
GetGiscode.GetGiscodeListener {
	private ProgressBar progress;
	private TextView noty;
	private ForecastForRegion task;
    private Context context;
    private String giscode = new String();
//    private RegionTask rt;
    private boolean active;
//    private boolean locationFound;
//    private Handler waitHandler = new Handler();
//	private LocationListenerPlayServices locationListener;
//    private LocationListenerStandart locationListener2;
	private GetGiscode giscodeListener;
    SharedPreferences sPref;
    SharedPreferences.Editor ed;

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
//		locationFound = false;
        active = true;
        sPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ed = sPref.edit();
    }

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        giscode = data.getStringExtra(Constants.REGION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String giscodePrefs = sPref.getString(Constants.REGION, "");
		if (giscode != null && giscode.length() != 0){
            showForecast(giscode);
		} else if ((giscodePrefs != null && giscodePrefs.length() != 0)) {
            showForecast(giscodePrefs);
        } else {
////            if ((locationListener != null) && (locationListener2 != null)) {
////                locationListener.clear();
////                locationListener2.clear();
////            }


		    // showRegion();

			giscodeListener = GetGiscode.getInstance(context, true, this);
            giscodeListener.showRegion();
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
	public void onGetGiscode(String giscode) {
		if(giscode.equals(Constants.LONG_LOC)) {
			SimpleDialogs.gpsAlertBox(context.getString(R.string.GPS_error), context, active);
		} else if (giscode == null || giscode.length() == 0) {
			SimpleDialogs.alert(context.getString(R.string.error2), context, active);
		} else {
			ed.putString(Constants.REGION, giscode);
            ed.commit();
			showForecast(giscode);
		}
	}
    // private Runnable longLocation  = new Runnable (){
        // @Override
        // public void run() {
            // disableLocationListeners();
            // locationFound = true;
            // if(giscode == null && active) {
                // SimpleDialogs.gpsAlertBox(context.getString(R.string.GPS_error), context, active);
            // }
        // }
    // };
	// private void showRegion(){
        // boolean connected = false;
		// locationListener = LocationListenerPlayServices.getInstance(context);
        // locationListener2 = LocationListenerStandart.getInstance(context);
        // if(locationListener.servicesConnected(context)) {
            // locationListener.enableMyLocation();
            // locationListener.setLocationFound(this);
 //          // connected = true;
        // }
        // if(locationListener2.providersEnabled(context)) {
            // locationListener2.setLocationFound(this);
            // locationListener2.startLocation();
//     //       connected = true;
        // }
//      //  if (!connected) {
//        //    locationFound = true;
//          //  gpsAlertBox(context.getString(R.string.GPS_error), context);
//       // } else {
            // waitHandler.postDelayed(longLocation, Constants.TIMEOUT);
//       // }
	// }

	private void showForecast(String giscode){
        noty.setText(context.getString(R.string.pd_forecast));
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

	// public void onRegionTaskComplete(String giscode){
		// if(giscode == null && active) {
			// SimpleDialogs.gpsAlertBox(context.getString(R.string.GPS_error), context, active);
		// } else {
			// this.giscode = giscode;
			// ed.putString(Constants.REGION, giscode);
            // ed.commit();
			// showForecast(giscode);
		// }
	// }
	 // @Override
    // public void locationFound(Location location) {
         // if(!locationFound) {
            // if(waitHandler!=null){
                 // waitHandler.removeCallbacks(longLocation);
            // }
			// locationFound = true;
            // ed.putLong(Constants.LOC_TIME, location.getTime());
            // ed.commit();
			// rt = new RegionTask(context, location.getLatitude(), location.getLongitude(), this);
			// rt.execute();
			// disableLocationListeners();
		// }
	// }
	// public void disableLocationListeners() {
		// if(locationListener != null && locationListener.clientConnected()){
            // locationListener.disableMyLocation();
        // }
        // if(locationListener2 != null && locationListener2.request){
            // locationListener2.disableLocationUpdates();
        // }
	// }
}
