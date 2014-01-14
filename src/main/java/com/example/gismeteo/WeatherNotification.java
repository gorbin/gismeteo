package com.example.gismeteo;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WeatherNotification extends BroadcastReceiver {
	private final String REGION = "region", FIRST_NOTIF = "firstNotif", SECOND_NOTIF = "secondNotif";
	@Override
	public void onReceive(Context context, Intent intent) {
		String region = intent.getStringExtra(REGION);
	    boolean first = intent.getBooleanExtra(FIRST_NOTIF, false);
        boolean second = intent.getBooleanExtra(SECOND_NOTIF, false);
		Intent updater = new Intent(context, WeatherService.class);
		updater.putExtra(REGION,region);
        updater.putExtra(FIRST_NOTIF, first);
        updater.putExtra(SECOND_NOTIF, second);
        context.startService(updater);

	}
}