package com.example.gismeteo.task;

import android.content.Context;
import android.os.AsyncTask;

import com.example.gismeteo.R;
import com.example.gismeteo.constants.Constants;
import com.example.gismeteo.json.JSONFromURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class RegionTask extends AsyncTask<Void, String, String> {

    public interface RegionTaskListener {
        public void onRegionTaskComplete(String region);
    }

    private Context context;
    private double lat, lng;
    private RegionTaskListener callback;

    public RegionTask(Context context, double lat, double lng, RegionTaskListener callback) {
        this.context = context;
        this.callback = callback;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        String regionName = new String();
        String giscode = new String();
        try {
            regionName = regionFromLocation(lat, lng);
            giscode = gisCodeFromRegion(regionName, context);
            return giscode;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        callback.onRegionTaskComplete(result);
    }
    private String regionFromLocation(double lat, double lng) throws JSONException {
        String regionName = new String();
        JSONObject jsonObj = JSONFromURL.getJSON(String.format(context.getString(R.string.gapi_region_url), lat + "," + lng));
        String Status = jsonObj.getString(Constants.STATUS);
        if (Status.equalsIgnoreCase(Constants.OK)) {
            JSONArray results = jsonObj.getJSONArray(Constants.RESULTS).getJSONObject(0).getJSONArray(Constants.ADDRESS_COMPONENTS);
            for(int j=1;j<results.length();j++){
                String adminArea;
                adminArea = ((JSONArray)((JSONObject)results.get(j)).get(Constants.TYPES)).getString(0);
                if (adminArea.compareTo(Constants.ADML1) == 0) {
                    regionName = ((JSONObject)results.get(j)).getString(Constants.SHORT_NAME);
                }
            }
        }
        return regionName;
    }
    private String gisCodeFromRegion(String region, Context context) throws XmlPullParserException, IOException {
        if(region != null){
            String gisCode = new String();
            XmlPullParser xpp= context.getResources().getXml(R.xml.gismeteo_city);
            String tagName = new String();
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if(xpp.getEventType() == XmlPullParser.START_TAG) {
                    tagName = xpp.getName();
                }
                if(xpp.getEventType() == XmlPullParser.TEXT) {
                    if (tagName.equals(Constants.REG_NAME))
                        if(xpp.getText().equals(region)) {
                            return gisCode;
                        }
                    if(tagName.equals(Constants.REG_CODE)) {
                        gisCode = xpp.getText();
                    }
                }
                xpp.next();
            }
        }
        return null;
    }
}