package com.example.gismeteo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class WeatherService extends Service implements ForecastTaskListener{

	final String LOG_TAG = "myLogs";
	String region;
	// NotificationManager nm;
	public void onCreate() {
		super.onCreate();
		Log.e(LOG_TAG, "onCreate");
		// nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}
  
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(LOG_TAG, "onStartCommand");
		region = intent.getStringExtra("region");
		// Этот метод будет вызываться по событию, сочиним его позже
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher, "Test"+region, System.currentTimeMillis());
		Intent intentTL = new Intent(context, MainActivity.class);
		intentTL.putExtra("region",region);
		notification.setLatestEventInfo(context, "Test", "Do something!" + region,
		PendingIntent.getActivity(context, 0, intentTL, PendingIntent.FLAG_CANCEL_CURRENT));
		notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
		nm.notify(1, notification);
		// ===========================================================
		someTask();
		return super.onStartCommand(intent, flags, startId);
	}

	public void onDestroy() {
		super.onDestroy();
		Log.e(LOG_TAG, "onDestroy");
	}

	public IBinder onBind(Intent intent) {
		Log.e(LOG_TAG, "onBind");
		return null;
	}
  
	void someTask() {
		ForecastForRegion task = new ForecastForRegion(region, false, this);
		task.execute();
        // stopSelf();

  }
	public void onTaskComplete(ArrayList<Weather> forecast){
		if(forecast != null){
			if(forecast.get(2).getTemperatureMin()>forecast.get(0).getTemperatureMin()){
				sendNotif("Warmer to "+forecast.get(forecast.size() - 1).getTemperatureMin()+"C");
			} else {
				sendNotif("Colder to "+forecast.get(forecast.size() - 1).getTemperatureMin()+"C");
			}
		} else{ Log.e(LOG_TAG, "no forecast");} 
		stopSelf();
	
	}
	void sendNotif(String message) {
		Notification notif = new Notification(R.drawable.ic_launcher, message,
		System.currentTimeMillis());
    
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("region", region);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
    
		notif.setLatestEventInfo(this, "GisWeather", message, pIntent);
    
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
    
		nm.notify(2, notif);
	}
}