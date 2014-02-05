package com.example.gismeteo.interfaces;

import java.util.ArrayList;
import com.example.gismeteo.utils.Region;
public interface XMLRegionTaskListener {
    public void onXMLRegionTaskComplete(ArrayList<Region> regionList);
}