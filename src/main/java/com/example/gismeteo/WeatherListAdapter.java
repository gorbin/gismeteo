package com.example.gismeteo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class WeatherListAdapter extends BaseExpandableListAdapter {
    private final Context context;
    private final ArrayList<Weather> forecast;
	private String[] tempColorArray = new String[4];
    private String[] todArray = new String[4];
    private int height;
    public WeatherListAdapter(Context context, ArrayList<Weather> forecast, int height)
    {
        this.context = context;
        this.forecast = forecast;
        this.height = height;
		tempColorArray = context.getResources().getStringArray(R.array.temp_color);
		todArray = context.getResources().getStringArray(R.array.time_day);
    }
    static class ViewHolder {
        public TextView date, tod, weather, pressure, wind, relwet, heat;
    }
	private void WeatherColor(int position, View rowView) {
		for(int i = 0; i < todArray.length; i++)
		{
			if(forecast.get(position).getTimeOfDay().equals(todArray[i])){
				rowView.setBackgroundColor(Color.parseColor(tempColorArray[i]));
			}
		}
		// if (forecast.get(position).getTimeOfDay().equals(YTRO))
		// {
			// rowView.setBackgroundColor(Color.parseColor(tempColorArray[0]));
		// }
		// if (forecast.get(position).getTimeOfDay().equals(DAY))
		// {
			// rowView.setBackgroundColor(Color.parseColor(tempColorArray[1]));
		// }
		// if (forecast.get(position).getTimeOfDay().equals(EVENING))
		// {
			// rowView.setBackgroundColor(Color.parseColor(tempColorArray[2]));
		// }
		// if (forecast.get(position).getTimeOfDay().equals(NIGHT))
		// {
			// rowView.setBackgroundColor(Color.parseColor(tempColorArray[3]));
		// }

	}
	@Override
    public int getGroupCount() {
        return forecast.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return forecast.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return forecast.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_item, null, true);
            rowView.setMinimumHeight(height/5);
            ViewHolder holder = new ViewHolder();
            holder.date = (TextView) rowView.findViewById(R.id.date);
            holder.tod = (TextView) rowView.findViewById(R.id.tod);
            holder.weather = (TextView) rowView.findViewById(R.id.weather);
            rowView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.date.setText(forecast.get(groupPosition).dateString());
        holder.tod.setText(forecast.get(groupPosition).getTimeOfDay());
        holder.weather.setText(forecast.get(groupPosition).weatherString());
		WeatherColor(groupPosition, rowView);
		// if (isExpanded){
		   // rowView.setBackgroundColor(Color.parseColor("#F0F0F0"));
        // }
        // else{
			 // rowView.setBackgroundColor(Color.parseColor("#123456"));
        // }
        return rowView;

    }
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        View childView = convertView;
		if (childView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            childView = inflater.inflate(R.layout.child_view, null, true);
			childView.setMinimumHeight(height/5);
            ViewHolder holder = new ViewHolder();
			holder.pressure = (TextView) childView.findViewById(R.id.pressure);
			holder.wind = (TextView) childView.findViewById(R.id.wind);
			holder.relwet = (TextView) childView.findViewById(R.id.relwet);
			holder.heat = (TextView) childView.findViewById(R.id.heat);
			childView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) childView.getTag();
		holder.pressure.setText(String.format(context.getString(R.string.pressure),forecast.get(groupPosition).getPressure()));
        holder.wind.setText(String.format(context.getString(R.string.wind) ,forecast.get(groupPosition).windString()));
        holder.relwet.setText(context.getString(R.string.relwet)+forecast.get(groupPosition).getWetMin()+"% - "+forecast.get(groupPosition).getWetMax()+"%");
        holder.heat.setText(context.getString(R.string.heat)+forecast.get(groupPosition).getHeatMin()+"..."+forecast.get(groupPosition).getHeatMax());
		WeatherColor(groupPosition, childView);
        return childView;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
