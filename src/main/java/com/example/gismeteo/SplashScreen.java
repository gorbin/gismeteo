package com.example.gismeteo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.ArrayList;

public class SplashScreen extends Activity implements RegionTaskListener, ForecastTaskListener {
	
	private ProgressBar progress;
	private TextView noty;
	private ForecastForRegion task;
    private String region = new String();
    private RegionTask rt;
    private ArrayList<Weather> forecast = new ArrayList<Weather>();
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
		if (getIntent().getBooleanExtra("EXIT", false)) {
			finish();
		}
		noty = (TextView) findViewById(R.id.noty);
		noty.setText(this.getString(R.string.pd_message));
		progress = (ProgressBar) findViewById(R.id.progress);
    }
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        region = data.getStringExtra("region");
    }
    @Override
    protected void onResume() {
        super.onResume();
		if (region.length() == 0){
        showRegion();
		} else {
		showForecast();
		}
    }
	private void showRegion(){
	    rt = new RegionTask(this, region, this);
        rt.execute();
	}
	private void showForecast(){
	    task = new ForecastForRegion(this, region, false, this);
		task.execute();
	}
	public void onTaskComplete(ArrayList<Weather> forecast){
		Intent intent = new Intent(this,MainActivity.class);
		intent.putExtra("forecast",forecast);
		startActivity(intent);
		finish();
	}
	public void onRegionTaskComplete(String region){
		noty.setText(this.getString(R.string.pd_forecast));
		showForecast();
	}
	// protected void gpsAlertBox(String mymessage) {
        // final Context context = this;
        // AlertDialog.Builder ad;
        // ad = new AlertDialog.Builder(this);	
        // ad.setMessage(mymessage);
        // ad.setPositiveButton(this.getString(R.string.GPS_button), new DialogInterface.OnClickListener() {
            // public void onClick(DialogInterface dialog, int arg1) {
                // startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            // }
        // });
        // ad.setNegativeButton(this.getString(R.string.listreg_button), new DialogInterface.OnClickListener() {
            // public void onClick(DialogInterface dialog, int arg1) {
				// startActivityForResult(new Intent(((Dialog) dialog).getContext(),RegionList.class),1);
				// return;
            // }
        // });
        // ad.setCancelable(true);
        // ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            // public void onCancel(DialogInterface dialog) {
                // finish();
                // return;
            // }
        // });
        // ad.show();
    // }

	// public void alert(String message, Context context){
        // AlertDialog.Builder ad = new AlertDialog.Builder(this);
        // ad.setMessage(message);
        // ad.setCancelable(true);
        // ad.setPositiveButton(context.getString(R.string.close),
                // new DialogInterface.OnClickListener() {
                    // public void onClick(DialogInterface dialog, int id) {
                        // dialog.cancel();
                        // finish();
                    // }
                // }).create().show();
		// ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            // public void onCancel(DialogInterface dialog) {
                // finish();
                // return;
            // }
        // });
        // ad.show();
    // }

    class RegionTask extends AsyncTask<Void, String, String> {
        private Context thisContext;
		private String region;
		private GetLocation gl;
        private RegionTaskListener callback;
        private AlertIt ad = new AlertIt();
        
		public RegionTask(Context context, String region, RegionTaskListener callback) {
            thisContext = context;
			this.region = region;
            this.callback = callback;
            gl = new GetLocation(thisContext);
        }
		
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
		
        @Override
        protected String doInBackground(Void... params) {
            try {
				
                if(region.length() == 0){
					synchronized ("getLoc") {
					try {
						"getLoc".wait(20000);
					} catch (InterruptedException e) {e.printStackTrace();}}
					gl.checkRegion();
					region = gl.getRegion();
					if (region == null) {
						return null;
					}
				}               
                return region;
            } catch (Exception e) {
                e.printStackTrace();
				return null;
            }
        }
		
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
			if(result == null) {
				ad.gpsAlertBox(thisContext.getString(R.string.GPS_error),thisContext);
			} else {
			    callback.onRegionTaskComplete(result);
			}
		} 
		
    }
}
