package com.example.gismeteo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends Activity {
    private TextView date;
    private TextView tod;
    private TextView weather;
    private TextView pressure;
    private TextView wind;
    private TextView relwet;
    private TextView heat;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        date = (TextView) findViewById(R.id.date);
        tod = (TextView) findViewById(R.id.tod);
        weather = (TextView) findViewById(R.id.weather);
        pressure = (TextView) findViewById(R.id.pressure);
        wind = (TextView) findViewById(R.id.wind);
        relwet = (TextView) findViewById(R.id.relwet);
        heat = (TextView) findViewById(R.id.heat);
        Intent intent = getIntent();
        Weather weatherData =  (Weather) intent.getSerializableExtra("weatherData");
        date.setText(weatherData.dateString());
        tod.setText(weatherData.getTimeOfDay());
        weather.setText(weatherData.weatherString());
        pressure.setText(String.format(this.getString(R.string.pressure),weatherData.getPressure()));
        wind.setText(String.format(this.getString(R.string.wind) ,weatherData.windString()));
        relwet.setText(this.getString(R.string.relwet)+weatherData.getWetMin()+"% - "+weatherData.getWetMax()+"%");
        heat.setText(this.getString(R.string.heat)+weatherData.getHeatMin()+"..."+weatherData.getHeatMax());
    }
}
