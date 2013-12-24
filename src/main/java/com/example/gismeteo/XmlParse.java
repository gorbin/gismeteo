package com.example.gismeteo;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class XmlParse {

    private final static String FORECAST = "FORECAST", DAY = "day", MONTH = "month", YEAR = "year", TOD = "tod", TEMPERATURE = "TEMPERATURE";
    private final static String WEEKDAY = "weekday", CLOUDINESS = "cloudiness", PRECIPITATION = "precipitation", PHENOMENA = "PHENOMENA";
    private final static String MAX = "max", MIN = "min", PRESSURE = "PRESSURE", WIND = "WIND", DIRECTION = "direction";
    private final static String RELWET = "RELWET", HEAT = "HEAT";
    private final static String GIS_CODE = "gismeteo_code", REG_NAME = "region_name";
    private String gisCode;
    private ArrayList<Weather> forecastList = new ArrayList<Weather>();
    private String url = new String();
    private Context context;

    public XmlParse(Context context, String region) throws IOException, XmlPullParserException
    {
        this.context = context;

        gisCode = getGisCode(context, region);
        if(gisCode != null){
			url = String.format(context.getString(R.string.gismeteo_url),gisCode);
            parseUrl();
        }
    }
    public void parseUrl() throws IOException, XmlPullParserException
    {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser xpp = factory.newPullParser();
        URL input = new URL(url);
        xpp.setInput(input.openStream(), "utf-8");
        String day, month, year;
        Weather forecastItem = new Weather(context);
        int i = 0;
        while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
            if(xpp.getEventType() == XmlPullParser.START_TAG){
                if (xpp.getName().equals(FORECAST))
                {
                    day = xpp.getAttributeValue(null, DAY);
                    month = xpp.getAttributeValue(null, MONTH);
                    year = xpp.getAttributeValue(null, YEAR);
                    forecastItem.setDate(day, month, year);
                    forecastItem.setTimeOfDay(xpp.getAttributeValue(null, TOD));
                    forecastItem.setWeekDay(xpp.getAttributeValue(null, WEEKDAY));
                }
                if (xpp.getName().equals(PHENOMENA))
                {
                    forecastItem.setCloudiness(xpp.getAttributeValue(null, CLOUDINESS));
                    forecastItem.setPrecipitation(xpp.getAttributeValue(null, PRECIPITATION));
                }
                if (xpp.getName().equals(PRESSURE))
                {
                    forecastItem.setPressure(xpp.getAttributeValue(null, MAX), xpp.getAttributeValue(null, MIN));
                }
                if (xpp.getName().equals(TEMPERATURE))
                {
                    forecastItem.setTemperatureMax(xpp.getAttributeValue(null, MAX));
                    forecastItem.setTemperatureMin(xpp.getAttributeValue(null, MIN));
                }
                if (xpp.getName().equals(WIND))
                {
                    forecastItem.setWindMax(xpp.getAttributeValue(null, MAX));
                    forecastItem.setWindMin(xpp.getAttributeValue(null, MIN));
                    forecastItem.setWindDirection(xpp.getAttributeValue(null, DIRECTION));
                }
                if (xpp.getName().equals(RELWET))
                {
                    forecastItem.setWetMax(xpp.getAttributeValue(null, MAX));
                    forecastItem.setWetMin(xpp.getAttributeValue(null, MIN));
                }
                if (xpp.getName().equals(HEAT))
                {
                    forecastItem.setHeatMax(xpp.getAttributeValue(null, MAX));
                    forecastItem.setHeatMin(xpp.getAttributeValue(null, MIN));
                }
            }
            if(xpp.getEventType() == XmlPullParser.END_TAG){
                if (xpp.getName().equals(FORECAST))
                {
                    forecastList.add(i,forecastItem);
                    forecastItem = new Weather(context);
                    i++;
                }
            }
            xpp.next();
        }
    }
    public String getGisCode(Context context, String region) throws IOException, XmlPullParserException
    {
        if(region != null){
            String gisCode = new String();
            XmlPullParser xpp= context.getResources().getXml(R.xml.gismeteo_city);
            String tagName = new String();
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if(xpp.getEventType() == XmlPullParser.START_TAG) {
                    tagName = xpp.getName();
                }
                if(xpp.getEventType() == XmlPullParser.TEXT) {
                    if (tagName.equals(REG_NAME))
                        if(xpp.getText().equals(region)) {
                            return gisCode;
                        }
                    if(tagName.equals(GIS_CODE)) {
                        gisCode = xpp.getText();
                    }
                }
                xpp.next();
            }
        }
        return null;
    }
    public ArrayList<Weather> getForecast()
    {
        return forecastList;
    }
}