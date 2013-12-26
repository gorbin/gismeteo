package com.example.gismeteo;


import java.util.ArrayList;

public interface ForecastTaskListener {
    public void onTaskComplete(ArrayList<Weather> forecast);
}