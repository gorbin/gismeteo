package com.example.gismeteo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class WeatherService extends Service {

	final String LOG_TAG = "myLogs";
	NotificationManager nm;
	public void onCreate() {
		super.onCreate();
		Log.e(LOG_TAG, "onCreate");
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}
  
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(LOG_TAG, "onStartCommand");
		GetLocation gl = new GetLocation(this);
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
		new Thread(new Runnable() {
		public void run() {
        for (int i = 1; i<=5; i++) {
			Log.e(LOG_TAG, "i = " + i);
			try {
				TimeUnit.SECONDS.sleep(1);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
		try {
		  TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
		  e.printStackTrace();
		}
		sendNotif();
        // stopSelf();
      }
    }).start();
  }
	void sendNotif() {
		Notification notif = new Notification(R.drawable.ic_launcher, "Text in status bar",
		System.currentTimeMillis());
    
		Intent intent = new Intent(this, MainActivity.class);
		// intent.putExtra(MainActivity.FILE_NAME, "somefile");
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
    
		notif.setLatestEventInfo(this, "Notification's title", "Notification's text", pIntent);
    
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
    
		nm.notify(1, notif);
	}
}