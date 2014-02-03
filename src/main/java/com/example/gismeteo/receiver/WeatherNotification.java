package com.example.gismeteo.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.gismeteo.service.WeatherService;
import com.example.gismeteo.constants.Constants;

public class WeatherNotification extends BroadcastReceiver {
	// private final String REGION = "region", FIRST_NOTIF = "firstNotif", SECOND_NOTIF = "secondNotif";
	@Override
	public void onReceive(Context context, Intent intent) {
		String region = intent.getStringExtra(Constants.REGION);
	    boolean first = intent.getBooleanExtra(Constants.NOTIF, false);
        // boolean second = intent.getBooleanExtra(SECOND_NOTIF, false);
		Intent updater = new Intent(context, WeatherService.class);
		updater.putExtra(Constants.REGION,region);
        updater.putExtra(Constants.NOTIF, first);
        // updater.putExtra(SECOND_NOTIF, second);
        context.startService(updater);

	}
}