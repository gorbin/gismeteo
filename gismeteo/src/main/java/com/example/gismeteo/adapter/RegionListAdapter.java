package com.example.gismeteo.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RegionListAdapter extends ArrayAdapter<String> {
    private final Activity context;
	private final String[] nums;
    private final String[] names;

    public MyArrayAdapter(Activity context, String[] nums, String[] names) {
        super(context, R.layout.region_row, nums, names);
        this.context = context;
		this.nums = nums;
        this.names = names;
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
        holder.textViewNum.setText(nums[position]);
        holder.textViewName.setText(names[position]);
        return rowView;
    }
}