package com.example.gismeteo.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.gismeteo.service.WeatherService;
import com.example.gismeteo.constants.Constants;

public class WeatherNotification extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String giscode = intent.getStringExtra(Constants.REGION);
		Intent updater = new Intent(context, WeatherService.class);
		updater.putExtra(Constants.REGION, giscode);
        context.startService(updater);
	}
}