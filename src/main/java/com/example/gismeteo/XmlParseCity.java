package com.example.gismeteo;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class XmlParseCity {

	private final static String GIS_CODE = "gismeteo_code", REG_CODE = "region_code"; 
	private Strring gisCode;
    public XmlParseCity(Context context, String region) throws IOException, XmlPullParserException
    {
		XmlPullParser xpp = context.getResources().getXml(R.xml.gismeteo_city);
		
        while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
            if(xpp.getEventType() == XmlPullParser.START_TAG) {
				if (xpp.getName().equals(GIS_CODE)){	
					if(xpp.getEventType() == XmlPullParser.TEXT) {
						giscode = xpp.getText()
					}
				}
				if (xpp.getName().equals(REG_CODE)) {
					if(xpp.getEventType() == XmlPullParser.TEXT) {
						if(xpp.getText().equals(region))
						{
							return;
						}
					}
				}
			}		
        }
            xpp.next();
    }

    public String getGisCode()
    {
        return gisCode;
    }
}
