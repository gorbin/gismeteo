package com.example.gismeteo;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class XmlParse {
    private ArrayList<Weather> forecastList = new ArrayList<Weather>();
    private String url="http://informer.gismeteo.ru/xml/29634_1.xml";
    public XmlParse(Context context) throws IOException, XmlPullParserException
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
                if (xpp.getName().equals("FORECAST"))
                {
                    day = xpp.getAttributeValue(null,"day");
                    month = xpp.getAttributeValue(null,"month");
                    year = xpp.getAttributeValue(null,"year");
                    forecastItem.setDate(day, month, year);
                    forecastItem.setTimeOfDay(xpp.getAttributeValue(null,"tod"));
                    forecastItem.setWeekDay(xpp.getAttributeValue(null,"weekday"));
                }
                if (xpp.getName().equals("PHENOMENA"))
                {
                    forecastItem.setCloudiness(xpp.getAttributeValue(null,"cloudiness"));
                    forecastItem.setPrecipitation(xpp.getAttributeValue(null, "precipitation"));
                }
                if (xpp.getName().equals("PRESSURE"))
                {
                    forecastItem.setPressure(xpp.getAttributeValue(null, "max"), xpp.getAttributeValue(null, "min"));
                }
                if (xpp.getName().equals("TEMPERATURE"))
                {
                    forecastItem.setTemperatureMax(xpp.getAttributeValue(null,"max"));
                    forecastItem.setTemperatureMin(xpp.getAttributeValue(null, "min"));
                }
                if (xpp.getName().equals("WIND"))
                {
                    forecastItem.setWindMax(xpp.getAttributeValue(null,"max"));
                    forecastItem.setWindMin(xpp.getAttributeValue(null, "min"));
                    forecastItem.setWindDirection(xpp.getAttributeValue(null,"direction"));
                }
                if (xpp.getName().equals("RELWET"))
                {
                    forecastItem.setWetMax(xpp.getAttributeValue(null,"max"));
                    forecastItem.setWetMin(xpp.getAttributeValue(null, "min"));
                }
                if (xpp.getName().equals("HEAT"))
                {
                    forecastItem.setHeatMax(xpp.getAttributeValue(null,"max"));
                    forecastItem.setHeatMin(xpp.getAttributeValue(null, "min"));
                }
            }
            if(xpp.getEventType() == XmlPullParser.END_TAG){
                if (xpp.getName().equals("FORECAST"))
                {
                    forecastList.add(i,forecastItem);
                    forecastItem = new Weather(context);
                    i++;
                }
            }
            xpp.next();
        }

    }
    public ArrayList<Weather> getForecast()
    {
        return forecastList;
    }
}
