package com.example.gismeteo;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class RegionList extends Activity implements AdapterView.OnItemClickListener {
	private final static String REG_NAME = "region_name";
    private ListView regionListView;
    private ArrayList<String> regionList = new ArrayList<String>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
 
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.regions);
		regionListView = (ListView)findViewById(R.id.region_list);
		regionListView.setOnItemClickListener(this);
		XmlPullParser xpp = this.getResources().getXml(R.xml.gismeteo_city);
		String tagName = new String();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if(xpp.getEventType() == XmlPullParser.START_TAG) {
                    tagName = xpp.getName();
                }
                if(xpp.getEventType() == XmlPullParser.TEXT) {
                    if (tagName.equals(REG_NAME)){
                        regionList.add(xpp.getText());
                    }
                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,regionList);
		regionListView.setAdapter(adapter);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.main, menu);
			return false;
	}
	@Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra("region", regionList.get(position).toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}