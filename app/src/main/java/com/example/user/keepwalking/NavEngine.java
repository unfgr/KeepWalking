package com.example.user.keepwalking;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by User on 22/4/2018.
 */

public class NavEngine {

    public NavEngine() {
    }


    public int getPointsDistance(LatLng p1, LatLng p2){
        Location locationA = new Location("point A");
        locationA.setLatitude(p1.latitude);
        locationA.setLongitude(p1.longitude);
        Location locationB = new Location("point B");
        locationB.setLatitude(p2.latitude);
        locationB.setLongitude(p2.longitude);
        return (int)locationA.distanceTo(locationB);
    }


}
