package com.example.gismeteo.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;

import com.example.gismeteo.R;
import com.example.gismeteo.utils.Region;


public class RegionListAdapter extends BaseAdapter {
    private final Activity context;
	private ArrayList<Region> regionList;
		private ArrayList<Region> arraylist;

    public RegionListAdapter(Activity context, ArrayList<Region> regionList) {
        this.context = context;
		this.regionList = regionList;
        this.arraylist = new ArrayList<Region>();
        this.arraylist.addAll(regionList);
    }
    static class ViewHolder {
        public TextView textViewNum;
        public TextView textViewName;
    }

    @Override
    public int getCount() {
        return regionList.size();
    }

    @Override
    public Object getItem(int i) {
        return regionList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
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
        holder.textViewNum.setText(regionList.get(position).getNum());
        holder.textViewName.setText(regionList.get(position).getName());
        return rowView;
    }

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