package com.example.gismeteo.utils;

public class Region {

    private String num;
    private String name;
    private String giscode;
    
    public Region() {
    }
    
    public void setNum(String num) {
		if (num.length() == 1) {
			num = "0"+num;
		}
        this.num = num;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setGisCode(String giscode) {
		this.giscode = giscode;
    }
    
    public String getNum() {
        return num;
    }
    public String getName() {
        return name;
    }
    public String getGisCode() {
        return giscode;
    }

}