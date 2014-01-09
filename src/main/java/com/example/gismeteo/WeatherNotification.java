package com.example.gismeteo;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WeatherNotification extends BroadcastReceiver {
	int i;
	@Override
	public void onReceive(Context context, Intent intent) {
		i++;
		String region = intent.getStringExtra("region");
	
		Intent updater = new Intent(context, WeatherService.class);
		updater.putExtra("region",region);
		context.startService(updater);
		Log.d("AlarmReceiver", "Called context.startService from AlarmReceiver.onReceive");
		// Интент для активити, которую мы хотим запускать при нажатии на уведомление
		
	}
}