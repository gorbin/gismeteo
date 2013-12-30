package com.example.gismeteo;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WeatherNotification extends BroadcastReceiver {
	int i;
	@Override
	public void onReceive(Context context, Intent intent) {
		i++;
		String region = intent.getStringExtra("region");
	// Этот метод будет вызываться по событию, сочиним его позже
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher, "Test"+region, System.currentTimeMillis());
		// Интент для активити, которую мы хотим запускать при нажатии на уведомление
		Intent intentTL = new Intent(context, MainActivity.class);
		notification.setLatestEventInfo(context, "Test", "Do something!" + region,
		PendingIntent.getActivity(context, 0, intentTL, PendingIntent.FLAG_CANCEL_CURRENT));
		notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
		nm.notify(i, notification);
		// Установим следующее напоминание.
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		intent.putExtra("region", region+""+i);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
		intent, PendingIntent.FLAG_CANCEL_CURRENT);
		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000*10, pendingIntent);
	}
}