package com.example.gismeteo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;

public class SplashScreen extends Activity implements RegionTaskListener, ForecastTaskListener {
	
	private ProgressBar progress;
	private TextView noty;
	private ForecastForRegion task;
    private String region = new String();
    private RegionTask rt;
    private boolean active;
	private final String REGION = "region", FORECAST = "forecast", EXIT = "EXIT";
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        active = true;
        setContentView(R.layout.splash_screen);
		if (getIntent().getBooleanExtra(EXIT, false)) {
			finish();
		}
		noty = (TextView) findViewById(R.id.noty);
		noty.setText(this.getString(R.string.pd_message));
		progress = (ProgressBar) findViewById(R.id.progress);
    }
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        region = data.getStringExtra(REGION);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        region = intent.getStringExtra(REGION);
		if (region.length() == 0){
        showRegion();
		} else {
		showForecast();
		}
    }
    @Override
    public void onDestroy() {
        super.onStop();
        active = false;
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
		if(forecast != null) {
			Intent intent = new Intent(this,MainActivity.class);
			intent.putParcelableArrayListExtra(FORECAST, forecast);
            intent.putExtra(REGION, region);
			startActivity(intent);
			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
			finish();
		} else {
			alert(this.getString(R.string.error), this);
		}
	}
	public void onRegionTaskComplete(String region){
		if(region == null) {
			gpsAlertBox(this.getString(R.string.GPS_error), this);
		} else {
			this.region = region;
			noty.setText(this.getString(R.string.pd_forecast));
			showForecast();
		}
	}

    class RegionTask extends AsyncTask<Void, String, String> {
        private Context thisContext;
		private String region;
		private GetLocation gl;
        private RegionTaskListener callback;
        private final String THREAD_WAIT = "getLoc";
        
		public RegionTask(Context context, String region, RegionTaskListener callback) {
            thisContext = context;
			this.region = region;
            this.callback = callback;

        }
		
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
		
        @Override
        protected String doInBackground(Void... params) {
            try {
                gl = new GetLocation(thisContext);
                if(region.length() == 0){
					synchronized (THREAD_WAIT) {
					try {
						THREAD_WAIT.wait(20000);
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
			    gpsAlertBox(thisContext.getString(R.string.GPS_error),thisContext);
			} else {
			    callback.onRegionTaskComplete(result);
			}
		} 
		
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
		}).create().show();
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
