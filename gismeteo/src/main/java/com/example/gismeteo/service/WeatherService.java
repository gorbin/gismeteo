package com.example.gismeteo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import java.util.ArrayList;

import com.example.gismeteo.R;
import com.example.gismeteo.SplashScreen;
import com.example.gismeteo.task.ForecastForRegion;
import com.example.gismeteo.utils.Weather;
import com.example.gismeteo.constants.Constants;


public class WeatherService extends Service implements ForecastForRegion.ForecastTaskListener{

    private String giscode;
	private NotificationManager nm;
    private PendingIntent pIntent;
    private Intent notificationIntent;

	public void onCreate() {
		super.onCreate();
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}
	public int onStartCommand(Intent intent, int flags, int startId) {
		giscode = intent.getStringExtra(Constants.REGION);

        notificationIntent = new Intent(this, SplashScreen.class);
        notificationIntent.putExtra(Constants.REGION, giscode);
        pIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

		notificationTask(giscode);
		return super.onStartCommand(intent, flags, startId);
	}

	public void onDestroy() {
		super.onDestroy();
	}

	public IBinder onBind(Intent intent) {
		return null;
	}

	void notificationTask(String giscode) {
		ForecastForRegion task = new ForecastForRegion(this, giscode, false, this);
		task.execute();
    }

	public void onTaskComplete(ArrayList<Weather> forecast){
		if(forecast != null){
            sendNotif(forecast.get(0).getCloudiness() + ", " + forecast.get(0).getPrecipitation(),
                      String.format(this.getString(R.string.current_notif), forecast.get(0).getHeatMin()),
                      101);
        } else{ Log.e(Constants.LOG_TAG, "no forecast");} 
		stopSelf();
	}
	void sendNotif(String title, String message, int i) {
        NotificationCompat.Builder notif  = new NotificationCompat.Builder(this)
                .setContentIntent(pIntent)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(message)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);
        nm.notify(i, notif.build());
	}
}