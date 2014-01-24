package com.example.gismeteo.interfaces;


import java.util.ArrayList;
import com.example.gismeteo.utils.Weather;
public interface ForecastTaskListener {
    public void onTaskComplete(ArrayList<Weather> forecast);
}