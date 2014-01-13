package com.example.gismeteo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
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
    NotificationManager nm;
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(LOG_TAG, "onStartCommand");
		region = intent.getStringExtra("region");
		// Этот метод будет вызываться по событию, сочиним его позже
        nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher, "Test"+region, System.currentTimeMillis());

        NotificationCompat.Builder notit  = new NotificationCompat.Builder(this)
                .setContentTitle("Test builder")
                .setContentText("Builder Do something!")
                .setSmallIcon(R.drawable.ic_launcher)
//                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher))
                .setTicker("Colder from Builder");
//                .build();
		Intent intentTL = new Intent(this, SplashScreen.class);
		intentTL.putExtra("region",region);
		notification.setLatestEventInfo(this, "Test", "Do something!" + region,
		PendingIntent.getActivity(this, 0, intentTL, PendingIntent.FLAG_CANCEL_CURRENT));
		notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
		nm.notify(1, notification);
        nm.notify(2, notit.build());
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
		ForecastForRegion task = new ForecastForRegion(this, region, false, this);
		task.execute();
        // stopSelf();
//        sendNotif("dend notif");

  }
	public void onTaskComplete(ArrayList<Weather> forecast){
		if(forecast != null){

			if(forecast.get(forecast.size() - 1).getTemperatureMin()>forecast.get(0).getTemperatureMin()){
				sendNotif("Warmer to "+forecast.get(forecast.size() - 1).getTemperatureMin()+" C", 101);
			} else {
				sendNotif("Colder to "+forecast.get(forecast.size() - 1).getTemperatureMin()+" C", 101);
			}
            for (int i = 0; i < forecast.size(); i++) {
                if(forecast.get(i).getTimeOfDay().equals("Ночь")){
                    sendNotif("Temperature at night "+forecast.get(i).getTemperatureMin()+" C", 102);
                }
            }
		} else{ Log.e(LOG_TAG, "no forecast");} 
		stopSelf();
	
	}
	void sendNotif(String message, int i) {
//		Notification notif = new Notification(R.drawable.ic_launcher, message,
//		System.currentTimeMillis());
        NotificationCompat.Builder notif  = new NotificationCompat.Builder(this)
                .setContentTitle("At night")
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher)
//                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher))
                .setTicker(message);
//                .build();
		Intent intent = new Intent(this, SplashScreen.class);
		intent.putExtra("region", region);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
    
//		notif.setLatestEventInfo(this, "GisWeather", message, pIntent);
    
//		notif.flags |= Notification.FLAG_AUTO_CANCEL;
    
//		nm.notify(i, notif);
        nm.notify(i, notif.build());
	}
}