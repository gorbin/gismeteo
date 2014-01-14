package com.example.gismeteo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class WeatherService extends Service implements ForecastTaskListener{

    private final String REGION = "region", FIRST_NOTIF = "firstNotif", SECOND_NOTIF = "secondNotif", LOG_TAG = "myLogs";
	private String region;
	private NotificationManager nm;
    private PendingIntent pIntent;
    private Intent notificationIntent;
    private String[] todArray = new String[4];
    boolean firstNotif = false, secondNotif = false;
	public void onCreate() {
		super.onCreate();
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        todArray = this.getResources().getStringArray(R.array.time_day);
	}
	public int onStartCommand(Intent intent, int flags, int startId) {
		region = intent.getStringExtra(REGION);
        firstNotif = intent.getBooleanExtra(FIRST_NOTIF, false);
        secondNotif = intent.getBooleanExtra(SECOND_NOTIF, false);
        //============================================================
        notificationIntent = new Intent(this, SplashScreen.class);
        notificationIntent.putExtra(REGION, region);
        pIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		// ===========================================================
		notificationTask();
		return super.onStartCommand(intent, flags, startId);
	}
	public void onDestroy() {
		super.onDestroy();
	}
	public IBinder onBind(Intent intent) {
		return null;
	}
	void notificationTask() {
		ForecastForRegion task = new ForecastForRegion(this, region, false, this);
		task.execute();
    }
	public void onTaskComplete(ArrayList<Weather> forecast){
		if(forecast != null){
            if(firstNotif) {
                sendNotif(forecast.get(0).getCloudiness() + ", " + forecast.get(0).getPrecipitation(),
                          String.format(this.getString(R.string.current_notif), forecast.get(0).getHeatMin()),
                          101);
            }
            if(secondNotif) {
                for (int i = 0; i < forecast.size(); i++) {
                    if(forecast.get(i).getTimeOfDay().equals(todArray[todArray.length-1])){
                        sendNotif(forecast.get(i).getCloudiness() + ", " + forecast.get(i).getPrecipitation(),
                                  String.format(this.getString(R.string.night_notif), forecast.get(i).getTemperatureMin()),
                                  102);
                    }
                }
            }
		} else{ Log.e(LOG_TAG, "no forecast");} 
		stopSelf();
	}
	void sendNotif(String title, String message, int i) {
        NotificationCompat.Builder notif  = new NotificationCompat.Builder(this)
                .setContentIntent(pIntent)
                .setContentTitle(title)
                .setContentText(message)
//                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher))
                .setTicker(message)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);
//                .build();
        nm.notify(i, notif.build());
	}
}