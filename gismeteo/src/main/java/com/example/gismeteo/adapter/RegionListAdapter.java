package com.example.gismeteo.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

import com.example.gismeteo.utils.Region;


public class RegionListAdapter extends ArrayAdapter<String> {
    private final Activity context;
	private final ArrayList<Region> regionList;
		private ArrayList<Region> arraylist;

    public MyArrayAdapter(Activity context, ArrayList<Region> regionList) {
        super(context, R.layout.region_row, nums, names);
        this.context = context;
		this.regionList = regionList;
		this.arraylist = regionList;
    }
    static class ViewHolder {
        public TextView textViewNum;
        public TextView textViewName;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.region_row, null, true);
            holder = new ViewHolder();
            holder.textViewNum = (TextView) rowView.findViewById(R.id.num);
            holder.textViewName = (TextView) rowView.findViewById(R.id.name);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        holder.textViewNum.setText(regionList.get(position).getNums());
        holder.textViewName.setText(regionList.get(position).getNames());
        return rowView;
    }

	// Filter Class
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		regionList.clear();
		if (charText.length() == 0) {
			regionList.addAll(arraylist);
		} else {
			for (Region regionFilter : arraylist) {
				if((regionFilter.getName().toLowerCase(Locale.getDefault()).contains(charText))||(regionFilter.getNum().toLowerCase(Locale.getDefault()).contains(charText))){
					regionList.add(regionFilter);
				}
			}
		}
		notifyDataSetChanged();
	}
}