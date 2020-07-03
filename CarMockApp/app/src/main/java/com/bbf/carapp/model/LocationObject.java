package com.bbf.carapp.model;

import java.io.Serializable;

public class LocationObject implements Serializable {

    private double longitude;
    private double latitude;

    public LocationObject(){}

    public LocationObject(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
