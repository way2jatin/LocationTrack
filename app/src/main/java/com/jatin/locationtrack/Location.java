package com.jatin.locationtrack;

/**
 * Created by uw on 17/4/17.
 */

public class Location {

    String name;
    String devId;
    double lat;
    double lon;

    public Location(double lat, double lon,String name,String devId) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.devId = devId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
