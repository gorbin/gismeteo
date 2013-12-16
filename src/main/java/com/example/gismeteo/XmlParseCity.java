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
	private String gisCode;
    public XmlParseCity(Context context, String region) throws IOException, XmlPullParserException
    {
		XmlPullParser xpp = context.getResources().getXml(R.xml.gismeteo_city);
        String tagName = new String();
        while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
            if(xpp.getEventType() == XmlPullParser.START_TAG) {
			    tagName = xpp.getName();
			}
			if(xpp.getEventType() == XmlPullParser.TEXT) {
                if (tagName.equals(REG_CODE))
                    if(xpp.getText().equals(region)) {
							return;
						}
                if(tagName.equals(GIS_CODE)) {
                    gisCode = xpp.getText();
				}
			}
            xpp.next();
        }
    }

    public String getGisCode()
    {
        return gisCode;
    }
}
