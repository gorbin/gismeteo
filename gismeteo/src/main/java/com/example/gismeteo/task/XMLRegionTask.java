package com.example.gismeteo.task;

import android.content.Context;
import android.os.AsyncTask;

import com.example.gismeteo.R;
import com.example.gismeteo.interfaces.XMLRegionTaskListener;
import com.example.gismeteo.constants.Constants;
import com.example.gismeteo.utils.Region;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class XMLRegionTask extends AsyncTask<Void, Void, ArrayList<Region>> {
        private Context context;
        private ArrayList<Region> taskRegionList;
        private XMLRegionTaskListener callback;

        public XMLRegionTask(Context context, ArrayList<Region> regionList, XMLRegionTaskListener callback) {
            this.context = context;
            this.taskRegionList = regionList;
            this.callback = callback;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<Region> doInBackground(Void... params) {
            XmlPullParser xpp = context.getResources().getXml(R.xml.gismeteo_city);
			Region regionItem = new Region();
            String tagName = new String();
            int i = 0;
            try {
                while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                    if(xpp.getEventType() == XmlPullParser.START_TAG) {
                        tagName = xpp.getName();
                    }
                    if(xpp.getEventType() == XmlPullParser.TEXT) {
                        if (tagName.equals(Constants.REG_NAME)){
							regionItem.setName(xpp.getText());
                        }
                        if (tagName.equals(Constants.REG_NUM)){
							regionItem.setNum(xpp.getText());
                        }
						if (tagName.equals(Constants.REG_CODE)){
							regionItem.setGisCode(xpp.getText());
                        }
                    }
					if(xpp.getEventType() == XmlPullParser.END_TAG){
						if (xpp.getName().equals(Constants.ITEM)) {
							taskRegionList.add(i,regionItem);
							regionItem = new Region();
							i++;
						}
					}
                    xpp.next();
                }
                return taskRegionList;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(ArrayList<Region> result) {
            super.onPostExecute(result);
            callback.onXMLRegionTaskComplete(result);
            }
}