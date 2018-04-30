package com.example.user.keepwalking;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by User on 22/4/2018.
 */

public class PathPoint {
    private double lat;
    private double lon;
    private double speed;
    private String type = "normal";
    private float gnssAccuracy;
    private String accessibility = "good";
    private String mobility = "walk";
    private String comments = "";

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(String accessibility) {
        this.accessibility = accessibility;
    }

    public String getMobility() {
        return mobility;
    }

    public void setMobility(String mobility) {
        this.mobility = mobility;
    }

    public float getGnssAccuracy() {
        return gnssAccuracy;
    }

    public void setGnssAccuracy(float gnssAccuracy) {
        this.gnssAccuracy = gnssAccuracy;
    }

    public PathPoint() {

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

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
