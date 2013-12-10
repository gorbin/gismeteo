package com.example.gismeteo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class WeatherListAdapter extends ArrayAdapter<Weather> {

    private final Context context;
    private final ArrayList<Weather> forecast;

    public WeatherListAdapter(Context context, ArrayList<Weather> forecast)
    {
        super(context, R.layout.list_item, forecast);
        this.context = context;
        this.forecast = forecast;

    }
    static class ViewHolder {
        public TextView date, tod, weather;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item, null, true);
            holder = new ViewHolder();
            holder.date = (TextView) rowView.findViewById(R.id.date);
            holder.tod = (TextView) rowView.findViewById(R.id.tod);
            holder.weather = (TextView) rowView.findViewById(R.id.weather);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        holder.date.setText(forecast.get(position).dateString());
        holder.tod.setText(forecast.get(position).getTimeOfDay());
        holder.weather.setText(forecast.get(position).weatherString());
        return rowView;
    }
}
