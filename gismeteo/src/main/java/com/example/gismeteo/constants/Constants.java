package com.example.gismeteo.constants;

public class Constants {
	public static final String LOG_TAG = "myLogs";
    public static final String EXIT = "EXIT";
    public static final String FORECAST = "forecast";
    public static final String REGION = "region";
	public static final String NOTIF = "firstNotif";
	public static final String LOC_TIME = "location_time";
	public static final String LONG_LOC = "longloc";
    public static final int TIMEOUT = 1000 * 20;
    public static final int HOUR = 1000 * 60 * 60;
    public static final int MIN = 1000 * 60;
	public static final int TIME_FOR_LOC = 3  * 60 * 60 * 1000;
	public static int UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
	public static int FAST_INTERVAL_CEILING_IN_MILLISECONDS = 1000;

    // gismeteo constants
    public static final String REG_NAME = "region_name";
	public static final String REG_NUM = "region_code";
	public static final String REG_CODE = "gismeteo_code";
	public static final String ITEM = "item";

    // google maps constants
    public final static String STATUS = "status";
    public final static String OK = "OK";
    public final static String RESULTS = "results";
    public final static String ADDRESS_COMPONENTS = "address_components";
    public final static String TYPES = "types";
    public final static String ADML1 = "administrative_area_level_1";
    public final static String SHORT_NAME = "short_name";

}